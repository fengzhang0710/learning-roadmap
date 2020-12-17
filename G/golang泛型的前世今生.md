### golang与泛型

一：为什么golang不支持泛型？

我们参考下golang的官网的FAQ(https://golang.org/doc/faq#generics)

>Generics may well be added at some point. We don't feel an urgency for them, although we understand some programmers do.
>
>Go was intended as a language for writing server programs that would be easy to maintain over time. (See [this article](https://talks.golang.org/2012/splash.article) for more background.) The design concentrated on things like scalability, readability, and concurrency. Polymorphic programming did not seem essential to the language's goals at the time, and so was left out for simplicity.
>
>The language is more mature now, and there is scope to consider some form of generic programming. However, there remain some caveats.
>
>Generics are convenient but they come at a cost in complexity in the type system and run-time. We haven't yet found a design that gives value proportionate to the complexity, although we continue to think about it. Meanwhile, Go's built-in maps and slices, plus the ability to use the empty interface to construct containers (with explicit unboxing) mean in many cases it is possible to write code that does what generics would enable, if less smoothly.
>
>The topic remains open. For a look at several previous unsuccessful attempts to design a good generics solution for Go, see [this proposal](https://golang.org/issue/15292).

总结如下：

* 你们觉得重要，我们觉得不重要。
* 我们更关心扩展性，可读性，并发性；易用和多态我们放在最后。
* 现在有设计，但是还是草稿。
* 目前的map和slice，拼接上interface{}和拆装箱，很多情况上已经满足了泛型做的事情，除了有点不优雅。



二：目前的一些可选方案

话虽如此，泛型仍然是很重要的，一个简单的排序接口，不可能根据所有的类型都设计一遍。

```go
// int
func Sort(slice []int) {}

//string
func Sort(slice []string) {}

。。。。
```



解决方案：

A：golang里函数是一等公民，可以把对应需要动态的函数作为参数。

​	用例：golang自带包里的sort方法

```go
//src/sort/slice.go
func Slice(slice interface{}, less func(i, j int) bool) 
```

​	用法：

```go
	intArr := []int{1, 2, 3}
	stringArr := []string{"1", "2", "3"}

	sort.SliceStable(intArr, func(i, j int) bool {
		return intArr[i] - intArr[j] > 0
	})

	sort.SliceStable(stringArr, func(i, j int) bool {
		return intArr[i] - intArr[j] > 0
	})
```



给出一种可行性的草稿版本

```go
//方法定义
func Reverse (type Element) (s []Element) {
    first := 0
    last := len(s) - 1
    for first < last {
        s[first], s[last] = s[last], s[first]
        first++
        last--
    }
}
```

* 对比java/C++中的泛型，使用'<>'完成，golang选择用()
* (Type Element)标识这是个泛型方法， 其中的element为泛型类型的标识符，类似与java的'E','V','K'



使用方法

```go
func ReverseAndPrint(s []int) {
    Reverse(int)(s)
    fmt.Println(s)
}
```



原类型meta-type：

​	上面是一个简单的泛型例子，其实相对是一个特例，因为在泛型方法内，没有调用任何的泛型中的方法，设想我们需要有一个方法，他的内容是需要返回



https://www.youtube.com/watch?v=WzgLqE-3IhY&ab_channel=GopherAcademy









//第二篇文章：

上一个版本的内容已经被溢出，

>This version of the design draft has many similarities to the one presented on July 31, 2019, but contracts have been removed and replaced by interface types, and the syntax has changed.





* type parameter的定义变掉了，上一个版本里，用的还是()，而最新的用的是[]，(为什么不用()<>?)
* 



golang的2019年的proposal https://go.googlesource.com/proposal/+/refs/heads/master/design/go2draft-contracts.md

golang的2020年的proposal https://go.googlesource.com/proposal/+/refs/heads/master/design/go2draft-type-parameters.md#methods-may-not-take-additional-type-arguments%29