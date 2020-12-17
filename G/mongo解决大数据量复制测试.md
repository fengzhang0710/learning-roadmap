# mongo解决大数据量复制测试



### 场景：

​	类似于github的fork功能，数据集也提供fork功能。fork别人的公开数据集，就会全量复制一份别人的标注标签(label)。类似于mysql的 **insert into ...  select...**操作。



##### 

##### 前提:

* 目前生产的mongo版本是4.0

  

**目前的方案：**

​	因为目前生产的是4.0的mongo，所以当时实现功能时，并为使用$merge命令，而是使用 client -> server -> client -> server流程

	*	client -> server : label-store服务从mongo的server中，拉取(1024)个label到client
	*	server -> client : mongo server推送1024条 label
	*	client -> server : client 调用 /batchInsert, 把labels插入到mongo
	*	... 循环1~3，直到复制完所有的label

![forkDataset](/Users/feng.zhang/Downloads/forkDataset.jpg)



##### **效率：**

​	要完整复制记录，这必定是一个很耗时的操作(github的fork，如果遇到大项目也会pending)。

* 复制一条dataset的数据库记录
* 复制contentSet
  * 复制contentSet的db record, 一条记录
  * 复制所有的segment，两条记录
  * 复制所有的sensor，无sensor
  * 复制所有的object，共37w  (mysql:  insert into ... select ...)   18s
* 复制labelSet
  * 复制labelSet的db record，一条记录
  * 复制所有的label，共37w (client -> server -> client -> server)  480s



##### **优化方案：**

​	client—server来回pull-push的方式，中途有大量不必要的网络传输，数据复制。必须采用类似mysql的 insert into ... select ..命令，客户端通知server进行复制，server无需和client进行数据复制。



##### **$merge:**

​	$merge命令类似于mysql的 insert into ... select...，客户端只需要一个命令，server端会复制对应的pipeline的最后结果，推送结果到对应的collection里，过程中全在server端进行，无需于客户端交互。

* $mege命令从**mongo4.2**支持
* $mege命令有一个参数，从**mongo4.4**开始支持put到同一个**collection(collection等同于mysql的table)**
* 参考链接：https://docs.mongodb.com/manual/reference/operator/aggregation/merge/index.html



##### 优化后的效率：

| 数据量 |     10w | 37w |
| :-----  | ---- |---- |
| 修改前   |   41.17s   | 480s|
|       修改后 | 8.07s     |    28.38s|

* 修改前使用分页的形式查询，分页到后面会原来越慢，非线性递增
* 修改后统一由server端执行，速度会快很多，而且可以保证线性

##### 问题：

​	目前生产上的用的是**aliyun的mongo产品**，但是目前**aliyun的mongo一键升级只支持升级到4.2**，并**没有升级到4.4**的功能。新建的mongo产品支持4.4，但是不支持升级到4.4。

​	秉承效率最大化的原则，通过新建一个4.4的mongo库，然后通过mongoshake(mongo迁移工具)，将prod的数据迁移到4.4的新库里，切换数据平台的mongo链接到新的4.4库，完成升级。



##### mongoShake

​	通过oplog，在库与库直接同步mongo的内容

* 新建一个4.4的库
* 通过aliyun的mongoshake，同步到新建的库里
* 重启服务，更换新的mongo链接

* https://help.aliyun.com/document_detail/122621.html?spm=a2c4g.11186623.2.21.4ef741829RHPRb#table-kn1-53g-chb