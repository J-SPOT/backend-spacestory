package com.juny.spacestory.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpUtils {

  public static String getClientIp(HttpServletRequest request) {
    String[] headers = {
      "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    String ip = request.getHeader("X-Forwarded-For");

    for (var header : headers) {
      if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader(header);
      }
      log.info("{}: {}", header, ip);
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    if (ip.equals("0:0:0:0:0:0:0:1")) {
      ip = "127.0.0.1";
    }
    log.info("ip: {}", ip);
    return ip;
  }
}
