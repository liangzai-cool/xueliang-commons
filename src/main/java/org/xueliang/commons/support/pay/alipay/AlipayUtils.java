package org.xueliang.commons.support.pay.alipay;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author XueLiang
 * @date 2018/11/7 23:07
 */
public class AlipayUtils {

    private AlipayUtils () {

    }

    public static Map<String, String> alipayTradePagePayNotifyToMap(AlipayTradePagePayNotify notify) {
        try {
            Map<String, String> paramMap = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(notify.getClass());
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String name = propertyDescriptor.getName();
                Method readMethod = propertyDescriptor.getReadMethod();
                String packageName = readMethod.getDeclaringClass().getPackage().getName();
                if (packageName.startsWith("java")) {
                    continue;
                }
                if (readMethod == null) {
                    continue;
                }
                Object value = readMethod.invoke(notify);
                if (Objects.isNull(value)) {
                    continue;
                }
                paramMap.put(name, (String) value);
            }
            return paramMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AlipayTradePagePayNotify notify = new AlipayTradePagePayNotify();
        notify.setApp_id("xxx");
        System.out.println(alipayTradePagePayNotifyToMap(notify));
    }
}
