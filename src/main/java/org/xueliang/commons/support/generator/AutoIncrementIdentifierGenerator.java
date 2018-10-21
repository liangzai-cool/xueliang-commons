package org.xueliang.commons.support.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.xueliang.commons.DataStatusEnum;

/**
 * 如果不是用spring，去掉下面的接口实现和afterPropertiesSet方法即可
 */
public class AutoIncrementIdentifierGenerator extends AbstractIdentifierGenerator implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(AutoIncrementIdentifierGenerator.class);

    private DSLContext dsl;
    
    private String tableName;

    /**
     * 默认的appId
     */
    private String defaultAppId;

    @Override
    public String nextId() {
        return nextId(null);
    }

    @Override
    public String nextId(String table) {
        return nextId(table, "");
    }

    /**
     * 注意：若 appId 与 ${code defaultAppId} 不同会引起主键冲突
     * 数据库需要使用id和app_id做联合主键
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
        if (StringUtils.isEmpty(appId)) {
            checkDefaultAppId();
            return defaultAppId;
        }
        return appId;
    }

    private void checkDefaultAppId() {
        LOGGER.info("default app id is [{}]", defaultAppId);
        Assert.hasLength(defaultAppId, "please assign defaultAppId value");
    }

    public void afterPropertiesSet() {
        checkDefaultAppId();
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

    public String getDefaultAppId() {
        return defaultAppId;
    }

    public void setDefaultAppId(String defaultAppId) {
        this.defaultAppId = defaultAppId;
    }
}
