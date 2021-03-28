package com.zf1976.ant.auth.enhance;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

@SuppressWarnings("all")
public class MD5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        return DigestUtils.md5DigestAsHex(charSequence.toString()
                                                      .getBytes());
    }

    /**
     * 匹配
     *
     * @param rawPassword raw
     * @param encodedPassword encode
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encode = this.encode(rawPassword);
        return ObjectUtils.nullSafeEquals(encodedPassword, encode);
    }

}
