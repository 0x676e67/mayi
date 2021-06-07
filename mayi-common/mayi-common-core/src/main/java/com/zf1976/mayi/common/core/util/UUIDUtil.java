package com.zf1976.mayi.common.core.util;


import org.springframework.util.AlternativeJdkIdGenerator;

import java.math.BigInteger;
import java.util.Locale;
import java.util.UUID;

/**
 * @author mac
 * @date 2021/5/17
 */
public class UUIDUtil {

    private static final AlternativeJdkIdGenerator ALTERNATIVE_JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();

    public static UUID fromString32(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            throw new NullPointerException("uuid can't be null");
        } else if (uuid.length() == 36) {
            return UUID.fromString(uuid);
        } else if (uuid.length() == 32) {
            return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 24) + "-" + uuid.substring(24, 32));
        } else {
            return null;
        }
    }

    public static BigInteger uuidToBigInteger(UUID uuid) {
        BigInteger value1 = BigInteger.valueOf(uuid.getMostSignificantBits());
        BigInteger value2 = BigInteger.valueOf(uuid.getLeastSignificantBits());
        return value1.compareTo(value2) < 0 ? value2.multiply(value2)
                                                    .add(value1) : value1.multiply(value1)
                                                                         .add(value1)
                                                                         .add(value2);
    }

    public static String getUuid() {
        return ALTERNATIVE_JDK_ID_GENERATOR.generateId()
                                           .toString();
    }

    public static String getUuid32() {
        return getUuid().replace("-", "");
    }

    public static String getUpperCaseUuid() {
        return getUuid32().toUpperCase(Locale.ROOT);
    }

}
