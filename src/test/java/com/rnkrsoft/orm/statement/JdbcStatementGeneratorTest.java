package com.rnkrsoft.orm.statement;

import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.orm.extractor.EntityExtractorHelper;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import org.junit.Test;

public class JdbcStatementGeneratorTest {

    @Test
    public void testInsert() {
        TableMetadata tableMetadata = new EntityExtractorHelper().extractTable(UserEntity.class);
        ColumnMetadata[] columnMetadatas = (ColumnMetadata[]) tableMetadata.getColumnMetadataSet().values().toArray(new ColumnMetadata[tableMetadata.getColumnMetadataSet().size()]);
        UserEntity entity = new UserEntity();
        entity.setUsername("test1");
        entity.setPassword("ssssssssssss");
        JdbcStatement statement = new JdbcStatement(JdbcStatementType.INSERT, tableMetadata, columnMetadatas, null, entity);
        JdbcStatementGenerator.insert(statement);
        System.out.println(statement.getPlaceholderSql());
        System.out.println(statement.getValues());
    }

    @Test
    public void insertSelective() {
        TableMetadata tableMetadata = new EntityExtractorHelper().extractTable(UserEntity.class);
        UserEntity entity = new UserEntity();
        entity.setUsername("test1");
        entity.setPassword("ssssssssssss");
        JdbcStatement statement = new JdbcStatement(JdbcStatementType.INSERT_SELECTIVE, tableMetadata, new ColumnMetadata[]{tableMetadata.getColumn("username"), tableMetadata.getColumn("password")}, null, entity);
        JdbcStatementGenerator.insertSelective(statement);
        System.out.println(statement.getPlaceholderSql());
        System.out.println(statement.getValues());
    }

    @Test
    public void deletePrimaryKey() {
        TableMetadata tableMetadata = new EntityExtractorHelper().extractTable(UserEntity.class);
        UserEntity entity = new UserEntity();
        entity.setUsername("test1");
        entity.setPassword("ssssssssssss");
        JdbcStatement statement = new JdbcStatement(JdbcStatementType.DELETE_PRIMARY_KEY, tableMetadata, null, null, entity);
        JdbcStatementGenerator.deletePrimaryKey(statement);
        System.out.println(statement.getPlaceholderSql());
        System.out.println(statement.getValues());
    }

    @Test
    public void deleteSelective() {
        TableMetadata tableMetadata = new EntityExtractorHelper().extractTable(UserEntity.class);
        StringBuilder builder = new StringBuilder();
        UserEntity entity = new UserEntity();
        entity.setUsername("test1");
        entity.setPassword("ssssssssssss");
        JdbcStatement statement = new JdbcStatement(JdbcStatementType.DELETE_SELECTIVE, tableMetadata, new ColumnMetadata[]{tableMetadata.getColumn("username"), tableMetadata.getColumn("password")}, null, entity);
        JdbcStatementGenerator.deleteSelective(statement);
        System.out.println(statement.getPlaceholderSql());
        System.out.println(statement.getValues());
    }

    @Test
    public void updatePrimaryKey() {
        TableMetadata tableMetadata = new EntityExtractorHelper().extractTable(UserEntity.class);
        UserEntity entity = new UserEntity();
        entity.setUsername("test1");
        entity.setPassword("ssssssssssss");
        JdbcStatement statement = new JdbcStatement(JdbcStatementType.UPDATE_PRIMARY_KEY, tableMetadata, null, null, entity);
        JdbcStatementGenerator.updatePrimaryKey(statement);
        System.out.println(statement.getPlaceholderSql());
        System.out.println(statement.getValues());
    }

    @Test
    public void updatePrimaryKeySelective() {
        TableMetadata tableMetadata = new EntityExtractorHelper().extractTable(UserEntity.class);
        StringBuilder builder = new StringBuilder();
        UserEntity entity = new UserEntity();
        entity.setUsername("test1");
        entity.setPassword("ssssssssssss");
        JdbcStatement statement = new JdbcStatement(JdbcStatementType.UPDATE_PRIMARY_KEY_SELECTIVE, tableMetadata, new ColumnMetadata[]{tableMetadata.getColumn("username"), tableMetadata.getColumn("password")}, null, entity);
        JdbcStatementGenerator.updatePrimaryKeySelective(statement);
        System.out.println(statement.getPlaceholderSql());
        System.out.println(statement.getValues());
    }

    @Test
    public void testSelectSelective() {
        TableMetadata tableMetadata = new EntityExtractorHelper().extractTable(UserEntity.class);
        StringBuilder builder = new StringBuilder();
        UserEntity entity = new UserEntity();
        entity.setPassword("ssssssssssss");
        JdbcStatement statement = new JdbcStatement(JdbcStatementType.SELECT_SELECTIVE, tableMetadata, new ColumnMetadata[]{tableMetadata.getColumn("password")}, null, entity);
        JdbcStatementGenerator.selectSelective(statement);
        System.out.println(statement.getPlaceholderSql());
        System.out.println(statement.getValues());
    }
}