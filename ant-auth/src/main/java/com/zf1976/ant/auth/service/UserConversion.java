package com.zf1976.ant.auth.service;

import com.zf1976.ant.auth.pojo.vo.UserInfoVo;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * @author ant
 * Create by Ant on 2020/9/10 4:12 下午
 */
@Mapper()
public interface UserConversion {
    UserConversion INSTANCE = Mappers.getMapper(UserConversion.class);

    /**
     * 转 认证信息
     *
     * @param sysUser 系统用户
     * @return /
     */
    UserInfoVo convert(SysUser sysUser);
}
