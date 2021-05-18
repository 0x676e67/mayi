package com.zf1976.ant.common.component.mail;

import com.zf1976.ant.common.component.mail.impl.ValidateServiceImpl;

/**
 * @author mac
 */
public class ValidateFactory {

    private static ValidateService validateService;

    private ValidateFactory() {}

    public static ValidateService getInstance() {
        if (validateService == null) {
            synchronized (ValidateFactory.class) {
                if (validateService == null) {
                    validateService = ValidateServiceImpl.getInstance();
                }
            }
        }
        return validateService;
    }
}
