package com.zf1976.ant.common.encrypt;

import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author mac
 * @date 2021/4/6
 */
@SuppressWarnings("all")
public class MD5Encoder {

    public String encode(CharSequence charSequence) {
        return DigestUtils.md5DigestAsHex(charSequence.toString()
                                                      .getBytes());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encode = this.encode(rawPassword);
        return ObjectUtils.nullSafeEquals(encodedPassword, encode);
    }
}
