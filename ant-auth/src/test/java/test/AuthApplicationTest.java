package test;

import com.zf1976.ant.auth.AuthApplication;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.auth.service.impl.ResourceService;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.dao.SysUserDao;
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
    SysUserDao userDao;

    @Autowired
    SysRoleDao roleDao;

    @Autowired
    ResourceService service;


    @Test
    public void sessionTest(){
        System.out.println(roleDao.selectListByUserId(1L));

    }


}
