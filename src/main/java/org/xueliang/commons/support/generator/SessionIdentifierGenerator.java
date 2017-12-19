package org.xueliang.commons.support.generator;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SessionIdentifierGenerator implements IdentifierGenerator {

    private static final SecureRandom random = new SecureRandom();

    @Override
    public synchronized String nextId() {
        return new BigInteger(200, random).toString(32);
    }

    @Override
    public String nextId(String flag) {
        return nextId();
    }
}
