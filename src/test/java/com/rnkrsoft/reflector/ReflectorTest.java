package com.rnkrsoft.reflector;

import com.rnkrsoft.orm.DemoEntity;
import org.junit.Assert;
import org.junit.Test;

public class ReflectorTest {
    @Test
    public void benchmark() throws Exception {
        for (int i = 0; i < 100000; i++) {
            DemoEntity entity1 = DemoEntity.builder().name("test1").build();
            Assert.assertEquals("test1", entity1.getName());
            DemoEntity entity2 = DemoEntity.builder().name("test1").build();
            entity2.setName("test2");
            Assert.assertEquals("test2", entity2.getName());
        }

    }
    @Test
    public void getMethod() throws Exception {
        Reflector reflector = Reflector.reflector(DemoEntity.class);
        for (int i = 0; i < 100000; i++) {
            DemoEntity entity1 = DemoEntity.builder().name("test1").build();
            Reflector.Invoker getter = reflector.getGetter("name");
            Assert.assertEquals("test1", getter.invoke(entity1));
            DemoEntity entity2 = reflector.newInstance();
            Reflector.Invoker setter = reflector.getSetter("name");
            setter.invoke(entity2, "test2");
            Assert.assertEquals("test2", entity2.getName());
        }

    }

    @Test
    public void getMethod2() throws Exception {
        Reflector reflector = Reflector.reflector(Demo.class);
        for (int i = 0; i < 100000; i++) {
            Demo entity1 =  new Demo("test1");
            Reflector.Invoker getter = reflector.getGetter("name");
            Assert.assertEquals("test1", getter.invoke(entity1));
            Demo entity2  = reflector.newInstance();
            Reflector.Invoker setter = reflector.getSetter("name");
            setter.invoke(entity2, "test2");
            Assert.assertEquals("test2", getter.invoke(entity2));
        }

    }
    static class Demo{
        String name;

        public Demo() {
        }

        public Demo(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}