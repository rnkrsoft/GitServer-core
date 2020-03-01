package com.rnkrsoft.orm.extractor;

import com.rnkrsoft.logtrace.ErrorContextFactory;
import com.rnkrsoft.orm.JdbcStatementType;
import com.rnkrsoft.orm.Pagination;
import com.rnkrsoft.orm.annotation.*;
import com.rnkrsoft.orm.generator.JdbcStatement;
import com.rnkrsoft.orm.generator.JdbcStatementGenerator;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 实体信息提取器
 */
@Slf4j
public final class EntityExtractorHelper {
    static Map<String, JdbcStatement> STATEMENT_CACHE = new HashMap<String, JdbcStatement>();
    EntityExtractor extractor;

    public static final Map<Class, TableMetadata> TABLES_CACHE = new HashMap<Class, TableMetadata>();

    /**
     * 提取实体上的元信息
     *
     * @param entityClass 实体类
     * @return 元信息
     */
    public TableMetadata extractTable(Class entityClass) {
        //如果缓存包含则直接返回
        if (TABLES_CACHE.containsKey(entityClass)) {
            return TABLES_CACHE.get(entityClass);
        }
        Table tableAnnORM = (Table) entityClass.getAnnotation(Table.class);
        if (tableAnnORM == null) {
            throw ErrorContextFactory.instance().activity("提取实体类{}的元信息", entityClass)
                    .message("没使用ORM注解{}", Table.class)
                    .solution("建议使用{}注解", Table.class)
                    .runtimeException();
        }
        TableMetadata tableMetadata = TableMetadata.builder().entityClass(entityClass).entityClassName(entityClass.getSimpleName()).build();

        //解析注解
        if (tableAnnORM != null) {
            extractor = new OrmEntityExtractor();
        }
        extractor.extractTable(tableMetadata);
        //提取字段
        extractFields(tableMetadata);
        if (tableMetadata.getPrimaryKeyColumns().isEmpty()) {
            throw ErrorContextFactory.instance()
                    .activity("提取实体类{}的元信息", entityClass)
                    .message("不允许无物理主键的实体")
                    .solution("建议在主键字段标注{}注解", PrimaryKey.class)
                    .runtimeException();
        }
        return tableMetadata;
    }

    /**
     * 提取实体类的所有字段
     *
     * @param tableMetadata 表元信息
     */
    public void extractFields(TableMetadata tableMetadata) {
        Class entityClass = tableMetadata.getEntityClass();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            StringColumn stringColumn = field.getAnnotation(StringColumn.class);
            NumberColumn numberColumn = field.getAnnotation(NumberColumn.class);
            DateColumn dateColumn = field.getAnnotation(DateColumn.class);
            if (stringColumn == null && numberColumn == null && dateColumn == null) {
                return;
            }
            //任意使用了一个字段注解的
            ColumnMetadata columnMetadata = ColumnMetadata.builder()
                    .tableMetadata(tableMetadata)
                    .entityClass(tableMetadata.getEntityClass())
                    .columnField(field)
                    .javaName(field.getName())
                    .javaType(field.getType())
                    .build();
            //提取该字段元信息
            if (!extractor.extractField(columnMetadata)) {
                continue;
            }
            //将最终的字段元信息放入表中
            tableMetadata.addColumn(columnMetadata);
        }
    }

    public JdbcStatement dynamic(TableMetadata tableMetadata, JdbcStatementType type, Object obj) {
        JdbcStatement statement = null;
        if (type == JdbcStatementType.INSERT || type == JdbcStatementType.DELETE_PRIMARY_KEY || type == JdbcStatementType.UPDATE_PRIMARY_KEY){
            statement = STATEMENT_CACHE.get(tableMetadata.getTableName() + "@" + type.getCode());
        }
        if (statement != null){
            return statement;
        }
        Collection<ColumnMetadata> columnMetadataList = tableMetadata.getColumnMetadataSet().values();
        int maxColumnSize = columnMetadataList.size();
        ColumnMetadata[] noneNullColumns = new ColumnMetadata[maxColumnSize];
        int i = 0;
        Object entity = null;
        if (obj instanceof Pagination){
            entity = ((Pagination)obj).getEntity();
        }else{
            entity = obj;
        }
        for (ColumnMetadata columnMetadata : columnMetadataList) {
            Object val = columnMetadata.getValue(entity);
            if (val == null) {
                continue;
            }
            noneNullColumns[i] = columnMetadata;
            i++;
        }
        noneNullColumns = Arrays.copyOf(noneNullColumns, i);
        if (statement == null){
            statement = new JdbcStatement(type, tableMetadata, noneNullColumns, obj);
            JdbcStatementGenerator.placeholderSql(statement);
        }
        return statement;
    }
}
