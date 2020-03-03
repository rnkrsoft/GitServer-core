package com.rnkrsoft.gitserver.http;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class JSONTest {

    @Test
    public void parse() {
        Map<String, String> map = JSON.parse("{action:'doa', data:{name:'1', age:234,address:'ssfds'}}");
        Assert.assertEquals("doa", map.get("action"));
        Assert.assertEquals("{name:'1', age:234,address:'ssfds'}", map.get("data"));
    }
}