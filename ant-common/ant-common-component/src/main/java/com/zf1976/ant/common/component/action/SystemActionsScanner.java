package com.zf1976.ant.common.component.action;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mac
 * @date 2020/12/29
 **/
@Component
public class SystemActionsScanner implements ActionsScanner {

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    @Override
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Override
    public Map<Class<?>, String> doScan(String basePackage) {
        Map<Class<?>, String> classFileMap = new HashMap<>(16);
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils
                    .convertClassNameToResourcePath(SystemPropertyUtils
                                                            .resolvePlaceholders(basePackage))
                    + "/**/*.class";
            Resource[] resources = this.resourcePatternResolver
                    .getResources(packageSearchPath);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    String filePath = null;
                    try {
                        filePath = metadataReader.getResource()
                                                 .getFile()
                                                 .getAbsolutePath()
                                                 .replace("\\target\\classes", "\\src\\main\\java")
                                                 .replace(".class", ".java");
                    } catch (IOException ignored) {

                    }
                    try {
                        classFileMap.put(Class.forName(metadataReader .getClassMetadata().getClassName()),filePath );
                    } catch (ClassNotFoundException ignored) {

                    }
                }
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "I/O failure during classpath scanning", ex);
        }
        return classFileMap;
    }
}
