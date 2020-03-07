package com.rnkrsoft.gitserver.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import static org.junit.Assert.*;

public class AjaxRequestTest {
    static class DemoBean1 {
        String name;
        int age;

        public DemoBean1(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "DemoBean1{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    static class DemoBean2 {
        String name;

        public DemoBean2(String name) {
            this.name = name;
        }
    }

    @Test
    public void getData() {
        Gson gson = new GsonBuilder().create();
        AjaxRequest request = new AjaxRequest("register_user_action", gson.toJson(new DemoBean1("123", 21)));
        String json = gson.toJson(request);

        AjaxRequest request1 = new GsonBuilder().create().fromJson(json, AjaxRequest.class);

        System.out.println(request1.getData());
    }
}