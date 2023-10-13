package io.github.longxiaoyun.is.utils;

public class UserAgentUtil {

    private UserAgentUtil() {
    }

    /**
     * 解析 User-Agent
     * @param userAgent User-Agent
     * @return 解析后的 User-Agent
     */
    public static String parseUserAgent(String userAgent) {
        if (!userAgent.isEmpty() && userAgent.charAt(0) == '*' && (userAgent.length() == 1 || Character.isWhitespace(userAgent.charAt(1)))) {
            // User-Agent: *
            return "*";
        } else {
            int end = 0;
            for (; end < userAgent.length(); end++) {
                final char ch = userAgent.charAt(end);
                if (!Character.isAlphabetic(ch) && ch != '-' && ch != '_') {
                    break;
                }
            }
            return userAgent.substring(0, end);
        }
    }
}
