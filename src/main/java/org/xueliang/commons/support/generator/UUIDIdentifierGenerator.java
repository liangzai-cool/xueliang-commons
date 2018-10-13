package org.xueliang.commons.support.generator;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class UUIDIdentifierGenerator extends AbstractIdentifierGenerator {

    @Override
    public String nextId(String flag) {
        String uuid = UUID.randomUUID().toString();
        return StringUtils.isEmpty(flag) ? uuid : flag + uuid;
    }

}
