package com.rnkrsoft.orm.extractor;

import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.orm.metadata.TableMetadata;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntityExtractorHelperTest {

    @Test
    public void extractTable() {
        EntityExtractorHelper helper = new EntityExtractorHelper();
        TableMetadata metadata = helper.extractTable(UserEntity.class);
        System.out.println(metadata);
    }
}