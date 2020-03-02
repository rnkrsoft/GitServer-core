package com.rnkrsoft.orm.statement;

import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.orm.condition.JdbcCondition;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by woate on 2020/3/1.
 * 执行语句抽象的对象形式，包含了执行需要的全部信息
 */
public class JdbcStatement {
    /**
     * 语句类型
     */
    @Getter
    JdbcStatementType type;
    /**
     * 表元信息
     */
    @Getter
    TableMetadata tableMetadata;
    /**
     * 实体类
     */
    @Getter
    Object entity;
    /**
     * 分页对象
     */
    @Getter
    Pagination pagination;
    /**
     * 非NULL的实体类上的字段元数据
     */
    @Getter
    final ColumnMetadata[] nonNullColumns;
    /**
     * 带占位符的SQL语句字符串
     */
    @Getter
    @Setter
    String placeholderSql;
    /**
     * 条件对象
     */
    @Getter
    JdbcCondition condition;
    /**
     * 占位符字段元信息
     */
    @Getter
    final List<ColumnMetadata> placeholderColumns = new ArrayList<ColumnMetadata>();

    public JdbcStatement(JdbcStatementType type, TableMetadata tableMetadata, ColumnMetadata[] nonNullColumns, JdbcCondition condition, Object obj) {
        this.type = type;
        this.tableMetadata = tableMetadata;
        this.nonNullColumns = nonNullColumns;
        this.condition = condition;
        if (obj instanceof Pagination) {
            pagination = (Pagination) obj;
            entity = pagination.getEntity();
        } else {
            entity = obj;
        }
    }

    /**
     * 获取占位符对应的值数组
     * @return 值数组
     */
    public Object[] getPlaceholderValues() {
        Object[] values = new Object[placeholderColumns.size()];
        int columnSize = placeholderColumns.size();
        for (int i = 0; i < columnSize; i++) {
            values[i] = placeholderColumns.get(i).getValue(entity);
        }
        return values;
    }
}
