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
    @Getter
    JdbcStatementType type;
    @Getter
    TableMetadata tableMetadata;
    @Getter
    Object entity;
    @Getter
    Pagination pagination;
    @Getter
    final ColumnMetadata[] nonNullColumns;
    @Getter
    @Setter
    String placeholderSql;
    @Getter
    JdbcCondition condition;
    @Getter
    final List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();

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

    public Object[] getValues() {
        Object[] values = new Object[columns.size()];
        int columnSize = columns.size();
        for (int i = 0; i < columnSize; i++) {
            values[i] = columns.get(i).getValue(entity);
        }
        return values;
    }
}
