package io.github.longxiaoyun.is.robotstxt.service;

public interface RobotsMatchStrategy {

    /**
     * 根据给定指令计算 ALLOW 规则的优先级
     * @param path 要计算 ALLOW 规则优先级的路径
     * @param pattern ALLOW 指令
     * @return 匹配优先级（值越高，ALLOW 规则越可能生效）
     */
    int matchAllowPriority(final String path, final String pattern);


    /**
     * 根据给定指令计算 DISALLOW 规则的优先级
     * @param path 要计算 DISALLOW 规则优先级的路径
     * @param pattern DISALLOW 指令
     * @return 匹配优先级（值越高，DISALLOW 规则越可能生效）
     */
    int matchDisallowPriority(final String path, final String pattern);
}
