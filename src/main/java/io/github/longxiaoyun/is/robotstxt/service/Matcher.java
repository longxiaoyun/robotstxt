package io.github.longxiaoyun.is.robotstxt.service;

import java.util.List;

public interface Matcher {

    /**
     * 根据robots.txt的规则,检测是否至少有一个User-Agent 允许访问指定的url
     * @param userAgents user agent 列表
     * @param url 要检测的url
     * @return true: 允许访问; false: 不允许访问
     */
    boolean isAllowed(final List<String> userAgents, final String url);


    /**
     * 根据robots.txt的规则,检测给定的User-Agent是否允许访问指定的url
     * @param userAgent user agent
     * @param url 要检测的url
     * @return true: 允许访问; false: 不允许访问
     */
    boolean isAllowed(final String userAgent, final String url);


    /**
     * 忽略全局规则（“*”），根据robots.txt的其他规则组,检测是否至少有一个User-Agent 允许访问指定的url
     * @param userAgents user agent 列表
     * @param url 要检测的url
     * @return true: 允许访问; false: 不允许访问
     */
    boolean isAllowedIgnoreGlobal(final List<String> userAgents, final String url);
}
