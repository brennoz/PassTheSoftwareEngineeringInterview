package com.interview.algorithms.arrays_and_hashing.q087_encode_decode_strings;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    // Length-prefix framing: {length}#{content} — decoder jumps by length, never searches '#' inside content
    public String encode(List<String> strs) {
        StringBuilder res = new StringBuilder();
        for (String s : strs) {
            res.append(s.length()).append('#').append(s);
        }
        return res.toString();
    }

    // Two-cursor parse: i=read head, j races to '#', then leaps content-length chars
    public List<String> decode(String str) {
        List<String> res = new ArrayList<>();
        int i = 0;
        while (i < str.length()) {
            int j = i;
            while (str.charAt(j) != '#') j++;
            int length = Integer.parseInt(str.substring(i, j));
            i = j + 1;
            j = i + length;
            res.add(str.substring(i, j));
            i = j;
        }
        return res;
    }
}
