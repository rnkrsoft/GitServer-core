package com.rnkrsoft.orm.generator;

import com.rnkrsoft.orm.JdbcStatementType;
import com.rnkrsoft.orm.Pagination;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    final List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();

    public JdbcStatement(JdbcStatementType type, TableMetadata tableMetadata, ColumnMetadata[] nonNullColumns, Object obj) {
        this.type = type;
        this.tableMetadata = tableMetadata;
        this.nonNullColumns = nonNullColumns;
        if (obj instanceof Pagination){
            pagination = (Pagination)obj;
            entity = pagination.getEntity();
        }else{
            entity = obj;
        }
    }

    public Object[] getValues(){
        Object[] values = new Object[columns.size()];
        int columnSize = columns.size();
        for (int i = 0; i < columnSize; i++) {
            values[i] =  columns.get(i).getValue(entity);
        }
        return values;
    }
}
