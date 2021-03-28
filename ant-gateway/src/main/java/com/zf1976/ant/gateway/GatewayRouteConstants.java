package com.zf1976.ant.gateway;

/**
 * @author ant
 * Create by Ant on 2021/3/6 8:30 AM
 */
public interface GatewayRouteConstants {

    /**
     * 管理中心路由
     */
    String ADMIN_ROUTE = "/api/**";

    /**
     * 认证中心路由
     */
    String AUTH_ROUTE = "/oauth/**";

    /**
     * 自定义路由
     */
    String CUSTOMER_ROUTE = "/service/v1/**";

    String ADMIN_ROUTE_PREFIX = "/admin/v1";

    String TEST_PREFIX = "/api";
}
