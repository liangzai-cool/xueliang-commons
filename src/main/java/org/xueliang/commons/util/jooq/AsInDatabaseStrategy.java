package org.xueliang.commons.util.jooq;

import org.apache.commons.lang3.StringUtils;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;

public class AsInDatabaseStrategy extends DefaultGeneratorStrategy {

    /**
     * Override these to specify what a setter in Java should look like. Setters
     * are used in TableRecords, UDTRecords, and POJOs. This example will name
     * setters "set[NAME_IN_DATABASE]"
     */
    @Override
    public String getJavaSetterName(Definition definition, Mode mode) {
        return "set" + StringUtils.capitalize(definition.getOutputName());
    }

    /**
     * Just like setters...
     */
    @Override
    public String getJavaGetterName(Definition definition, Mode mode) {
        return "get" + StringUtils.capitalize(definition.getOutputName());
    }
}
