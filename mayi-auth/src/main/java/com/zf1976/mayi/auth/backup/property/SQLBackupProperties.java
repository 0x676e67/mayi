package com.zf1976.mayi.auth.backup.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ant
 * Create by Ant on 2021/6/8 4:58 上午
 */
@Component
@ConfigurationProperties(prefix = "sql-backup")
public class SQLBackupProperties {

    private String home;

    private String directory;

    public String getHome() {
        return home;
    }

    public SQLBackupProperties setHome(String home) {
        this.home = System.getProperty(home);
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public SQLBackupProperties setDirectory(String directory) {
        this.directory = directory;
        return this;
    }
}
