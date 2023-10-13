package io.github.longxiaoyun.is.service.impl;

import io.github.longxiaoyun.is.service.Matcher;
import lombok.Getter;
import io.github.longxiaoyun.is.entity.RobotsContent;
import io.github.longxiaoyun.is.entity.RobotsGroup;
import io.github.longxiaoyun.is.entity.RobotsRule;
import io.github.longxiaoyun.is.enums.DirectiveType;
import io.github.longxiaoyun.is.service.RobotsMatchStrategy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@Getter
public class RobotsMatcher implements Matcher {
    private static final Logger logger = Logger.getLogger(RobotsMatcher.class.getName());

    @Getter
    private static class Match {
        /** Priority based on agent-specific rules */
        private int prioritySpecific = 0;
        /** Priority based on global wildcard (*) rules */
        private int priorityGlobal = 0;

        void updateSpecific(final int priority) {
            prioritySpecific = Math.max(prioritySpecific, priority);
        }

        void updateGlobal(final int priority) {
            priorityGlobal = Math.max(priorityGlobal, priority);
        }

        public void resetGlobal() {
            priorityGlobal = 0;
        }
    }

    private final RobotsContent robotsContent;
    private final RobotsMatchStrategy matchingStrategy = new RobotsLongestMatchStrategy();

    public RobotsMatcher(final RobotsContent robotsContent) {
        this.robotsContent = robotsContent;
    }

    private static String getPath(final String url) {
        final URL parsedUrl;
        try {
            parsedUrl = new URL(url);
        } catch (final MalformedURLException e) {
            logger.warning("Malformed URL: "+url+", replaced with /");
            return "/";
        }
        String path = parsedUrl.getPath();
        final String args = parsedUrl.getQuery();
        if (args != null) {
            path += "?" + args;
        }

        return path;
    }

    /**
     * 计算 {@link Match} ALLOW and DISALLOW 规则的优先级，如果至少一个User-Agent在robots.txt文件解析后的User-Agent指令列表中命中或全局应用（如果不忽略全局规则），则视规则有效。
     * @param userAgents user agent 列表
     * @param path 要检测的url
     * @param ignoreGlobal 如果设置为true，则不考虑全局规则
     * @return ALLOW and DISALLOW 规则的优先级
     */
    private Map.Entry<Match, Match> computeMatchPriorities(final List<String> userAgents, final String path, final boolean ignoreGlobal) {
        final Match allow = new Match();
        final Match disallow = new Match();
        boolean foundSpecificGroup = false;

        for (RobotsGroup group : robotsContent.getRobotsGroups()) {
            final boolean isSpecificGroup = userAgents.stream().anyMatch(userAgent -> group.getUserAgents().stream().anyMatch(userAgent::equalsIgnoreCase));
            foundSpecificGroup |= isSpecificGroup;
            if (!isSpecificGroup && (ignoreGlobal || !group.isGlobal())) {
                continue;
            }
            allowHandler(group.getRobotsRules(), isSpecificGroup, allow, path, ignoreGlobal, group);
            disallowHandler(group.getRobotsRules(), isSpecificGroup, disallow, path, ignoreGlobal, group);
        }

        // 如果至少有一个规则命中当前userAgent，则忽略全局规则（"*"）
        if (foundSpecificGroup) {
            allow.resetGlobal();
            disallow.resetGlobal();
        }

        return Map.entry(allow, disallow);
    }


    private Map.Entry<Match, Match> computeMatchPriorities(final List<String> userAgents, final String path) {
        return computeMatchPriorities(userAgents, path, false);
    }


    /**
     * 当且仅当基于ALLOW和DISALLOW规则匹配结果为ALLOW时，返回true
     * @param allow ALLOW 规则
     * @param disallow DISALLOW 规则
     * @return 如果ALLOW和DISALLOW规则的优先级允许，则返回true
     */
    private static boolean allowVerdict(final Match allow, final Match disallow) {
        if (allow.getPrioritySpecific() > 0 || disallow.getPrioritySpecific() > 0) {
            return allow.getPrioritySpecific() >= disallow.getPrioritySpecific();
        }

        if (allow.getPriorityGlobal() > 0 || disallow.getPriorityGlobal() > 0) {
            return allow.getPriorityGlobal() >= disallow.getPriorityGlobal();
        }

        return true;
    }

    @Override
    public boolean isAllowed(final List<String> userAgents, final String url) {
        final String path = getPath(url);
        Map.Entry<Match, Match> matches = computeMatchPriorities(userAgents, path);
        return allowVerdict(matches.getKey(), matches.getValue());
    }

    @Override
    public boolean isAllowed(String userAgent, String url) {
        return isAllowed(Collections.singletonList(userAgent), url);
    }

    @Override
    public boolean isAllowedIgnoreGlobal(final List<String> userAgents, final String url) {
        final String path = getPath(url);
        Map.Entry<Match, Match> matches = computeMatchPriorities(userAgents, path, true);
        return allowVerdict(matches.getKey(), matches.getValue());
    }

    /**
     * allow 规则处理
     * @param robotsRules robots.txt文件解析后的规则列表
     * @param isSpecificGroup 当前组是否是特定组
     * @param allow ALLOW 规则
     * @param path 要检测的url
     * @param ignoreGlobal 是否忽略全局规则（“*”）
     * @param group 当前组
     */
    private void allowHandler(Set<RobotsRule> robotsRules, boolean isSpecificGroup, Match allow, String path, boolean ignoreGlobal, RobotsGroup group) {
        robotsRules.stream().filter(rule -> rule.getDirectiveType() == DirectiveType.ALLOW).forEach(rule -> {
            final int priority = matchingStrategy.matchAllowPriority(path, rule.getValue());
            if (isSpecificGroup) {
                allow.updateSpecific(priority);
            }
            if (!ignoreGlobal && group.isGlobal()) {
                allow.updateGlobal(priority);
            }
        });
    }

    /**
     * disallow 规则处理
     * @param robotsRules robots.txt文件解析后的规则列表
     * @param isSpecificGroup 当前组是否是特定组
     * @param disallow DISALLOW 规则
     * @param path 要检测的url
     * @param ignoreGlobal 是否忽略全局规则（“*”）
     * @param group 当前组
     */
    private void disallowHandler(Set<RobotsRule> robotsRules, boolean isSpecificGroup, Match disallow, String path, boolean ignoreGlobal, RobotsGroup group) {
        robotsRules.stream().filter(rule -> rule.getDirectiveType() == DirectiveType.DISALLOW).forEach(rule -> {
            final int priority = matchingStrategy.matchDisallowPriority(path, rule.getValue());
            if (isSpecificGroup) {
                disallow.updateSpecific(priority);
            }
            if (!ignoreGlobal && group.isGlobal()) {
                disallow.updateGlobal(priority);
            }
        });
    }
}
