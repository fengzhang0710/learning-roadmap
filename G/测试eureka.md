eureka的内容



本地跑测试：

1、本地下载docker

2、拉取eureka的image，本地启动eureka

3、修改本地的环境变量ENVIRONMENT = "local",本地运行即可



远程跑测试：





本地跑client：









发布相关：

两个脚本来打基础镜像，把go的依赖内容拉进来

dockerFile，直接拉取基础镜像，然后在基础镜像上面，加上我们的代码

