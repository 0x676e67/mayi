package test;

import com.zf1976.ant.auth.AuthApplication;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.auth.service.impl.ResourceService;
import com.zf1976.ant.common.core.util.JSONUtil;
import com.zf1976.ant.common.core.util.RequestUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

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
        String ipRegion = RequestUtil.getIpRegion();
        System.out.println(ipRegion);
    }


}
