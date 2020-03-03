package com.rnkrsoft.orm.extractor;

import com.rnkrsoft.logtrace.ErrorContextFactory;
import com.rnkrsoft.orm.annotation.*;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woate on 2020/3/1.
 * 实体信息提取器
 */
@Slf4j
public final class EntityExtractorHelper {
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
            ColumnMetadata columnMetadata = new ColumnMetadata(false, tableMetadata, tableMetadata.getEntityClass(), field, field.getName(), field.getType(), (stringColumn != null ? stringColumn.name() : (numberColumn != null ? numberColumn.name() : dateColumn.name())));
            //提取该字段元信息
            if (!extractor.extractField(columnMetadata)) {
                continue;
            }
            //将最终的字段元信息放入表中
            tableMetadata.addColumn(columnMetadata);
        }
    }
}
