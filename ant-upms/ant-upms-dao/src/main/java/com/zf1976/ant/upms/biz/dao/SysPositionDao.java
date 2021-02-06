package com.zf1976.ant.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.ant.upms.biz.pojo.po.SysPosition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 岗位(SysJob)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:03
 */
@Repository
public interface SysPositionDao extends BaseMapper<SysPosition> {

    /**
     * 查询用户岗位
     *
     * @param userId 用户id
     * @return 岗位collection
     */
    List<SysPosition> selectListByUserId(@Param("userId") long userId);

    /**
     * 根据id 删除user-job
     * @param ids id
     */
    void deleteUserRelationById(@Param("ids") Collection<Long> ids);

}
