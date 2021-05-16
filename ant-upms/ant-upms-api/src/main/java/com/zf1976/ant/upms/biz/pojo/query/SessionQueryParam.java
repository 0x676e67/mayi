package com.zf1976.ant.upms.biz.pojo.query;


/**
 * @author mac
 * @date 2021/1/23
 **/
public class SessionQueryParam extends AbstractQueryParam {

    private String filter;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "SessionQueryParam{" +
                "filter='" + filter + '\'' +
                '}';
    }
}
