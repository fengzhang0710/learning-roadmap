labelSet的各个字段含义以及例子

```json
labelSet的每个字段的含义
{
  "id":"",		//labelSet的唯一id
  "meta":{},  // labelSet的一些meta属性，不同类型的labelSet有不同的meta
  "ownerId":"",  //所属人id
  "groupId":"",  //所属组id
  "contentSetId":"", //该标注集所属的图片集id
  "contentSetFilter":"", //特定type的labelSet会使用，主要是模型评估
  "type":"", //标注集的类型，目前主要有三种 1.真值标注集；2.模型评估模板的标注集；3.模型评估结果标注集
  "status":"", //标注集的状态，分为创建，发布，统计中，统计完成
  "statisticsIp": "", //标注集的统计时统计协程所在的机器ip(业务无关)
  "statistics" : { 	//标注集的统计内容
    "objectCount" : 10,  //该标注集包含的图片的数量
    "labelCount" : 10, //该标注集下产生的标注实体的数量(一个图片对应一个标注实体，一般与objectCount相同，如果有漏标则会比objectCount小)
    "annotationCount": 100, //该标注集下产生的所有的标注框的数量(一个图片会产生多个标注框)
  } ,
  "updatedAt" : time.Time, //上次更新时间, 关于labelSet的改动都会触发更新时间(label的暂时不会)
  "publishedAt": time.Time, //发布时间
  "public": bool, // 是否是公开数据集下的公开标注集
}
```



###几个字段的具体解释

#### 1.type

type字段是区分不同类型的labelSet的，目前有三种labelSet

-真值的labelSet：type = 3,主要有算法team和标注team生成，包含的值为人工/手动筛选出来的标注框。 会在数据集详情里展示。

```json
//一个真值的labelSet的样例
{
  
  "meta":{
    "name":"一个真值的labelSet，dog and cat",
    "taskTypes":[ //代表具体的task内容，主要描述这个标注集标注的具体标签类型是哪些，所属的nameSpace等	
      {
        "task_type":"box2D", //代表是一个2d矩形的标注
        "categories":, // 代表筛选的具体标签类型
      },
    ],
  },
  
  "type":3, //3代表真值的labelSet
  "status":4, //状态，代表已经完成了标注
  "ownerId":"sj169xhas62n819k1jn81ns9022msao3",
  "groupId":"cd716245ae80528690bab3068af6f9f2",
  "contentSetId":"348a17f4-0553-4c0b-9be0-03501504c12f",
  "contentSetFilter":{}, //用来做筛选的，真值的labelSet此字段为空
  
}


{
  "code" : 10.0,
  "name" : "2D矩形",
  "categories" : [{
      "subcategories" : [{
          "name" : "楼房",
          "nameEn" : "building",
          "subcategories" : null,
          "id" : "C00090001"
        }, {
          "name" : "墙",
          "nameEn" : "wall",
          "subcategories" : null,
          "id" : "C00090002"
        }, {
          "name" : "桥",
          "nameEn" : "bridge",
          "subcategories" : null,
          "id" : "C00090003"
        }, {
          "subcategories" : null,
          "id" : "C00090004",
          "name" : "隧道",
          "nameEn" : "tunnel"
        }, {
          "id" : "C00090005",
          "name" : "天花板",
          "nameEn" : "ceiling",
          "subcategories" : null
        }, {
          "id" : "C00090006",
          "name" : "柱子",
          "nameEn" : "pillar",
          "subcategories" : null
        }],
      "id" : "C00090000",
      "name" : "建筑",
      "nameEn" : "architecture"
    }, {
      "id" : "C00120000",
      "name" : "电器",
      "nameEn" : "appliance",
      "subcategories" : [{
          "id" : "C00120001",
          "name" : "电视",
          "nameEn" : "tv",
          "subcategories" : null
        }, {
          "id" : "C00120002",
          "name" : "显示器",
          "nameEn" : "moniter",
          "subcategories" : null
        }]
    }, {
      "id" : "C00100000",
      "name" : "地面",
      "nameEn" : "ground",
      "subcategories" : [{
          "id" : "C00100001",
          "name" : "道路",
          "nameEn" : "road",
          "subcategories" : null
        }, {
          "subcategories" : null,
          "id" : "C00100002",
          "name" : "人行道",
          "nameEn" : "sidewalk"
        }, {
          "id" : "C00100003",
          "name" : "路沿",
          "nameEn" : "curb",
          "subcategories" : null
        }, {
          "subcategories" : null,
          "id" : "C00100004",
          "name" : "路肩",
          "nameEn" : "road_shoulder"
        }, {
          "subcategories" : null,
          "id" : "C00100005",
          "name" : "停车位",
          "nameEn" : "parking"
        }, {
          "id" : "C00100006",
          "name" : "轨道",
          "nameEn" : "railtrack",
          "subcategories" : null
        }, {
          "id" : "C00100007",
          "name" : "自然地面",
          "nameEn" : "terrain",
          "subcategories" : null
        }]
    }, {
      "id" : "C00020000",
      "name" : "特殊",
      "nameEn" : "special",
      "subcategories" : [{
          "id" : "C00020001",
          "name" : "采集设备自身",
          "nameEn" : "ego",
          "subcategories" : null
        }, {
          "subcategories" : null,
          "id" : "C00020002",
          "name" : "未成像区域",
          "nameEn" : "invalid"
        }]
    }, {
      "name" : "日用品",
      "nameEn" : "commodity",
      "subcategories" : [{
          "id" : "C00130001",
          "name" : "瓶子",
          "nameEn" : "bottle",
          "subcategories" : null
        }],
      "id" : "C00130000"
    }, {
      "subcategories" : null,
      "id" : "C00080000",
      "name" : "车道线",
      "nameEn" : "lane_line"
    }, {
      "id" : "C00150000",
      "name" : "自然",
      "nameEn" : "nature",
      "subcategories" : [{
          "id" : "C00150001",
          "name" : "天空",
          "nameEn" : "sky",
          "subcategories" : null
        }, {
          "subcategories" : null,
          "id" : "C00150002",
          "name" : "绿植",
          "nameEn" : "vegetation"
        }]
    }, {
      "name" : "交通标志",
      "nameEn" : "traffic_sign",
      "subcategories" : null,
      "id" : "C00060000"
    }, {
      "subcategories" : [{
          "nameEn" : "car",
          "subcategories" : null,
          "id" : "C00040001",
          "name" : "小汽车"
        }, {
          "subcategories" : null,
          "id" : "C00040002",
          "name" : "巴士",
          "nameEn" : "bus"
        }, {
          "nameEn" : "van",
          "subcategories" : null,
          "id" : "C00040003",
          "name" : "货车"
        }, {
          "subcategories" : null,
          "id" : "C00040004",
          "name" : "卡车",
          "nameEn" : "truck"
        }, {
          "id" : "C00040005",
          "name" : "电车",
          "nameEn" : "tram",
          "subcategories" : null
        }, {
          "id" : "C00040006",
          "name" : "火车",
          "nameEn" : "train",
          "subcategories" : null
        }, {
          "name" : "拖车",
          "nameEn" : "trailer",
          "subcategories" : null,
          "id" : "C00040007"
        }, {
          "name" : "紧急车辆",
          "nameEn" : "emergency_vehicle",
          "subcategories" : null,
          "id" : "C00040008"
        }, {
          "nameEn" : "construction_vehicle",
          "subcategories" : null,
          "id" : "C00040009",
          "name" : "工程车"
        }, {
          "nameEn" : "bicycle",
          "subcategories" : null,
          "id" : "C00040010",
          "name" : "自行车"
        }, {
          "subcategories" : null,
          "id" : "C00040011",
          "name" : "摩托车",
          "nameEn" : "motorcycle"
        }, {
          "subcategories" : null,
          "id" : "C00040012",
          "name" : "电动车",
          "nameEn" : "electricbike"
        }, {
          "name" : "飞机",
          "nameEn" : "airplane",
          "subcategories" : null,
          "id" : "C00040013"
        }, {
          "id" : "C00040014",
          "name" : "轮船",
          "nameEn" : "boat",
          "subcategories" : null
        }],
      "id" : "C00040000",
      "name" : "交通工具",
      "nameEn" : "vehicle"
    }, {
      "id" : "C00110000",
      "name" : "家具",
      "nameEn" : "furniture",
      "subcategories" : [{
          "name" : "椅子",
          "nameEn" : "chair",
          "subcategories" : null,
          "id" : "C00110001"
        }, {
          "name" : "沙发",
          "nameEn" : "sofa",
          "subcategories" : null,
          "id" : "C00110002"
        }, {
          "subcategories" : null,
          "id" : "C00110003",
          "name" : "桌子",
          "nameEn" : "table"
        }, {
          "nameEn" : "houseplant",
          "subcategories" : null,
          "id" : "C00110004",
          "name" : "盆栽"
        }]
    }, {
      "name" : "交通灯",
      "nameEn" : "traffic_light",
      "subcategories" : null,
      "id" : "C00070000"
    }, {
      "id" : "C00010000",
      "name" : "其他",
      "nameEn" : "other",
      "subcategories" : null
    }, {
      "id" : "C00140000",
      "name" : "动物",
      "nameEn" : "animal",
      "subcategories" : [{
          "name" : "鸟",
          "nameEn" : "bird",
          "subcategories" : null,
          "id" : "C00140001"
        }, {
          "subcategories" : null,
          "id" : "C00140002",
          "name" : "猫",
          "nameEn" : "cat"
        }, {
          "id" : "C00140003",
          "name" : "狗",
          "nameEn" : "dog",
          "subcategories" : null
        }, {
          "nameEn" : "cow",
          "subcategories" : null,
          "id" : "C00140004",
          "name" : "牛"
        }, {
          "subcategories" : null,
          "id" : "C00140005",
          "name" : "马",
          "nameEn" : "horse"
        }, {
          "id" : "C00140006",
          "name" : "羊",
          "nameEn" : "sheep",
          "subcategories" : null
        }]
    }, {
      "name" : "人",
      "nameEn" : "person",
      "subcategories" : [{
          "name" : "行人",
          "nameEn" : "pedestrian",
          "subcategories" : null,
          "id" : "C00030001"
        }, {
          "nameEn" : "rider",
          "subcategories" : null,
          "id" : "C00030002",
          "name" : "骑手"
        }, {
          "name" : "坐着的人",
          "nameEn" : "sitting_person",
          "subcategories" : null,
          "id" : "C00030003"
        }, {
          "name" : "婴儿车",
          "nameEn" : "stroller",
          "subcategories" : null,
          "id" : "C00030004"
        }, {
          "id" : "C00030005",
          "name" : "轮椅",
          "nameEn" : "wheelchair",
          "subcategories" : null
        }],
      "id" : "C00030000"
    }, {
      "name" : "路面障碍",
      "nameEn" : "on_road_obstacle",
      "subcategories" : [{
          "nameEn" : "pole",
          "subcategories" : null,
          "id" : "C00050001",
          "name" : "竖杆"
        }, {
          "subcategories" : null,
          "id" : "C00050002",
          "name" : "栅栏",
          "nameEn" : "guardrail"
        }, {
          "nameEn" : "traffic_cone",
          "subcategories" : null,
          "id" : "C00050003",
          "name" : "锥形桶"
        }, {
          "nameEn" : "warning_post",
          "subcategories" : null,
          "id" : "C00050004",
          "name" : "警示柱"
        }, {
          "subcategories" : null,
          "id" : "C00050005",
          "name" : "减速带",
          "nameEn" : "speed_bump"
        }, {
          "nameEn" : "boom_barrier",
          "subcategories" : null,
          "id" : "C00050006",
          "name" : "道闸"
        }, {
          "name" : "防撞护栏",
          "nameEn" : "trafficBarrier",
          "subcategories" : null,
          "id" : "C00050007"
        }, {
          "nameEn" : "bicycle_rack",
          "subcategories" : null,
          "id" : "C00050008",
          "name" : "自行车停车架"
        }, {
          "id" : "C00050009",
          "name" : "瓦砾",
          "nameEn" : "debris",
          "subcategories" : null
        }],
      "id" : "C00050000"
    }]
}
```



-模型评估模板的labelSet：type=1， 主要用于模型评估，在创建模型评估时，筛选了具体的标注集后，会根据这些标注集的labels做一次标签和数量的筛选，此时会保存一个评估模板，用来评估结果的labelSet做样例



```json
//一个模型评估模板的labelSet
{
  "meta":{
    "name":"一个评估模板的labelSet，由前端新建评估的时候生成",
    "parentLabelSetId":"", //parentLabelSetId,保存了真值的labelSet的id，新建评估是用(算法 or 标注里的真值的labelSet，根据里面meta里的taskTypes和具体labels中标注出的标签做筛选)
    "taskTypes":{},
  },
  
  "type":1,
  "status":4,
  "ownerId":"sj169xhas62n819k1jn81ns9022msao3",
  "groupId":"cd716245ae80528690bab3068af6f9f2",
  "contentSetId":"348a17f4-0553-4c0b-9be0-03501504c12f",
  "contentSetFilter":{
    "sampleCount":100,  //根据筛选出来的所有符合条件的object，做一个subArray,只提取部分的objectPaths
    "sampleStrategy":"average", //取subArray的策略，可以是平均，随机等
    "values":[
      {
        "category":{
          
        }
      },
    ],
    
  }, //会保存筛选真值的labelSet的values和sampleCount等相关字段
}
```



-模型评估结果的labelSet：type=2，主要用于模型评估，用户

