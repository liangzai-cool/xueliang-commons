package org.xueliang.commons.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.xueliang.commons.DataStatusEnum;

public class ConfigProvider {
    
    private static final Logger LOGGER = LogManager.getLogger(ConfigProvider.class);
    
    private DSLContext dsl;
    private String[] configTables;
    
    public String getConfig(String id, String defaultValue) {
        String configValue = defaultValue;
        String currentConfigTable = null;
        for (int i = 0; i < configTables.length; i++) {
            currentConfigTable = configTables[i];
            if (StringUtils.isNotEmpty(currentConfigTable)) {
              Record record = dsl.select().from("`" + currentConfigTable + "`").where("id = ? and status >= ?", id, DataStatusEnum.NORMAL.getValue()).fetchOne();
                if (record != null) {
                    Object object = record.get("value");
                    if (object != null) {
                        configValue = String.valueOf(object);
                        break;
                    }
                }
            }
        }
        LOGGER.info("get config from table [{}] id is [{}], value is [{}]", currentConfigTable, id, configValue);
        return configValue;
    }
    
    public Map<String, String> getAllConfig(String prefix) {
        Map<String, String> map = new HashMap<>();
        Arrays.stream(configTables).forEach(configTable -> {
            if (StringUtils.isNotEmpty(configTable)) {
                Result<Record> result = dsl.select().from("`" + configTable + "`").where("id like ? and status >= ?", prefix + "%", DataStatusEnum.NORMAL.getValue()).fetch();
                result.forEach(configRecord -> {
                    Object configId = configRecord.get("id");
                    Object configValue = configRecord.get("value");
                    if (configValue != null) {
                        String id = configId.toString();
                        String value = configValue.toString();
                        if (map.containsKey(id)) {
                            LOGGER.info("override config[id={}], old value is [{}] new value is [{}]", id, map.get(id), value);
                        } else {
                            LOGGER.info("new config[id={}], value is [{}]", id, value);
                        }
                        map.put(id, value);
                    }
                });
              }
        });
        return map;
    }
    
    public String getConfig(String id) {
        return getConfig(id, "");
    }
    
    public String getConfigReplacedVariables(String id) {
        String value = getConfig(id);
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String expression = matcher.group(0).replace("${", "\\$\\{").replace("}", "\\}");
            String configKeyName = matcher.group(1);
            String configValue = getConfig(configKeyName, null);
            if (configValue == null) {
                continue;
            }
            value = value.replaceAll(expression, Matcher.quoteReplacement(configValue));
        }
        LOGGER.info("get config replaced variables, id is [{}], value is [{}]", id, value);
        return value;
    }
    
    public DSLContext getDsl() {
        return dsl;
    }
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }
    public String[] getConfigTables() {
        return configTables;
    }
    public void setConfigTables(String... configTables) {
        this.configTables = configTables;
    }
}
