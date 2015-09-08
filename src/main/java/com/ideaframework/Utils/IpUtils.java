package com.ideaframework.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class IpUtils {

	private final static String IPV4_REGEX = "(?:[01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])(?:\\.(?:[01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])){3}";

	public final static Matcher IPV4_MATCHER = Pattern.compile(IPV4_REGEX).matcher("");

	public static String getRequestIpAddress(HttpServletRequest request) {
		String realIP = request.getHeader("x-forwarded-for");
		if (realIP != null && realIP.length() != 0) {
			while ((realIP != null && realIP.equals("unknow"))) {
				realIP = request.getHeader("x-forwarded-for");
			}
			if (realIP.indexOf(',') >= 0) {
				realIP = realIP.substring(0, realIP.indexOf(','));
			}
		}
		if (realIP == null || realIP.length() == 0 || "unknown".equalsIgnoreCase(realIP)) {
			realIP = request.getHeader("Proxy-Clint-IP");
		}
		if (realIP == null || realIP.length() == 0 || "unknown".equalsIgnoreCase(realIP)) {
			realIP = request.getHeader("WL-Proxy-Clint-IP");
		}
		if (realIP == null || realIP.length() == 0 || "unknown".equalsIgnoreCase(realIP)) {
			realIP = request.getRemoteAddr();
		}
		return realIP;
	}

	public static int ipv4ToInt(String ipv4) {
		if (!IPV4_MATCHER.reset(ipv4).matches()) {
			throw new IllegalArgumentException("IPv4 format ERROR!");
		}
		String[] strs = ipv4.split("\\.");
		int result = 0;
		for (int i = 0, k = strs.length; i < k; i++) {
			result |= Integer.parseInt(strs[i]) << ((k - 1 - i) * 8);
		}
		return result;
	}

	public static String int4Ipv4(int ipv4Int) {
		StringBuilder sb = new StringBuilder(15);
		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				sb.append('.');
			}
			sb.append((ipv4Int >>> ((3 - i) * 8)) & 0xff);
		}
		return sb.toString();
	}

	public static boolean isIpInForbiddenIp(String forbiddenIp, String ip) {
		if (StringUtils.isBlank(forbiddenIp))
			return false;
		if (StringUtils.isBlank(ip))
			return true;
		if (forbiddenIp.equals(ip))
			return true;
		if (forbiddenIp.indexOf("*") >= 0) {
			String[] fip = forbiddenIp.split("\\.");
			String[] fromIp = ip.split("\\.");
			if (fip.length != 4 || fromIp.length != 4)
				return false;
			for (int i = 0; i < 4; i++) {
				String s1 = fip[i];
				String s2 = fromIp[i];
				if (!s1.equals(s2)) {
					if (!s1.equals("*"))
						return false;
				}
			}
			return true;
		}
		return false;
	}
}
