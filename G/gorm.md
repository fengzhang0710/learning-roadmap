gorm是一个golang的orm框架，支持纯orm对象映射以及写sql，主推的是纯orm对象映射相关，本文是关于gorm的一些基本操作。



##### 一.实体定义，定义一个最简单的ER关系

学生表(Student)为主体，包含一下几种关系：

belongs to: 每个学生都属于某一个班级(class)之中，所以Student表里包含一个class_id
have_one:每个学生都有一个自己专属的书桌(table),所以书桌表中有一个student_id
have_many:每个学生都有多本书(book),所以book表中有一个student_id
many_to_many:每个学生都有多门科目(lesson)可选,没门科目可以有多个学生勾选，所以多对多会有一个中间表(student_lesson)，完成关联关系

```
![img](https://lexiangla.com/assets/9f8cfa02d00b11eabca20a58ac135f67)
```

```go
//班级表
type Class Struct {
  ID string
  Name string
}

//书桌表
type Table struct{
  ID string
  Name string
  Student_ID string
}

//书本表，每本书属于唯一一个用户
type Book struct {
  ID string
  Name string
  Student_ID string
}

//学生选修的科目中间表
type Student_Lesson struct {
  Student_ID
  Lesson_ID
}

//科目， 数学，语文等
type Lesson struct {
  ID string
  Name string
}

//学生表
type Student struct {
  ID string
  Name string
  student_Class Class						//学生所属科目
  student_Table Table						//学生拥有的唯一课桌
  student_books []Book					//学生所有图书
  selected_lessons []Lesson			//学生所有已选课表
}

```





##### 二：查询

1、查询单体对象

现在想查询名字为'graviti'的学生，当我们根据StudentQueryParam时，gorm会判断该entity对应的表，以及该entity中非空的值，自己拼接成对应的sql，然后decode进result的数组中。

```go
//查询代码，db为gorm的数据库链接
studentQueryParam := Student{Name:"graviti"}//查询参数
studentResult := []Student{}	//查询结果
db.Where(studentQueryParam).Find(&studentResult)//gorm查询以及将结果decode成对应structure
```



2、级联查询(预加载)

上面讲的是最简单的级联查询，但是往往我们需要的不仅仅是这个用户的信息，比如在查询这个学生的同时，需要同时查询出 **所属班级**,**拥有的课桌 **,**所有拥有的书籍**,**所有选择了的课表**,要怎么做级联查询？



a. gorm针对与这种类型，提供了级联查询的tag，当我们设置好了主表的id后，根据主表的id可以级联查询出从表的内容，并且注入到主表里，需要做的就是修改主表的tag(tag是golang里提供的类似注解的功能)

* belongs_to: 本身不需要任何注解，即可自动级联查询出内容，在查询代码里指定需要查询这个从表即可
* have_one: 本身可以不加任何注解，会使用当前表的id字段作为外键去外表查询(也可以自己指定)
* have_many:一对多， 同一对一
* Many_to_many:多对多，需要添加额外注解"gorm:many2many:student_lesson"指名中间表表名



```go
//1,修改学生表的定义
type Student struct {
	ID               string
	Name             string
	Student_Class    Class    //所属班级,belongs_to, 不需要任何注解
	Student_Table    Table    `gorm:"foreignKey:student_id"` //学生拥有的唯一课桌
	Student_books    []Book   `gorm:"foreignKey:student_id"` //学生所有图书
  Selected_lessons []Lesson `gorm:"many2many:student_lesson"`  //学生所有已选课表,
}
```



b.修改查询的代码，指明需要级联查询。  

**db.Preload("Student_Class").Preload("Student_Table").Preload("Student_books")**

```go
//2,查询代码，db为gorm的数据库链接
studentQueryParam := Student{Name:"graviti"}//查询参数
studentResult := []Student{}	//查询结果
db.Where(studentQueryParam).Preload("Student_Class").Preload("Student_Table").Preload("Student_books").Find(&studentResult)//gorm查询以及将结果decode成对应structure
```



3.原理：

gorm本质上会先查询出符合条件的学生，以及这个学生所属的class_id,student_id,根据这些id取从表查询每个符合条件的内容，最后注入到entity里，具体执行的sql语句如下

```sql
#1,先查询符合条件的学生信息
select * from Student where name = 'graviti';
#结果 id: student_id_01, name: graviti, class_id: class_id_01

#根据结果里的class_id，在class表里查询所属的class内容
select * from Class where class_id = 'class_id_01'

#根据查询出的student_id,在Student_Table表里查询这个学生所属的唯一课桌，只有一条
select * from Table where student_id = 'student_id_01'  limit 1

#同上，查询出所有的课本
select * from Book where student_id = 'student_id_01'

#查询所选的课程，多对多查询，需要根据中间表做join
select * from Lesson where lesson_id in (select lesson_id from student_lesson where student_id  = 'student_id_01')

```



4.局限性

- 从表的可以加筛选条件，但是这个筛选条件**只针对与从表的sql语句，不会影响主表的筛选**

```sql
#db.preload("Class where name = ?", "a class name")
# 转换后的sql
select * from Class where class_id = 'class_id_01' and name = 'a class name'

```

所以 **如果需要查询学生中，所属班级的名称为'a class'**的所有学生，以及所属的班级信息等，需要自己拼接对应的join sql做筛选，gorm本身没有对这个做api

```go
db.Where(studentParam).preload("Class").Joins("join Class on student.class_id = Class.id and Class.Name = ?", "a class name")

#这样第一个查询student的sql会变成如下
select Student.* from Student join Class on student.class_id = Class.id and Class.Name = 'a class name'

```

- 从表的查询逻辑优化

  ​	目前可以看到，gorm的逻辑是，先查询所有符合条件的主表记录(Student)，对于主表中包含的其他字段，如果需要级联查询出来，再分别对不同的表做额外的查询操作(student_id in ('123', '321','234'))。

  ​	这种操作会有大量的从表 in 查询，对于简单的一对一，其实join后整合是一个更有效率的方法( [join-vs-sub-query](https://stackoverflow.com/questions/2577174/join-vs-sub-query)),目前gorm没有提供这样的接口，对于用户而言，智能自己写plain sql，然后根据返回的泛型row自己decode成对应的struct.



##### 三：级联保存

​	上面讲了怎么级联查询，下面说一下级联保存的逻辑

1.和级联保存一样，我们期望的效果是，当我们给定了一个entity实体，我们希望gorm可以自动帮我们保存对应的各种关联关系

```go
# 学生实体的定义
type Student struct {
	ID               string
	Name             string
	Student_Class    Class    //所属班级,belongs_to, 不需要任何注解
	Student_Table    Table    `gorm:"foreignKey:student_id"` //学生拥有的唯一课桌
	Student_books    []Book   `gorm:"foreignKey:student_id"` //学生所有图书
  Selected_lessons []Lesson `gorm:"many2many:student_lesson"`  //学生所有已选课表,
}

// 一个学生实体 & 所属班级 & 专属课桌 & 三本课本 & 两门课
saveStudent := Student{
  ID : uuid.New(),  //新指定id
  Name : "张三",  	 //指定名称
  Student_Class : Class {
    Name : "三年二班",
  },
  Student_Table : Table {
    Name : "张三的专属课桌",
  },
  Student_books : []Book{
    {
      Name : "语文课本",
    },
    {
      Name : "数学课本",
    },
    {
      Name : "英语课本",
    },
  },
  
  Student_class: []Class{
    {
      Name : "语文课",
    }，  
    {
      Name : "数学课",
    },
  },
  
}
```



​	针对于上面的实体，我们想要的操作是：

* 先查询class的name是否存在，如果存在，则使用已有的class.ID,否则新增一条class记录，同时将新生成的class的id保存给student的class_id字段   insert into Class if not exists name = '三年二班',

- 保存student记录     insert into Student (ID, Name, class_ID) values('张三id', '三年二班的id', '张三')
- 保存一个课桌记录 insert into Table (ID, Name, Student_ID) values ('课桌id', '张三的专属课桌', '张三id')
- 保存三本课本记录   insert into Book(ID, Name, Student_ID) values ('语文课本id', '语文课本','张三id') * 3
- 检查Lesson表，关于语文课/数学课(唯一键)是否存在。如果存在，则使用已经存在了的课表id；如果不存在，则新增一条课程记录，在保存中间记录关系。



a.修改entity的定义，级联保存的三种等级

- 默认不填写：最激进的做法，如果给定的子表字段没有id，则会新增一份记录，保存这个id；如果给定了id，则会根据给定的字段来update这个从表的记录, 下面的entity1因为没有给定任何一个id，则会在所有从表里记录一份id内容，然后保存关联关系。

```go
// 一个学生实体 & 所属班级 & 专属课桌 & 三本课本 & 两门课
saveStudent := Student{
  ID : uuid.New(),  //新指定id
  Name : "张三",  	 //指定名称
  Student_Class : Class {
    Name : "三年二班",
  },
  Student_Table : Table {
    Name : "张三的专属课桌",
  },
  Student_books : []Book{
    {
      Name : "语文课本",
    },
    {
      Name : "数学课本",
    },
    {
      Name : "英语课本",
    },
  },
  
  Student_class: []Class{
    {
      Name : "语文课",
    }，  
    {
      Name : "数学课",
    },
  },
}


//给定了从表(class,lesson这两个表；book和table是have_one关系，不属于join表)id的Student，会同时update从表的记录
saveStudent := Student{
  ID : uuid.New(),  //新指定id
  Name : "张三",  	 //指定名称
  Student_Class : Class {
    ID : "new class id",  //不存在，则会更新
    Name : "三年二班",
  },
  Student_Table : Table {
    Name : "张三的专属课桌",
  },
  Student_books : []Book{
    {
      Name : "语文课本",
    },
    {
      Name : "数学课本",
    },
    {
      Name : "英语课本",
    },
  },
  
  Student_class: []Class{
    {
      ID : "a exists class id",   //如果这个id存在，则会update这个id的内容为新给定的实体记录
      Name : "语文课",
    }，  
    {
      ID : "a not exits class id",  //如果这个id不存在，则会新增一条这个记录，同时保存关联关系
      Name : "数学课",
    },
  },
  
}
```

* 如果给定id，则不更新记录(往往我们不会给定所有的字段，以及某些字段含义会变化)；如果不给定id，则新增记录

```go
type Student struct {
  
  
}
```

