package com.zf1976.ant.auth.controller.security;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.auth.pojo.ResourceLink;
import com.zf1976.ant.auth.pojo.ResourceNode;
import com.zf1976.ant.auth.pojo.po.SysResource;
import com.zf1976.ant.auth.service.impl.DynamicDataSourceService;
import com.zf1976.ant.common.core.foundation.DataResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author mac
 * @date 2021/5/12
 */
@RestController
@RequestMapping(value =
        "/oauth/security/resource"
)
public class ResourceController {

    private final DynamicDataSourceService dynamicDataSourceService;

    public ResourceController(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('admin')")
    public DataResult<IPage<ResourceNode>> selectResourceByPage(@RequestBody Page<SysResource> page) {
        return DataResult.success(this.dynamicDataSourceService.selectResourceNodeByPage(page));
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public DataResult<List<ResourceLink>> selectResourceLinkList() {
        return DataResult.success(this.dynamicDataSourceService.selectResourceLinkList());
    }


}
