package com.rnkrsoft.orm;

import com.rnkrsoft.orm.annotation.*;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
public abstract class BaseEntity implements Serializable {
    @Comment("创建日期")
    @DateColumn(name = "CREATE_DATE", nullable = false, type = DateType.TIMESTAMP, currentTimestamp = true)
    Date createDate;

    @Comment("更新日期")
    @DateColumn(name = "LAST_UPDATE_DATE", nullable = false, type = DateType.TIMESTAMP, onUpdate = true, currentTimestamp = true)
    Date lastUpdateDate;
}