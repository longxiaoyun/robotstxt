package io.github.longxiaoyun.is.robotstxt.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import io.github.longxiaoyun.is.robotstxt.enums.DirectiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Accessors(chain = true)
public class RobotsGroup implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RobotsGroup.class);

    private final Set<String> userAgents;
    private final Set<RobotsRule> robotsRules;
    private boolean global = false;

    public RobotsGroup() {
        userAgents = new HashSet<>();
        robotsRules = new HashSet<>();
    }

    // Intended to be used from tests only.
    RobotsGroup(final List<String> userAgents, final List<RobotsRule> robotsRules) {
        this(userAgents, robotsRules, false);
    }

    // Intended to be used from tests only.
    RobotsGroup(final List<String> userAgents, final List<RobotsRule> robotsRules, final boolean global) {
        this.userAgents = new HashSet<>(userAgents);
        this.robotsRules = new HashSet<>(robotsRules);
        this.global = global;
    }

    public void addUserAgent(final String userAgent) {
        // Google-specific optimization: a '*' followed by space and more characters
        // in a user-agent record is still regarded a global rule.
        if (!userAgent.isEmpty() && userAgent.charAt(0) == '*' && (userAgent.length() == 1 || Character.isWhitespace(userAgent.charAt(1)))) {

            if (userAgent.length() > 1 && Character.isWhitespace(userAgent.charAt(1))) {
                logger.atInfo().log("Assuming {} user-agent as *", userAgent);
            }

            global = true;
        } else {
            int end = 0;
            for (; end < userAgent.length(); end++) {
                final char ch = userAgent.charAt(end);
                if (!Character.isAlphabetic(ch) && ch != '-' && ch != '_') {
                    break;
                }
            }
            userAgents.add(userAgent.substring(0, end));
        }
    }

    public void addRule(final DirectiveType directiveType, final String directiveValue) {
        robotsRules.add(new RobotsRule(directiveType, directiveValue));
    }

    public boolean hasRule(final DirectiveType directiveType, final String directiveValue) {
        return robotsRules.contains(new RobotsRule(directiveType, directiveValue));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RobotsGroup other = (RobotsGroup) obj;
        return Objects.equals(userAgents, other.userAgents)
                && Objects.equals(robotsRules, other.robotsRules)
                && Objects.equals(global, other.global);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAgents, robotsRules);
    }

}
