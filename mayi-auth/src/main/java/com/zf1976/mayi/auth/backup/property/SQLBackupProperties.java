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

    private Integer dayTotal;

    private Integer fileCountSize;

    public Integer getFileCountSize() {
        return fileCountSize;
    }

    public SQLBackupProperties setFileCountSize(Integer fileCountSize) {
        this.fileCountSize = fileCountSize;
        return this;
    }

    public Integer getDayTotal() {
        return dayTotal;
    }

    public SQLBackupProperties setDayTotal(Integer dayTotal) {
        this.dayTotal = dayTotal;
        return this;
    }

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
