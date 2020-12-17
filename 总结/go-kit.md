​	go-kit



endpoint.Endpoint 

* 是一个function，这个function是一个接受ctx,和req，然后返回response和error的方法









speedle

spdl，安全策略定义语言

* 





policy的优先级是怎么确定的



认证的policy

![img](https://speedle.io/img/speedle/authzpolicy.png)

认证的policy，针对特定的人做限制，不根据role做限制

​	目前如果我们需要对所有人做限制的话，那么需要使用这样的policy，把人配置为通配符，然后在condition里做限制

​	action-get  都可以

​	action-update 必须是本人才可以，在if condition里查询对应的内容，具体要看这个if-condition怎么做判断的