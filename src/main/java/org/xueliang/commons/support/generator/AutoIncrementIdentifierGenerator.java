package org.xueliang.commons.support.generator;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.xueliang.commons.DataStatusEnum;

public class AutoIncrementIdentifierGenerator extends AbstractIdentifierGenerator {

    private DSLContext dsl;
    
    private String tableName;

    @Override
    public String nextId() {
        return nextId(null);
    }

    @Override
    public String nextId(String table) {
        String id = "auto.increment" + (StringUtils.isEmpty(table) ? "" : "." + table);
        long[] value = new long[] {1};
        generator(id, value);
        return String.valueOf(value[0]);
    }

    protected void generator(String id, long[] value) {
        dsl.transaction(configuration -> {
            Record record = dsl.resultQuery("select * from `" + tableName + "` where id = ? and status >= ?", id, DataStatusEnum.NORMAL.getValue()).fetchOne();
            if (record == null) {
                dsl.query("delete from `" + tableName + "` where id = ?", id);
                dsl.query("insert into `" + tableName + "`(id, value, status, created_time) values(?, ?, ?, now())", id, value[0], DataStatusEnum.NORMAL.getValue()).execute();
                return;
            }
            value[0] = record.get("value", Long.class);
            dsl.query("update `" + tableName + "` set value = ?, updated_time = now() where id = ?", value[0] + 1, id).execute();
        });
    }

    public DSLContext getDsl() {
        return dsl;
    }
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
