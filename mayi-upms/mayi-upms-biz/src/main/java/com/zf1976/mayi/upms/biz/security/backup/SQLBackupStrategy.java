package com.zf1976.mayi.upms.biz.security.backup;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author ant
 * Create by Ant on 2021/6/8 4:21 上午
 */
@SuppressWarnings("unused")
public interface SQLBackupStrategy {

    /**
     * 生成备份文件
     *
     * @date 2021-05-14 18:12:23
     * @param fileDirectory 备份目录
     * @return {@link boolean}
     */
    default boolean backup(String fileDirectory) {
        return this.backup(Paths.get(fileDirectory));
    }

    /**
     * 生成备份文件
     *
     * @param fileDirectory 文件目录
     * @param more 子目录
     * @return {@link boolean}
     */
    default boolean backup(String fileDirectory, String more) {
        return this.backup(Paths.get(fileDirectory, more));
    }

    /**
     * 生成备份文件
     *
     * @param path path
     * @return {@link boolean}
     */
    default boolean backup(Path path) {
        return this.backup(path.toFile());
    }


    /**
     * 生成备份文件
     *
     * @param fileDirectory 文件目录对象
     * @return {@link boolean}
     */
    boolean backup(File fileDirectory);

    /**
     * 根据备份文件恢复
     *
     * @param absolutePath 备份文件绝对路径
     * @return {@link boolean}
     */
    default boolean recover(String absolutePath) {
        return this.recover(Paths.get(absolutePath));
    }

    /**
     * 根据备份文件恢复
     *
     * @param parentPath 父目录
     * @param moreChildrenPath 子目录
     * @return {@link boolean}
     */
    default boolean recover(String parentPath, String ...moreChildrenPath) {
        return this.recover(Paths.get(parentPath, moreChildrenPath));
    }

    /**
     * 根据备份文件恢复
     *
     * @param path path
     * @return {@link boolean}
     */
    default boolean recover(Path path) {
        return this.recover(path.toFile());
    }

    /**
     * 根据备份文件恢复
     *
     * @param absolutePathFile 绝对路径文件
     * @return {@link boolean}
     */
    boolean recover(File absolutePathFile);

    /**
     * SQL逻辑文件批处理恢复
     *
     * @param inputStream 文件流
     * @return {@link boolean}
     */
    boolean recover(InputStream inputStream);


    /**
     * 获取链接的数据库
     *
     * @return {@link String}
     */
    String getDatabase();

}
