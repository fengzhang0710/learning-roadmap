package com.fengz;

import java.util.Arrays;

public class Fibonacci {

    /**
     *
     * 斐波那契数列的几种写法
     *
     * 要考虑溢出的问题
     * 递归：2的n次方
     * 迭代：用两个变量一直存上一个的数字，on
     * 数组：
     *
     * @param args
     */
    public static void main(String[] args) {

        int num = 5;
        Fibonacci f1 = new Fibonacci();
        int i = f1.fibonacci_1(num);
        System.out.println(i);
        i = f1.fibonacci_2(num);
        System.out.println(i);

        f1.fibonacci_3(num);


    }


    /**
     * 时间复杂度位o(2的n次方)
     * @param num
     * @return
     */
    public int fibonacci_1(int num){
        if (num == 1 || num == 2){
            return 1;
        }
        return fibonacci_1(num-1) + fibonacci_1(num-2);
    }

    /**
     * 时间复杂度o(n)
     * @param num
     * @return
     */
    public int fibonacci_2(int num){
        if (num == 0 || num == 1){
            return 1;
        }
        int before1 = 1, before2 = 1, result = 0;
        for (int i = num; i > 3; i--){
            before2 = before1 + before2;
            before1 = before2 - before1;
        }

        return before1 + before2;
    }

    //数组实现的方式，用数组把前面的内容全部记下来，本质上和上面的内容没啥大的区别，但是因为要额外定义一个内容，所以其实还是不太行的，空间复杂度更高一点
    public void fibonacci_3(int num){
        int []arr = new int[num];
        for (int i = 0; i < num; i++) {
            if (i == 0 || i == 1){
                arr[i] = 1;
            }else{
                arr[i] = arr[i-1] + arr[i-2];
            }

        }
        System.out.println(Arrays.asList(arr));

    }


    //矩阵的实现方式，url = https://blog.csdn.net/xuesong218/article/details/81130982

    /**
     * fn / fn-1 = 1*fn-1 + fn-2 /fn-1
     */

    //公式法
}
