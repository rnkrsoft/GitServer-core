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
        boolean matchedKey = false;//是否匹配了键
        boolean matchedValue = false;//是否匹配了值
        int quotationIdx = -1;//引号开始索引
        int matchComma = -1;//逗号的索引
        int commaIdx = -1;//
        boolean gameOver = false;//结束
        int beginValueIdx = -1;//值开始索引
        boolean firstMatchedChar = false;//当前是否为第一个匹配的字符
        boolean matchedColon = false;//是否匹配了冒号
        boolean matchedEscape = false;//是否已匹配转义字符
        for (int i = 0; i < length; i++) {
            char c = chars[i];
            if (c == ' ' || c == '\n' || c == '\r') {
                if (matchedKey && matchedColon && !matchedValue) {//匹配了键，匹配了冒号，第一个字符，同时没有值匹配，说明当前是冒号之后值开始之前
                    if (firstMatchedChar) {
                        map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                        keyLength = 0;
                        valueLength = 0;
                        matchedValue = true;
                        continue;//已处理当前字符
                    }
                }
            } else if (c == ':') {//当当前是冒号时需要检查之前是否有转义字符
                if (matchedEscape) {

                } else {//不是转义字符，则匹配冒号
                    matchedColon = true;
                    continue;//已处理当前字符
                }
            }
            if (gameOver) {
                throw new IllegalArgumentException("illegal json format!");
            }
            if (matchedEscape) {
                if (c == '\\' || c == '{' || c == '}' || c == ',' || c == '\'' || c >= '0' || c <= '9' || c == '-' || c == '.') {
                    if (matchedKey){
                        key[keyLength++] = c;
                    }else{
                        value[valueLength++] = c;
                    }
                }else{
                    if (matchedKey){
                        key[keyLength++] = '\\';
                        key[keyLength++] = c;
                    }else{
                        value[valueLength++]  = '\\';
                        value[valueLength++] = c;
                    }
                }
                continue;
            }
            if (c == '{') {
                if (topBeginObjectIdx == -1) {//如果顶层的开始符号{还没有
                    topBeginObjectIdx = i;
                    continue;//已处理当前字符
                } else {
                    map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                    keyLength = 0;
                    valueLength = 0;
                    matchedValue = false;
                }
            } else if (c == '}') {
                if (matchComma == 1) {
                    throw new IllegalArgumentException("illegal json format! cause: index[" + commaIdx + "] = \"" + chars[commaIdx] + "\" is illegal!");
                }
                gameOver = true;
            } else if (c == ',') {
                //如果不在引号包裹里的逗号，是语法符号
                if (quotationIdx == -1) {
                    matchedKey = false;
                    matchedValue = false;
                    matchedColon = false;
                    beginValueIdx = -1;
                    commaIdx = i;
                    matchComma = 1;
                    firstMatchedChar = false;
                }
            } else if (c == '\'') {
                if (matchComma == -1 || matchComma == 1) {
                    matchComma = 0;
                    commaIdx = -1;
                }
                if (quotationIdx == -1) {
                    quotationIdx = i;
                } else if (quotationIdx != -1) {
                    if (!matchedKey) {
                        if (quotationIdx > -1) {
                            matchedKey = true;
                        }
                    } else if (!matchedValue) {
                        map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                        keyLength = 0;
                        valueLength = 0;
                        matchedValue = true;
                    } else if (matchedKey && matchedValue) {
                        throw new IllegalArgumentException("illegal json format! cause: index[" + i + "] = \"" + c + "\" is illegal!");
                    }
                    quotationIdx = -1;
                }
            } else if (c >= '0' || c <= '9' || c == '-' || c == '.') {
                if (matchedKey && matchedColon) {
                    if (!firstMatchedChar && beginValueIdx == -1) {
                        firstMatchedChar = true;
                        beginValueIdx = i;
                    }
                } else if (matchedKey && matchedColon && firstMatchedChar && matchedValue) {
                    throw new IllegalArgumentException("illegal json format! cause: index[" + i + "] = \"" + c + "\" is illegal!");
                }
            }else if (c == '\\'){
                if (matchedEscape){//当前字符是\ 并且重置matchedEscape
                    if (matchedKey){
                        key[keyLength++] = c;
                    }else{
                        value[valueLength++] = c;
                    }
                    matchedEscape = false;
                }else{//当前字符是转义字符
                    matchedEscape = true;
                }
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