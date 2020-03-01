package com.rnkrsoft.orm.entity;

import com.rnkrsoft.orm.annotation.Comment;
import com.rnkrsoft.orm.annotation.DateColumn;
import com.rnkrsoft.orm.annotation.DateType;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
@Data
public abstract class BaseEntity implements Serializable {
    @Comment("创建日期")
    @DateColumn(name = "CREATE_DATE", nullable = false, type = DateType.TIMESTAMP, currentTimestamp = true)
    Date createDate;

    @Comment("更新日期")
    @DateColumn(name = "LAST_UPDATE_DATE", nullable = false, type = DateType.TIMESTAMP, onUpdate = true, currentTimestamp = true)
    Date lastUpdateDate;
}