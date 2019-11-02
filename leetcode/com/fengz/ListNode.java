package com.fengz;

 //* Definition for singly-linked list.
  public class ListNode {
      int val;
      ListNode next;
      ListNode(int x) { val = x; }
  }

/**
 * 时间复杂强度
 */
class Solution {
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        ListNode dummyNode = new ListNode(0);
        //在这个地方的内容是必须的，不然到时候找不到头节点了
        //1.保证没有污染原本的节点的内容；2.到时候curr用来迭代，dummy用来返回值
        ListNode q = l1 , p = l2 , curr = dummyNode;
        int carry = 0 ;

        while(q != null || p != null){
            int number1 = q == null ? 0 : q.val;
            int number2 = p == null ? 0 : p.val;

            //在这一步就添加上carry的内容，因为运行起来不知道是第几个
            int sum = carry + number1 + number2;
            carry = sum/10; //余数未10

            ListNode nextNode = new ListNode(sum%10);
            curr.next = nextNode;
            curr = curr.next;   //指向下一个内容，方便迭代。这个不会影响到dummy，因为这个复制的是临时变量的引用
            //这里也是同理
            if (q != null) q = q.next;
            if (p != null) p = p.next;

        }

        //这个每次有有俩个内容相加，都会自己刷新一次，所以不用担心会有上次遗留的问题，如果他有值，那么一定是需要添加一个的
        if (carry > 0){
            curr.next = new ListNode(carry);
        }

        return dummyNode.next;
    }


    public static void main(String[] args) {

        ListNode l1 = new ListNode(9);
        l1.next = new ListNode(9);
        ListNode l2 = new ListNode(1);

        Solution.addTwoNumbers(l1,l2);

    }
}