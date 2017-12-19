package org.xueliang.commons.support.generator;

import java.util.UUID;

public class UUIDIdentifierGenerator implements IdentifierGenerator {

    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String nextId(String flag) {
        return flag + nextId();
    }

}
