package com.zf1976.ant.common.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mac
 * @date 2021/5/11
 */
public class IpUtil {

    private static final String REGEX = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public static void main(String[] args) throws Exception {
        String ipAddress = RequestUtil.getIpAddress();
        String ipRegion = RequestUtil.getIpRegion();
        Matcher matcher = PATTERN.matcher(ipAddress);
        if (matcher.find()) {
            System.out.println(matcher.group(0));
        }
    }

    /**
     * 获取本地ip地址
     *
     * @return {@link String}
     * @date 2021-05-11 13:38:10
     */
    public static String getInterLocalIP() throws Exception {
        return InetAddress.getLocalHost()
                          .getHostAddress();
    }

    /**
     * 获取内网ip地址
     *
     * @return {@link String}
     * @date 2021-05-11 13:37:51
     */
    public static String getInterIP() {
        // 本地IP，如果没有配置外网IP则返回它
        String localIp = null;
        // 外网IP
        String netip = null;
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException("get NetworkInterfaces Error");
        }
        InetAddress ip;
        // 是否找到外网IP
        boolean finded = false;
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                // 外网IP
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                    // 内网IP
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    localIp = ip.getHostAddress();
                }
            }
        }
        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localIp;
        }
    }

    /**
     * 获取请求ip地址
     *
     * @return {@link String}
     * @date 2021-05-17 10:29:28
     */
    public static String getOutIPV4() {
        String ip = "";
        String api = "https://ip.chinaz.com";

        StringBuilder inputLine = new StringBuilder();
        String read;
        URL url;
        HttpURLConnection urlConnection;
        BufferedReader in = null;
        try {
            url = new URL(api);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            while ((read = in.readLine()) != null) {
                inputLine.append(read)
                         .append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
        Matcher m = p.matcher(inputLine.toString());
        if (m.find()) {
            String ipstr = m.group(1);
            ip = ipstr;
        }
        return ip;
    }

}
