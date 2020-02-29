package com.rnkrsoft.orm.util;

import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.orm.annotation.NameMode;
import com.rnkrsoft.orm.annotation.WordMode;
import org.junit.Test;

import static org.junit.Assert.*;

public class SqlScriptUtilsTest {

    @Test
    public void generateCreateTable() {
        String sql = SqlScriptUtils.generateCreateTable(UserEntity.class, NameMode.auto, null, NameMode.auto, null, NameMode.auto, null, null, WordMode.lowerCase, WordMode.lowerCase, false);
        System.out.println(sql);
    }
}