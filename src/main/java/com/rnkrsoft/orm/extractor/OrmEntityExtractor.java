package com.rnkrsoft.orm.extractor;

import com.rnkrsoft.logtrace.ErrorContextFactory;
import com.rnkrsoft.orm.SupportedJdbcType;
import com.rnkrsoft.orm.annotation.*;
import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * Created by rnkrsoft.com on 2017/1/7.
 * 提取标注ORM注解的实体类
 */
@Slf4j
public class OrmEntityExtractor implements EntityExtractor {

    public EntityExtractor extractTable(TableMetadata tableMetadata) {
        Table tableAnn = (Table) tableMetadata.getEntityClass().getAnnotation(Table.class);
        Comment commentAnn = (Comment) tableMetadata.getEntityClass().getAnnotation(Comment.class);
        //提取表名
        if (tableMetadata.getTableName() == null && tableAnn != null && tableAnn.name() != null) {
            tableMetadata.setTableName(tableAnn.name());
            tableMetadata.setSchema(tableAnn.schema());
            tableMetadata.setPrefix(tableAnn.prefix());
            tableMetadata.setSuffix(tableAnn.suffix());
        }
        //提取表注释
        if (commentAnn != null && commentAnn.value() != null) {
            tableMetadata.setComment(commentAnn.value());
        }
        return this;
    }


    public EntityExtractor extractFieldString(ColumnMetadata columnMetadata) {
        StringColumn stringColumn = columnMetadata.getColumnField().getAnnotation(StringColumn.class);
        if (stringColumn == null) {
            return this;
        }
        Class fieldClass = columnMetadata.getJavaType();
        if (fieldClass != String.class) {
            throw ErrorContextFactory.instance()
                    .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                    .message("字段{}数据类型和注解类型支持的映射数据不一致", columnMetadata.getJavaName())
                    .solution("将字段{}的类型从{}修改为{}", columnMetadata.getJavaName(), fieldClass, String.class)
                    .runtimeException();
        }
        String dataType = null;
        String jdbcType = null;
        if (stringColumn != null) {
            if (stringColumn.type() == StringType.CHAR) {
                dataType = "CHAR(" + stringColumn.length() + ")";
                jdbcType = "CHAR";
            } else if (stringColumn.type() == StringType.VARCHAR) {
                dataType = "VARCHAR(" + stringColumn.length() + ")";
                jdbcType = "VARCHAR";
            } else if (stringColumn.type() == StringType.TEXT) {
                dataType = "TEXT";
                jdbcType = "LONGVARCHAR";
            } else if (stringColumn.type() == StringType.AUTO) {
                if (stringColumn.length() <= 0) {
                    throw ErrorContextFactory.instance()
                            .activity("提取实体类{}的元信息中{}字段", columnMetadata.getEntityClass(), columnMetadata.getJavaName())
                            .message("字段{}为VARCHAR类型，但是没有指定length", columnMetadata.getJdbcName())
                            .solution("在标注{}注解上的属性{}设置字符串长度", StringColumn.class, "length")
                            .runtimeException();
                } else if (stringColumn.length() > 255) {
                    log.warn("实体[{}]字段[{}]的文本长度超过了256，自动使用TEXT数据类型", columnMetadata.getEntityClass().getName(), columnMetadata.getJdbcName());
                    dataType = "TEXT";
                    jdbcType = "LONGVARCHAR";
                } else {
                    dataType = "VARCHAR(" + stringColumn.length() + ")";
                    jdbcType = "VARCHAR";
                }
            }
            columnMetadata.setDefaultValue(stringColumn.defaultValue());
            Class enumClass = stringColumn.enumClass();
            columnMetadata.setEnumClass(enumClass);
        }
        if (jdbcType != null) {
            columnMetadata.setJdbcType(SupportedJdbcType.valueOfCode(jdbcType));
        }
        if (dataType != null) {
            columnMetadata.setFullJdbcType(dataType);
        }
        return this;
    }


    public EntityExtractor extractFieldNumber(ColumnMetadata columnMetadata) {
        NumberColumn numberColumn = columnMetadata.getColumnField().getAnnotation(NumberColumn.class);
        if (numberColumn == null) {
            return this;
        }
        Class fieldClass = columnMetadata.getJavaType();
        String dataType = null;
        String jdbcType = null;
        if (fieldClass != BigDecimal.class
                && fieldClass != Long.class
                && fieldClass != Long.TYPE
                && fieldClass != Integer.class
                && fieldClass != Integer.TYPE
                && fieldClass != Boolean.class
                && fieldClass != Boolean.TYPE
                ) {
            throw ErrorContextFactory.instance()
                    .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                    .message("字段{}数据类型和注解类型支持的映射数据不一致", columnMetadata.getJavaName())
                    .solution("将字段{}的类型从{}修改为[{},{},{},{},{},{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, BigDecimal.class, Long.class, Long.TYPE, Integer.class, Integer.TYPE, Boolean.class, Boolean.TYPE)
                    .runtimeException();
        }
        if (numberColumn != null) {
            if (numberColumn.type() != null) {
                if (numberColumn.type() == NumberType.AUTO) {
                    if (fieldClass == Long.TYPE) {
                        dataType = "BIGINT";
                        jdbcType = "BIGINT";
                    } else if (fieldClass == Long.class) {
                        dataType = "BIGINT";
                        jdbcType = "BIGINT";
                    } else if (fieldClass == Integer.TYPE) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else if (fieldClass == Integer.class) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else if (fieldClass == Boolean.TYPE) {
                        dataType = "TINYINT";
                        jdbcType = "TINYINT";
                    } else if (fieldClass == Boolean.class) {
                        dataType = "TINYINT";
                        jdbcType = "TINYINT";
                    } else if (fieldClass == BigDecimal.class) {
                        if (numberColumn.scale() > 0 && numberColumn.precision() > 0 && numberColumn.precision() > numberColumn.scale()) {
                            dataType = "DECIMAL(" + numberColumn.precision() + "," + numberColumn.scale() + ")";
                        } else if (numberColumn.precision() > 0 && numberColumn.scale() == 0) {
                            dataType = "DECIMAL(" + numberColumn.precision() + ")";
                        } else if (numberColumn.precision() > numberColumn.scale() && numberColumn.scale() == 0) {
                            dataType = "DECIMAL(18)";
                        } else {
                            dataType = "DECIMAL(18,2)";
                        }
                        jdbcType = "DECIMAL";
                    }
                } else if (numberColumn.type() == NumberType.BOOLEAN) {
                    if (fieldClass == Boolean.TYPE) {
                        dataType = "TINYINT";
                        jdbcType = "TINYINT";
                    } else if (fieldClass == Boolean.class) {
                        dataType = "TINYINT";
                        jdbcType = "TINYINT";
                    } else {
                        throw ErrorContextFactory.instance()
                                .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                                .message("字段{}为为非整数类型，标注注解为整数型", columnMetadata.getJavaName())
                                .solution("将字段类型修改为[{},{}]任意一种或者修改注解", Boolean.class, Boolean.TYPE)
                                .runtimeException();
                    }
                } else if (numberColumn.type() == NumberType.BYTE || numberColumn.type() == NumberType.SHORT || numberColumn.type() == NumberType.INTEGER) {
                    if (fieldClass == Byte.TYPE) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else if (fieldClass == Byte.class) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else if (fieldClass == Short.TYPE) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else if (fieldClass == Short.class) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else if (fieldClass == Integer.TYPE) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else if (fieldClass == Integer.class) {
                        dataType = "INTEGER";
                        jdbcType = "INTEGER";
                    } else {
                        throw ErrorContextFactory.instance()
                                .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                                .message("字段{}为为非整数类型，标注注解为整数型", columnMetadata.getJavaName())
                                .solution("将字段类型修改为[{},{},{},{}]任意一种或者修改注解", Integer.class, Integer.TYPE, Boolean.class, Boolean.TYPE)
                                .runtimeException();
                    }
                } else if (numberColumn.type() == NumberType.LONG) {
                    if (fieldClass == Long.TYPE) {
                        dataType = "BIGINT";
                        jdbcType = "BIGINT";
                    } else if (fieldClass == Long.class) {
                        dataType = "BIGINT";
                        jdbcType = "BIGINT";
                    } else {
                        throw ErrorContextFactory.instance()
                                .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                                .message("字段{}为为非长整数类型，标注注解为长整数型", columnMetadata.getJavaName())
                                .solution("将字段类型修改为[{},{}]任意一种或者修改注解", Long.class, Long.TYPE)
                                .runtimeException();
                    }
                } else if (numberColumn.type() == NumberType.DECIMAL) {
                    if (fieldClass != BigDecimal.class) {
                        throw ErrorContextFactory.instance()
                                .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                                .message("字段{}为为非小数类型，标注注解为小数型", columnMetadata.getJavaName())
                                .solution("将字段类型修改为{}", BigDecimal.class)
                                .runtimeException();
                    }
                    if (numberColumn.scale() > 0 && numberColumn.precision() > 0 && numberColumn.precision() > numberColumn.scale()) {
                        dataType = "DECIMAL(" + numberColumn.precision() + "," + numberColumn.scale() + ")";
                    } else if (numberColumn.precision() > 0 && numberColumn.scale() == 0) {
                        dataType = "DECIMAL(" + numberColumn.precision() + ")";
                    } else if (numberColumn.precision() > numberColumn.scale() && numberColumn.scale() == 0) {
                        dataType = "DECIMAL(18)";
                    } else {
                        dataType = "DECIMAL(18,2)";
                    }
                    jdbcType = "DECIMAL";
                } else {
                    throw ErrorContextFactory.instance()
                            .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                            .message("字段{}数据类型没有配置type", columnMetadata.getJavaName())
                            .solution("type属性需要设置值")
                            .runtimeException();
                }
            } else {
                if (fieldClass == BigDecimal.class) {
                    if (numberColumn.scale() > 0 && numberColumn.precision() > 0 && numberColumn.precision() > numberColumn.scale()) {
                        dataType = "DECIMAL(" + numberColumn.precision() + "," + numberColumn.scale() + ")";
                    } else if (numberColumn.precision() > 0 && numberColumn.scale() == 0) {
                        dataType = "DECIMAL(" + numberColumn.precision() + ")";
                    } else if (numberColumn.precision() > numberColumn.scale() && numberColumn.scale() == 0) {
                        dataType = "DECIMAL(18)";
                    } else {
                        dataType = "DECIMAL(18,2)";
                    }
                    jdbcType = "DECIMAL";
                } else if (fieldClass == Long.TYPE) {
                    dataType = "BIGINT";
                    jdbcType = "BIGINT";
                } else if (fieldClass == Long.class) {
                    dataType = "BIGINT";
                    jdbcType = "BIGINT";
                } else if (fieldClass == Integer.TYPE) {
                    dataType = "INTEGER";
                    jdbcType = "INTEGER";
                } else if (fieldClass == Integer.class) {
                    dataType = "INTEGER";
                    jdbcType = "INTEGER";
                } else if (fieldClass == Short.TYPE) {
                    dataType = "INTEGER";
                    jdbcType = "INTEGER";
                } else if (fieldClass == Short.class) {
                    dataType = "INTEGER";
                    jdbcType = "INTEGER";
                } else if (fieldClass == Byte.TYPE) {
                    dataType = "INTEGER";
                    jdbcType = "INTEGER";
                } else if (fieldClass == Byte.class) {
                    dataType = "INTEGER";
                    jdbcType = "INTEGER";
                } else if (fieldClass == Boolean.TYPE) {
                    dataType = "TINYINT";
                    jdbcType = "TINYINT";
                } else if (fieldClass == Boolean.class) {
                    dataType = "TINYINT";
                    jdbcType = "TINYINT";
                } else {
                    throw ErrorContextFactory.instance()
                            .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                            .message("字段{}数据类型不支持{}属性", columnMetadata.getJavaName(), columnMetadata.getJavaType())
                            .solution("将字段{}的类型从{}修改为[{},{},{},{},{},{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, BigDecimal.class, Long.class, Long.TYPE, Integer.class, Integer.TYPE, Boolean.class, Boolean.TYPE)
                            .runtimeException();
                }
            }
            columnMetadata.setDefaultValue(numberColumn.defaultValue());
            Class enumClass = numberColumn.enumClass();
            columnMetadata.setEnumClass(enumClass);
        }
        if (jdbcType != null) {
            columnMetadata.setJdbcType(SupportedJdbcType.valueOfCode(jdbcType));
        }
        if (dataType != null) {
            columnMetadata.setFullJdbcType(dataType);
        }

        return this;
    }


    /**
     * 提取字段上的日期元信息
     *
     * @param columnMetadata 元信息
     */
    public EntityExtractor extractFieldDate(ColumnMetadata columnMetadata) {
        ErrorContextFactory.instance().activity("提取实体类{}的元信息", columnMetadata.getEntityClass());
        DateColumn dateColumn = columnMetadata.getColumnField().getAnnotation(DateColumn.class);
        if (dateColumn == null) {
            return this;
        }
        Class fieldClass = columnMetadata.getJavaType();
        if (fieldClass != java.util.Date.class && fieldClass != java.sql.Date.class && fieldClass != java.sql.Time.class && fieldClass != java.sql.Timestamp.class) {
            throw ErrorContextFactory.instance()
                    .message("字段{}数据类型和注解类型支持的映射数据不一致", columnMetadata.getJavaName())
                    .solution("将字段{}的类型从{}修改为[{},{},{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, java.util.Date.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class)
                    .runtimeException();
        }
        String fullJdbcType = null;
        String jdbcType = null;
        if (dateColumn != null) {
            if (dateColumn.type() == DateType.DATE) {
                if (fieldClass == java.sql.Time.class) {
                    throw ErrorContextFactory.instance()
                            .message("字段{}为时间戳数据类型，要求Java数据结构能够提供年月日的精度", columnMetadata.getJavaName())
                            .solution("将字段{}的类型从{}修改为[{},{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, java.util.Date.class, java.sql.Date.class, java.sql.Timestamp.class)
                            .runtimeException();

                }
                fullJdbcType = "DATE";
                jdbcType = "DATE";
            } else if (dateColumn.type() == DateType.TIME) {
                if (fieldClass == java.sql.Date.class) {
                    throw ErrorContextFactory.instance()
                            .message("字段{}为时间戳数据类型，要求Java数据结构能够提供时分秒的精度", columnMetadata.getJavaName())
                            .solution("将字段{}的类型从{}修改为[{},{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, java.util.Date.class, java.sql.Time.class, java.sql.Timestamp.class)
                            .runtimeException();

                }
                fullJdbcType = "TIME";
                jdbcType = "TIME";
            } else if (dateColumn.type() == DateType.TIMESTAMP) {
                if (fieldClass == java.sql.Date.class || fieldClass == java.sql.Time.class) {
                    throw ErrorContextFactory.instance()
                            .message("字段{}为时间戳数据类型，要求Java数据结构能够提供年月日时分秒的精度", columnMetadata.getJavaName())
                            .solution("将字段{}的类型从{}修改为[{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, java.util.Date.class, java.sql.Timestamp.class)
                            .runtimeException();

                }
                fullJdbcType = "TIMESTAMP";
                jdbcType = "TIMESTAMP";
            } else if (dateColumn.type() == DateType.DATETIME) {
                if (fieldClass == java.sql.Date.class || fieldClass == java.sql.Time.class) {
                    throw ErrorContextFactory.instance()
                            .message("字段{}为时间戳数据类型，要求Java数据结构能够提供年月日时分秒的精度", columnMetadata.getJavaName())
                            .solution("将字段{}的类型从{}修改为[{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, java.util.Date.class, java.sql.Timestamp.class)
                            .runtimeException();

                }
                fullJdbcType = "TIMESTAMP";
                jdbcType = "TIMESTAMP";
            } else if (dateColumn.type() == DateType.AUTO) {
                if (fieldClass == java.sql.Date.class) {
                    fullJdbcType = "TIMESTAMP";
                    jdbcType = "TIMESTAMP";
                } else if (fieldClass == java.util.Date.class) {
                    fullJdbcType = "DATE";
                    jdbcType = "DATE";
                } else if (fieldClass == java.sql.Time.class) {
                    fullJdbcType = "TIME";
                    jdbcType = "TIME";
                } else if (fieldClass == java.sql.Timestamp.class) {
                    fullJdbcType = "TIMESTAMP";
                    jdbcType = "TIMESTAMP";
                } else {
                    throw ErrorContextFactory.instance()
                            .message("字段{}数据类型和注解类型支持的映射数据不一致", columnMetadata.getJavaName())
                            .solution("将字段{}的类型从{}修改为[{},{},{},{}]任意一种", columnMetadata.getJavaName(), fieldClass, java.util.Date.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class)
                            .runtimeException();
                }
            }
            columnMetadata.setDefaultValue(dateColumn.defaultValue());
        }
        if (jdbcType != null) {
            columnMetadata.setJdbcType(SupportedJdbcType.valueOfCode(jdbcType));
        }
        if (fullJdbcType != null) {
            columnMetadata.setFullJdbcType(fullJdbcType);
        }
        if (dateColumn.nullable()) {
            throw ErrorContextFactory.instance()
                    .message("字段'{}'为时间类型'{}'不允许为空", columnMetadata.getJavaName(), columnMetadata.getJdbcType())
                    .runtimeException();
        }
        if (dateColumn.currentTimestamp()) {
            if (columnMetadata.getJdbcType() == SupportedJdbcType.TIMESTAMP) {
                columnMetadata.setDefaultValue("CURRENT_TIMESTAMP");
            } else {
                throw ErrorContextFactory.instance()
                        .message("字段'{}' 不能设置currentTimestamp,必须在数据库类型为TimeStamp类型上设置", columnMetadata.getJavaName())
                        .runtimeException();
            }
        }
        if (dateColumn.onUpdate()) {
            if (columnMetadata.getJdbcType() == SupportedJdbcType.TIMESTAMP) {
                columnMetadata.setOnUpdateCurrentTimestamp(dateColumn.onUpdate());
            } else {
                throw ErrorContextFactory.instance()
                        .message("字段'{}' 不能设置onUpdate,必须在数据库类型为TimeStamp类型上设置", columnMetadata.getJavaName())
                        .runtimeException();
            }
        }
        ErrorContextFactory.instance().activity(null);
        return this;
    }

    public EntityExtractor extractFieldPrimaryKey(ColumnMetadata columnMetadata) {
        Field field = columnMetadata.getColumnField();
        PrimaryKey primaryKey = columnMetadata.getColumnField().getAnnotation(PrimaryKey.class);
        PrimaryKeyStrategy strategy = null;
        if (primaryKey == null) {
            return this;
        } else if (primaryKey.strategy() == PrimaryKeyStrategy.AUTO) {
            strategy = PrimaryKeyStrategy.UUID;
        } else if (primaryKey.strategy() == PrimaryKeyStrategy.IDENTITY) {
            strategy = PrimaryKeyStrategy.IDENTITY;
            if (columnMetadata.getDefaultValue() != null && !columnMetadata.getDefaultValue().isEmpty()) {
                throw ErrorContextFactory.instance()
                        .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                        .message("字段{}使用了自增主键同时使用了默认值'{}'", field.getName(), columnMetadata.getDefaultValue())
                        .solution("将字段{}的只使用自增主键", field.getName())
                        .runtimeException();
            }
        } else if (primaryKey.strategy() == PrimaryKeyStrategy.UUID) {
            strategy = PrimaryKeyStrategy.UUID;
        }
        if (strategy == PrimaryKeyStrategy.IDENTITY) {
            if (columnMetadata.getJavaType() != Integer.class
                    && columnMetadata.getJavaType() != Integer.TYPE
                    && columnMetadata.getJavaType() != Long.class
                    && columnMetadata.getJavaType() != Long.TYPE
                    ) {
                throw ErrorContextFactory.instance()
                        .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                        .message("字段{}使用了自增主键，类型必须为整数", field.getName())
                        .solution("将字段{}的类型从{}修改为{}或者{}", field.getName(), columnMetadata.getJavaType(), Integer.class, "int")
                        .runtimeException();
            }
        } else if (strategy == PrimaryKeyStrategy.UUID) {
            if (columnMetadata.getJavaType() != String.class) {
                throw ErrorContextFactory.instance()
                        .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                        .message("字段{}使用了UUID，类型必须为字符串", field.getName())
                        .solution("将字段{}的类型从{}修改为{}", field.getName(), columnMetadata.getJavaType(), String.class)
                        .runtimeException();
            }
        } else {
        }

        if (columnMetadata.getNullable() != null && columnMetadata.getNullable()) {
            throw ErrorContextFactory.instance()
                    .activity("提取实体类{}的元信息", columnMetadata.getEntityClass())
                    .message("字段{}是主键，该字段不允许为空", field.getName())
                    .solution("在字段{}将{}属性修改为{}", field.getName(), "nullabe", false)
                    .runtimeException();
        }
        columnMetadata.setPrimaryKey(true);
        columnMetadata.setPrimaryKeyStrategy(strategy);
        columnMetadata.setPrimaryKeyFeature(primaryKey.feature());
        return this;
    }


    /**
     * 提字段信息
     *
     * @param columnMetadata 字段元信息
     * @return 是否处理
     */
    public boolean extractField(ColumnMetadata columnMetadata) {
        Field field = columnMetadata.getColumnField();
        Class entityClass = columnMetadata.getEntityClass();
        NumberColumn numberColumn = field.getAnnotation(NumberColumn.class);
        StringColumn stringColumn = field.getAnnotation(StringColumn.class);
        DateColumn dateColumn = field.getAnnotation(DateColumn.class);
        Comment comment = field.getAnnotation(Comment.class);
        Ignore ignore = field.getAnnotation(Ignore.class);
        if (ignore != null) {
            return false;
        }
        if ("schema".equals(columnMetadata.getJavaName())) {
            return false;
        }
        //提取字段注释
        if (comment != null) {
            columnMetadata.setComment(comment.value());
        }
        //统计同一类注解使用几个
        int count = 0;
        if (numberColumn != null) {
            count++;
        }
        if (stringColumn != null) {
            count++;
        }
        if (dateColumn != null) {
            count++;
        }
        if (count == 0) {
            throw ErrorContextFactory.instance()
                    .activity("提取实体类{}的元信息", entityClass)
                    .message("字段{}未按照约定标注{}或{}或{}注解", field.getName(), StringColumn.class, NumberColumn.class, DateColumn.class)
                    .solution("在字段{}上标注{}或{}或{}注解", field.getName(), StringColumn.class, NumberColumn.class, DateColumn.class)
                    .runtimeException();

        }

        if (count != 1) {
            throw ErrorContextFactory.instance()
                    .activity("提取实体类{}的元信息", entityClass)
                    .message("字段{}同时标注{}或{}或{}注解的组合", field.getName(), StringColumn.class, NumberColumn.class, DateColumn.class)
                    .solution("在字段{}上只能标注{}或{}或{}注解中的一种", field.getName(), StringColumn.class, NumberColumn.class, DateColumn.class)
                    .runtimeException();

        }
        if (stringColumn != null) {
            if (columnMetadata.getJdbcName() == null) {
                columnMetadata.setJdbcName(stringColumn.name());
            }
            if (columnMetadata.getNullable() == null) {
                columnMetadata.setNullable(stringColumn.nullable());
            }
        }
        if (numberColumn != null) {
            if (columnMetadata.getJdbcName() == null) {
                columnMetadata.setJdbcName(numberColumn.name());
            }
            if (columnMetadata.getNullable() == null) {
                columnMetadata.setNullable(numberColumn.nullable());
            }
        }
        if (dateColumn != null) {
            if (columnMetadata.getJdbcName() == null) {
                columnMetadata.setJdbcName(dateColumn.name());
            }
            if (columnMetadata.getNullable() == null) {
                columnMetadata.setNullable(dateColumn.nullable());
            }
        }
        extractFieldString(columnMetadata);
        extractFieldNumber(columnMetadata);
        extractFieldDate(columnMetadata);
        extractFieldPrimaryKey(columnMetadata);
        return true;
    }
}
