package com.zf1976.ant.common.core.util;


import com.power.common.model.CommonResult;
import com.power.common.util.StringUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author mac
 * Create by Ant on 2020/9/29 23:06
 */
public final class RequestUtils extends RequestContextHolder {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUtils.class);
    private static final String UNKNOWN_HOST = "UNKNOWN HOST";
    private static final String UNKNOWN = "unknown";
    private static final String REGION = "intranet";
    private static final String IP_REGION_BASE_URL;
    private static final int IP_LENGTH = 15;
    private static final JacksonJsonParser JSON_PARSER = new JacksonJsonParser();

    static {
        IP_REGION_BASE_URL = SpringContextHolder.getProperties("ip.region.base-url");
    }

    /**
     * 获取真实ip地址
     *
     * @param httpServletRequest 请求
     * @return ip
     */
    public static String getIpAddress(HttpServletRequest httpServletRequest) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = httpServletRequest.getHeader("X-Forwarded-For");
        if (LOG.isDebugEnabled()) {
            LOG.debug("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = httpServletRequest.getHeader("Proxy-Client-IP");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = httpServletRequest.getHeader("HTTP_CLIENT_IP");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = httpServletRequest.getHeader("HTTP_X_FORWARDED_FOR");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = httpServletRequest.getRemoteAddr();
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
        try (CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault()) {
            httpClient.start();
            HttpGet request = new HttpGet(IP_REGION_BASE_URL + ip);
            Future<HttpResponse> future = httpClient.execute(request, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse httpResponse) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} -> get ip region is completed!", Thread.currentThread());
                    }
                }

                @Override
                public void failed(Exception e) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info(e.getMessage(), e.getCause());
                    }
                }

                @Override
                public void cancelled() {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("{} -> get ip region is cancelled!", Thread.currentThread());
                    }
                }
            });

            HttpResponse httpResponse = future.get();
            return getResult(httpResponse);
        } catch (Exception ignore) {
            LOG.error("get ip region error");
            return CommonResult.fail()
                               .getMessage();
        }
    }

    /**
     * 获取ip区域
     *
     * @param request request
     * @return region
     */
    public static String getIpRegion(HttpServletRequest request) {
        try {
            String ipRegion = getIpAddress(request);
            return getIpRegion(ipRegion);
        } catch (Exception e) {
            LOG.error("get ip region error", e.getCause());
            return StringUtil.ENMPTY;
        }
    }

    /**
     * 获取浏览器
     *
     * @param request 请求
     * @return 浏览器名称
     */
    public static String getBrowser(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.USER_AGENT);
        return UserAgent.parseUserAgentString(header)
                        .getBrowser()
                        .getName();
    }

    /**
     * 获取操作系统类型
     *
     * @param request 名称
     * @return 操作系统名称
     */
    public static String getOpsSystemType(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.USER_AGENT);
        return UserAgent.parseUserAgentString(header)
                        .getOperatingSystem()
                        .getName();
    }


    /**
     * 获取响应内容
     *
     * @param response response
     * @return result
     */
    @SuppressWarnings("unchecked")
    private static String getResult(HttpResponse response) {
        try (InputStream inputStream = response.getEntity()
                                               .getContent()) {
            byte[] bytes = new byte[inputStream.available()];
            if (inputStream.read(bytes) != -1) {
                String content = new String(bytes, StandardCharsets.ISO_8859_1);
                String gbk = new String(content.getBytes(StandardCharsets.ISO_8859_1), Charset.forName("GBK"));
                String utf8 = new String(gbk.getBytes(Charset.defaultCharset()));
                byte[] utf8Bytes = utf8.getBytes(Charset.defaultCharset());
                String result = new String(utf8Bytes, Charset.defaultCharset());
                Map<String, Object> parseMap = JSON_PARSER.parseMap(result);
                List<Map<String, String>> data = (List<Map<String, String>>) parseMap.get("data");
                if (CollectionUtils.isEmpty(data)) {
                    return REGION;
                }
                return data.get(0).get("location");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResult.fail()
                           .getMessage();
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

    public static String getAuthentication() {
        return getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    }

}
