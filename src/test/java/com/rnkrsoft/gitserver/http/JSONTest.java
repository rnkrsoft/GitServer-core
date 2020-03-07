package com.rnkrsoft.gitserver.http;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class JSONTest {
    @Test
    public void test1(){
        JSON.Stack stack = new JSON.Stack(10);
        for (int i = 0; i <10 ; i++) {
            stack.push(i);
        }
        Assert.assertEquals(9, stack.pos);
        for (int i = 0; i < 10; i++) {
            System.out.println(stack.pop());
        }
        Assert.assertEquals(-1, stack.pos);
    }
    @Test
    public void parse1() {
        Map<String, String> map = JSON.parse("{action:'doa', data:12345} ");
        Assert.assertEquals("doa", map.get("action"));
        Assert.assertEquals("12345", map.get("data"));

        Map<String, String> map1= JSON.parse("{action: 'doa' , data: 12345 } ");
        Assert.assertEquals("doa", map1.get("action"));
        Assert.assertEquals("12345", map1.get("data"));
    }

    @Test
    public void parse2() {
        Map<String, String> map = JSON.parse("{action:'doa', data:{name:'1234'} } ");
        Assert.assertEquals("doa", map.get("action"));
        Assert.assertEquals("{name:'1234'}", map.get("data"));
    }

    @Test
    public void parse3() {
        Map<String, String> map = JSON.parse("{action:'doa', data:{name:{age:123}} } ");
        Assert.assertEquals("doa", map.get("action"));
        Assert.assertEquals("{name:{age:123}}", map.get("data"));
    }


    @Test
    public void parse4() {
        Map<String, String> map1 = JSON.parse("{action:'doa', data:{name:{age:{xxxx:111}, yyyy:'1111', zzz:{xxx:zzzz}},    \t\t\t\n\r zzz:{xxx:zzzz}} } ");
        Assert.assertEquals("doa", map1.get("action"));
        Assert.assertEquals("{name:{age:{xxxx:111}, yyyy:'1111', zzz:{xxx:zzzz}},    \t\t\t\n\r zzz:{xxx:zzzz}}", map1.get("data"));
    }
}