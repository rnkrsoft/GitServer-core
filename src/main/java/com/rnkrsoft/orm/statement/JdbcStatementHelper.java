package com.rnkrsoft.orm.statement;

import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.orm.condition.JdbcCondition;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woate on 2020/3/1.
 * 语句帮助类，用于统一管理语句的生成机制
 */
public final class JdbcStatementHelper {
    static Map<String, JdbcStatement> STATEMENT_CACHE = new HashMap<String, JdbcStatement>();

    /**
     * 根据传入的语句类型和条件覆盖对象生成语句对象
     *
     * @param tableMetadata     表元信息
     * @param type              语句类型
     * @param condition 条件覆盖对象，如果传入了，则语句对象将不能使用缓存
     * @param obj               对象
     * @return 语句对象
     */
    public static JdbcStatement statement(TableMetadata tableMetadata, JdbcStatementType type, JdbcCondition condition, Object obj) {
        JdbcStatement statement = null;
        //条件覆盖对象为null时才能使用缓存的语句对象
        if (condition == null) {
            if (type == JdbcStatementType.INSERT || type == JdbcStatementType.DELETE_PRIMARY_KEY || type == JdbcStatementType.UPDATE_PRIMARY_KEY) {
                statement = STATEMENT_CACHE.get(tableMetadata.getTableName() + "@" + type.getCode());
            }
        }
        if (statement != null) {
            return statement;
        }
        Collection<ColumnMetadata> columnMetadataList = tableMetadata.getColumnMetadataSet().values();
        int maxColumnSize = columnMetadataList.size();
        ColumnMetadata[] noneNullColumns = new ColumnMetadata[maxColumnSize];
        int i = 0;
        Object entity = null;
        if (obj instanceof Pagination) {
            entity = ((Pagination) obj).getEntity();
        } else {
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
        if (statement == null) {
            statement = new JdbcStatement(type, tableMetadata, noneNullColumns, condition, obj);
            JdbcStatementGenerator.placeholderSql(statement);
        }
        return statement;
    }
}
