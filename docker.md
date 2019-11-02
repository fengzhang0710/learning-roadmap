## docker



lightning执行docker命令是在哪个机器上执行的？

​	





docker项目提供了在linux内核功能之上，协同在一起的高级工具。目标是帮助开发和运维人员更容易的跨系统跨主机交付应用程序和他们的依赖。

docker通过docker容器，一个安全的，基于轻量级容器的环境实现这个目标。

容器是由镜像通过命令行手工创建货通过dockerfile自动创建。



## docker file

>
>
>dockerfile是由一系列命令和参数构成的脚本，这些命令和脚本用于基础镜像，并最终创建一个新的镜像，简化了从头到位的流程和部署操作，从from命令开始，跟各种方法，命令和参数，产出一个新的可以用于创建容器的镜像



* from命令

  定义了使用哪个基础镜像启动构建流程，基础镜像可以是任意镜像，如果基础镜像没有被发现，docker会从docker image index来查找这个镜像。from是docker的收个命令

* add命令

  两个参数，源和目标。基本作用是从原系统的文件系统上复制文件到目标容器的文件系统。如果源是一个url，会被下载并复制到容器里

* entrypoint

  配置容器启动后执行的命令，不可被docker run提供的参数覆盖

  可以帮助你配置一个容器，使他可初始化，结合cmd 和 entrypoint





azure的模板部署的流程

* 自己编写json的配置文件，这个配置文件由azure服务器读取
* azure里面的json文件里面，有拉取镜像的步骤之类的，在我们的azure的存储里面，可以自己看到
* 我们的这个镜像是在134的机器上配置的，在/home/jenkins/workspace对应下面
* 通过jenkins的job，读取我们修改的docker的dockerfile，制作出的这个镜像，然后手动上传到对应的服务器上，azure的话，直接通过web上传



开发的步骤，直接修改你的代码，然后在jenkins的对应job下面打包就可以了

 Lightning-Docker-Image-Beta-dev 开发分支

 Lightning-Docker-Image-Beta 正式发布分支



docker 命令

build 根据本目录的docerkfile，制作一个docker的镜像，名字叫做你给定的名字,放到本地

docerk build -t kyligence/lightning:latest .



save，把docker里制作的镜像从本地的镜像仓库里拉出来,存放到本地的哪个位置

docker save kyligence/lightning:latest > kyligence_lightning.tar



kcm 的 docker job

```shell
#cp kyligence_cloud.tar.gz
pwd
exit 0
rm -rf ./*.tar.gz
rm -rf ./*.tar
cp ${local_package_path} ./

cp -rf deploy/build/lightningdocker/Dockerfile ./
cp -rf deploy/build/lightningdocker/entrypoint.sh ./
chmod +x ./entrypoint.sh

cp -rf /usr/local/terra_provider/bin/terraform12 ./terraform

#新建一个新的docker的容器，根据dockerfilebuild出来的
docker build -t kyligence/lightning:latest .
#把新建出来的这个镜像拷贝出来，变成本地的一个jar包
docker save kyligence/lightning:latest > kyligence_lightning.tar
#压缩命令，把后面的这个tar包打包成.tar.gz的jar包
tar zcvf kyligence_lightning.tar.gz kyligence_lightning.tar

echo "configure AzureChinaCloud env"
export AZURE_STORAGE_ACCOUNT=kyhub
export AZURE_STORAGE_ACCESS_KEY=u4jmIe5jsp6KyFjAQD/ZFzydLbOayxD5B6vL8qnjj+n56lYuQMQh0v9fXY1nL+38oky7TAH8WVhK0uCrmd7wDw==
export container_name=lightningpub
export blob_name=dev/kyligence_lightning.tar.gz
export file_to_upload=./kyligence_lightning.tar.gz
export AZURE_CN_FINISH_URL="AzureCN_URL: $AZURE_STORAGE_ACCOUNT/packages/$blob_name"

#az cloud set -n AzureChinaCloud
#az login --service-principal -u 0aa8ffe4-e69b-4b6a-af0a-7f65d401b4f2 -p Q@?f[2cx@ImZiybkRiAs2k8b0ZIxmhs: --tenant 44201040-9667-4462-83b9-ffae7dd7af16

#az storage blob upload --container-name $container_name --file $file_to_upload --name $blob_name

echo "upload to aws"
#cp ~/.aws/credentials_china ~/.aws/credentials
#aws s3 cp ./kyligence_lightning.tar.gz s3://public.kyligence.io/newtendev/

#cp ~/.aws/credentials_global ~/.aws/credentials
#aws s3 cp ./kyligence_lightning.tar.gz s3://public.kyligence.io/newtendev/

#upload form local to aws is too slow, use one ec2 in beijing to do the job

#ssh ec2-user@52.80.66.105 'sudo /home/ec2-user/jenkins/newten_upload_to_s3_dev.sh'



```



这个是根据基本镜像构建出最新的lightning的配置

```dockerfile
# 拉取的docker镜像，是从本地构建的， 具体怎么构建的需要在看一下了,可能只有一些基本的配置，jdk等的内容
FROM kyligence/lightning:base

ADD kyligence_cloud.tar.gz /opt/
ADD ./entrypoint.sh /opt/
ENTRYPOINT ["/opt/entrypoint.sh"]

```



这个是dockerfilebase，应该是构建基本镜像使用的

```dockerfile
FROM fnproject/base
# openjdk-8-base contains no GUI support. see https://pkgs.alpinelinux.org/package/testing/x86_64/openjdk8-jre-base
RUN mkdir -p /opt && apk update && apk add --no-cache openjdk8-jre-base bash openssh openssl procps nss
ADD terraform /usr/bin/

```



这个是entrypoint.sh 就是用这个镜像启动容器后，最开始执行的命令

```shell
#!/bin/bash
# 就是很简单的一个执行shell的命令
/opt/kyligence_cloud/bin/cloud.sh start
tail -f /dev/null

```





修改模板的流程

​	1.正常开发代码

​	2.提交到upstream的分支

​	3.使用jenkins的lightning的release，打包出一个lightning的jar包，名字要是叫做lightning.tar.gz

​		http://10.1.1.50:8080/view/lightning/job/lightning_package_release/

​	4.把包上传到134的jenkins的slave里面，这样方便docker的dev的job做操作

​	5.运行docker的dev的内容，这样读取内容，打出一个lightning的docker的镜像，是一个tar包，这个包就是使用模板读取的时候，使用的包，把这个包上传到公网的web的地址里(可以在json文件里找到)

​		

​	6.这个内容打包出来了，接下来要修改一下json文件，然后把json文件修改到azure等平台读取json的地址就可以了

​	7.运行docker的下一个任务，就可以把内容上传了，先从134上scp到本地，然后本地从web的内容上传



##  docker的jenkins的问题

要先用lightning的release的打一个包，打出来的包，在用docker的打包出来就是我们要的内容，

打包好的docker镜像，是dev环境的，后面构建json的也有一个专门的jenkins的job Lightning-Ops-Template-dev

上传的内容也是dev的内容

```shell
#先把git里面的内容拉一份下来
cp deploy/opstemplates/kyligence_cloud_aws_3_0.json ./kyligence_cloud_aws_3_0-dev.json

#use dev package before uplaod
# 把里面的newten改成newten-dev
sed -i "s|public.kyligence.io/newten|public.kyligence.io/newtendev|g" ./kyligence_cloud_aws_3_0-dev.json

cp ~/.aws/credentials_china ~/.aws/credentials
aws s3 cp ./kyligence_cloud_aws_3_0-dev.json s3://public.kyligence.io/opstemplate/ --acl public-read

#update azure template
cp deploy/opstemplates/kyligence_lighting_azure_3.0.json ./kyligence_lighting_azure_3.0-dev.json
sed -i "s|public.kyligence.io/newten|public.kyligence.io/newtendev|g" ./kyligence_lighting_azure_3.0-dev.json
#就是在这一步，把lightningpub里的内容修改成了lightningpup/dev的内容，这样就可以用dev的jar包
sed -i "s|lightningpub|lightningpub/dev|g" ./kyligence_lighting_azure_3.0-dev.json

export AZURE_STORAGE_ACCOUNT=kyhub
export AZURE_STORAGE_ACCESS_KEY=u4jmIe5jsp6KyFjAQD/ZFzydLbOayxD5B6vL8qnjj+n56lYuQMQh0v9fXY1nL+38oky7TAH8WVhK0uCrmd7wDw==
export container_name=lightningpub
export blob_name=opstemplate/kyligence_lighting_azure_3.0-dev.json
export file_to_upload=./kyligence_lighting_azure_3.0-dev.json


az cloud set -n AzureChinaCloud
az login --service-principal -u 0aa8ffe4-e69b-4b6a-af0a-7f65d401b4f2 -p Q@?
f[2cx@ImZiybkRiAs2k8b0ZIxmhs: --tenant 44201040-9667-4462-83b9-ffae7dd7af16
# 只上传了一个json文件，但是没看到有上传docker镜像的步骤？上传的步骤之前是有的，只是后来不太行，自己手动上传
aws
	global
	cn
azure
	global	kyligencekeys
	cn			 kyhub
az storage blob upload --container-name $container_name --file $file_to_upload --name $blob_name



```



链接也是dev的链接，具体链接的内容是在lightning代码的issue里面搜索模板就可以的

https://github.com/Kyligence/Lightning/issues/164

global:
54f27b90-e26d-4e15-8acb-6ebe45ed3d19
nayWxCYxh97NwsCdL9NDO75DeBSgHqTh/GB4MqxtMtQ=
9b3afd1e-9454-4d38-a14c-a0670c820d48



皮囊之下

一个明星的诞生







# aws修改的内容

aws的json，找list文件

aws ami，找64位架构的机器，换成这个内容