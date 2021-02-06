package com.zf1976.ant.upms.biz.pojo.vo.user;


import com.zf1976.ant.upms.biz.pojo.enums.GenderEnum;
import com.zf1976.ant.upms.biz.pojo.vo.dept.DepartmentVO;
import lombok.Data;

import java.util.Date;

/**
 * @author Windows
 */
@Data
public class UserVO {

    /**
     * id
     */
    private Long id;

    /**
     * 部门
     */
    private DepartmentVO department;

    /**
     * 头像
     */
    private String avatarName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别
     */
    private GenderEnum gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：1启用、0禁用
     */
    private Boolean enabled;

    /**
     * 创建日期
     */
    private Date createTime;

}

