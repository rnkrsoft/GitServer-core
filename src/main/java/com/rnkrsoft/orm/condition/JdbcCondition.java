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

    public static JdbcConditionBuilder builder() {
        return new JdbcConditionBuilder();
    }

    public static class JdbcConditionBuilder {
        private final Map<String, JdbcConditionItem> items = new HashMap<String, JdbcConditionItem>();

        public JdbcConditionBuilder define(String name, LogicMode logicMode, ValueMode valueMode) {
            items.put(name, new JdbcConditionItem(name, logicMode, valueMode));
            return this;
        }

        public JdbcConditionBuilder andEq(String name) {
            return define(name, LogicMode.AND, ValueMode.EQ);
        }

        public JdbcConditionBuilder andLt(String name) {
            return define(name, LogicMode.AND, ValueMode.LT);
        }

        public JdbcConditionBuilder andLte(String name) {
            return define(name, LogicMode.AND, ValueMode.LTE);
        }

        public JdbcConditionBuilder andGt(String name) {
            return define(name, LogicMode.AND, ValueMode.GT);
        }

        public JdbcConditionBuilder andGte(String name) {
            return define(name, LogicMode.AND, ValueMode.GTE);
        }

        public JdbcConditionBuilder andNe(String name) {
            return define(name, LogicMode.AND, ValueMode.NE);
        }

        public JdbcConditionBuilder andLike(String name) {
            return define(name, LogicMode.AND, ValueMode.LIKE);
        }

        public JdbcConditionBuilder orEq(String name) {
            return define(name, LogicMode.OR, ValueMode.EQ);
        }

        public JdbcConditionBuilder orLt(String name) {
            return define(name, LogicMode.OR, ValueMode.LT);
        }

        public JdbcConditionBuilder orLte(String name) {
            return define(name, LogicMode.OR, ValueMode.LTE);
        }

        public JdbcConditionBuilder orGt(String name) {
            return define(name, LogicMode.OR, ValueMode.GT);
        }

        public JdbcConditionBuilder orGte(String name) {
            return define(name, LogicMode.OR, ValueMode.GTE);
        }

        public JdbcConditionBuilder orNe(String name) {
            return define(name, LogicMode.OR, ValueMode.NE);
        }

        public JdbcConditionBuilder orLike(String name) {
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
