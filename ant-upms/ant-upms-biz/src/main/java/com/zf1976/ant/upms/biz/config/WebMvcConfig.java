package com.zf1976.ant.upms.biz.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.power.common.util.FileUtil;
import com.zf1976.ant.upms.biz.handle.MetaDataHandler;
import com.zf1976.ant.upms.biz.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * @author mac
 * Create by Ant on 2020/8/30 下午1:58
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer, InitializingBean {

    public WebMvcConfig(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    private final FileProperties fileProperties;

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations and resource handler requests.
     * Interceptors can be registered to apply to all requests or be limited
     * to a subset of URL patterns.
     *
     * @param registry registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MetaDataHandler());
        return globalConfig;
    }

    /**
     * 前后端分离跨域设置
     *
     * @param registry 跨域注册
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**" )
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*")
                .maxAge(1800);
    }

    /**
     * 映射静态资源路径
     *
     * @param registry 路径注册
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        final FileProperties.Relative relative = this.fileProperties.getRelative();
        final FileProperties.Real real = this.fileProperties.getReal();
        final String avatarPath = resolveSystemPath(real.getAvatarPath());
        final String filePath = resolveSystemPath(real.getFilePath());
        registry.addResourceHandler("/static/**", relative.getAvatarUrl(), relative.getFileUrl())
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/", avatarPath, filePath);

    }

    /**
     * 获取完整文件路径
     *
     * @param realPath 配置路径
     * @return /
     */
    private String resolveSystemPath(String realPath) {
        final String homePath = System.getProperty("user.home");
        final File file = new File(homePath + this.fileProperties.getWorkFilePath(), realPath);
        FileUtil.mkdirs(file.getAbsolutePath());
        return ResourceUtils.FILE_URL_PREFIX + file.getAbsolutePath() + AntPathMatcher.DEFAULT_PATH_SEPARATOR;
    }

    @Override
    public void afterPropertiesSet() {
        final String homePath = System.getProperty("user.home");
        final FileProperties.Real real = this.fileProperties.getReal();
        FileProperties.setAvatarRealPath(homePath + this.fileProperties.getWorkFilePath() + real.getAvatarPath());
        FileProperties.setFileRealPath(homePath + this.fileProperties.getWorkFilePath() + real.getFilePath());
    }
}
