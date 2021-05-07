package test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.auth.AuthApplication;
import com.zf1976.ant.auth.dao.ClientDetailsDao;
import com.zf1976.ant.auth.pojo.ResourceTree;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.auth.service.impl.ResourceService;
import com.zf1976.ant.upms.biz.dao.SysResourceDao;
import com.zf1976.ant.upms.biz.pojo.po.SysResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author mac
 * @date 2021/2/5
 **/
@SpringBootTest(classes = {AuthApplication.class})
public class AuthApplicationTest {

    @Autowired
    private OAuth2ClientService clientService;

    @Autowired
    ResourceService service;

    @Test
    public void sessionTest(){
        System.out.println(clientService.clientDetailsIPage(new Page<>(1, 10)).getRecords());
        System.out.println(service.getResourceTree());
    }
}
