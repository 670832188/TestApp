package com.dev.kit.testapp.algorithm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.util.LogUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Author: cuiyan
 * Date:   2019/4/6 11:09
 * Desc:
 */
public class AlgorithmActivity1 extends BaseStateViewActivity {

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return null;
    }


    /**
     * 一百盏灯排成一排，初始状态是亮的， 编号为 1-100,100个人编号1-100.每个人从1号灯开始逐次走过这100盏灯。
     * 每个人只拉他对应编号的倍数的灯，问最后亮着几盏灯？
     */
    private void algorithmTest() {
        int[] lamps = new int[100];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= 100; j++) {
                if ((i) % (j) == 0) {
                    lamps[i - 1] ^= 1;
                }
            }
            if (lamps[i] == 1) {
                sb.append(i).append(",");
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        LogUtil.e("mytag", "lamps: " + sb.toString());

    }


    /**
     * LeetCode第一题
     * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
     * <p>
     * 你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
     */
    public int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] == target - nums[i]) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }


    /**
     * 给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
     * <p>
     * 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
     * <p>
     * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode newNode = new ListNode(0);
        ListNode currentNode = newNode;
        int extra = 0;
        while (l1 != null || l2 != null) {
            int node1Val = l1 == null ? 0 : l1.val;
            int node2Val = l2 == null ? 0 : l2.val;
            int sum = node1Val + node2Val + extra;
            currentNode.next = new ListNode(sum % 10);
            extra = sum >= 10 ? 1 : 0;
            if (extra > 0) {
                currentNode.next.next = new ListNode(extra);
            }
            currentNode = currentNode.next;
            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        }
        return newNode.next;
    }

    private class ListNode {
        private int val;
        private ListNode next;

        public ListNode(int value) {
            this.val = value;
        }

        public int getValue() {
            return val;
        }

        public ListNode getNext() {
            return next;
        }
    }

    /**
     * 给定两个大小为 m 和 n 的有序数组 nums1 和 nums2。
     * <p>
     * 请你找出这两个有序数组的中位数，并且要求算法的时间复杂度为 O(log(m + n))。
     * <p>
     * 你可以假设 nums1 和 nums2 不会同时为空。
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        double result;
        int len = nums1.length + nums2.length;
        int[] nums = new int[len];
        int len1 = nums1.length;
        int len2 = nums2.length;
        int index1 = 0;
        int index2 = 0;
        int k = 0;
        while (index1 < len1 && index2 < len2) {
            if (nums1[index1] < nums2[index2]) {
                nums[k] = nums1[index1];
                index1++;
            } else {
                nums[k] = nums2[index2];
                index2++;
            }
            k++;
        }

        for (int i = k; i < len; i++) {
            if (index1 == len1) {
                nums[i] = nums2[index2];
                index2++;
            } else {
                nums[i] = nums1[index1];
                index1++;
            }
        }


        int midIndex = len / 2;
        if (len % 2 == 0) {
            result = (nums[midIndex - 1] + nums[midIndex]) / 2d;
        } else {
            result = nums[midIndex];
        }
        return result;
    }

    /**
     * 将两个有序链表合并为一个新的有序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
     * <p>
     * 示例：
     * <p>
     * 输入：1->2->4, 1->3->4
     * 输出：1->1->2->3->4->4
     */
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode newNode = new ListNode(0);
        ListNode currentNode = newNode;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                currentNode.next = l1;
                l1 = l1.next;
            } else {
                currentNode.next = l2;
                l2 = l2.next;
            }
            currentNode = currentNode.next;
        }
        currentNode.next = l1 == null ? l2 : l1;
        return newNode.next;
    }


    /**
     * 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
     * <p>
     * 示例 1:
     * <p>
     * 输入: 123
     * 输出: 321
     * 示例 2:
     * <p>
     * 输入: -123
     * 输出: -321
     * 示例 3:
     * <p>
     * 输入: 120
     * 输出: 21
     * 注意:
     * <p>
     * 假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−231,  231 − 1]。请根据这个假设，如果反转后整数溢出那么就返回 0。
     */

    public int reverse(int x) {
        long reversedValue = 0;
        while (x != 0) {
            reversedValue = reversedValue * 10 + x % 10;
            x /= 10;
        }
        if (reversedValue > Integer.MAX_VALUE || reversedValue < Integer.MIN_VALUE) {
            return 0;
        }

        return (int) reversedValue;
    }


    /**
     * 判断一个整数是否是回文数。回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
     * <p>
     * 示例 1:
     * <p>
     * 输入: 121
     * 输出: true
     * 示例 2:
     * <p>
     * 输入: -121
     * 输出: false
     * 解释: 从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。
     * 示例 3:
     * <p>
     * 输入: 10
     * 输出: false
     * 解释: 从右向左读, 为 01 。因此它不是一个回文数。
     * 进阶:
     * <p>
     * 你能不将整数转为字符串来解决这个问题吗？
     */
    public boolean isPalindrome(int x) {
        if (x < 0) {
            return false;
        }
        long reversedValue = 0;
        while (x != 0) {
            reversedValue = reversedValue * 10 + x % 10;
            x /= 10;
        }

        return reversedValue == x;
    }

    /**
     * 请你来实现一个 atoi 函数，使其能将字符串转换成整数。
     * <p>
     * 首先，该函数会根据需要丢弃无用的开头空格字符，直到寻找到第一个非空格的字符为止。
     * <p>
     * 当我们寻找到的第一个非空字符为正或者负号时，则将该符号与之后面尽可能多的连续数字组合起来，作为该整数的正负号；假如第一个非空字符是数字，则直接将其与之后连续的数字字符组合起来，形成整数。
     * <p>
     * 该字符串除了有效的整数部分之后也可能会存在多余的字符，这些字符可以被忽略，它们对于函数不应该造成影响。
     * <p>
     * 注意：假如该字符串中的第一个非空格字符不是一个有效整数字符、字符串为空或字符串仅包含空白字符时，则你的函数不需要进行转换。
     * <p>
     * 在任何情况下，若函数不能进行有效的转换时，请返回 0。
     * <p>
     * 说明：
     * <p>
     * 假设我们的环境只能存储 32 位大小的有符号整数，那么其数值范围为 [−231,  231 − 1]。如果数值超过这个范围，qing返回  INT_MAX (231 − 1) 或 INT_MIN (−231) 。
     * <p>
     * 示例 1:
     * <p>
     * 输入: "42"
     * 输出: 42
     * 示例 2:
     * <p>
     * 输入: "   -42"
     * 输出: -42
     * 解释: 第一个非空白字符为 '-', 它是一个负号。
     * 我们尽可能将负号与后面所有连续出现的数字组合起来，最后得到 -42 。
     * 示例 3:
     * <p>
     * 输入: "4193 with words"
     * 输出: 4193
     * 解释: 转换截止于数字 '3' ，因为它的下一个字符不为数字。
     * 示例 4:
     * <p>
     * 输入: "words and 987"
     * 输出: 0
     * 解释: 第一个非空字符是 'w', 但它不是数字或正、负号。
     * 因此无法执行有效的转换。
     * 示例 5:
     * <p>
     * 输入: "-91283472332"
     * 输出: -2147483648
     * 解释: 数字 "-91283472332" 超过 32 位有符号整数范围。
     * 因此返回 INT_MIN (−231) 。
     */
    public int myAtoi(String str) {

        if (str == null || str.trim().length() == 0) {
            return 0;
        }

        str = str.trim();
        int len = str.length();
        BigInteger value = BigInteger.valueOf(0);
        int sign = 1;
        for (int i = 0; i < len; i++) {
            char v = str.charAt(i);
            if (i == 0) {
                if (v == '-') {
                    sign = -1;
                } else if (v >= '0' && v <= '9') {
                    value = BigInteger.valueOf(v - '0');
                } else if (v != '+') {
                    break;
                }
            } else if (v >= '0' && v <= '9') {
                value = value.multiply(BigInteger.valueOf(10)).add(BigInteger.valueOf(v - '0'));
            } else {
                break;
            }
        }
        value = value.multiply(BigInteger.valueOf(sign));
        if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            return Integer.MAX_VALUE;
        }
        if (value.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
            return Integer.MIN_VALUE;
        }

        return value.intValue();
    }


    public int myAtoi1(String str) {
        if (str == null || str.trim().length() == 0) {
            return 0;
        }

        str = str.trim();
        int len = str.length();
        long value = 0;
        int sign = 1;
        for (int i = 0; i < len; i++) {
            char v = str.charAt(i);
            if (i == 0) {
                if (v == '-') {
                    sign = -1;
                } else if (v >= '0' && v <= '9') {
                    value = v - '0';
                } else if (v != '+') {
                    break;
                }
            } else if (v >= '0' && v <= '9') {
                value = value * 10 + v - '0';
                if (sign == 1 && value > Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                } else if (sign == -1 && value < Integer.MIN_VALUE) {
                    return Integer.MIN_VALUE;
                }
            } else {
                break;
            }
        }
        value = sign * value;
        if (value > Integer.MAX_VALUE) {
            value = Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            value = Integer.MIN_VALUE;
        }
        return (int) value;
    }


    public double myPow(double x, int n) {
        if (n == 0) return 1;
        double temp = myPow(x, n / 2);
        if (n % 2 == 1) {
            return temp * temp * x;
        } else if (n % 2 == -1) {
            return temp * temp / x;
        } else {
            return temp * temp;
        }
    }

    /**
     * 给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。
     * <p>
     * 你的算法时间复杂度必须是 O(log n) 级别。
     * <p>
     * 如果数组中不存在目标值，返回 [-1, -1]。
     * <p>
     * 示例 1:
     * <p>
     * 输入: nums = [5,7,7,8,8,10], target = 8
     * 输出: [3,4]
     * 示例 2:
     * <p>
     * 输入: nums = [5,7,7,8,8,10], target = 6
     * 输出: [-1,-1]
     */

    public int[] searchRange(int[] nums, int target) {
        int[] location = new int[]{-1, -1};
        if (nums == null || nums.length == 0) {
            return location;
        }
        int targetIndex = binSearch(nums, target, 0, nums.length - 1);
        if (targetIndex == -1) {
            return location;
        }

        int pre = targetIndex - 1;
        while (pre >= 0) {
            if (nums[pre] == target) {
                location[0] = pre;
                pre--;
            } else {
                break;
            }
        }
        int after = targetIndex + 1;
        while (after < nums.length) {
            if (nums[after] == target) {
                location[1] = after;
                after++;
            } else {
                break;
            }
        }
        if (location[0] == -1) {
            location[0] = targetIndex;
        }

        if (location[1] == -1) {
            location[1] = targetIndex;
        }
        return location;
    }

    public int binSearch(int[] nums, int target, int left, int right) {
        int mid = (left + right) / 2;
        if (nums[mid] == target) {
            return mid;
        }
        if (left >= right) {
            return -1;
        } else if (nums[mid] < target) {
            return binSearch(nums, target, mid + 1, right);
        } else {
            return binSearch(nums, target, left, mid - 1);
        }
    }


    /**
     * 给定一个排序数组，你需要在原地删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。
     * <p>
     * 不要使用额外的数组空间，你必须在原地修改输入数组并在使用 O(1) 额外空间的条件下完成。
     * <p>
     * 示例 1:
     * <p>
     * 给定数组 nums = [1,1,2],
     * <p>
     * 函数应该返回新的长度 2, 并且原数组 nums 的前两个元素被修改为 1, 2。
     * <p>
     * 你不需要考虑数组中超出新长度后面的元素。
     * 示例 2:
     * <p>
     * 给定 nums = [0,0,1,1,1,2,2,3,3,4],
     * <p>
     * 函数应该返回新的长度 5, 并且原数组 nums 的前五个元素被修改为 0, 1, 2, 3, 4。
     * <p>
     * 你不需要考虑数组中超出新长度后面的元素。
     */

    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int index = 1;
        int lastVal = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (lastVal != nums[i]) {
                lastVal = nums[i];
                nums[index] = nums[i];
                index++;
            }
        }

        return index;
    }


    /**
     * 给定一个数组 nums 和一个值 val，你需要原地移除所有数值等于 val 的元素，返回移除后数组的新长度。
     * <p>
     * 不要使用额外的数组空间，你必须在原地修改输入数组并在使用 O(1) 额外空间的条件下完成。
     * <p>
     * 元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。
     * <p>
     * 示例 1:
     * <p>
     * 给定 nums = [3,2,2,3], val = 3,
     * <p>
     * 函数应该返回新的长度 2, 并且 nums 中的前两个元素均为 2。
     * <p>
     * 你不需要考虑数组中超出新长度后面的元素。
     * 示例 2:
     * <p>
     * 给定 nums = [0,1,2,2,3,0,4,2], val = 2,
     * <p>
     * 函数应该返回新的长度 5, 并且 nums 中的前五个元素为 0, 1, 3, 0, 4。
     * <p>
     * 注意这五个元素可为任意顺序。
     * <p>
     * 你不需要考虑数组中超出新长度后面的元素。
     */

    public int removeElement(int[] nums, int val) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int len = nums.length;
        int index = 0;
        for (int i = 0; i < len; i++) {
            if (nums[i] != val) {
                nums[index] = nums[i];
                index++;
            }
        }
        return index;
    }


    /**
     * 给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。
     * <p>
     * 示例：
     * <p>
     * 给定一个链表: 1->2->3->4->5, 和 n = 2.
     * <p>
     * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
     * 说明：
     * <p>
     * 给定的 n 保证是有效的。
     * <p>
     * 进阶：
     * <p>
     * 你能尝试使用一趟扫描实现吗？
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if (head == null) {
            return head;
        }
        int count = 1;
        ListNode node = head;
        while ((node = node.next) != null) {
            count++;
        }
        if (count < n || n <= 0) {
            return head;
        }
        node = new ListNode(0);
        node.next = head;
        ListNode pre = node;
        int index = 0;
        while (head != null) {
            if (count - n == index) {
                pre.next = head.next;
                return node.next;
            }
            index++;
            pre = head;
            head = head.next;
        }
        return node.next;
    }

    /**
     * 有效的括号
     * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
     * <p>
     * 有效字符串需满足：
     * <p>
     * 左括号必须用相同类型的右括号闭合。
     * 左括号必须以正确的顺序闭合。
     * 注意空字符串可被认为是有效字符串。
     * <p>
     * 示例 1:
     * <p>
     * 输入: "()"
     * 输出: true
     * 示例 2:
     * <p>
     * 输入: "()[]{}"
     * 输出: true
     * 示例 3:
     * <p>
     * 输入: "(]"
     * 输出: false
     * 示例 4:
     * <p>
     * 输入: "([)]"
     * 输出: false
     * 示例 5:
     * <p>
     * 输入: "{[]}"
     * 输出: true
     */
    public boolean isValid(String s) {
        if (s == null || (s = s.trim()).length() % 2 != 0) {
            return false;
        }
        if (s.length() == 0) {
            return true;
        }
        Map<Character, Character> kvMap = new HashMap<>();
        kvMap.put(')', '(');
        kvMap.put('}', '{');
        kvMap.put(']', '[');

        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            Character c = s.charAt(i);
            if (kvMap.containsKey(c)) {
                if (stack.empty()) {
                    stack.push(c);
                } else if (stack.peek() == kvMap.get(c)) {
                    stack.pop();
                } else {
                    stack.push(c);
                }
            } else {
                stack.push(c);
            }
        }
        return stack.empty();
    }


    /**
     * 两两交换链表中的节点
     * 给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。
     * <p>
     * 你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。
     * <p>
     * <p>
     * <p>
     * 示例:
     * <p>
     * 给定 1->2->3->4, 你应该返回 2->1->4->3.
     */
    public ListNode swapPairs(ListNode head) {

        if (head == null || head.next == null) {
            return head;
        }
        ListNode root = new ListNode(0);
        root.next = head;
        ListNode controlNode = root;
        ListNode preNode = head;
        ListNode nextNode;
        int change = 1;
        while (head != null) {
            change ^= 1;
            if (change == 1) {
                nextNode = head.next;
                controlNode.next = head;
                head.next = preNode;
                preNode.next = nextNode;
                controlNode = preNode;
                head = nextNode;
            } else {
                preNode = head;
                head = head.next;
            }
        }
        return root.next;
    }


    /**
     * 实现strStr()
     * 实现 strStr() 函数。
     * <p>
     * 给定一个 haystack 字符串和一个 needle 字符串，在 haystack 字符串中找出 needle 字符串出现的第一个位置 (从0开始)。如果不存在，则返回  -1。
     * <p>
     * 示例 1:
     * <p>
     * 输入: haystack = "hello", needle = "ll"
     * 输出: 2
     * 示例 2:
     * <p>
     * 输入: haystack = "aaaaa", needle = "bba"
     * 输出: -1
     * 说明:
     * <p>
     * 当 needle 是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。
     * <p>
     * 对于本题而言，当 needle 是空字符串时我们应当返回 0 。这与C语言的 strstr() 以及 Java的 indexOf() 定义相符。
     * <p>
     * 在真实的面试中遇到过这道题？
     */
    public int strStr(String haystack, String needle) {
        if (needle == null || needle.length() <= 0) {
            return 0;
        }
        if (haystack == null || haystack.length() == 0 || haystack.length() < needle.length()) {
            return -1;
        }
        char[] t = haystack.toCharArray();
        char[] p = needle.toCharArray();
        int i = 0;
        int j = 0;
        int[] next = getNext(needle);
        while (i < t.length && j < p.length) {
            if (j == -1 || t[i] == p[j]) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }

        if (j == p.length) {
            return i - j;
        } else {
            return -1;
        }
    }

    public int[] getNext(String needle) {
        char[] p = needle.toCharArray();
        if (p.length == 1) {
            return new int[]{-1};
        }
        int[] next = new int[p.length];
        next[0] = -1;
        int j = 0;
        int k = -1;
        while (j < p.length - 1) {
            if (k == -1 || p[j] == p[k]) {
                if (p[++j] == p[++k]) {
                    next[j] = next[k];
                } else {
                    next[j] = k;
                }
            } else {
                k = next[k];
            }
        }
        return next;
    }


    /**
     * 搜索插入位置
     * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
     * <p>
     * 你可以假设数组中无重复元素。
     * <p>
     * 示例 1:
     * <p>
     * 输入: [1,3,5,6], 5
     * 输出: 2
     * 示例 2:
     * <p>
     * 输入: [1,3,5,6], 2
     * 输出: 1
     * 示例 3:
     * <p>
     * 输入: [1,3,5,6], 7
     * 输出: 4
     * 示例 4:
     * <p>
     * 输入: [1,3,5,6], 0
     * 输出: 0
     */
    public int searchInsert(int[] nums, int target) {
        return binSearch1(nums, target, 0, nums.length - 1);
    }

    public int binSearch1(int[] nums, int target, int left, int right) {
        int mid = (left + right) / 2;
        if (nums[mid] == target) {
            return mid;
        }
        if (left >= right) {
            if (nums[left] < target) {
                return left + 1;
            } else {
                return left;
            }
        } else if (nums[mid] < target) {
            return binSearch(nums, target, mid + 1, right);
        } else {
            return binSearch(nums, target, left, mid - 1);
        }
    }

    /**
     * 反转从位置 m 到 n 的链表。请使用一趟扫描完成反转。
     * <p>
     * 说明:
     * 1 ≤ m ≤ n ≤ 链表长度。
     * <p>
     * 示例:
     * <p>
     * 输入: 1->2->3->4->5->NULL, m = 2, n = 4
     * 输出: 1->4->3->2->5->NULL
     */
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (head == null) {
            return head;
        }
        ListNode node = new ListNode(0);
        node.next = head;
        Stack<ListNode> reverseStack = new Stack<>();
        ListNode sepNodePre = node;
        ListNode sepNodeLast = null;
        int index = 1;
        while (head != null) {
            if (index >= m && index <= n) {
                reverseStack.push(head);
                if (index == n) {
                    sepNodeLast = head.next;
                    break;
                }
            } else {
                sepNodePre = head;
            }
            head = head.next;
            index++;
        }
        while (!reverseStack.empty()) {
            sepNodePre.next = reverseStack.pop();
            sepNodePre = sepNodePre.next;
        }
        sepNodePre.next = sepNodeLast;
        return node.next;
    }

    /**
     * 给定两个有序整数数组 nums1 和 nums2，将 nums2 合并到 nums1 中，使得 num1 成为一个有序数组。
     * <p>
     * 说明:
     * <p>
     * 初始化 nums1 和 nums2 的元素数量分别为 m 和 n。
     * 你可以假设 nums1 有足够的空间（空间大小大于或等于 m + n）来保存 nums2 中的元素。
     * 示例:
     * <p>
     * 输入:
     * nums1 = [1,2,3,0,0,0], m = 3
     * nums2 = [2,5,6],       n = 3
     * <p>
     * 输出: [1,2,2,3,5,6]
     */
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int index1 = 0;
        int index2 = 0;
        int index = 0;
        int[] tmp = new int[nums1.length];
        while (index1 < m && index2 < n) {
            if (nums1[index1] < nums2[index2]) {
                tmp[index] = nums1[index1];
                index1++;
            } else {
                tmp[index] = nums2[index2];
                index2++;
            }
            index++;
        }
        if (index1 == m) {
            for (int i = index2; i < n; i++) {
                tmp[index] = nums2[i];
                index++;
            }
        } else {
            for (int i = index1; i < m; i++) {
                tmp[index] = nums1[i];
                index++;
            }
        }
        for (int i = 0; i < tmp.length; i++) {
            nums1[i] = tmp[i];
        }
    }

    /**
     * 给定一个排序链表，删除所有重复的元素，使得每个元素只出现一次。
     * <p>
     * 示例 1:
     * <p>
     * 输入: 1->1->2
     * 输出: 1->2
     * 示例 2:
     * <p>
     * 输入: 1->1->2->3->3
     * 输出: 1->2->3
     */
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode node = new ListNode(0);
        ListNode pre = head;
        node.next = head;
        head = head.next;
        int val;
        while (head != null) {
            val = head.val;
            if (pre.val != val) {
                pre.next = head;
                pre = head;
            } else {
                pre.next = null;
            }
            head = head.next;
        }
        return node.next;
    }
}
