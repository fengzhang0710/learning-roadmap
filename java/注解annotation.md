## 注解

### 注解和xml

以前都是基于xml，可以实现松耦合，但是xml慢慢更复杂，维护成本也变高

注解，是一种紧耦合的方式，可以在方法上，类上，字段属性上假注解，高效率

追求低耦合就要抛弃高效率；追求效率就会遇到耦合的现象。



### 注解的本质

**java.lang.annotation.Annotation**接口中有一句话，用来描述注解

>
>The common interface extended by all annotation types
>
>被所有的注解类型继承的通用接口

这也就意味着，所有的注解类型都是继承了一个这个类的接口，反编译任何一个注解类，都是一个接口。

**一个注解的准确意义上来说，只不是过一种特殊的注释而已，如果没有解析它的代码，可能连注释都不如**

解析一个类或者方法的注解往往有两种形式，一种是编期内直接的扫描，另一种是运行期的反射。

eg：override类

如果一个方法被override注解修饰，那么编译器就会检查当前方法的方法签名是否真正重写了父类的某个方法。

这种是编译器已经直到的类

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override {

}

//等价于
public interface Override extends Annotation{
    
}
```



### 元注解

元注解是用来修饰注解的注解，通常用在注解的定义上

* @Target：注解的作用目标

  注解的类型有：elementtype

  * type	 类，接口和枚举类型
  * field：
  * method
  * parameter
  * constructor
  * local_variable
  * annotation_type
  * package
  * type_parameter
  * type_use

* Retention：注解的生命周期

  * source：只在源代码期间，编译期之后就消失了
  * class:会被编译到class文件里面，但是不会在运行时保留
  * runtime:一直都会被保留

* Documented：注解是否应当被包含在javadoc里面

* Inherited：是否允许之类继承这个注解



