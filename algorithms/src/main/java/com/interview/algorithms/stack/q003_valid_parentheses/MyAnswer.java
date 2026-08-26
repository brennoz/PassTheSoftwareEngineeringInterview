package com.interview.algorithms.stack.q003_valid_parentheses;

import java.util.ArrayDeque;
import java.util.Deque;

public class MyAnswer {

    public static void main (String[] args) {
        MyAnswer my = new MyAnswer();
        System.out.println(my.isValid("()[]{}")); //true
        System.out.println(my.isValid("([)]")); //false
        System.out.println(my.isValid("{[]}")); //true
        System.out.println(my.isValid(")")); //true
        System.out.println(my.isValid("[")); //false
    }

    public boolean isValid(String s) {
        // write your solution here
        /*
            (
            @ ( @
            ((
            @ ( ( @
    ((]
         */

        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                Character top = stack.pop();
                if (c == ')' && top != '(') return false;
                if (c == ']' && top != '[') return false;
                if (c == '}' && top != '{') return false;
            }
        }

        return stack.isEmpty();
    }
}
