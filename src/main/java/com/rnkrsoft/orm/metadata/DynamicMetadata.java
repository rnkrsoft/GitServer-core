package com.rnkrsoft.orm.metadata;

import com.rnkrsoft.orm.metadata.ColumnMetadata;
import lombok.Getter;

@Getter
public class DynamicMetadata {
    String placeholderSql;
    ColumnMetadata[] columns;
    Object[] values;

    public DynamicMetadata(String placeholderSql, ColumnMetadata[] columns, Object[] values) {
        this.placeholderSql = placeholderSql;
        this.columns = columns;
        this.values = values;
    }
}
