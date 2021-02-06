package com.zf1976.ant.common.core.dev;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.config")
public class FileProperties {

    private FileProperties.Relative relative;

    private FileProperties.Real real;

    /**
     * 文件大小
     */
    private String fileMaxSize;

    /**
     * 头像大小
     */
    private String avatarMaxSize;

    /**
     * 工作文件路径
     */
    private String workFilePath;

    /**
     * 头像完整路径
     */
    public static String avatarRealPath;

    /**
     * 文件完整路径
     */
    public static String fileRealPath;


    public static String getAvatarRealPath() {
        return avatarRealPath;
    }

    public static void setAvatarRealPath(String avatarRealPath) {
        FileProperties.avatarRealPath = avatarRealPath;
    }

    public static String getFileRealPath() {
        return fileRealPath;
    }

    public static void setFileRealPath(String fileRealPath) {
        FileProperties.fileRealPath = fileRealPath;
    }

    @Data
    public static class Relative{

        /**
         * 头像url
         */
        private String avatarUrl;

        /**
         * 文件url
         */
        private String fileUrl;
    }

    @Data
    public static class Real{

        /**
         * 头像相对路径
         */
        private String avatarPath;

        /**
         * 文件相对路径
         */
        private String filePath;
    }
}
