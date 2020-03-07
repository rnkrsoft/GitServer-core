package com.rnkrsoft.gitserver.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rnkrsoft.com on 2019/9/19.
 * 一个简单的解析JSON文本到键值对的工具
 */
public class JSON {
    static class Stack{
        int[] indexes;
        int pos = -1;

        public Stack(int size) {
            this.indexes = new int[size];
        }

        /**
         * 压入数据
         * @param index
         * @return
         */
        public Stack push(int index){
            if (pos + 1 > indexes.length){
                throw new IllegalArgumentException("stack overflow!");
            }
            indexes[++pos] = index;
            return this;
        }
        public boolean isEmpty(){
            return pos == -1;
        }
        /**
         * 取栈顶数据
         * @return
         */
        public int peek(){
            if (pos == -1){
                return -1;
            }
            return indexes[pos];
        }

        /**
         * 弹出数据
         * @return
         */
        public int pop(){
            if (pos == -1){
                return -1;
            }
            int r = indexes[pos];
            if (r > -1){
                indexes[pos] = 0;
                pos--;
            }
            return r;
        }
    }
    /**
     * 将传入的URL解析为Map
     *
     * @param url URL对象
     * @return 键值对
     * @throws IOException IO异常
     */
    public static final Map<String, String> parse(URL url) throws IOException {
        InputStream is = null;
        try {
            is = url.openStream();
            String json = read(is);
            return JSON.parse(json);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
    }

    /**
     * 将传入的JSON解析为MAP对象
     *
     * @param json JSON数据
     * @return 键值对
     */
    public static final Map<String, String> parse(String json) {
        char[] chars = json.toCharArray();
        final char[] key = new char[chars.length];
        int keyLength = 0;
        final char[] value = new char[chars.length];
        int valueLength = 0;
        final Map<String, String> map = new HashMap<String, String>();
        int length = chars.length;//JSON字符串长度
        int topBeginObjectIdx = -1;//JSON对象最外层{开始开始坐标
        boolean matchedKey = false;//是否匹配键完成
        boolean matchedQuotation = false;//是否匹配了引号
        boolean matchedValue = false;//是否匹配值完成
        boolean firstMatchedChar = false;//当前是否为第一个匹配的字符
        boolean matchedColon = false;//是否匹配了冒号
        boolean matchedEscape = false;//是否已匹配转义字符
        Stack matchedBeginObjectStack = new Stack(100);
        boolean over = false;
        for (int i = 0; i < length; i++) {
            char c = chars[i];
            if (over) {
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    continue;//已处理当前字符
                } else {
                    throw new IllegalArgumentException("illegal json");
                }
            }
            if (matchedValue) {
                //如果不在引号包裹里的逗号，是语法符号
                map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                keyLength = 0;
                valueLength = 0;
                matchedKey = false;
                matchedValue = false;
                matchedColon = false;
                firstMatchedChar = false;
            }
            //如果前一个是转义字符，则将当前字符放入键或者值中
            if (matchedEscape) {
                if (c == '\\' || c == '{' || c == '}' || c == ',' || c == '\'' || c == ':' || c >= '0' || c <= '9' || c == '-' || c == '.') {
                    if (matchedKey) {
                        key[keyLength++] = c;
                    } else {
                        value[valueLength++] = c;
                    }
                } else {
                    if (matchedKey) {
                        key[keyLength++] = '\\';
                        key[keyLength++] = c;
                    } else {
                        value[valueLength++] = '\\';
                        value[valueLength++] = c;
                    }
                }
                continue;//已处理当前字符
            }

            if (!matchedBeginObjectStack.isEmpty()) {
                if (c == '}'){
                    value[valueLength++] = c;
                    matchedBeginObjectStack.pop();
                    //弹出后，栈为空，说明当前对象结束
                    if (matchedBeginObjectStack.isEmpty()) {
                        matchedValue = true;
                    }
                }else if (c == '{'){
                    value[valueLength++] = c;
                    matchedBeginObjectStack.push(i);
                }else{
                    value[valueLength++] = c;
                }
                continue;//已处理当前字符
            }
            if (c == '{') {
                if (topBeginObjectIdx == -1) {//如果顶层的开始符号{还没有
                    topBeginObjectIdx = i;
                    continue;//已处理当前字符
                } else{//第一次出现{
                    if (!firstMatchedChar) {
                        firstMatchedChar = true;
                    }
                    value[valueLength++] = c;
                    matchedBeginObjectStack.push(i);//将{当前位置存放到栈中
                    continue;//已处理当前字符
                }
            } else if (c == '}') {//顶层对象结束
                if (!matchedBeginObjectStack.isEmpty()) {//如果开JSON对象开始符号栈中有则为对象中，并非最顶层结束
                    throw new IllegalArgumentException("illegal json");
                } else {
                    if (matchedKey && matchedValue && matchedColon) {
                        map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                        over = true;
                    } else {
                        if (matchedQuotation) {
                            throw new IllegalArgumentException("illegal json");
                        } else {
                            map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                            over = true;
                        }
                    }
                }
            } else if (c == ',') {
                if (!matchedKey && !firstMatchedChar) {
                    firstMatchedChar = true;
                } else if (!matchedKey && firstMatchedChar) {
                    matchedKey = true;
                    firstMatchedChar = false;
                } else if (matchedKey && !matchedColon && !firstMatchedChar) {
                    throw new IllegalArgumentException("illegal json");
                } else if (matchedKey && matchedColon && !firstMatchedChar) {
                } else if (matchedKey && matchedColon && firstMatchedChar) {
                    matchedValue = true;
                }
                continue;//已处理当前字符
            } else if (c == '\'') {
                if (!firstMatchedChar) {
                    matchedQuotation = true;
                } else if (matchedQuotation) {
                    matchedQuotation = false;
                }
                continue;//已处理当前字符
            } else if (c == ':') {
                matchedKey = true;
                firstMatchedChar = false;
                matchedColon = true;
                continue;//已处理当前字符
            } else if (c >= '0' || c <= '9' || c == '-' || c == '.') {
                if (matchedColon) {
                    value[valueLength++] = c;
                } else {
                    key[keyLength++] = c;
                }
                if (!firstMatchedChar) {
                    firstMatchedChar = true;
                }
            } else if (c == '\\') {
                if (matchedEscape) {//当前字符是\ 并且重置matchedEscape
                    if (matchedColon) {
                        value[valueLength++] = c;
                    } else {
                        key[keyLength++] = c;
                    }
                    matchedEscape = false;
                    if (!firstMatchedChar) {
                        firstMatchedChar = true;
                    }
                } else {//当前字符是转义字符
                    matchedEscape = true;
                }
            } else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                continue;//已处理当前字符
            }
        }
        return map;
    }

    static String read(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        long count;
        int n;
        byte[] buffer = new byte[4096];
        for (count = 0L; -1 != (n = input.read(buffer)); count += (long) n) {
            output.write(buffer, 0, n);
        }
        return new String(output.toByteArray(), "UTF-8");
    }
}