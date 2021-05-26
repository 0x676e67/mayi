package com.zf1976.ant.auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.ant.auth.pojo.po.SysResource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author mac
 * @date 2020/12/25
 **/
@Repository
public interface SysResourceDao extends BaseMapper<SysResource> {

}
