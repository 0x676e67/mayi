package com.zf1976.mayi.upms.biz.security.pojo;

import java.util.Date;

/**
 * @author ant
 * Create by Ant on 2021/6/8 6:20 上午
 */
public class BackupFile {

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 是否为目录
     */
    private Boolean isDirectory;

    /**
     * 可读
     */
    private Boolean canRead;

    /**
     * 可写
     */
    private Boolean canWrite;

    /**
     * 是否隐藏文件
     */
    private Boolean hidden;

    /**
     * 最后更改时间
     */
    private Date lastModifyDate;

    /**
     * MD5值
     */
    private String md5;

    public String getMd5() {
        return md5;
    }

    public BackupFile setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getName() {
        return name;
    }

    public BackupFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getSize() {
        return size;
    }

    public BackupFile setSize(String size) {
        this.size = size;
        return this;
    }

    public Boolean getDirectory() {
        return isDirectory;
    }

    public BackupFile setDirectory(Boolean directory) {
        isDirectory = directory;
        return this;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public BackupFile setCanRead(Boolean canRead) {
        this.canRead = canRead;
        return this;
    }

    public Boolean getCanWrite() {
        return canWrite;
    }

    public BackupFile setCanWrite(Boolean canWrite) {
        this.canWrite = canWrite;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public BackupFile setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public BackupFile setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
        return this;
    }

    @Override
    public String toString() {
        return "BackupFile{" +
                "name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", isDirectory=" + isDirectory +
                ", canRead=" + canRead +
                ", canWrite=" + canWrite +
                ", hidden=" + hidden +
                ", date=" + lastModifyDate +
                '}';
    }
}
