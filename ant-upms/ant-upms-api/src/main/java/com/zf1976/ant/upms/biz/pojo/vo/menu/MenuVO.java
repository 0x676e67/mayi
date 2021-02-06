package com.zf1976.ant.upms.biz.pojo.vo.menu;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/11/12
 **/
@Data
public class MenuVO {

    /**
     * id
     */
    private Long id;

    /**
     * 子节点
     */
    private List<MenuVO> children;

    /**
     * 上级菜单ID
     */
    private Long pid;

    /**
     * 菜单类型
     */
    private Integer type;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 排序
     */
    private Integer menuSort;

    /**
     * 是否存在子菜单
     */
    private Boolean hasChildren;

    /**
     * 是否叶子
     */
    private Boolean leaf;

    /**
     * 图标
     */
    private String icon;

    /**
     * 路由地址
     */
    private String routePath;

    /**
     * 是否外链
     */
    private Boolean iframe;

    /**
     * 缓存
     */
    private Boolean cache;

    /**
     * 隐藏
     */
    private Boolean hidden;

    /**
     * 权限
     */
    private String permission;

    /**
     * 创建日期
     */
    private Date createTime;

}
