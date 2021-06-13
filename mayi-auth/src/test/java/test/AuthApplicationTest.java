package test;

import com.zf1976.mayi.auth.AuthApplication;
import com.zf1976.mayi.auth.RemoteUserService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.constants.SecurityConstants;
import com.zf1976.mayi.upms.biz.pojo.User;

import com.zf1976.mayi.auth.service.OAuth2ClientService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author mac
 * @date 2021/2/5
 **/
@SpringBootTest(classes = {AuthApplication.class})
public class AuthApplicationTest {

    @Autowired
    private OAuth2ClientService clientService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Test
    public void sessionTest() {
        DataResult<User> r = remoteUserService.getUser("admin", SecurityConstants.FROM_IN);
    }
}
