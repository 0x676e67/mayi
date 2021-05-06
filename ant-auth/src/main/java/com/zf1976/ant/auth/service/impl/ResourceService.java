package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf1976.ant.auth.pojo.ResourceTree;
import com.zf1976.ant.upms.biz.dao.SysResourceDao;
import com.zf1976.ant.upms.biz.pojo.po.SysResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mac
 * @date 2021/4/26
 */
@Service
public class ResourceService extends ServiceImpl<SysResourceDao, SysResource> {


    public ResourceTree generatorTree(){
        final List<SysResource> resourceList = super.lambdaQuery().list();
        return new ResourceTree(resourceList);
    }
}
