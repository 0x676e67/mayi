package com.zf1976.ant.auth.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.upms.biz.pojo.vo.role.RoleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author mac
 * @date 2021/5/29
 */
@FeignClient("ant-upms-bzi")
public interface RoleServiceClient {

    @GetMapping("/api/roles/all")
    DataResult<IPage<RoleVO>> selectAllRole();
}
