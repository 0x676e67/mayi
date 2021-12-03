/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package com.zf1976.mayi.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.mayi.upms.biz.pojo.po.SysPosition;
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
     * 根据id 删除user-job
     * @param ids id
     */
    void deleteRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询用户岗位
     *
     * @param userId 用户id
     * @return {@link List<SysPosition>}
     */
    List<SysPosition> selectListByUserId(@Param("userId") Long userId);

    /**
     * 根据用户名查询用户岗位
     *
     * @param username 用户名
     * @return {@link  List<SysPosition>}
     */
    List<SysPosition> selectByUsername(@Param("username") String username);

}
