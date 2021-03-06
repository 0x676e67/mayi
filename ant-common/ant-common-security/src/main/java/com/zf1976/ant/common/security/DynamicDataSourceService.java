package com.zf1976.ant.common.security;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.common.component.action.ActionsScanner;
import com.zf1976.ant.common.component.load.annotation.CaffeinePut;
import com.zf1976.ant.common.security.annotation.Authorize;
import com.zf1976.ant.upms.biz.dao.SysPermissionDao;
import com.zf1976.ant.upms.biz.dao.SysResourceDao;
import com.zf1976.ant.upms.biz.pojo.po.SysPermission;
import com.zf1976.ant.upms.biz.pojo.po.SysResource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
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

    public DynamicDataSourceService(SysResourceDao sysResourceDao, ActionsScanner actionsScanner) {
        this.actionsScanner = actionsScanner;
        this.sysResourceDao = sysResourceDao;
        this.matcherMethodMap = new ConcurrentHashMap<>(16);
        this.allowMethodSet = new CopyOnWriteArraySet<>();
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

    @CaffeinePut(namespace = "dynamic", key = "loadDataSource")
    public Map<String, Collection<ConfigAttribute>> loadDataSource() {
        Map<String, Collection<ConfigAttribute>> matcherResourceMap = new ConcurrentHashMap<>(16);
        //清空缓存
        if (!CollectionUtils.isEmpty(this.matcherMethodMap)) {
            this.matcherMethodMap.clear();
            this.allowMethodSet.clear();
        }
        // 构造完整url 资源路径
        ChainWrappers.lambdaQueryChain(this.sysResourceDao)
                     .isNull(SysResource::getPid)
                     .list()
                     .forEach(rootResource -> {
                         // 资源
                         Map<Long, String> resourceMap = new ConcurrentHashMap<>(16);
                         Map<Long, String> methodMap = new ConcurrentHashMap<>(16);
                         this.collectChildrenResourcePath(rootResource.getId(),
                                                          rootResource.getUrl(),
                                                          rootResource.getMethod(),
                                                          rootResource.getAllow(),
                                                          () -> resourceMap,
                                                          () -> methodMap,
                                                          () -> this.allowMethodSet);
                         resourceMap.forEach((id, path) -> {
                             Set<ConfigAttribute> permissions = this.baseMapper.getPermission(id)
                                                                               .stream()
                                                                               .map(attribute -> (ConfigAttribute) () -> attribute)
                                                                               .collect(Collectors.toSet());
                             matcherResourceMap.put(path, permissions);
                             this.matcherMethodMap.put(path, methodMap.get(id));
                         });
                     });

        return matcherResourceMap;
    }

    public Map<String, String> getMatcherMethodMap() {
        if (CollectionUtils.isEmpty(this.matcherMethodMap)) {
            this.loadDataSource();
        }
        return this.matcherMethodMap;
    }

    private void collectChildrenResourcePath(Long nodeId, String path, String method, Boolean allow,
                                             Supplier<Map<Long, String>> var1,
                                             Supplier<Map<Long, String>> var2,
                                             Supplier<Set<String>> var3) {
        Assert.notNull(nodeId, "nodeId cannot been null");
        Assert.notNull(var1, "supplier cannot been null");
        Assert.notNull(var2, "supplier cannot been null");
        boolean condition = true;
        List<SysResource> resources = ChainWrappers.lambdaQueryChain(this.sysResourceDao)
                                              .eq(SysResource::getPid, nodeId)
                                              .list();
        for (SysResource node : resources) {
            // 路径深搜
            String nodePath = path.concat(node.getUrl());
            this.collectChildrenResourcePath(node.getId(), nodePath, node.getMethod(),node.getAllow(), var1, var2, var3);
            condition = false;
        }
        if (condition) {
            var1.get().put(nodeId, path);
            var2.get().put(nodeId, method);
            if (allow) {
                var3.get().add(path);
            }
        }
    }

    private void collectPermissions(Long id, Supplier<Collection<ConfigAttribute>> collectionSupplier) {
        Assert.notNull(id, "permission id cannot been null");
        Assert.notNull(collectionSupplier, "supplier cannot been null");
        super.lambdaQuery()
             .select(SysPermission::getId, SysPermission::getValue)
             .eq(SysPermission::getPid, id)
             .list()
             .forEach(sysPermission -> {
                 if (sysPermission.getValue() != null) {
                     collectionSupplier.get().add(sysPermission::getValue);
                 }
                 this.collectPermissions(sysPermission.getId(), collectionSupplier);
             });
    }

    @CaffeinePut(namespace = "dynamic", key = "allow")
    public Set<String> getAllowUri() {
        return this.allowMethodSet;
    }

}
