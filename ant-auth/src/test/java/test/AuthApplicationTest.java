package test;

import com.zf1976.ant.auth.AuthApplication;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.auth.service.impl.ResourceService;
import com.zf1976.ant.auth.system.MySqlStrategyBackup;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.dao.SysUserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;


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

    @Autowired
    DataSource dataSource;

    @Value("${sql-backup.path}")
    String path;

    @Test
    public void backupTest(){
        final MySqlStrategyBackup strategyBackupHolder = new MySqlStrategyBackup(this.dataSource);
        final boolean backup = strategyBackupHolder.backup(System.getProperty("user.home"), this.path);
        if (backup){
            System.out.println("备份成功");
        }
        if (strategyBackupHolder.recover("/Users/mac/work/backup/2021-05-14-21-32-46.sql")) {
            System.out.println("恢复成功");
        }
    }

    public static void main(String[] args) {
        final Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            System.out.println(entry.getKey() + "-" + entry.getValue());
        }
    }
}
