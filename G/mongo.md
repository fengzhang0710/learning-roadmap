### 前端-api部分

假设存在这样一个labelset

```json
//所有的一级子field都是约定好的
{
  "id" : "5df2f71c2d719106c3e133b0",
  //这个meta里，保存了这个labelset里的所有的已经筛选出来的标签的类型，相当于LabelValues里面的所有的值的汇总，其中的meta->taskTypes->categories->subcategories，这几个字段是一定存在的，其他的字段是additional的
  "meta" : {
    "name" : "场景识别-0001",
    "taskTypes" : [{
        "name" : "场景分类",
        "categories" : [{
            "nameEn" : "time",
            "subcategories" : [{
                "id" : "0cbb7dbd59ba40cbb24698f37ec34f8f",
                "name" : "白天",
                "nameEn" : "daytime"
              }, {
                "name" : "夜晚",
                "nameEn" : "nighttime",
                "id" : "a0486ee82146453293bb8b59f462d427"
              }],
            "id" : "5ddbb7521f943537b14b2bfd",
            "name" : "图中的时间是？"
          }, {
            "name" : "图中的天气是？",
            "nameEn" : "weather",
            "subcategories" : [{
                "id" : "8d3cab9e8319492eabf0c89d50258163",
                "name" : "晴天",
                "nameEn" : "sunny"
              }, {
                "id" : "2b0103a185194e968b25ae2dce3c4dbf",
                "name" : "多云",
                "nameEn" : "cloudy"
              }],
            "id" : "5ddbb7521f943537b14b2bfe"
          }],
        "code" : 7
      }]
  },
  "ownerid" : "LabelStore_Database_TestOwner1",
  "contentsetid" : "12345678-1234-1234-1234-123456789abc",
  "contentsetfilter" : {
    "weather" : "rain",
    "location" : {
      "Latitude" : 1.2,
      "Longitude" : 2.3
    },
    "mapStringInterface" : {
      "rawMapInterfaceKey1" : "rawMapInterfaceV1",
      "rawMIKNested" : {
        "k1" : "v1",
        "k2" : 2
      }
    }
  },
  
  //objectpaths是一个string类型的数组，保存已经有了的所有数组内容
  "objectpaths" : ["obj1.txt", "testCreateLabelSet/test2.png", "test/test3.jpg"]
}
```



listLabelSets的参数labelSet的Api的描述

参数labelSet并不是一个精确的匹配值，传递过来的参数labelSet里，其中有些字段是精确匹配，有些字段是使用or或者in匹配，最后把所有符合条件的labelSets以数组的形式返回



labelSet里的每个字段描述

* id的筛选语义是精准匹配，给定的string类型的id，会精准匹配(key为id)
  * 没有这个字段 ---> 则不筛选这个字段，mongo中的doc里不管有没有这个字段都可以，值是任意值也都可以
  * 有这个id字段，值为空string("")/null ---> 默认会忽略掉为空的字符串，效果同上，筛选全部(目前不支持筛选为空的id，也没这个需求，因为存的时候不会生成有空的id的labelSet)
  * 填写给定的字符串，默认会筛选出id为给定字符串的labelSet，不做是否合法检查
* limitation：目前是不支持精确查询该字段为空的情况的,即精确匹配ID = "" 的内容；目前也不支持模糊查询

```json
{"labelSet":{"id":"5df3539db92da137ce59b0b0"}} //精确匹配

//这两种情况下，都不会筛选这个字段，目前不支持查询id为空情况
{"labelSet":{}} //没有这个字段
{"labelSet":{"id":""}} //这个字段的值是空的
{"labelSet":{"id":null}} //这种筛选效果同上，null值对于基本类型的都是默认不筛选，效果等同于默认值
```



* 对于基本类型顶级元素的筛选(ownerid,contentsetid)，目前只提供了最基本的精确，比如要查询上述的ownerid和contentsetid，只需要构造这样的json即可(其他语义类似id)
  * 没有这个字段 ---> 则不筛选这个字段，mongo中的doc里不管有没有这个字段都可以，值是任意值也都可以
  * 填写为空string(bool类型填写false，数字类型填写0)，默认会忽略掉为空的字符串(其他的默认值)，筛选全部(目前不支持筛选为空的id，也没这个需求，因为存的时候不会生成有空的id的labelSet)
  * 填写给定的字符串，默认会筛选出id为给定字符串的labelSet，不做是否合法检查
  * limitation同上

```json
//例子参考id的例子
{"labelSet":{
  "ownerid" : "LabelStore_Database_TestOwner1",
  "contentsetid" : "12345678-1234-1234-1234-123456789abc"
}}
```

​		



* 对于顶级元素为基本类型数组(objectPaths)的查询，目前提供的语义为in，即：只要你给定的数组中，mongo里已经存储的数组里只要有一个元素匹配了给定的数组中任意一个元素，即算匹配成功，即取交集元素个数>1。目前暂时没有提供精确匹配的语义(即objectpaths要和给定的数组的元素一模一样，包括顺序和具体的内容)，暂时也没这个需求。
  
  1 没有这个字段 --->  默认不筛选这个字段。
  
  * 填写一个空的数组，默认会精确匹配这个空的数组，即mongo中这个数组的值为空的doc(如果想要查询元素为空的内容，可以新增一个数组，其中填写上一个空的字符串，这样就可以查询数组中有空元素的doc)。
  * 填写一个正常的数组，会以类似sql的in操作的语句，匹配mongo的库中的数组里，只要出现过给定参数数组中任意一个元素的doc(如果想要查询一个包含 "" empty string元素的数组，只需要在数组中加入这个元素即可)。
  * Limitation：目前给定的数组，都是按照in的方式存储的，暂时不支持给定的参数数组精确匹配mongo中存的数组 eg: 给定["123","234"]数组，想要精确匹配mongo中存["123","234"]的数组的内容。

```json
//对于objectpaths的筛选，默认使用的是类似于sql的in，只要存在一个即算匹配
//目前没有精确匹配的

//没有objectpaths这个字段，则mongo不会判断这个字段的值，甚至这个字段不存在也可以
{"labelSet":{  "ownerid" : "LabelStore_Database_TestOwner1"}} 

//有这个字段，但是给定的数组里的值为一个长度为空的数组，当写成这种形式的时候，mongo会精确匹配objectpaths为[]空数组的doc。
{"labelSet":{  "ownerid" : "LabelStore_Database_TestOwner1","objectpaths":[]}} 

//当时用null做匹配的时候，效果和不写这个字段相同，该null值会被忽略掉
{"labelSet":{  "ownerid" : "LabelStore_Database_TestOwner1","objectpaths":null}} 


//当给定一个数组的时候，mongo中存的数组，只要有一个元素和传递的参数数组相同，那么就算匹配成功，类似于sql的in，取两个数组的交集，只要这个交集>1就算匹配成功
{"labelSet":{  "ownerid" : "LabelStore_Database_TestOwner1","objectpaths":["test/test3.jpg","test/test2.jpg"]}} 

```



* 对于嵌套的顶级元素(contentsetfilter,meta)，筛选的语义比较复杂
  * meta本身是一个map[string]interface{},但是内部会有一些包含了数组的value,而且这种关系还有可能是嵌套的，所以如果想要筛选meta中的具体的字段的field，具体例子如下



eg1:筛选meta内部一级目录的基本内容

​	比如要筛选上述中的meta的name的字段，因为是基本类型，所以语义同上

```json
{
  "ownerid" : "LabelStore_Database_TestOwner1",
  "contentsetid" : "12345678-1234-1234-1234-123456789abc",
  "meta": 
  				{
            "name":"场景识别-0001",//只筛选name一个字段，只要meta中的name是相同即可
            "age" : 0,						//下面的这三个字段，因为填写的内容是mongo中默认的值内容，所以会被
            "flag": false,				//忽略掉，默认不筛选这三个字段，也不判断是否存在(已知Limit)
            "string2":"",
          } 
  																//剩余没有填写的字段，默认不筛选，也不判断是否存在
  
}
```



eg2:筛选嵌套类型的数组(即数组中为嵌套类型，筛选嵌套类型的值)

​	比如要筛选meta中taskTypes的内容,taskTypes是一个数组，所以针对于数组的内容，提供的语义都是in,即只要mongo中有一个数组的内容，满足给定的数组的任何一个元素的所有内容，即算匹配成功

```json
//数组中的每个元素互相匹配的关系是or
{
  "ownerid" : "LabelStore_Database_TestOwner1",
  "contentsetid" : "12345678-1234-1234-1234-123456789abc",
  "meta": {
   //对于内部的基本类型字段，规则如上一条。 多个筛选条件拼接规则为and
    "name":"场景识别-0001",
    //这个例子中， 给定的taskTypes是一个数组，数组中有两个元素，一个name是"场景分类",另一个是"图像识别"，所以在mongo中，只要有doc，他的name是"场景分类"或者是"图像识别",只要满足一个即可。即数组中不同的元素的匹配关系为or。
    "taskTypes": [
      {
      	     "name" : "场景分类",
    	}, 
      {
      	     "name" : "图像识别"
    	}
    ],
    
          },
}

//--------------------------------------------------------
//数组中单个元素内部，匹配的关系是and，如下eg
{
  "ownerid" : "LabelStore_Database_TestOwner1",
  "contentsetid" : "12345678-1234-1234-1234-123456789abc",
  "meta": {
    "name":"场景识别-0001",
//数组中两个元素，他们直接的匹配关系是or，主要mongo中taskTypes的数组里，有元素匹配下列给的两个元素中的任意一个，就算匹配成功(元素之间，元素内部是and)
    "taskTypes": [
      {
        		//对于单个元素内部，name和age他们的筛选条件是and，即如果doc中数组的元素需要匹配这个元素，需要						//满足name和age两个字段才算匹配成功。
      	     "name" : "场景分类",
        			"age" : 10,
    	}, 
      {
      	     "name" : "图像识别"
    	}
    ],
    
          },
}
```



eg3:对于嵌套的数组

​	我们实际的例子中，给定的doc嵌套的比较复杂， meta中的taskTypes是一个数组，数组中的的每个元素内部，也是一个结构体，其中也有具体的value类型是数组的，一直嵌套下去(目前是嵌套了三层,meta->taskTypes[]->categories[]->subcategories[]).虽然嵌套的深度比较深，但是其实筛选语义和单数组是一致的

```json
//数组中的每个元素互相匹配的关系是or
{
  "ownerid" : "LabelStore_Database_TestOwner1",
  "contentsetid" : "12345678-1234-1234-1234-123456789abc",
  "meta": {
    "name":"场景识别-0001",
    //这个例子中， 给定的taskTypes是一个数组，数组中有两个元素，一个name是"场景分类",另一个是"图像识别"，所以在mongo中，只要有doc，他的name是"场景分类"或者是"图像识别",只要满足一个即可。即数组中不同的元素的匹配关系为or。
    "taskTypes": [
      {
      	     "name" : "场景分类",
              //嵌套类型中，有数组类型的值，那么其实筛选的条件类似外层的数组值
        		 "categories": [            
               {
                 "nameEn" : "time",
               }
             ]
    	}, 
      {
      	     "name" : "图像识别"
    	}
    ],
    
          },
}

```





### 后端-mongo部分

mongo中存储的doc的实例

![A MongoDB document.](https://docs.mongodb.com/manual/_images/crud-annotated-document.bakedsvg.svg)



mongo的查询

​	1.层级的概念

​	mongo中查询有一个层级的概念，doc下的第一层的field是直接目录,如上图，name,age,status,groups就是顶层目录，第一级的直接目录

```json
{
  "name" : "sue",
  "age"	:	26,
  "status" : "A",
  "father" : {"name":"frank","age":56}
}

```

而上面这个例子中， father里的name和age就不是一级目录，他们已经是一个嵌套的doc了，他们并不是一级目录，属于二级目录，以此类推。

2.查询

​	一级目录的查询，mongo中是不限定field出现的顺序的，也不要求所有的field都要出现的。所以

```shell
db.person.find({"name":"sue"})，这个筛选条件会吧一级目录里所有的名字叫sue的人筛选出来，而其他字段，则没有任何要求
db.person.find({"name":"sue","age":26})，这个筛选条件会要求两个字段同时匹配
db.person.find({"age":26,"name":"sue"})，顺序也不影响着两个字段的关系
```

​	二级目录的查询，mongo中是需要精确匹配的，需要所有字段，而且顺序需要一致才可以

```shell
db.person.find({"name":"sue","father": {"name":"frank"}) //这样是筛选不出来的，因为他的字段并没有填写完整
db.person.find({"name":"sue","father": {"age":56,"name":"frank"}) //这样也是筛选不出来的，因为虽然字段填写完整了，但是传入的筛选条件的字段顺序和mongo中存储的字段顺序不一致，mongo也是不能筛选出来对应的内容的
db.person.find({"name":"sue",
"father": {"name":"frank","age":56} //只有这样才能精确匹配，筛选出我们想要的结果)
```



为了解决二级目录的精确匹配问题(因为我们实际筛选的时候，往往是不能拿到所有字段来匹配的，而且mongo中存储的顺序和我们传入的字段顺序有时候并不一致，mongo存储时会颠倒顺序)，所以为了解决这个问题，mongo官方给出的是

点分法，把二级目录里的字段，映射到一级目录里

```shell
db.person.find({"father.name":"frank"})//把耳机目录里的字段，通过.的方式拼接上一级目录，这样就可以满足一级目录的筛选条件，不要求全部字段都存在，也不要求给定字段的顺序和mongo中存储的顺序一致
```



数组的查询

​	数组中可以是基本类型的元素，也可以是子document

```shell
{
	"name" : "sue",
	"age" :	30,
	"groups" : ["stu","intern","news","sports"],
	"parents":[{"name":"frank","age":60},{"name":"monica","age":50}]
}

{
	"name" : "sue",
	"age" :	30,
	"groups" : ["stu","intern","news","sports"],
	"parents":[{"name":"lip","age":28},{"name":"tami","age":50}]
}

#对于基本类型的数组，只能使用$in来做筛选，判断数组中是否满足给定的in条件
#如下的筛选条件里面，只要group里面有一个内容和给定的相同，就能筛选出给定的值
db.person.find({"groups":{$in:["intern","sports","some unkonwn field values"]}})


##----------------------------------------------------
##数组的元素是复合类型的筛选
#对于嵌套类型的数组，可以使用$in，也可以使用$elemMatch配合$or完成复杂的in操作(嵌套类型，直接的$in会有二级目录的精确匹配和字段顺序的要求，所以只能使用elemMatch和or配合完成操作)

$elemMatch
db.person.find({"name":"sue","parents":{$elemMatch:{query1,query2}})
#其中的query代表的是各种的query条件，比如
db.person.find({"name":"sue","parents":{$elemMatch:{{"name":"frank","age":{$gte:10}}}})
#判断名字是frank的，年龄大于等于10岁的query条件
#是一个筛选条件，只要数组中，任意一个元素的值满足所有的给定条件，即算判断成功
#可能会有疑问，这里的筛选，是针对于二级目录的筛选，但是他们并没有按照给定的顺序，但是仍然能查询出来对应的值
#原因就是因为，$elemMatch会重置顶级目录，意思就是，query里面的每个字段，匹配的规则都和顶级目录的匹配规则一样，满足类似于sql的or条件语句，不需要筛选全部即可。

#所以当我们配合$or使用的时候，就会有这种效果
db.person.find({"parents":{$elemMatch:{$or[{query1,query2}]}})
代表的是，只要parents这个数组里，任意一个元素，只要匹配给定的$or语句中给定的任意一个元素，即算匹配成功比如
eg3:
{"parents":{$elemMatch:{$or:[{"name":"sue","age":"60"},{"name":"tami"}]}}
#比如这个筛选条件，他会去筛选给定的parents数组中，任意一个元素的name为sue，age为60；或者name为tami的元素，只要有一个元素满足给定数组中的任意一个子元素，即算满足筛选条件，parent算匹配成功
```




回到我们的实际的例子中来，我们的结构是一个三层嵌套的结构，这个结构的meta里面，会保存当前这个labelset的taskTypes，对应一个嵌套类型的数组。而这个嵌套结构体里面，每一个都会有一个categories字段，这个字段代表的是这个taskTypes下所有筛选出来的categories,而其中每个categories下面，都会有一个subcategories数组，代表这个categories里所有的子categories，具体的例子请看最上面。

那么前端的需求是，前端只需要知道子结构体的某一些字段(不知道全部，所以二级目录的问题等不到解决)，所以我们必须flatten一下，同时，对于子数组的匹配情况，我们也有不同的需求，对于子数组，我们需要匹配的是，只要满足有一个即可。所以必须使用elemMatch配合or，完成前端的需求

```json
//bson.M，是mongo中的map类型
//bson.A，是mongo中的array类型
{
    "labelSet":{
        "id":"5df2f71c2d719106c3e133b0",
        "meta":{
            "taskTypes":[
                {
                    "name":"场景分类",
                    "categories":[
                        {
                            "nameEn":"animal",
                            "subcategories":[
                                {
                                    "nameEn":"dog",
                                    "id":"dogId"
                                },
                                {
                                    "nameEn":"cat",
                                    "id":"catId"
                                }
                            ]
                        },
                        {
                            "nameEn":"street",
                            "subcategories":[
                                {
                                    "nameEn":"passenger",
                                    "id":"passengerId"
                                },
                                {
                                    "nameEn":"car",
                                    "id":"carId"
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    }
}
```


上述的json文件，请求了meta中的信息，其中的taskTypes一个数组，是一个元素构成(也可以是多个，与下同)。
筛选的主要是场景分类里的内容，场景分类里，主要是筛选了两个categories，一个是动物animal的，一个是街景street的，这两个场景里，每个场景内部都有两个子categories，分别为dog,cat和passenger，car。我们的目的是筛选所有的doc里，只要他的subcategories里有任意一个给定的subcategories，即算匹配成功。


所以我们在筛选的时候，对于数组类型的内容，就是用elemMatch做匹配，代表数组中主要有一个满足所有条件即可就算成功
使用or来做匹配，代表只要满足一个给定的数组中的任意一个计算匹配成功
拼接后的样子类似：

```
































```