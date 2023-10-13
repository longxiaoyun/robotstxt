package io.github.longxiaoyun.is.robotstxt.service.impl;

import io.github.longxiaoyun.is.robotstxt.entity.RobotsContent;
import io.github.longxiaoyun.is.robotstxt.entity.RobotsGroup;
import io.github.longxiaoyun.is.robotstxt.enums.DirectiveType;
import io.github.longxiaoyun.is.robotstxt.service.ParserHandler;
import io.github.longxiaoyun.is.robotstxt.service.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RobotsParseHandler implements ParserHandler {
    private static final Logger logger = LoggerFactory.getLogger(RobotsParseHandler.class.getName());

    protected RobotsContent robotsContent;
    private RobotsGroup currentGroup;
    private boolean foundContent;

    @Override
    public void handleStart() {
        robotsContent = new RobotsContent();
        currentGroup = new RobotsGroup();
        foundContent = false;
    }

    private void flushCompleteGroup(boolean createNew) {
        robotsContent.addGroup(currentGroup);
        if (createNew) {
            currentGroup = new RobotsGroup();
        }
    }

    @Override
    public void handleEnd() {
        flushCompleteGroup(false);
    }

    private void handleUserAgent(final String value) {
        if (foundContent) {
            flushCompleteGroup(true);
            foundContent = false;
        }
        currentGroup.addUserAgent(value);
    }

    private static boolean isHexChar(final byte b) {
        return Character.isDigit(b) || ('a' <= b && b <= 'f') || ('A' <= b && b <= 'F');
    }


    /**
     * 规范化路径：转义 US-ASCII 字符集之外的字符（例如 /SanJoséSellers ==> /Sanjos%C3%A9Sellers）并标准化转义字符（例如 %aa ==> %AA）
     * @param path 要规范化的路径
     * @return 转义和标准化后的路径
     */
    private static String maybeEscapePattern(final String path) {
        final byte[] bytes = path.getBytes(StandardCharsets.UTF_8);

        int unescapedCount = 0;
        boolean notCapitalized = false;

        // Check if any changes required
        for (int i = 0; i < bytes.length; i++) {
            if (i < bytes.length - 2
                    && bytes[i] == '%'
                    && isHexChar(bytes[i + 1])
                    && isHexChar(bytes[i + 2])) {
                if (Character.isLowerCase(bytes[i + 1]) || Character.isLowerCase(bytes[i + 2])) {
                    notCapitalized = true;
                }
                i += 2;
            } else if ((bytes[i] & 0x80) != 0) {
                unescapedCount++;
            }
        }

        // Return if no changes needed
        if (unescapedCount == 0 && !notCapitalized) {
            return path;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i < bytes.length - 2
                    && bytes[i] == '%'
                    && isHexChar(bytes[i + 1])
                    && isHexChar(bytes[i + 2])) {
                stringBuilder.append((char) bytes[i++]);
                stringBuilder.append((char) Character.toUpperCase(bytes[i++]));
                stringBuilder.append((char) Character.toUpperCase(bytes[i]));
            } else if ((bytes[i] & 0x80) != 0) {
                stringBuilder.append('%');
                stringBuilder.append(Integer.toHexString((bytes[i] >> 4) & 0xf).toUpperCase());
                stringBuilder.append(Integer.toHexString(bytes[i] & 0xf).toUpperCase());
            } else {
                stringBuilder.append((char) bytes[i]);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void handleDirective(final DirectiveType directiveType, final String directiveValue) {
        switch (directiveType) {
            case USER_AGENT -> {
                handleUserAgent(directiveValue);
            }
            case ALLOW, DISALLOW -> {
                foundContent = true;
                if (currentGroup.isGlobal() || !currentGroup.getUserAgents().isEmpty()) {
                    final String path = maybeEscapePattern(directiveValue);
                    currentGroup.addRule(directiveType, path);

                    if (directiveType == DirectiveType.ALLOW) {
                        // Google-specific optimization: 'index.htm' and 'index.html' are normalized to '/'.
                        final int slashPos = path.lastIndexOf('/');

                        if (slashPos != -1) {
                            final String fileName = path.substring(slashPos + 1);
                            if ("index.htm".equals(fileName) || "index.html".equals(fileName)) {
                                final String normalizedPath = path.substring(0, slashPos + 1) + '$';

                                if (!currentGroup.hasRule(DirectiveType.ALLOW, normalizedPath)) {
                                    logger.info("Allowing normalized path: {} -> {}", directiveValue, normalizedPath);
                                    currentGroup.addRule(DirectiveType.ALLOW, normalizedPath);
                                }
                            }
                        }
                    }
                }
            }
            case SITEMAP, UNKNOWN -> {
                foundContent = true;
            }
        }
    }

    @Override
    public Matcher compute() {
        return new RobotsMatcher(robotsContent);
    }
}
