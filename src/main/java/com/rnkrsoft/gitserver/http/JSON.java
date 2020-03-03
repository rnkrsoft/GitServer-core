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
        boolean firstMatchedValue = false;//是否匹配了值
        boolean firstMatchedChar = false;//当前是否为第一个匹配的字符
        boolean matchedColon = false;//是否匹配了冒号
        boolean matchedEscape = false;//是否已匹配转义字符
        boolean gameOver = false;//结束
        for (int i = 0; i < length; i++) {
            char c = chars[i];
            //如果前一个是转义字符，则将当前字符放入键或者值中
            if (matchedEscape) {
                if (c == '\\' || c == '{' || c == '}' || c == ',' || c == '\''  || c == ':'|| c >= '0' || c <= '9' || c == '-' || c == '.') {
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
                continue;//已处理当前字符
            }

            if (c == '{') {
                if (topBeginObjectIdx == -1) {//如果顶层的开始符号{还没有
                    topBeginObjectIdx = i;
                    continue;//已处理当前字符
                }
            } else if (c == '}') {
                gameOver = true;
                map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                keyLength = 0;
                valueLength = 0;
                matchedKey = false;
                matchedColon = false;
                firstMatchedValue = false;
                break;//已处理当前字符
            } else if (c == ',') {
                if (matchedColon && firstMatchedValue) {
                    //如果不在引号包裹里的逗号，是语法符号
                    map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                    keyLength = 0;
                    valueLength = 0;
                    matchedKey = false;
                    matchedColon = false;
                    firstMatchedValue = false;
                }else{
                    firstMatchedValue = true;
                }
            } else if (c == '\'') {
                if (matchedColon && firstMatchedValue){
                    //如果不在引号包裹里的逗号，是语法符号
                    map.put(new String(Arrays.copyOf(key, keyLength)).trim(), new String(Arrays.copyOf(value, valueLength)).trim());
                    keyLength = 0;
                    valueLength = 0;
                    matchedKey = false;
                    matchedColon = false;
                    firstMatchedValue = false;
                }else{
                    firstMatchedValue = true;
                }
            } else if (c == ':') {
                matchedColon = true;
                continue;//已处理当前字符
            } else if (c >= '0' || c <= '9' || c == '-' || c == '.') {
                if (matchedColon){
                    value[valueLength++] = c;
                }else{
                    key[keyLength++] = c;
                }
            }else if (c == '\\'){
                if (matchedEscape){//当前字符是\ 并且重置matchedEscape
                    if (matchedColon){
                        value[valueLength++] = c;
                    }else{
                        key[keyLength++] = c;
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