package com.zf1976.mayi.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.mayi.upms.biz.pojo.po.SysDict;
import org.springframework.stereotype.Repository;

/**
 * 数据字典(SysDict)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:04
 */
@Repository
public interface SysDictDao extends BaseMapper<SysDict> {

}
