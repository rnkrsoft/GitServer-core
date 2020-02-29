package com.rnkrsoft.orm.generator;

import com.rnkrsoft.orm.JdbcStatementType;
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
    final ColumnMetadata[] nonNullColumns;
    @Getter
    @Setter
    String placeholderSql;
    @Getter
    final List<Object> values = new ArrayList<Object>();
    @Getter
    final List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();

    public JdbcStatement(JdbcStatementType type, TableMetadata tableMetadata, ColumnMetadata[] nonNullColumns, Object entity) {
        this.type = type;
        this.tableMetadata = tableMetadata;
        this.nonNullColumns = nonNullColumns;
        this.entity = entity;
    }
}
