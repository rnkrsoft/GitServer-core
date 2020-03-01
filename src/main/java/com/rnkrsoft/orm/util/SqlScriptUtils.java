package com.rnkrsoft.orm.util;

import com.rnkrsoft.orm.annotation.DatabaseType;
import com.rnkrsoft.util.StringUtils;
import com.rnkrsoft.interfaces.EnumBase;
import com.rnkrsoft.interfaces.EnumIntegerCode;
import com.rnkrsoft.interfaces.EnumStringCode;
import com.rnkrsoft.orm.SupportedJdbcType;
import com.rnkrsoft.orm.annotation.NameMode;
import com.rnkrsoft.orm.annotation.PrimaryKeyStrategy;
import com.rnkrsoft.orm.annotation.WordMode;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.extractor.EntityExtractorHelper;
import com.rnkrsoft.orm.metadata.TableMetadata;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by rnkrsoft.com on 2018/4/26.
 * 实体工具类，用于从实体类上提取注解，生成封装
 */
@Slf4j
public abstract class SqlScriptUtils {
    /**
     * @param entityClass
     * @param schema           指定的数据库模式，如果为null，则使用元信息数据库模式
     * @param prefix
     * @param suffix
     * @param engine           指定的数据引擎，如果为null，则使用元信息数据引擎
     * @param sqlMode
     * @param keywordMode
     * @param createBeforeTest
     * @return
     */
    public static String generateCreateTable(Class entityClass,
                                             NameMode schemaMode,
                                             String schema,
                                             NameMode prefixMode,
                                             String prefix,
                                             NameMode suffixMode,
                                             String suffix,
                                             String engine,
                                             WordMode sqlMode,
                                             WordMode keywordMode,
                                             boolean createBeforeTest) {
        EntityExtractorHelper helper = new EntityExtractorHelper();
        TableMetadata tableMetadata = helper.extractTable(entityClass);
        if (schemaMode == NameMode.auto) {
            if (schema != null) {
                tableMetadata.setSchema(schema);
            }
        } else if (schemaMode == NameMode.entity) {

        } else if (schemaMode == NameMode.customize) {
            tableMetadata.setSchema(schema);
        }
        if (prefixMode == NameMode.auto) {
            if (!StringUtils.isBlank(prefix)) {
                tableMetadata.setPrefix(prefix);
            }
        } else if (prefixMode == NameMode.entity) {

        } else if (prefixMode == NameMode.customize) {
            tableMetadata.setPrefix(prefix);
        }
        if (suffixMode == NameMode.auto) {
            if (suffix != null) {
                tableMetadata.setSuffix(suffix);
            }
        } else if (suffixMode == NameMode.entity) {

        } else if (suffixMode == NameMode.customize) {
            tableMetadata.setSuffix(suffix);
        }
        tableMetadata.setDataEngine(engine);
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        try {
            generateCreateTable(os, tableMetadata, sqlMode, keywordMode, createBeforeTest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return os.toString();
    }

    /**
     * 生成建表语句
     *
     * @param os               输出流
     * @param tableMetadata    表元信息
     * @param sqlMode          SQL语句是否大小写
     * @param keywordMode      关键字是否大小写
     * @param createBeforeTest 建表之前是否进行测试
     */
    public static void generateCreateTable(ByteArrayOutputStream os,
                                           TableMetadata tableMetadata,
                                           WordMode sqlMode,
                                           WordMode keywordMode,
                                           boolean createBeforeTest) throws IOException {
        StringBuilder sql = new StringBuilder(convert("CREATE TABLE", keywordMode));
        if (createBeforeTest) {
            sql.append(convert(" IF NOT EXISTS", keywordMode));
        }
        String table = tableMetadata.getFullTableName();
        table = convert(table, sqlMode);
        sql.append(" " + table);
        sql.append("(").append(System.getProperty("line.separator"));
        int autoIncrementCnt = 0;
        List<String> orderColumns =  tableMetadata.getColumns();
        for (String name : orderColumns) {
            ColumnMetadata columnMetadata = (ColumnMetadata) tableMetadata.getColumnMetadataSet().get(name);
            sql.append(" ")
                    .append(convert(name, sqlMode))
                    .append(" ")
                    .append(convert(columnMetadata.getFullJdbcType(), keywordMode));
            sql.append(" ");
            if (columnMetadata.getNullable()) {
                sql.append(convert("NULL", keywordMode));
            } else {
                sql.append(convert("NOT NULL", keywordMode));
            }
            sql.append(" ");
            String defval = columnMetadata.getDefaultValue();
            if (columnMetadata.getJdbcType() == SupportedJdbcType.DATE
                    || columnMetadata.getJdbcType() == SupportedJdbcType.DATETIME
                    || columnMetadata.getJdbcType() == SupportedJdbcType.TIMESTAMP) {
                if (defval == null || defval.isEmpty()) {
                    defval = "1971-01-01 00:00:00";
                }
            } else if (columnMetadata.getJdbcType() == SupportedJdbcType.DECIMAL) {
                if (defval == null || defval.isEmpty()) {
                    defval = "0";
                }
            } else if (columnMetadata.getJdbcType() == SupportedJdbcType.TINYINT
                    || columnMetadata.getJdbcType() == SupportedJdbcType.SMALLINT
                    || columnMetadata.getJdbcType() == SupportedJdbcType.INT
                    || columnMetadata.getJdbcType() == SupportedJdbcType.INTEGER
                    || columnMetadata.getJdbcType() == SupportedJdbcType.BIGINT
            ) {
                if (columnMetadata.getPrimaryKeyStrategy() == PrimaryKeyStrategy.IDENTITY) {
                    if (defval == null || defval.isEmpty()) {
                        defval = "0";
                    }
                }
                //如果MySQL中Text是不支持默认值为空的
            } else if (columnMetadata.getJdbcType() == SupportedJdbcType.VARCHAR
                    || columnMetadata.getJdbcType() == SupportedJdbcType.CHAR
                    || columnMetadata.getJdbcType() == SupportedJdbcType.LONGVARCHAR
            ) {
                //NOTHING
            } else {

            }
            if (defval != null && !defval.isEmpty()) {
                if (columnMetadata.getJdbcType() == SupportedJdbcType.VARCHAR
                        || columnMetadata.getJdbcType() == SupportedJdbcType.CHAR
                        || columnMetadata.getJdbcType() == SupportedJdbcType.LONGVARCHAR) {
                    sql.append(convert(" DEFAULT ", keywordMode) + "'" + defval + "' ");
                } else if (columnMetadata.getJdbcType() == SupportedJdbcType.DATE
                        || columnMetadata.getJdbcType() == SupportedJdbcType.DATETIME
                        || columnMetadata.getJdbcType() == SupportedJdbcType.TIMESTAMP) {
                    if (defval.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                        sql.append(convert(" DEFAULT CURRENT_TIMESTAMP", keywordMode));
                    } else {
                        sql.append(convert(" DEFAULT ", keywordMode) + "'" + defval + "'");
                    }
                } else if (columnMetadata.getJdbcType() == SupportedJdbcType.TINYINT
                        || columnMetadata.getJdbcType() == SupportedJdbcType.SMALLINT
                        || columnMetadata.getJdbcType() == SupportedJdbcType.INT
                        || columnMetadata.getJdbcType() == SupportedJdbcType.INTEGER
                        || columnMetadata.getJdbcType() == SupportedJdbcType.BIGINT) {
                    if (columnMetadata.getPrimaryKeyStrategy() == PrimaryKeyStrategy.IDENTITY) {
                        sql.append(convert(" AUTO_INCREMENT", keywordMode));
                        autoIncrementCnt++;
                    } else {
                        sql.append(convert(" DEFAULT ", keywordMode) + defval + "");
                    }
                } else {
                    sql.append(convert(" DEFAULT ", keywordMode) + defval + "");
                }
            }

            if (columnMetadata.getOnUpdateCurrentTimestamp()) {
                sql.append(convert(" ON UPDATE CURRENT_TIMESTAMP", keywordMode));
            }
            if (columnMetadata.getComment() != null && !columnMetadata.getComment().trim().isEmpty()) {
               if (tableMetadata.getType() == DatabaseType.Oracle || tableMetadata.getType() == DatabaseType.Sqlite){
                   sql.append(convert(" COMMENT '", keywordMode)).append(columnMetadata.getComment());
                   if (columnMetadata.getEnumClass() != null
                           && columnMetadata.getEnumClass() != EnumBase.class
                           && columnMetadata.getEnumClass() != EnumStringCode.class
                           && columnMetadata.getEnumClass() != EnumIntegerCode.class
                   ) {
                       if (EnumBase.class.isAssignableFrom(columnMetadata.getEnumClass())) {
                           if (EnumStringCode.class.isAssignableFrom(columnMetadata.getEnumClass())) {
                               sql.append(" ");
                               for (Object val : columnMetadata.getEnumClass().getEnumConstants()) {
                               }
                           } else if (EnumIntegerCode.class.isAssignableFrom(columnMetadata.getEnumClass())) {
                               sql.append(" ");
                               for (Object val : columnMetadata.getEnumClass().getEnumConstants()) {

                               }
                           } else {

                           }
                       }
                   }
                   sql.append("'");
               }
            }
            sql.append(",").append(System.getProperty("line.separator"));
        }

        sql.append(primaryKey(tableMetadata.getPrimaryKeyColumnNameList(), sqlMode, keywordMode) + System.getProperty("line.separator"));
        sql.append(") ");
        if (autoIncrementCnt > 1) {
            throw new IllegalArgumentException(tableMetadata.getTableName() + "自增主键只允许一个");
        }

        if (!StringUtils.isBlank(tableMetadata.getDataEngine())) {
            sql.append(convert("ENGINE=", keywordMode) + tableMetadata.getDataEngine() + " ");
        }
        //如果实体类上有注解
        if (!StringUtils.isBlank(tableMetadata.getComment())) {
            if (tableMetadata.getType() == DatabaseType.Oracle){
                sql.append(convert("COMMENT = '", keywordMode) + tableMetadata.getComment() + "'");
            }
        }
        os.write(sql.toString().getBytes());
    }

    public static String generateDropTable(Class entityClass,
                                           NameMode schemaMode,
                                           String schema,
                                           NameMode prefixMode,
                                           String prefix,
                                           NameMode suffixMode,
                                           String suffix,
                                           WordMode sqlMode,
                                           WordMode keywordMode,
                                           boolean dropBeforeTest) {
        EntityExtractorHelper helper = new EntityExtractorHelper();
        TableMetadata tableMetadata = helper.extractTable(entityClass);
        if (schemaMode == NameMode.auto) {
            if (schema != null) {
                tableMetadata.setSchema(schema);
            }
        } else if (schemaMode == NameMode.entity) {

        } else if (schemaMode == NameMode.customize) {
            tableMetadata.setSchema(schema);
        }
        if (prefixMode == NameMode.auto) {
            if (!StringUtils.isBlank(prefix)) {
                tableMetadata.setPrefix(prefix);
            }
        } else if (prefixMode == NameMode.entity) {

        } else if (prefixMode == NameMode.customize) {
            tableMetadata.setPrefix(prefix);
        }
        if (suffixMode == NameMode.auto) {
            if (suffix != null) {
                tableMetadata.setSuffix(suffix);
            }
        } else if (suffixMode == NameMode.entity) {

        } else if (suffixMode == NameMode.customize) {
            tableMetadata.setSuffix(suffix);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        try {
            generateDropTable(os, tableMetadata, sqlMode, keywordMode, dropBeforeTest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toString();
    }

    /**
     * 生成删表语句
     *
     * @param os             输出流
     * @param tableMetadata  表元信息
     * @param sqlMode        SQL语句是否大小写
     * @param keywordMode    关键字是否大小写
     * @param dropBeforeTest 删表之前进行测试
     */
    public static void generateDropTable(ByteArrayOutputStream os,
                                         TableMetadata tableMetadata,
                                         WordMode sqlMode,
                                         WordMode keywordMode,
                                         boolean dropBeforeTest) throws IOException {
        StringBuilder sql = new StringBuilder(convert("DROP TABLE", keywordMode));
        if (dropBeforeTest) {
            sql.append(convert(" IF EXISTS", keywordMode));
        }
        sql.append(" ");
        String table = convert(tableMetadata.getFullTableName(), sqlMode);
        sql.append(table);
        os.write(sql.toString().getBytes("UTF-8"));
    }

    /**
     * 生成截断表语句
     *
     * @param os            输出流
     * @param tableMetadata 表元信息
     * @param sqlMode       SQL语句是否大小写
     * @param keywordMode   关键字是否大小写
     */
    public static void generateTruncateTable(ByteArrayOutputStream os,
                                             TableMetadata tableMetadata,
                                             WordMode sqlMode,
                                             WordMode keywordMode) throws IOException {
        StringBuilder sql = new StringBuilder(convert("TRUNCATE TABLE", keywordMode));
        sql.append(" ");
        String table = tableMetadata.getFullTableName();
        table = convert(table, sqlMode);
        sql.append(table);
        os.write(sql.toString().getBytes("UTF-8"));
    }

    /**
     * 生成主键
     *
     * @param primaryKeys 逐渐列表
     * @param sqlMode     sql大小写模式
     * @param keywordMode 关键词大小写模式
     * @return 主键语句
     */
    static String primaryKey(List<String> primaryKeys, WordMode sqlMode, WordMode keywordMode) {
        String primaryKey = convert(" PRIMARY KEY(", keywordMode);
        for (int i = 0; i < primaryKeys.size(); i++) {
            if (i == 0) {
                primaryKey = primaryKey + convert(primaryKeys.get(i), sqlMode);
            } else {
                primaryKey = primaryKey + "," + convert(primaryKeys.get(i), sqlMode);
            }
        }
        primaryKey = primaryKey + ")";
        return primaryKey;
    }

    /**
     * 通过实体生成SQL select 后的列名
     *
     * @param entityClass 实体
     * @param keywordMode 关键大小写
     * @param sqlMode     SQL关键大小写
     * @param newline     是否换行
     * @return 列名
     */
    public static String generateSqlHead(Class<?> entityClass, WordMode keywordMode, WordMode sqlMode, boolean newline) {
        EntityExtractorHelper helper = new EntityExtractorHelper();
        TableMetadata tableMetadata = helper.extractTable(entityClass);
        StringBuilder builder = new StringBuilder();
        int index = 0;
        Map<String, ColumnMetadata> fields = tableMetadata.getColumnMetadataSet();
        List<String> orderColumns =  tableMetadata.getColumns();
        for (String name : orderColumns) {
            ColumnMetadata columnMetadata = fields.get(name);
            index++;
            //如果不是第一个字段，且要换行
            if (builder.length() > 0 && newline) {
                builder.append(System.getProperty("line.separator"));
            }
            builder.append(convert(columnMetadata.getJdbcName(), sqlMode))
                    .append(convert(" AS ", keywordMode))
                    .append(columnMetadata.getJavaName());
            if (index != tableMetadata.getColumnMetadataSet().size()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    /**
     * 生成默认模式的建表语句
     *
     * @param os
     * @param keywordMode 关键大小写
     * @param sqlMode     SQL关键大小写
     * @param entities
     * @throws IOException
     */
    public static void generateSql(ByteArrayOutputStream os,
                                   WordMode sqlMode,
                                   WordMode keywordMode,
                                   Class... entities) throws IOException {
        for (Class entityClass : entities) {
            EntityExtractorHelper helper = new EntityExtractorHelper();
            TableMetadata tableMetadata = helper.extractTable(entityClass);
            generateDropTable(os, tableMetadata, sqlMode, keywordMode, false);
            os.write(System.getProperty("line.separator").getBytes("UTF-8"));
            generateCreateTable(os, tableMetadata, sqlMode, keywordMode, false);
            os.write(System.getProperty("line.separator").getBytes("UTF-8"));
        }
    }

    /**
     * 创建SQL语句
     *
     * @param keywordMode 关键大小写
     * @param sqlMode     SQL关键大小写
     * @param entities
     * @throws IOException
     */
    public static void generateSql(WordMode sqlMode,
                                   WordMode keywordMode,
                                   Class... entities) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        for (Class entityClass : entities) {
            EntityExtractorHelper helper = new EntityExtractorHelper();
            TableMetadata tableMetadata = helper.extractTable(entityClass);
            generateDropTable(os, tableMetadata, sqlMode, keywordMode, false);
            os.write(System.getProperty("line.separator").getBytes("UTF-8"));
            generateCreateTable(os, tableMetadata, sqlMode, keywordMode, false);
            os.write(System.getProperty("line.separator").getBytes("UTF-8"));
        }
        FileOutputStream fos = new FileOutputStream("./target/sqlScript.sql");
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    /**
     * 创建脚本
     *
     * @param scriptFileName   脚本文件存放路径
     * @param schemaMode       数据库名模式类型
     * @param schema           数据库名模式
     * @param prefixMode       前缀类型
     * @param prefix           前缀
     * @param suffixMode       后缀类型
     * @param suffix           后缀
     * @param sqlMode          SQL字段大小写模式
     * @param keywordMode      SQL关键字大小写模式
     * @param testBeforeCreate 创建前进行测试
     * @param dropBeforeCreate 创建前进行删除
     * @param packages         包路径数组
     */
    public static void generate(String scriptFileName,
                                NameMode schemaMode,
                                String schema,
                                NameMode prefixMode,
                                String prefix,
                                NameMode suffixMode,
                                String suffix,
                                WordMode sqlMode,
                                WordMode keywordMode,
                                boolean testBeforeCreate,
                                boolean dropBeforeCreate,
                                String... packages) throws IOException {
    }

    public static String convert(String sql, WordMode mode) {
        if (mode == WordMode.lowerCase) {
            return sql.toLowerCase();
        } else if (mode == WordMode.upperCase) {
            return sql.toUpperCase();
        } else {
            return sql;
        }
    }
}
