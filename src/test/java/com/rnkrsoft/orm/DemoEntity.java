package com.rnkrsoft.orm;

import com.rnkrsoft.orm.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Comment("演示表")
@Table(name = "demo")
public class DemoEntity {
    @PrimaryKey(strategy = PrimaryKeyStrategy.NONE)
    @StringColumn(name = "name", nullable = false, length = 36)
    @Comment("用户名")
    String name;
    @NumberColumn(name = "age", defaultValue = "1", nullable = false)
    @Comment("年龄")
    Integer age;

    @NumberColumn(name = "sex", defaultValue = "1", nullable = false)
    @Comment("性别")
    Integer sex;

    public DemoEntity() {
    }

    public DemoEntity(String name, Integer age, Integer sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
}
