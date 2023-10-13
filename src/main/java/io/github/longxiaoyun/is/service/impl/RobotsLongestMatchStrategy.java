package io.github.longxiaoyun.is.service.impl;

import io.github.longxiaoyun.is.service.RobotsMatchStrategy;

public class RobotsLongestMatchStrategy implements RobotsMatchStrategy {


    /**
     * 检查给定路径是否可以与给定模式匹配。“*”视为通配符，“$”视为终止符号（当且仅当其位于模式末尾时）
     * @param path 要匹配的路径
     * @param pattern 要匹配的模式
     * @return 如果给定路径与给定模式匹配，则为true
     */
    private static boolean matches(final String path, final String pattern) {
        final int[] prefixes = new int[path.length() + 1];
        prefixes[0] = 0;
        int prefixesCount = 1;

        for (int i = 0; i < pattern.length(); i++) {
            final char ch = pattern.charAt(i);

            if (ch == '$' && i + 1 == pattern.length()) {
                return prefixes[prefixesCount - 1] == path.length();
            }

            // 如果出现“*”，所有路径前缀可从最短路径中匹配一个
            if (ch == '*') {
                prefixesCount = path.length() - prefixes[0] + 1;
                for (int j = 1; j < prefixesCount; j++) {
                    prefixes[j] = prefixes[j - 1] + 1;
                }
            } else {
                int newPrefixesCount = prefixesExpand(prefixesCount, prefixes, path, ch);
                if (newPrefixesCount == -1) {
                    return false;
                }
                prefixesCount = newPrefixesCount;
            }
        }

        return true;
    }

    @Override
    public int matchAllowPriority(String path, String pattern) {
        return matches(path, pattern) ? pattern.length() : -1;
    }

    @Override
    public int matchDisallowPriority(String path, String pattern) {
        return matches(path, pattern) ? pattern.length() : -1;
    }

    /**
     * 每次迭代前一个前缀，并尝试扩展一个字符
     * @param prefixesCount 前缀数量
     * @param prefixes 前缀数组
     * @param path 路径
     * @param ch 字符
     * @return 新的前缀数量
     */
    private static int prefixesExpand(int prefixesCount, int[] prefixes, String path, char ch) {
        int newPrefixesCount = 0;
        for (int j = 0; j < prefixesCount; j++) {
            if (prefixes[j] < path.length() && path.charAt(prefixes[j]) == ch) {
                prefixes[newPrefixesCount++] = prefixes[j] + 1;
            }
        }
        if (newPrefixesCount == 0) {
            return -1;
        }
        return newPrefixesCount;
    }
}
