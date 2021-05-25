package test;

import com.zf1976.ant.auth.AuthApplication;
import com.zf1976.ant.auth.pojo.ResourceLink;
import com.zf1976.ant.auth.service.impl.DynamicDataSourceService;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.auth.system.MySqlStrategyBackup;
import com.zf1976.ant.common.encrypt.EncryptUtil;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.dao.SysUserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
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
    SysUserDao userDao;

    @Autowired
    SysRoleDao roleDao;


    @Autowired
    DataSource dataSource;

    @Value("${sql-backup.path}")
    String path;

    @Autowired
    DynamicDataSourceService dynamicDataSourceService;


    @Test
    public void sessionTest() {
        try {
            System.out.println(EncryptUtil.encryptForRsaByPublicKey("123456"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void backupTest() {
        final MySqlStrategyBackup strategyBackupHolder = new MySqlStrategyBackup(this.dataSource);
        final boolean backup = strategyBackupHolder.backup(System.getProperty("user.home"), this.path);
        if (backup) {
            System.out.println("备份成功");
        }
        if (strategyBackupHolder.recover("/Users/mac/work/backup/2021-05-14-21-32-46.sql")) {
            System.out.println("恢复成功");
        }
    }
    @Test
    public void resourceTreeTest() {
        List<ResourceLink> resourceLinkList = this.dynamicDataSourceService.selectResourceLinkList();
        System.out.println(resourceLinkList);
    }
}
