package com.zf1976.ant.common.core.util;


import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author mac
 * Create by Ant on 2020/9/29 23:06
 */
public final class RequestUtil extends RequestContextHolder {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUtil.class);
    private static final String UNKNOWN_HOST = "UNKNOWN HOST";
    private static final String UNKNOWN = "unknown";
    private static final String REGION = "intranet";
    private static final int IP_LENGTH = 15;
    private static final HttpClient HTTP_CLIENT;
    private static final String IP_API;

    static {
        IP_API = SpringContextHolder.getProperties("ip.region.base-url");
        HTTP_CLIENT = HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_1_1)
                                .connectTimeout(Duration.ofMillis(500))
                                .build();
    }

    /**
     * 获取真实ip地址
     *
     * @return ip
     */
    public static String getIpAddress() {
        final HttpServletRequest request = getRequest();
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (LOG.isDebugEnabled()) {
            LOG.debug("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > IP_LENGTH) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!(UNKNOWN.equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 获取ip区域
     *
     * @param ip ip
     * @return 区域
     */
    public static String getIpRegion(String ip) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                                           .version(HttpClient.Version.HTTP_1_1)
                                           .GET()
                                           .timeout(Duration.ofMillis(500))
                                           .uri(URI.create(IP_API + ip))
                                           .build();
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return getRegionResult(response);
        } catch (IOException | InterruptedException ignored) {
            return UNKNOWN_HOST;
        }
    }

    /**
     * 获取ip区域
     *
     * @return region
     */
    public static String getIpRegion() {
        try {
            String ipRegion = IpUtil.getInterIP();
            return getIpRegion(ipRegion);
        } catch (Exception e) {
            LOG.error("get ip region error", e.getCause());
            return UNKNOWN;
        }
    }

    /**
     * 获取响应内容
     *
     * @param response response
     * @return result
     */
    @SuppressWarnings("rawtypes")
    private static String getRegionResult(HttpResponse<String> response) {
        String resultJson = JSONUtil.toJsonString(response.body());
        Map dataMap = JSONUtil.readValue(resultJson, Map.class);
        if (!CollectionUtils.isEmpty(dataMap)) {
            return (String) ((Map) ((List) dataMap.get("data")).get(0)).get("location");
        }
        return UNKNOWN;
    }

    /**
     * 获取请求对象
     *
     * @return request
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = null;
        try {
            servletRequestAttributes = getServletRequestAttrs();
            Assert.notNull(servletRequestAttributes, "servlet request cannot been null");
            return servletRequestAttributes.getRequest();
        } catch (Exception ignored) {
            return (HttpServletRequest) servletRequestAttributes;
        }

    }


    /**
     * 获取User-Agent名称
     *
     * @return User-Agent名称
     */
    public static String getUserAgent() {
        final HttpServletRequest request = getRequest();
        String header = request.getHeader(HttpHeaders.USER_AGENT);
        return UserAgent.parseUserAgentString(header)
                        .getBrowser()
                        .getName();
    }

    /**
     * 获取操作系统类型
     *
     * @return 操作系统名称
     */
    public static String getOpsSystemType() {
        final HttpServletRequest request = getRequest();
        String header = request.getHeader(HttpHeaders.USER_AGENT);
        return UserAgent.parseUserAgentString(header)
                        .getOperatingSystem()
                        .getName();
    }

    /**
     * 获取请求属性
     *
     * @return /
     */
    public static ServletRequestAttributes getServletRequestAttrs(){
        final RequestAttributes reqAttrs = RequestContextHolder.getRequestAttributes();
        if(Objects.nonNull(reqAttrs)){
            return (ServletRequestAttributes) reqAttrs;
        }
        return null;
    }

    /**
     * 获取远程主机
     *
     * @return /
     */
    public static String getRemoteHost(){
        final HttpServletRequest request = getRequest();
        return request.getRemoteHost();
    }

    /**
     * 获取本机地址
     *
     * @return /
     */
    public static String getLocalAddress() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return UNKNOWN;
        }
        byte[] bytes = address.getAddress();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i>0) {
                builder.append(".");
            }
            builder.append(bytes[i] & 0xff);
        }
        return builder.toString();
    }

}
