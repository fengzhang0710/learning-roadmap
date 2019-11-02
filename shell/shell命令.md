## 学习sample.sh脚本

* dirname 去除文件名的非目录部分，仅显示与目标有关的内容。

  dirname $0 , 这个代表

* 



echo

* 输出文件
* 输出内容到文件里 echo "" >> filename

cat命令

* 用来查看文件的内容 cat [AEnbTVs] filename
* 用来链接和合并文件  cat file1 file2 > file3

more命令，可以分页显示文本文件的内容

* more [fpcsu+n-n] filename

less， more命令只能向后翻看，less可以向后，也可以向前翻看

tail；直接查看文本结尾的内容

​	tail -f 可以监听文件的新增内容





grep global search regular expression and print out the line 命令全面搜索正则表达式并把行打印出来，是一个强大的文本搜索工具，他能够使用正则表达式搜索文本，并

​	grep [-acinv] [--color=auto] '搜索字符串' filename

​	-i 忽略大小写 -n输出行号 -v反响选择



sed命令

​	vim是交互形的文本编辑模式，可以用键盘命令交互性的curd内容到文本里。

​	sed，流编辑模式，需要预先提供一组规则，然后根据这些规则来编辑数据

* 每次只处理一行数据；
* 根据提供的规则命令匹配并修改数据。

* 将执行结果输出，一直重复，完成所有数据处理



sed [选项] [脚本命令] filename

-e 脚本命令，会将后跟的脚本命令添加到已有的命令中

-f 脚本命令文件，该选项会将后面的脚本命令添加到以后的命令中

-n 默认情况下，sed回在所有脚本执行完毕后，自动输出处理后的内容，而该选项会屏蔽启动输出

-i 该选项回修改源文件



awk，另一个文本处理工具，与sed类似