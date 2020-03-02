package com.rnkrsoft.orm.dao;

import com.rnkrsoft.orm.Orm;
import com.rnkrsoft.orm.OrmSetting;
import com.rnkrsoft.orm.annotation.NameMode;
import com.rnkrsoft.orm.annotation.WordMode;
import com.rnkrsoft.orm.condition.JdbcCondition;
import com.rnkrsoft.orm.entity.Pagination;
import com.rnkrsoft.orm.extractor.EntityExtractorHelper;
import com.rnkrsoft.orm.jdbc.SupportedJdbcType;
import com.rnkrsoft.orm.jdbc.executor.JdbcExecutor;
import com.rnkrsoft.orm.jdbc.executor.SimpleJdbcExecutor;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import com.rnkrsoft.orm.session.SessionFactory;
import com.rnkrsoft.orm.statement.JdbcStatement;
import com.rnkrsoft.orm.statement.JdbcStatementHelper;
import com.rnkrsoft.orm.statement.JdbcStatementType;
import com.rnkrsoft.orm.util.SqlScriptUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orm 数据访问对象
 * Created by woate on 2020/02/24.
 */
public class DataAccessObject<T> {
    JdbcExecutor jdbcExecutor;
    TableMetadata tableMetadata;

    final static EntityExtractorHelper entityExtractorHelper = new EntityExtractorHelper();
    final static Map<Class, TableMetadata> tableMetadataMap = new HashMap<Class, TableMetadata>();
    final static Map<Class, DataAccessObject> dataAccessObjects = new HashMap<Class, DataAccessObject>();

    public static <T> DataAccessObject<T> dao(Class<T> entityClass){
        return dataAccessObjects.get(entityClass);
    }
    /**
     * 初始化
     * @param setting
     */
    public static void init(OrmSetting setting){
        scan(setting.getEntityClasses());
    }
    /**
     * 扫描实体类对象
     *
     * @param entityClasses 实体类列表
     */
   public static void scan(Set<Class> entityClasses) {
        for (Class entityClass : entityClasses) {
            TableMetadata tableMetadata = entityExtractorHelper.extractTable(entityClass);
            DataAccessObject dao = new DataAccessObject( new SimpleJdbcExecutor(), tableMetadata);
            dataAccessObjects.put(entityClass, dao);
            tableMetadataMap.put(entityClass, tableMetadata);
        }
    }
    /**
     * 一个用于将结果转换成JavaBean的行映射器
     */
    private final Orm.RowMapper JAVABEAN_ROW_MAPPER = new Orm.RowMapper<T>() {
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
    };

    public DataAccessObject(JdbcExecutor jdbcExecutor, TableMetadata tableMetadata) {
        this.jdbcExecutor = jdbcExecutor;
        this.tableMetadata = tableMetadata;
    }

    /**
     * 创建一个实体类对应的表
     *
     * @return 返回影响记录条数应该总为1，执行失败抛出异常
     */
    public int createTable() {
        String sql = SqlScriptUtils.generateCreateTable(tableMetadata.getEntityClass(), NameMode.auto, null, NameMode.auto, null, NameMode.auto, null, null, WordMode.lowerCase, WordMode.lowerCase, false);
        try {
            return this.jdbcExecutor.executeUpdate(SessionFactory.getSession(), sql);
        } catch (SQLException e) {
            throw new RuntimeException("insert happens error!", e);
        }
    }

    /**
     * 删除一个实体类对应的表
     *
     * @return 返回影响记录条数应该总为1，执行失败抛出异常
     */
    public int dropTable() {
        String sql = SqlScriptUtils.generateDropTable(tableMetadata.getEntityClass(), NameMode.auto, null, NameMode.auto, null, NameMode.auto, null, WordMode.lowerCase, WordMode.lowerCase, false);
        try {
            return this.jdbcExecutor.executeUpdate(SessionFactory.getSession(), sql);
        } catch (SQLException e) {
            throw new RuntimeException("insert happens error!", e);
        }
    }

    /**
     * 将一个实体记录插入到数据库
     *
     * @param entity 实体类
     * @return 影响条数应该为1，如果插入失败返回0或者抛出异常
     * @see #insertSelective(Object)
     */
    public int insert(T entity) {
        JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.INSERT, null, entity);
        try {
            return this.jdbcExecutor.executeUpdate(SessionFactory.getSession(), statement);
        } catch (SQLException e) {
            throw new RuntimeException("insert happens error!", e);
        }
    }

    /**
     * 将一个实体记录非null的字段值插入到数据库
     *
     * @param entity 实体类
     * @return 影响条数应该为1，如果插入失败返回0或者抛出异常
     * @see #insert(Object)
     */
    public int insertSelective(T entity) {
        JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.INSERT_SELECTIVE, null, entity);
        try {
            return this.jdbcExecutor.executeUpdate(SessionFactory.getSession(), statement);
        } catch (SQLException e) {
            throw new RuntimeException("insertSelective happens error!", e);
        }
    }

    /**
     * 更新一个拥有主键值的实体中所有字段的值到数据库
     *
     * @param entity 实体类
     * @return 影响条数应该为1，如果插入失败返回0或者抛出异常
     * @see #updateByPrimaryKeySelective(Object)
     */
    public int updateByPrimaryKey(T entity) {
        JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.UPDATE_PRIMARY_KEY, null, entity);
        try {
            return this.jdbcExecutor.executeUpdate(SessionFactory.getSession(), statement);
        } catch (SQLException e) {
            throw new RuntimeException("updateByPrimaryKey happens error!", e);
        }
    }

    /**
     * 更新一个拥有主键值的实体中非null字段的值到数据库
     *
     * @param entity 实体类
     * @return 影响条数应该为1，如果插入失败返回0或者抛出异常
     * @see #updateByPrimaryKey(Object)
     */
    public int updateByPrimaryKeySelective(T entity) {
        JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.UPDATE_PRIMARY_KEY_SELECTIVE, null, entity);
        try {
            return this.jdbcExecutor.executeUpdate(SessionFactory.getSession(), statement);
        } catch (SQLException e) {
            throw new RuntimeException("updateByPrimaryKeySelective happens error!", e);
        }
    }

    /**
     * 删除一个拥有主键值的实体的记录，其他字段即使有值也不参与条件
     *
     * @param entity 实体类
     * @return 影响条数应该为1，如果插入失败返回0或者抛出异常
     */
    public int deleteByPrimaryKey(T entity) {
        JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.DELETE_PRIMARY_KEY, null, entity);
        try {
            return this.jdbcExecutor.executeUpdate(SessionFactory.getSession(), statement);
        } catch (SQLException e) {
            throw new RuntimeException("deleteByPrimaryKey happens error!", e);
        }
    }

    /**
     * 统计一个满足实体中非null字段的值记录条数
     *
     * @param entity    实体类
     * @param condition 条件对象
     * @return 影响条数应该为1，如果插入失败返回0或者抛出异常
     */
    public int countSelective(T entity, JdbcCondition condition) {
        final JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.COUNT_SELECTIVE, condition, entity);
        try {
            return this.jdbcExecutor.executeQueryFirst(SessionFactory.getSession(), statement, new Orm.RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt(1);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("countSelective happens error!", e);
        }
    }

    /**
     * 查询一个满足实体中非null字段的值的所有记录
     *
     * @param entity    实体类
     * @param condition 条件对象
     * @return 记录列表，无结果时返回空list
     */
    public List<T> selectSelective(T entity, JdbcCondition condition) {
        final JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.SELECT_SELECTIVE, condition, entity);
        try {
            return this.jdbcExecutor.executeQuery(SessionFactory.getSession(), statement, JAVABEAN_ROW_MAPPER);
        } catch (SQLException e) {
            throw new RuntimeException("selectSelective happens error!", e);
        }
    }

    /**
     * 按照一个实体的物理主键字段查询记录
     *
     * @param entity 实体类
     * @return 如果存在记录则返回对象否则返回null
     */
    public T selectByPrimaryKey(T entity) {
        final JdbcStatement statement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.SELECT_PRIMARY_KEY, null, entity);
        try {
            List<T> result = this.jdbcExecutor.executeQuery(SessionFactory.getSession(), statement, JAVABEAN_ROW_MAPPER);
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

    /**
     * 根据分页对象进行分页查询满足条件非null字段的记录，并统计总条数
     *
     * @param pagination 分页对象
     * @param condition  条件对象
     * @return 分页对象
     */
    public Pagination<T> querySelective(Pagination<T> pagination, JdbcCondition condition) {
        JdbcStatement countStatement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.COUNT_SELECTIVE, condition, pagination);
        //1.构建统计记录的sql语句，并执行
        Integer total = null;
        try {
            total = this.jdbcExecutor.executeQueryFirst(SessionFactory.getSession(), countStatement, new Orm.RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt(1);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("query happens error!", e);
        }
        //2.构建查询记录的sql语句，并执行
        List<T> records = null;
        JdbcStatement selectStatement = JdbcStatementHelper.statement(tableMetadata, JdbcStatementType.PAGINATION_SELECT_SELECTIVE, condition, pagination);
        try {
            records = this.jdbcExecutor.executeQuery(SessionFactory.getSession(), selectStatement, JAVABEAN_ROW_MAPPER);
        } catch (SQLException e) {
            throw new RuntimeException("query happens error!", e);
        }
        pagination.setTotal(total);
        pagination.setRecords(records);
        return pagination;
    }

    public Pagination<T> query(Pagination<T> pagination, String sqlPlaceholder) {
        //TODO 未实现
        throw new RuntimeException("未实现");
    }
}
