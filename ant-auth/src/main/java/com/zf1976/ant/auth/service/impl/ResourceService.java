package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf1976.ant.auth.pojo.ResourceNode;
import com.zf1976.ant.auth.pojo.ResourceTree;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.upms.biz.dao.SysResourceDao;
import com.zf1976.ant.upms.biz.pojo.po.SysResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/4/26
 */
@Service
public class ResourceService extends ServiceImpl<SysResourceDao, SysResource> {


    public List<ResourceNode> getResourceTree(){
        final List<SysResource> resourceList = super.lambdaQuery().list();
        return this.generatorResourceTree(resourceList);
    }

    /**
     * 构建资源树
     *
     * @date 2021-05-07 08:42:41
     * @param resourceList 资源列表
     * @return {@link List<ResourceNode>}
     */
    private List<ResourceNode> generatorResourceTree(List<SysResource> resourceList) {
        final List<ResourceNode> nodes = new ArrayList<>();
        final Collection<ResourceNode> nodeCollection = resourceList.stream()
                                                                    .map(ResourceNode::new)
                                                                    .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
        for (ResourceNode var1 : nodeCollection) {
            // 根节点
            if (var1.getPid() == null) {
                nodes.add(var1);
            }
            for (ResourceNode var2 : nodeCollection) {
                if (var1.getId().equals(var2.getPid())) {
                    if (var1.getChildren() == null) {
                        var1.setChildren(new ArrayList<>());
                    }
                    // 添加子节点
                    var1.getChildren().add(var2);
                }
            }
        }
        return nodes;
    }
}
