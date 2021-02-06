package com.zf1976.ant.upms.biz.pojo.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author mac
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuBuildVO {

    private String name;

    private String path;

    private Boolean hidden;

    private String redirect;

    private String component;

    private Boolean alwaysShow;

    private MenuMetaVo meta;

    private List<MenuBuildVO> children;

    public void intiMeta(String title, String icon, boolean noCache) {
        final MenuMetaVo menuMetaVo = new MenuMetaVo();
        menuMetaVo.setIcon(icon);
        menuMetaVo.setTitle(title);
        menuMetaVo.setNoCache(noCache);
        this.meta = menuMetaVo;
    }

    public MenuBuildVO(MenuMetaVo menuMetaVo) {
        this.meta = menuMetaVo;
    }

    public MenuBuildVO() {}

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MenuMetaVo {

        private String title;

        private String icon;

        private Boolean noCache;

    }
}

