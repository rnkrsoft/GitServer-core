package com.rnkrsoft.orm.extractor;

import com.rnkrsoft.orm.metadata.ColumnMetadata;
import com.rnkrsoft.orm.metadata.TableMetadata;

/**
 * Created by rnkrsoft.com on 2018/4/6.
 * 实体提取器接口
 */
public interface EntityExtractor {
    /**
     * 提取实体类上的表信息
     * @param metadata 元信息
     * @return 提取器本身
     */
    EntityExtractor extractTable(TableMetadata metadata);

    /**
     * 提取实体类上文本字段信息
     * @param metadata 字段元信息
     * @return 提取器本身
     */
    EntityExtractor extractFieldString(ColumnMetadata metadata);

    /**
     * 提取实体类上数字字段信息
     * @param metadata 字段元信息
     * @return 提取器本身
     */
    EntityExtractor extractFieldNumber(ColumnMetadata metadata);
    /**
     * 提取实体类上日期字段信息
     * @param metadata 字段元信息
     * @return 提取器本身
     */
    EntityExtractor extractFieldDate(ColumnMetadata metadata);
    /**
     * 提取实体类上物理主键字段
     * @param metadata 字段元信息
     * @return 提取器本身
     */
    EntityExtractor extractFieldPrimaryKey(ColumnMetadata metadata);

    /**
     * 提取实体类上字段
     * @param metadata
     * @return
     */
    boolean extractField(ColumnMetadata metadata);
}