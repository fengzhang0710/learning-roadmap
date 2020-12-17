

oss上传文件解压

https://help.aliyun.com/document_detail/106155.html?spm=5176.11065259.1996646101.searchclickresult.1fe034e26hFz1y

1.只支持.zip文件

2.bucket需要支持授权

3.目录需要设置





限制：

​	单个文件不能超过5g





计费方式:

​	1.调用次数						前->100w次						 **1.33 元/百万次**

​	2.(CPU-内存等)执行时长  需要测试		40w秒->111个小时	一分钟起步，超过则实际计算

​		

![按量实例计量](http://docs-aliyun.cn-hangzhou.oss.aliyun-inc.com/assets/pic/54301/cn_zh/1568772931531/1.png)



![image-20200520112237217](/Users/feng.zhang/Library/Application Support/typora-user-images/image-20200520112237217.png)

可能

​	1.公网下行流量费用		    ->给外网传输才有

​	2.CDN回源费用 可以忽略  ->







limit

1.ZIP包解压处理单个压缩包的最大处理时间是10分钟，超过10分钟未完成的任务会解压失败

​	测试下最大可能会超过的大小



2.运行的内存 512mb -> 3GB



3.实例并发度（预留实例需要的）



2.若压缩包中包含非UTF-8或GB 2312编码的文件名或文件夹（文件目录）名，可能会导致解压后的文件名或文件目录名出现乱码、解压过程中断等情况，请使用UTF-8或GB 2312编码命名您的文件或文件目录。



3.压缩包是否能递归解压缩

​	1.zip里包含1.zip，不会循环解压，删除不会影响内含的压缩包

​    1.zip里包含之前同名的文件是否会替换? 会



​	

