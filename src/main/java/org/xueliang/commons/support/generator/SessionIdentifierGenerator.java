package org.xueliang.commons.support.generator;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 用于生成 session id
 * @author XueLiang
 * @date 2018/10/19 20:50:12
 */
public class SessionIdentifierGenerator extends AbstractIdentifierGenerator {

    private static final SecureRandom random = new SecureRandom();

    @Override
    public String nextId(String flag) {
        String randomValue = new BigInteger(200, random).toString(32);
        return StringUtils.isEmpty(flag) ? randomValue : flag + randomValue;
    }
}
