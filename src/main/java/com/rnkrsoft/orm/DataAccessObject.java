package com.rnkrsoft.orm;

import com.rnkrsoft.logtrace.TraceableRuntimeException;
import com.rnkrsoft.orm.extractor.EntityExtractorHelper;
import com.rnkrsoft.orm.generator.JdbcStatement;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.DynamicMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 * Orm 数据访问对象
 * Created by woate on 2020/02/24.
 */
public class DataAccessObject<T> {
    Orm orm;
    TableMetadata tableMetadata;

    public DataAccessObject(Orm orm, TableMetadata tableMetadata) {
        this.orm = orm;
        this.tableMetadata = tableMetadata;
    }


    public int insert(T entity) {
        JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.INSERT, entity);
        try {
            return this.orm.executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException("insert happens error!", e);
        }
    }

    public int insertSelective(T entity) {
        JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.INSERT, entity);
        try {
            return this.orm.executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException("insertSelective happens error!", e);
        }
    }

    public int updateByPrimaryKeySelective(T entity) {
        JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.INSERT, entity);
        try {
            return this.orm.executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException("updateByPrimaryKeySelective happens error!", e);
        }
    }

    public int updateByPrimaryKey(T entity) {
        JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.UPDATE_PRIMARY_KEY, entity);
        try {
            return this.orm.executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException("updateByPrimaryKey happens error!", e);
        }
    }

    public int deleteByPrimaryKey(T entity) {
        JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.DELETE_PRIMARY_KEY, entity);
        try {
            return this.orm.executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException("deleteByPrimaryKey happens error!", e);
        }
    }

    public int delete(T entity, Object logicMode) {
        JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.INSERT, entity);
        try {
            return this.orm.executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException("insert happens error!", e.getCause());
        }
    }

    public List<T> selectSelective(T entity) {
        final JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.SELECT_SELECTIVE, entity);
        try {
            return this.orm.executeQuery(statement, new Orm.RowMapper<T>() {
                @Override
                public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                    T obj = (T) tableMetadata.newObject();
                    List<String> columns = tableMetadata.getColumns();
                    for (String columnName : columns) {
                        ColumnMetadata metadata = tableMetadata.getColumn(columnName);
                        Object val = rs.getString(metadata.getJdbcName());
                        if (metadata.getJdbcType() == SupportedJdbcType.VARCHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.CHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.LONGVARCHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.TEXT) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.SMALLINT) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.INT) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.INTEGER) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.BIGINT) {
                            val = rs.getLong(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.DATE) {
                            java.sql.Date date = rs.getDate(metadata.getJdbcName());
                            val = (java.util.Date) date;
                        } else if (metadata.getJdbcType() == SupportedJdbcType.TIMESTAMP) {
                            java.sql.Timestamp timestamp = rs.getTimestamp(metadata.getJdbcName());
                            val = (java.util.Date) timestamp;
                        } else if (metadata.getJdbcType() == SupportedJdbcType.DATETIME) {
                            java.sql.Time time = rs.getTime(metadata.getJdbcName());
                            val = (java.util.Date) time;
                        } else {
                            val = rs.getString(metadata.getJdbcName());
                        }
                        metadata.setValue(obj, val);
                    }
                    return obj;
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("selectSelective happens error!", e);
        }
    }

    public T selectByPrimaryKey(T entity) {
        final JdbcStatement statement = this.orm.entityExtractorHelper.dynamic(tableMetadata, JdbcStatementType.SELECT_SELECTIVE, entity);
        try {
            List<T> result = this.orm.executeQuery(statement, new Orm.RowMapper<T>() {
                @Override
                public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                    T obj = (T) tableMetadata.newObject();
                    List<String> columns = tableMetadata.getColumns();
                    for (String columnName : columns) {
                        ColumnMetadata metadata = tableMetadata.getColumn(columnName);
                        Object val = rs.getString(metadata.getJdbcName());
                        if (metadata.getJdbcType() == SupportedJdbcType.VARCHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.CHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.LONGVARCHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.TEXT) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.SMALLINT) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.INT) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.INTEGER) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.BIGINT) {
                            val = rs.getLong(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.DATE) {
                            java.sql.Date date = rs.getDate(metadata.getJdbcName());
                            val = (java.util.Date) date;
                        } else if (metadata.getJdbcType() == SupportedJdbcType.TIMESTAMP) {
                            java.sql.Timestamp timestamp = rs.getTimestamp(metadata.getJdbcName());
                            val = (java.util.Date) timestamp;
                        } else if (metadata.getJdbcType() == SupportedJdbcType.DATETIME) {
                            java.sql.Time time = rs.getTime(metadata.getJdbcName());
                            val = (java.util.Date) time;
                        } else {
                            val = rs.getString(metadata.getJdbcName());
                        }
                        metadata.setValue(obj, val);
                    }
                    return obj;
                }
            });
            int size = result.size();
            if (size == 0) {
                return null;
            } else if (size == 1) {
                return result.get(0);
            } else {
                throw new SQLException("too many record...");
            }
        } catch (SQLException e) {
            throw new RuntimeException("selectByPrimaryKey happens error!", e);
        }
    }

    public Pagination<T> query(Pagination<T> pagination) {
        JdbcStatement countStatement = null;
//                this.orm.entityExtractorHelper.count(tableMetadata, JdbcStatementType.SELECT_SELECTIVE, pagination);
        JdbcStatement selectStatement = null;
//                this.orm.entityExtractorHelper.select(tableMetadata, JdbcStatementType.SELECT_SELECTIVE, pagination);
        //fixme 1。构建统计记录的sql语句，并执行
        Integer total = null;
        try {
            total = this.orm.executeQueryOne(countStatement, new Orm.RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt(1);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("query happens error!", e);
        }
        //fixme 2. 构建查询记录的sql语句，并执行
        List<T> records = null;
        try {
            records = this.orm.executeQuery(selectStatement, new Orm.RowMapper<T>() {
                @Override
                public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                    T obj = (T) tableMetadata.newObject();
                    List<String> columns = tableMetadata.getColumns();
                    for (String columnName : columns) {
                        ColumnMetadata metadata = tableMetadata.getColumn(columnName);
                        Object val = rs.getString(metadata.getJdbcName());
                        if (metadata.getJdbcType() == SupportedJdbcType.VARCHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.CHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.LONGVARCHAR) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.TEXT) {
                            val = rs.getString(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.SMALLINT) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.INT) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.INTEGER) {
                            val = rs.getInt(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.BIGINT) {
                            val = rs.getLong(metadata.getJdbcName());
                        } else if (metadata.getJdbcType() == SupportedJdbcType.DATE) {
                            java.sql.Date date = rs.getDate(metadata.getJdbcName());
                            val = (java.util.Date) date;
                        } else if (metadata.getJdbcType() == SupportedJdbcType.TIMESTAMP) {
                            java.sql.Timestamp timestamp = rs.getTimestamp(metadata.getJdbcName());
                            val = (java.util.Date) timestamp;
                        } else if (metadata.getJdbcType() == SupportedJdbcType.DATETIME) {
                            java.sql.Time time = rs.getTime(metadata.getJdbcName());
                            val = (java.util.Date) time;
                        } else {
                            val = rs.getString(metadata.getJdbcName());
                        }
                        metadata.setValue(obj, val);
                    }
                    return obj;
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("query happens error!", e);
        }
        pagination.setTotal(total);
        pagination.setRecords(records);
        return pagination;
    }

    public Pagination<T> query(Pagination<T> pagination, String sqlPlaceholder) {
        return pagination;
    }
}
