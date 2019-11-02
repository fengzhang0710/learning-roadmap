## Threadlocal

多线程之间使用，保证每个线程有一个自己的副本，保存的内容在自己的threadLocalMap里面，key是对应的threadlocal







### 方法调用



#### get方法

```java
    public T get() {
      	//获得当前线程，拿到当前线程里面的成员变量threadlocalmap
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
          	//this 这个方式是threadlocal的，所以这个this就是threadlocal，所以这个map的key就是threadlocal
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
```



* 获得当前线程
* 获得当前线程里的threadmap
* 判断map是部署空，不是空的话，就从map的getEntry方法里获取这个this的对应的entry



#### map的getEntry方法

entry内部类，弱引用了threadlocal这个类，把k调用了super的构造方法，当成了referent，在reference类里面，会被特殊对待



reference类

* referent
* referenceQueue
* next
* discovered





```java
        static class Entry extends WeakReference<ThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
```



``` java
        private Entry getEntry(ThreadLocal<?> key) {
          	// hashcode是新建threadlocal是
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key)
                return e;
            else
                return getEntryAfterMiss(key, i, e);
        }
```

* 这个map里面存的是entry，entry是一个weakreference，弱引用，在下一次gc的时候就会被回收掉



弱引用类型，除非有一个明确的引用，直接引用到了这个弱引用类型，不只是有一个弱引用对象类型，才不会被回收。

内存泄露的地方指的是线程里面threadlocalmap里的entry对象，如果不使用弱引用，当我们在完成一个这个threadlocal的使用后，吧threadlocal对象==null，但是，因为在每个thread里(假设线程还没有终止)，仍然保留这个threadlocalmap，里面的entry会有threadlocal的对象，导致这个entry不能被回收，里面的key和value都是浪费掉了。



双冒号：：

​	把方法当成参数传递进去



什么时候会被回收：只有当threadlocal对象直接引用不存在的时候，才会吧entry中的threadlocal对象清除掉；而threadlocal对象不存在的时候，也就以为这其实这个threadlocal对象已经不需要了



用了threadlocal弱引用的问题，我们在使用的途中，经常回找不到这个内容 xx 不存在，因为通常使用private static修饰的内

threadlocal对象通常使用private static修饰，





stream api

​	面向对象的语言中增加面向函数的设计思想



函数式编程，java里，函数必须存在与方法之中，导致内容很臃肿

有的时候，我们需要传递一些方法，比如根据名字找人/根据年龄找人这样的方法，他们有共同点，，也有不同点，



CompletableFuture

​	对于future的一个增强





functional interface  single abstract method interfaces

​	一个接口内，只有一个未实现的方法；该接口作为某个方法的参数；

​	其他的方法调用这个方法的时候，通过lambda表达式实现接口即可





mvn的插件

​	check-style的插件



java执行命令的任务是：

​	java -classpath demo-project-1.0.jar  com.fengz.Test
    java -cp  -Dloader.path=

java 的代码混淆，可以方便把jar包分享给客户而不用担心被误解
    proguard