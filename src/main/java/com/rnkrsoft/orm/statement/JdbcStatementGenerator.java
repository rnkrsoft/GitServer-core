package com.rnkrsoft.orm.statement;

import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.orm.annotation.LogicMode;
import com.rnkrsoft.orm.annotation.ValueMode;
import com.rnkrsoft.orm.condition.JdbcCondition;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;

import java.util.Collection;
import java.util.List;
/**
 * Created by woate on 2020/3/1.
 * 执行语句生成器，用于将表元数据和语句类型结合生成带有占位符的SQL语句，借助JDBC预编译机制去执行
 */
public class JdbcStatementGenerator {

    public static void placeholderSql(JdbcStatement statement) {
        JdbcStatementType type = statement.getType();
        if (type == JdbcStatementType.INSERT) {
            insert(statement);
        } else if (type == JdbcStatementType.INSERT_SELECTIVE) {
            insertSelective(statement);
        } else if (type == JdbcStatementType.DELETE_PRIMARY_KEY) {
            deletePrimaryKey(statement);
        } else if (type == JdbcStatementType.DELETE_SELECTIVE) {
            deleteSelective(statement);
        } else if (type == JdbcStatementType.UPDATE_PRIMARY_KEY) {
            updatePrimaryKey(statement);
        } else if (type == JdbcStatementType.UPDATE_PRIMARY_KEY_SELECTIVE) {
            updatePrimaryKeySelective(statement);
        } else if (type == JdbcStatementType.SELECT_SELECTIVE) {
            selectSelective(statement);
        } else if (type == JdbcStatementType.COUNT_SELECTIVE) {
            countSelective(statement);
        } else if (type == JdbcStatementType.PAGINATION_SELECT_SELECTIVE) {
            paginationSelectSelective(statement);
        }
    }


    public static void insert(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        String key = tableMetadata.getTableName() + "@insert";
        Collection<ColumnMetadata> columnMetadataSet = tableMetadata.getColumnMetadataSet().values();
        StringBuilder headerBuffer = new StringBuilder(1024);
        StringBuilder tailBuffer = new StringBuilder(1024);
        int i = 0;
        for (ColumnMetadata columnMetadata : columnMetadataSet) {
            if (i > 0) {
                headerBuffer.append(",");
                tailBuffer.append(",");
            }
            headerBuffer.append(" ").append(columnMetadata.getJdbcName());
            tailBuffer.append(" ?");
            statement.getColumns().add(columnMetadata);
            i++;
        }
        StringBuilder buffer = new StringBuilder(1024);
        buffer.append("insert into");
        buffer.append(" ").append(tableMetadata.getTableName()).append("(");
        buffer.append(headerBuffer);
        buffer.append(") values (");
        buffer.append(tailBuffer);
        buffer.append(")");
        statement.setPlaceholderSql(buffer.toString());
    }

    public static void insertSelective(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        ColumnMetadata[] nonNullColumns = statement.getNonNullColumns();
        StringBuilder headerBuffer = new StringBuilder(1024);
        StringBuilder tailBuffer = new StringBuilder(1024);
        int i = 0;
        for (ColumnMetadata columnMetadata : nonNullColumns) {
            if (i > 0) {
                headerBuffer.append(",");
                tailBuffer.append(",");
            }
            headerBuffer.append(" ").append(columnMetadata.getJdbcName());
            tailBuffer.append(" ?");
            statement.getColumns().add(columnMetadata);
            i++;
        }
        StringBuilder buffer = new StringBuilder(1024);
        buffer.append("insert into");
        buffer.append(" ").append(tableMetadata.getTableName()).append("(");
        buffer.append(headerBuffer);
        buffer.append(") values (");
        buffer.append(tailBuffer);
        buffer.append(")");
        String sql = buffer.toString();
        statement.setPlaceholderSql(sql);
    }

    public static void deletePrimaryKey(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        String key = tableMetadata.getTableName() + "@deletePrimaryKey";
        StringBuilder headerBuffer = new StringBuilder(1024);
        List<ColumnMetadata> primaryKeys = tableMetadata.getPrimaryKeyColumns();
        headerBuffer.append("delete from ").append(tableMetadata.getTableName()).append(" where").append(" ");
        for (int i = 0; i < primaryKeys.size(); i++) {
            ColumnMetadata column = primaryKeys.get(i);
            if (i > 0) {
                headerBuffer.append(" ").append(column.getLogicMode().getCode());
            }
            headerBuffer.append(" ").append(column.getJdbcName()).append(" ").append(column.getValueMode().getCode()).append(" ?");
            statement.getColumns().add(column);
        }
        statement.setPlaceholderSql(headerBuffer.toString());
    }

    public static void deleteSelective(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        ColumnMetadata[] nonNullColumns = statement.getNonNullColumns();
        StringBuilder buffer = new StringBuilder(1024);
        buffer.append("delete from ").append(tableMetadata.getTableName());
        JdbcCondition conditionOverride = statement.condition;
        int columnLength = nonNullColumns.length;
        if (columnLength > 0) {
            buffer.append(" where");
        }
        for (int i = 0; i < columnLength; i++) {
            ColumnMetadata column = nonNullColumns[i];
            buffer.append(" ");
            if (i > 0) {
                buffer.append(logicMode(column, conditionOverride).getCode());
            }
            buffer.append(" ").append(column.getJdbcName()).append(" ").append(valueMode(column, conditionOverride).getCode()).append(" ?");
            statement.getColumns().add(column);
        }
        statement.setPlaceholderSql(buffer.toString());
    }

    public static void updatePrimaryKey(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        String key = tableMetadata.getTableName() + "@updatePrimaryKey";
        StringBuilder headerBuffer = new StringBuilder(1024);
        List<String> nonPrimaryKeyColumns = tableMetadata.getNonPrimaryKeyColumnNameList();
        List<ColumnMetadata> primaryKeys = tableMetadata.getPrimaryKeyColumns();
        headerBuffer.append("update ").append(tableMetadata.getTableName()).append(" set");
        int count = 0;
        for (String name : nonPrimaryKeyColumns) {
            ColumnMetadata columnMetadata = tableMetadata.getColumn(name);
            if (count > 0) {
                headerBuffer.append(",");
            }
            headerBuffer.append(" ").append(columnMetadata.getJdbcName()).append(" = ? ");
            statement.getColumns().add(columnMetadata);
            count++;
        }
        if (!primaryKeys.isEmpty()) {
            headerBuffer.append(" where");
            for (int i = 0; i < primaryKeys.size(); i++) {
                ColumnMetadata column = primaryKeys.get(i);
                headerBuffer.append(" ");
                if (i > 0) {
                    headerBuffer.append(column.getLogicMode().getCode());
                }
                headerBuffer.append(column.getJdbcName()).append(" ").append(column.getValueMode().getCode()).append(" ?");
                statement.getColumns().add(column);
            }
        }
        statement.setPlaceholderSql(headerBuffer.toString());
    }


    public static void updatePrimaryKeySelective(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        ColumnMetadata[] nonNullColumns = statement.getNonNullColumns();
        StringBuilder buffer = new StringBuilder(1024);
        List<ColumnMetadata> primaryKeys = tableMetadata.getPrimaryKeyColumns();
        buffer.append("update ").append(tableMetadata.getTableName()).append(" set");
        int count = 0;
        for (ColumnMetadata columnMetadata : nonNullColumns) {
            if (primaryKeys.contains(columnMetadata)) {
                continue;
            }
            if (count > 0) {
                buffer.append(",");
            }
            buffer.append(" ").append(columnMetadata.getJdbcName()).append(" = ? ");
            statement.getColumns().add(columnMetadata);
            count++;
        }
        if (!primaryKeys.isEmpty()) {
            buffer.append(" where");
            for (int i = 0; i < primaryKeys.size(); i++) {
                ColumnMetadata column = primaryKeys.get(i);
                buffer.append(" ");
                if (i > 0) {
                    buffer.append(column.getLogicMode().getCode());
                }
                buffer.append(column.getJdbcName()).append(" ").append(column.getValueMode().getCode()).append(" ?");
                statement.getColumns().add(column);
            }
        }
        statement.setPlaceholderSql(buffer.toString());
    }

    public static void selectSelective(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        ColumnMetadata[] nonNullColumns = statement.getNonNullColumns();
        Collection<ColumnMetadata> columnMetadataSet = tableMetadata.getColumnMetadataSet().values();
        JdbcCondition conditionOverride = statement.condition;
        StringBuilder buffer = new StringBuilder(1024);
        buffer.append("select");
        int count = 0;
        for (ColumnMetadata columnMetadata : columnMetadataSet) {
            if (count > 0) {
                buffer.append(",");
            }
            buffer.append(" ").append(columnMetadata.getJdbcName());
            count++;
        }
        buffer.append(" from ").append(tableMetadata.getTableName());
        if (nonNullColumns.length > 0) {
            buffer.append(" where");
            for (int i = 0; i < nonNullColumns.length; i++) {
                ColumnMetadata columnMetadata = nonNullColumns[i];
                if (i > 0) {
                    buffer.append(" ").append(logicMode(columnMetadata, conditionOverride).getCode());
                }
                buffer.append(" ").append(columnMetadata.getJdbcName()).append(" ").append(valueMode(columnMetadata, conditionOverride).getCode()).append(" ?");
                statement.getColumns().add(columnMetadata);
            }
        }
        statement.setPlaceholderSql(buffer.toString());
    }

    static void countSelective(JdbcStatement statement) {
        TableMetadata tableMetadata = statement.getTableMetadata();
        ColumnMetadata[] nonNullColumns = statement.getNonNullColumns();
        Collection<ColumnMetadata> columnMetadataSet = tableMetadata.getColumnMetadataSet().values();
        JdbcCondition conditionOverride = statement.condition;
        StringBuilder buffer = new StringBuilder(1024);
        buffer.append("select count(1)");
        buffer.append(" from ").append(tableMetadata.getTableName());
        if (nonNullColumns.length > 0) {
            buffer.append(" where");
            for (int i = 0; i < nonNullColumns.length; i++) {
                ColumnMetadata columnMetadata = nonNullColumns[i];
                if (i > 0) {
                    buffer.append(" ").append(logicMode(columnMetadata, conditionOverride).getCode());
                }
                buffer.append(" ").append(columnMetadata.getJdbcName()).append(" ").append(valueMode(columnMetadata, conditionOverride).getCode()).append(" ?");
                statement.getColumns().add(columnMetadata);
            }
        }
        statement.setPlaceholderSql(buffer.toString());
    }

    static void paginationSelectSelective(JdbcStatement statement) {
        Pagination pagination = statement.getPagination();
        TableMetadata tableMetadata = statement.getTableMetadata();
        ColumnMetadata[] nonNullColumns = statement.getNonNullColumns();
        Collection<ColumnMetadata> columnMetadataSet = tableMetadata.getColumnMetadataSet().values();
        JdbcCondition conditionOverride = statement.condition;
        StringBuilder buffer = new StringBuilder(1024);
        buffer.append("select");
        int count = 0;
        for (ColumnMetadata columnMetadata : columnMetadataSet) {
            if (count > 0) {
                buffer.append(",");
            }
            buffer.append(" ").append(columnMetadata.getJdbcName());
            count++;
        }
        buffer.append(" from ").append(tableMetadata.getTableName());
        if (nonNullColumns.length > 0) {
            buffer.append(" where");
            for (int i = 0; i < nonNullColumns.length; i++) {
                ColumnMetadata columnMetadata = nonNullColumns[i];
                if (i > 0) {
                    buffer.append(" ").append(logicMode(columnMetadata, conditionOverride).getCode());
                }
                buffer.append(" ").append(columnMetadata.getJdbcName()).append(" ").append(valueMode(columnMetadata, conditionOverride).getCode()).append(" ?");
                statement.getColumns().add(columnMetadata);
            }
        }
        buffer.append(" limit ").append(pagination.getPageSize()).append(" offset ").append(pagination.getSkipRecordNum());
        statement.setPlaceholderSql(buffer.toString());
    }


    static ValueMode valueMode(ColumnMetadata columnMetadata, JdbcCondition condition) {
        if (condition == null) {
            return columnMetadata.getValueMode();
        } else {
            JdbcCondition.JdbcConditionItem conditionItem = condition.item(columnMetadata.getJdbcName());
            return conditionItem == null ? columnMetadata.getValueMode() : conditionItem.getValueMode();
        }
    }

    static LogicMode logicMode(ColumnMetadata columnMetadata, JdbcCondition condition) {
        if (condition == null) {
            return columnMetadata.getLogicMode();
        } else {
            JdbcCondition.JdbcConditionItem conditionItem = condition.item(columnMetadata.getJdbcName());
            return conditionItem == null ? columnMetadata.getLogicMode() : conditionItem.getLogicMode();
        }
    }
}
