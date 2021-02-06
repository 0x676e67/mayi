package com.zf1976.ant.upms.biz.pojo.dto.menu;

import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author Windows
 */
@Data
public class MenuDTO {

    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    protected Long id;

    /**
     * 上级菜单ID
     */
    private Long pid;

    /**
     * 菜单类型
     */
    @NotNull
    protected Integer type;

    /**
     * 菜单标题
     */
    @NotBlank
    protected String title;

    /**
     * 排序
     */
    @NotNull
    protected Integer menuSort;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 组件名
     */
    private String componentName;

    /**
     * 缓存
     */
    private Boolean cache;

    /**
     * 权限
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 路由地址
     */
    private String routePath;

    /**
     * 隐藏
     */
    private Boolean hidden;

    /**
     * 是否外链
     */
    private Boolean iframe;
}
