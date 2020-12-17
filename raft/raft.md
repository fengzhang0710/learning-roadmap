raft:



状态流转

1.任何一个node必须有其中一个状态

​		a. Follower state

​		b.candidate state

​		c.leader state



2.状态流转（选举流程）

​	1.所有的node都初始化为 follower

​	2.如果follower没有对应的leader，就会变成candidate

​	3.candidate去请求别人投票

​	4.获得多数的投票变成leader



所有的变化都由leader和client交互，再有leader和其他node交互



Log replication

每个修改都保存为一个node's log

1.client给到leader，目前value的状态是 uncommitted.

2.leader把信息发送给每个node，每个node会request的内容返回给leader

3.leader只有收到超过一半的follower的node，才会写入这个值

4.leader把值设置成committed，然后把值的内容发送给follower





leader selection

1.两个timeout settings

​	election timeout: 每个follower过多久会变成一个候选人：随机的150 ~ 300 ms

变成候选人后，就会给他们发







raft：

https://blog.csdn.net/baijiwei/article/details/78759364



raft的go的kv实现

https://zhuanlan.zhihu.com/p/91288179



状态机：

https://zhuanlan.zhihu.com/p/47434856

https://github.com/jakesgordon/javascript-state-machine





所以，状态转移传输的是可能是内存，而复制状态机会将来自客户端的操作或者其他外部事件，从Primary传输到Backup。



复制状态机：

分布式系统的容错问题，chubby，zookeeper





参考链接

1.raft的英文论文：https://raft.github.io/raft.pdf

raft的中文翻译论文：https://blog.csdn.net/baijiwei/article/details/78759364

2.raft的mit的学生指引：https://thesquareplanet.com/blog/students-guide-to-raft/

3.raft的etcd的代码介绍：https://www.cnblogs.com/myd620/p/13189604.html

4.segment fault里的golang的代码实现：https://segmentfault.com/a/1190000021801938

5.知乎的介绍：https://zhuanlan.zhihu.com/p/91288179

