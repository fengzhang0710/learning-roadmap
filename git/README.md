## git fetch 和 pull的区别

+ 相同点
+ 不同点

fetch: 相当于从远程获取最新版本到本地，不会自动merge

​	git fetch origin master

​	git log -p master .. origin/master

​	git merge origin/master

 含义：

​	从远程的origin的master主分支下载最新版本的到origin/master分支上

​	比较本地的master分支和origin/master分支的差别

​	最后进行合并	

可以类比为：

​	git fetch origin master:tmp  从orign的master合并到本地的tmp里面

​	git diff tmp 比较本地master和tmp

​	git merge tmp	把本地的tmp和master合并



git pull origin master: 相当于直接合并		





### git拉取远程某个分支到本地的某个分支

git fetch origin release_v1.6.2 release_v1.6.2

git push origin release_v1.5.2:release_v1.5.2



## git squash 和 rebase的区别







rebase有两个作用

​	在分支里使用，rebase -i 带上数字或者hash码值，把到给定的hash码值的commit合并掉

​	第二个作用，在分支里，rebase 另一个分支名称，可以合并分支，同时





## git里的一些操作优化

* 每天早上，记得切回master分支，从upstream里面拉取最新的master的代码，往正在开发的分支里rebase， git rebase -i master 把master分支里更新的内容rebase进来
* 每次提交pr/push之前，都要切回master分支，同上; git pull upsteam master
* 如果是你的分支里，有别人也在开发的内容，需要拉取别人的分支，记得在当前分支上新建一个test分支，在test分支里面，拉取别人的分支(比如你需要一些前端的代码)，测试，修改自己原本的分支，在test分支里面rebase原本的分支，这样可以保证git提交的内容不冲突，同时开发没问题，同时git log里的message也是没有问题的，干净的。



## git里的remote命令
remote只是代名词而已，可以理解成为本地的仓库连接上一些远程的仓库，具体为：git remote -v

* origin是clone下来后，就会自带的一个仓库，这个代表你clone下来的原始仓库，你要有权限才能向这个仓库里提交代码
* upstream，默认命名的仓库，代表你自己fork的仓库的原始仓库，这样就可以提pr等操作了
新增upstream的操作 git remote add upstream giturl.git
https://www.cnblogs.com/wuer888/p/7655856.html

##  git的基本开发流程

* 首先，有了账号之后，加入你们的组织，获得代码的提交权限，关于git的权限，目前还有没有太多的了解，需要在做看一些关于权限的文章

* 有了基本的读写权限之后，就可以开发了，我目前了解到的开发流程，仅仅适用与lightning里面，不代表具有通用性。

  * 从组织的rep中，fork一份代码到你自己账号里，然后git pull 自己账号下的代码到本地，此时会有三个rep，分别为：

    * 1.本地的rep，即为local
    * 2.自己账号下的rep，即为origin
    * 3.远程的组织的rep，即为upstram

  * 在自己本地pull/clone下来的rep，默认为master分支，和你clone时选择的分支有关系，可以使用git branch命令查看

  * git branch branch_name ，新建一个分支名称，我们一般以issue号码为名称，然后切换到对应的分支里面，git checkout -b branch_name一键创建分支并且切换过去，在对应的分支里面开发

  * 开发完毕后，参考操作优化第二条

  * 同步了最新的代码后，现在需要提交到origin的master里面， git push origin branch_name:master

  * 此时，在自己账号下的rep里面，就可以看到对应的修改内容，点击file diff ，就能看到对应的修改的内容

  * 点击下面的pull request，此时可以把修改的内容合并到upstream的master里面，点击提交pr，输入对应的信息和reviewer的人，申请让他们review代码 ps:关于权限相关的内容，暂时还没有了解，此时应该会有权限相关的步骤的说明

  * 别人review玩你的代码，同时没有冲突的话，你就可以合并代码的主分支了，合并完，在本地的master里pull最新的代码

  * 一般是不需要操作origin的分支的，它只是作为我们的一个中转站，如果需要操作的话，就使用更新完毕的local的分支，覆盖掉origin的分支即可。

    

提pr的时候，有可能你的分支的版本已经低与master的版本

*  如果有冲突，那就会提示让你修改冲突
* 如果没有冲突，他会自动merge一次pr到你的分支里面的内容里

以上都会导致你的pr里面，会有额外的内容，很难看，解决方法就是需要先拉取upstream的代码到本地，然后在branch里rebase一下拉取的最新的master的内容，这样再次提交就会把分支的根移动到最新的代码下，不会有额外的内容

-------



git里配置ssh(或者其他类似的代码仓库)

​	怎么认证你是你自己？使用ssh，不对称加密，代码仓库保留公约，你保留私钥，链接的时候，你把你的私钥发送给对面即可。

​	密钥对一般成对生成。要么在代码库里生成，然后吧你的私钥下载下来，拷贝到你的当前用户的.ssh文件夹下面，重命名为id_isa，这样就能完成认证；要么就在本地生成，然后把生成的公钥上传到代码库里，这样也是可以的

* 要注意私钥密码的权限问题，不能太大，600，否则会有bad permission
* 如果已经有了别的私钥，需要百度一下怎么在本地报错多个密钥

---





github的完整的工作流程
    1.项目到github
    2.zenhub，武装你的项目
        一套任务模板
        一套个人的todo list
        一套项目图标与统计
        一些其他的小彩蛋在github里面
    3.持续集成
        jenkins
        TravisCI / CircleCI 
    4.代码review
        Reviewable
    5.代码的测试覆盖率
        Coveralls
        代码测试：mock
            接口的压测：jmeter
            数据库类型的压测：tpc、tco
    6.快速沟通
        Slack





-----





git 撤销commit相关的内容

如果一个内容你已经commit了，然后现在想撤销commit，这个时候就分好几种情况

* 只想修改commit的信息  git commit --amend

* 1，撤销这个提交，但是不撤销add,这样你这个commit修改的内容都在,同时也有这个add的内容,可以继续修改,然后继续commit,也可以直接commit(类似修改commit信息,只不过如果只是想修改commit的信息的话，也有别的单独的命令)。 git reset --soft HEAD~2,   HEAD~1也可以写成HEAD^
* 2.撤销这个提交，保留内容，但是不保留add的信息,这样你可以再次add git reset --mixed HEAD~N
* 3.撤销这个提交,不保留内容,  git reset --hard HEAD~2,这样所有的内容都被还原到上次commit的内容了







----

git 查看所有的操作日志

git log --pretty=oneline



git 当前版本叫做HEAD, 上一个版本叫做 HEAD^, 两个叫做HEAD^^, 前一百个叫做 HEAD~100

---

git 的reset的log，可以让你知道git的reset的日志

Git reflog





---

git diff  查看工作区和暂存区的差异



git diff --cached 查看暂存区和仓库的差异

dit diff HEAD 查看工作区和仓库的差异

git add  -- git checkout

git commit -- git reset HEAD^







----

git工具

1.github desktop

2.source tree









----



git stash pop 后想恢复



git fsck

