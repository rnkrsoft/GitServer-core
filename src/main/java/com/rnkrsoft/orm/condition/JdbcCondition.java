package com.rnkrsoft.orm.condition;

import com.rnkrsoft.orm.annotation.LogicMode;
import com.rnkrsoft.orm.annotation.ValueMode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by woate on 2020/3/1.
 * Jdbc条件覆盖对象，用于实现对表字段已定义的值模式和逻辑模式进行覆盖
 */
public class JdbcCondition {
    private final Map<String, JdbcConditionItem> items;

    private JdbcCondition(Map<String, JdbcConditionItem> items) {
        this.items = items;
    }

    public JdbcConditionItem item(String jdbcColumnName) {
        return items.get(jdbcColumnName);
    }

    public static JdbcConditionOverrideBuilder builder() {
        return new JdbcConditionOverrideBuilder();
    }

    public static class JdbcConditionOverrideBuilder {
        private final Map<String, JdbcConditionItem> items = new HashMap<String, JdbcConditionItem>();

        public JdbcConditionOverrideBuilder define(String name, LogicMode logicMode, ValueMode valueMode) {
            items.put(name, new JdbcConditionItem(name, logicMode, valueMode));
            return this;
        }

        public JdbcConditionOverrideBuilder andEq(String name) {
            return define(name, LogicMode.AND, ValueMode.EQ);
        }

        public JdbcConditionOverrideBuilder andLt(String name) {
            return define(name, LogicMode.AND, ValueMode.LT);
        }

        public JdbcConditionOverrideBuilder andLte(String name) {
            return define(name, LogicMode.AND, ValueMode.LTE);
        }

        public JdbcConditionOverrideBuilder andGt(String name) {
            return define(name, LogicMode.AND, ValueMode.GT);
        }

        public JdbcConditionOverrideBuilder andGte(String name) {
            return define(name, LogicMode.AND, ValueMode.GTE);
        }

        public JdbcConditionOverrideBuilder andNe(String name) {
            return define(name, LogicMode.AND, ValueMode.NE);
        }

        public JdbcConditionOverrideBuilder andLike(String name) {
            return define(name, LogicMode.AND, ValueMode.LIKE);
        }

        public JdbcConditionOverrideBuilder orEq(String name) {
            return define(name, LogicMode.OR, ValueMode.EQ);
        }

        public JdbcConditionOverrideBuilder orLt(String name) {
            return define(name, LogicMode.OR, ValueMode.LT);
        }

        public JdbcConditionOverrideBuilder orLte(String name) {
            return define(name, LogicMode.OR, ValueMode.LTE);
        }

        public JdbcConditionOverrideBuilder orGt(String name) {
            return define(name, LogicMode.OR, ValueMode.GT);
        }

        public JdbcConditionOverrideBuilder orGte(String name) {
            return define(name, LogicMode.OR, ValueMode.GTE);
        }

        public JdbcConditionOverrideBuilder orNe(String name) {
            return define(name, LogicMode.OR, ValueMode.NE);
        }

        public JdbcConditionOverrideBuilder orLike(String name) {
            return define(name, LogicMode.OR, ValueMode.LIKE);
        }

        public JdbcCondition build() {
            JdbcCondition condition = new JdbcCondition(this.items);
            return condition;
        }
    }
    /**
     * Jdbc条件项，定义了覆盖的字段名对应的逻辑模式和值模式
     */
    public static class JdbcConditionItem {
        @Getter
        String name;
        @Getter
        LogicMode logicMode;
        @Getter
        ValueMode valueMode;
        @Getter
        Object value;

        JdbcConditionItem(String name, LogicMode logicMode, ValueMode valueMode) {
            this.name = name;
            this.logicMode = logicMode;
            this.valueMode = valueMode;
        }
    }
}
