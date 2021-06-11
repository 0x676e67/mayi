package test;

import com.zf1976.mayi.common.security.support.ResourceTrie;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import com.zf1976.mayi.upms.biz.SystemApplication;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.Map;

/**
 * @author mac
 * @date 2021/5/17
 */
@SpringBootTest(classes = {SystemApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SystemApplicationTest {

    @Autowired
    DynamicDataSourceService dynamicDataSourceService;

    @Test
    public void sessionTest() {
        ResourceTrie resourceTrie = new ResourceTrie();
        String url = "/api/roles/delete";
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Map<String, Collection<String>> stringCollectionMap = this.dynamicDataSourceService.loadDynamicDataSource();

        long mapStart = System.currentTimeMillis();
        for (Map.Entry<String, Collection<String>> entry : stringCollectionMap.entrySet()) {
            if (entry.getKey().equals(url)) {
                System.out.println("map count: " + (System.currentTimeMillis() - mapStart));
                break;
            }
        }

    }

}
