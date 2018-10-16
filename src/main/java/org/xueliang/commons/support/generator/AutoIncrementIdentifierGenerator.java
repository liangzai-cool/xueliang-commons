package org.xueliang.commons.support.generator;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.xueliang.commons.DataStatusEnum;

public class AutoIncrementIdentifierGenerator extends AbstractIdentifierGenerator {

    private DSLContext dsl;
    
    private String tableName;

    /**
     * 自增序列是否区分app_id
     * 注意：会引起主键冲突，数据库需要使用id和app_id做联合主键
     */
    private boolean diffAppId;

    @Override
    public String nextId() {
        return nextId(null);
    }

    @Override
    public String nextId(String table) {
        String id = "auto.increment" + (StringUtils.isEmpty(table) ? "" : "." + table);
        long[] value = new long[] {1};
        generator(id, "", value);
        return String.valueOf(value[0]);
    }

    /**
     * 区分appId
     * 注意：会引起主键冲突，数据库需要使用id和app_id做联合主键
     * @param table
     * @param appId
     * @return
     */
    public String nextId(String table, String appId) {
        String id = "auto.increment" + (StringUtils.isEmpty(table) ? "" : "." + table);
        long[] value = new long[] {1};
        String finalAppId = finalAppId(appId);
        generator(id, finalAppId, value);
        return String.valueOf(value[0]);
    }

    protected void generator(String id, String appId, long[] value) {
        dsl.transaction(configuration -> {
            Record record = dsl.select().from(tableName)
                    .where("id = ?", id)
                    .and("status = ?", DataStatusEnum.NORMAL.name())
                    .and("app_id = ?", appId).fetchOne();
            if (record == null) {
                dsl.query("delete from `" + tableName + "` where id = ? and app_id = ?", id, appId);
                dsl.query("insert into `" + tableName + "`(id, value, app_id, status, created_time) values(?, ?, ?, ?, now())",
                        id, value[0], appId, DataStatusEnum.NORMAL.name()).execute();
                return;
            }
            value[0] = record.get("value", Long.class);
            long nextId = value[0] + 1;
            dsl.query("update `" + tableName + "` set value = ?, updated_time = now() where id = ? and app_id = ?", nextId, id, appId).execute();
        });
    }

    private String finalAppId(String appId) {
        return diffAppId ? appId : StringUtils.EMPTY;
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

    public boolean isDiffAppId() {
        return diffAppId;
    }

    public void setDiffAppId(boolean diffAppId) {
        this.diffAppId = diffAppId;
    }
}
