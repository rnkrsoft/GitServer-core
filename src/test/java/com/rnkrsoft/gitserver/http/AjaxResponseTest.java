package com.rnkrsoft.gitserver.http;

import org.junit.Test;

import static org.junit.Assert.*;

public class AjaxResponseTest {
    static class DemoBean1{
        String name;
        int age;

        public DemoBean1(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
    @Test
    public void testToString() {

        AjaxResponse response = AjaxResponse.SUCCESS;
        response.setData(new DemoBean1("asffg",  21));
        System.out.println(response.toString());
    }
}