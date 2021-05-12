package com.zf1976.ant.auth.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.google.common.collect.Lists;
import com.zf1976.ant.common.component.action.ActionsScanner;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.core.constants.KeyConstants;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.security.annotation.Authorize;
import com.zf1976.ant.common.security.property.SecurityProperties;
import com.zf1976.ant.auth.dao.SysPermissionDao;
import com.zf1976.ant.auth.dao.SysResourceDao;
import com.zf1976.ant.auth.pojo.po.SysPermission;
import com.zf1976.ant.auth.pojo.po.SysResource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * @author mac
 * @date 2020/12/26
 **/
@Service
public class DynamicDataSourceService extends ServiceImpl<SysPermissionDao, SysPermission> {

    private final ActionsScanner actionsScanner;
    private final SysResourceDao sysResourceDao;
    private final Map<String, String> matcherMethodMap;
    private final Set<String> allowMethodSet;
    private final SecurityProperties securityProperties;

    public DynamicDataSourceService(SysResourceDao sysResourceDao, ActionsScanner actionsScanner, SecurityProperties securityProperties) {
        this.actionsScanner = actionsScanner;
        this.sysResourceDao = sysResourceDao;
        this.securityProperties = securityProperties;
        this.matcherMethodMap = new HashMap<>(16);
        this.allowMethodSet = new HashSet<>(16);
    }

    /**
     * 加载动态数据源资源 （URI--Permissions）
     *
     * @date 2021-05-05 19:53:43
     * @return {@link Map}
     */
    @CachePut(namespace = Namespace.DYNAMIC, key = KeyConstants.RESOURCES)
    public Map<String, Collection<String>> loadDynamicDataSource() {
        Map<String, Collection<String>> matcherResourceMap = new HashMap<>(16);
        //清空缓存
        if (!CollectionUtils.isEmpty(this.matcherMethodMap) || !CollectionUtils.isEmpty(allowMethodSet)) {
            this.matcherMethodMap.clear();
            this.allowMethodSet.clear();
        }
        // 构造完整url 资源路径
        ChainWrappers.lambdaQueryChain(this.sysResourceDao)
                     .isNull(SysResource::getPid)
                     .list()
                     .forEach(resource -> {
                         // 资源
                         Map<Long, String> resourceLink = new HashMap<>(16);
                         Map<Long, String> methodMap = new HashMap<>(16);
                         this.makeResourceLink(resource, resourceLink, methodMap);
                         resourceLink.forEach((id, path) -> {
                             // 权限值
                             List<String> permissions = this.baseMapper.getPermission(id)
                                                                       .stream()
                                                                       .distinct()
                                                                       .collect(Collectors.toList());
                             matcherResourceMap.put(path, permissions);
                             this.matcherMethodMap.put(path, methodMap.get(id));
                         });
                     });
        return matcherResourceMap;
    }

    /**
     * 构造资源路径
     *
     * @date 2021-05-05 19:56:08
     * @param parent 节点资源
     * @param resourceLinkMap id-path映射
     * @param methodMap id-method映射
     */
    private void makeResourceLink(SysResource parent, Map<Long, String> resourceLinkMap, Map<Long, String> methodMap) {
        var parentId = parent.getId();
        var parentUri = parent.getUri();
        var parentMethod = parent.getMethod();
        // 判断是否make
        var condition = true;
        List<SysResource> resources = ChainWrappers.lambdaQueryChain(this.sysResourceDao)
                                                   .eq(SysResource::getPid, parentId)
                                                   .list();
        if (resources != null) {
            for (SysResource child : resources) {
                // 路径深搜
                child.setUri(parentUri.concat(child.getUri()));
                // 继续make
                this.makeResourceLink(child, resourceLinkMap, methodMap);
                // make完成
                condition = false;
            }
        }
        if (condition) {
            resourceLinkMap.put(parentId, parentUri);
            methodMap.put(parentId, parentMethod);
            // 是否allow
            if (parent.getAllow()) {
                this.allowMethodSet.add(parentUri);
            }
        }
    }

    @CachePut(namespace = Namespace.DYNAMIC, key = KeyConstants.MATCH_METHOD)
    public Map<String, String> getMatcherMethodMap() {
        if (CollectionUtils.isEmpty(this.matcherMethodMap)) {
            this.loadDynamicDataSource();
        }
        return this.matcherMethodMap;
    }

    /**
     * redis 反序化回来变成set
     * @return getAllowUri
     */
    @CachePut(namespace = Namespace.DYNAMIC, key = KeyConstants.ALLOW)
    public List<String> getAllowUri() {
        CollectionUtils.mergeArrayIntoCollection(securityProperties.getIgnoreUri(), this.allowMethodSet);
        return Lists.newArrayList(this.allowMethodSet);
    }

    public void test() {
        Map<Class<?>, String> classStringMap = this.actionsScanner.doScan("com.zf1976.*.endpoint");
        for (Map.Entry<Class<?>, String> classStringEntry : classStringMap.entrySet()) {
            Class<?> aClass = classStringEntry.getKey();
            RequestMapping requestMapping = aClass.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                StringBuilder baseUri = new StringBuilder();
                for (String var1 : requestMapping.value()) {
                    baseUri.append(var1);
                }
                for (Method method : aClass.getDeclaredMethods()) {
                    StringBuilder builder = new StringBuilder(baseUri);
                    Authorize authorize = null;
                    for (Annotation methodAnnotation : method.getAnnotations()) {
                        if (methodAnnotation instanceof Authorize) {
                            authorize = (Authorize) methodAnnotation;
                        }
                        if (methodAnnotation instanceof GetMapping) {
                            GetMapping getMapping = (GetMapping) methodAnnotation;
                            for (String var2 : getMapping.value()) {
                                builder.append(var2);
                            }
                        }
                        if (methodAnnotation instanceof PostMapping) {
                            PostMapping postMapping = (PostMapping) methodAnnotation;
                            for (String var3 : postMapping.value()) {
                                builder.append(var3);
                            }
                        }

                        if (methodAnnotation instanceof PutMapping) {
                            PutMapping putMapping = (PutMapping) methodAnnotation;
                            for (String var4 : putMapping.value()) {
                                builder.append(var4);
                            }
                        }

                        if (methodAnnotation instanceof DeleteMapping) {
                            DeleteMapping deleteMapping = (DeleteMapping) methodAnnotation;
                            for (String var5 : deleteMapping.value()) {
                                builder.append(var5);
                            }
                        }

                        if (methodAnnotation instanceof PatchMapping) {
                            PatchMapping patchMapping = (PatchMapping) methodAnnotation;
                            for (String var6 : patchMapping.value()) {
                                builder.append(var6);
                            }
                        }
                    }
                    if (authorize != null) {
                        System.out.println(builder + "---" + Arrays.toString(authorize.value()));
                    } else {
                        System.out.println(builder);
                    }
                }
            }
        }
    }

}
