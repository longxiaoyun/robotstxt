package io.github.longxiaoyun.is.service.impl;

import io.github.longxiaoyun.is.enums.DirectiveType;
import io.github.longxiaoyun.is.exception.ParseException;
import io.github.longxiaoyun.is.service.Matcher;
import io.github.longxiaoyun.is.service.Parser;
import io.github.longxiaoyun.is.service.ParserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;


public class RobotsParser extends Parser {

    private static final Logger logger = LoggerFactory.getLogger(RobotsParser.class);
    private final int valueMaxLengthBytes;

    public RobotsParser(final ParserHandler parseHandler) {
        super(parseHandler);
        this.valueMaxLengthBytes = 2083;
    }

    RobotsParser(final ParserHandler parseHandler, final int valueMaxLengthBytes) {
        super(parseHandler);
        this.valueMaxLengthBytes = valueMaxLengthBytes;
    }

    private static boolean isWhitespace(final char ch) {
        return ch == ' ' || ch == '\t';
    }

    /**
     * 提取给定索引之间的子字符串并修剪前后的空白字符
     * @param bytes 数据提取源
     * @param beginIndex 开始索引，包括
     * @param endIndex 结束索引，不包括
     * @return 提取的子字符串，修剪了空格
     * @throws ParseException 如果给定索引之间只有空格字符
     */
    private static String trimBounded(final byte[] bytes, final int beginIndex, final int endIndex)
            throws ParseException {
        int begin = beginIndex;
        int end = endIndex;
        while (begin < endIndex && isWhitespace((char) bytes[begin])) {
            begin++;
        }
        while (end > beginIndex && isWhitespace((char) bytes[end - 1])) {
            end--;
        }
        if (begin >= end) {
            throw new ParseException();
        } else {
            return new String(Arrays.copyOfRange(bytes, begin, end), StandardCharsets.UTF_8);
        }
    }

    private static DirectiveType parseDirective(final String key) {
        if (key.equalsIgnoreCase("user-agent")) {
            return DirectiveType.USER_AGENT;
        } else {
            try {
                return DirectiveType.valueOf(key.toUpperCase());
            } catch (final IllegalArgumentException e) {
                final boolean disallowTypoDetected = Stream.of("dissallow", "dissalow", "disalow", "diasllow", "disallaw").anyMatch(s -> key.compareToIgnoreCase(s) == 0);
                if (disallowTypoDetected) {
                    logger.info("Fixed typo: {} -> disallow", key);
                    return DirectiveType.DISALLOW;
                }

                return DirectiveType.UNKNOWN;
            }
        }
    }

    /**
     * 从 robots.txt 正文中提取值，并根据需要将其转为 {@link this#valueMaxLengthBytes} 字节数组。大多数参数仅用于用于日志打印。
     * @param robotsTxtBodyBytes robots.txt 文件的内容
     * @param separator key 和 value 之间的分隔符索引
     * @param limit key 和 value 的结束索引
     * @param lineBegin 行开始索引
     * @param lineEnd 行结束索引
     * @param lineNumber robots.txt 文件中的行号
     * @return  robots.txt 给定行的解析值
     * @throws ParseException 如果行限制无效
     */
    private String getValue(final byte[] robotsTxtBodyBytes, final int separator, final int limit, final int lineBegin, final int lineEnd, final int lineNumber) throws ParseException {
        String value = trimBounded(robotsTxtBodyBytes, separator + 1, limit);

        // Google 特殊优化：因为没有搜索引擎会处理超过 2083 字节的URL，因此所有值都会被修剪以适应此大小
        final byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);

        // 我们将最大大小减少两个字节。因为如果最后一个字符被修剪为无效字符，这样做可以适应替换字符 (\uFFFD)
        final int maxLengthBytes = valueMaxLengthBytes - 2;

        if (valueBytes.length > maxLengthBytes) {
            logger.info("Value truncated to {} bytes.{}, {}, {}, {}", valueMaxLengthBytes,robotsTxtBodyBytes, lineBegin, lineEnd, lineNumber);
            value = new String(valueBytes, 0, maxLengthBytes, StandardCharsets.UTF_8);
        }

        return value;
    }

    private void parseLine(final byte[] robotsTxtBodyBytes, final int lineBegin, final int lineEnd, final int lineNumber) {
        int limit = lineEnd;
        int separator = lineEnd;
        int whitespaceSeparator = lineEnd;
        boolean hasContents = false;

        for (int i = lineBegin; i < lineEnd; i++) {
            final byte b = robotsTxtBodyBytes[i];
            if (b == '#') {
                limit = i;
                break;
            }
            if (!isWhitespace((char) b)) {
                hasContents = true;
            }
            if (isWhitespace((char) b) && hasContents && whitespaceSeparator == lineEnd) {
                whitespaceSeparator = i;
            }
            if (separator == lineEnd && b == ':') {
                separator = i;
            }
        }

        if (separator == lineEnd) {
            // Google 特殊优化：有些人忘记了冒号，因此兼容空格
            if (whitespaceSeparator != lineEnd) {
                logger.info("Assuming whitespace as a separator.{}， {}， {}， {}",
                        robotsTxtBodyBytes,
                        lineBegin,
                        lineEnd,
                        lineNumber);
                separator = whitespaceSeparator;
            } else {
                if (hasContents) {
                    logger.info("No separator found.{}, {}, {}, {}",
                            robotsTxtBodyBytes,
                            lineBegin,
                            lineEnd,
                            lineNumber);
                }
                return;
            }
        }

        final String key;
        try {
            key = trimBounded(robotsTxtBodyBytes, lineBegin, separator);
        } catch (ParseException e) {
            logger.warn( "No key found.{}, {}, {}, {}", robotsTxtBodyBytes, lineBegin, lineEnd, lineNumber);
            return;
        }

        DirectiveType directiveType = parseDirective(key);
        if (directiveType == DirectiveType.UNKNOWN) {
            logger.warn( "Unknown key.{}, {}, {}, {}", robotsTxtBodyBytes, lineBegin, lineEnd, lineNumber);
        }

        String value;
        try {
            value = getValue(robotsTxtBodyBytes, separator, limit, lineBegin, lineEnd, lineNumber);
        } catch (final ParseException e) {
            logger.warn( "No value found.{}, {}, {}, {}", robotsTxtBodyBytes, lineBegin, lineEnd, lineNumber);
            value = "";
            directiveType = DirectiveType.UNKNOWN;
        }
        parseHandler.handleDirective(directiveType, value);
    }

    @Override
    public Matcher parse(byte[] robotsTxtBodyBytes) {
        final byte[] bomUtf8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        int bomPos = 0;

        int posBegin = 0;
        int posEnd = 0;
        int lineNumber = 0;
        boolean previousWasCarriageReturn = false;

        parseHandler.handleStart();

        // 对字符进行迭代优于将文本拆分为行，避免创建额外的字符串并遵守标准换行符
        for (int i = 0; i <= robotsTxtBodyBytes.length; i++) {
            final byte b = (i == robotsTxtBodyBytes.length) ? (byte) '\0' : robotsTxtBodyBytes[i];

            // Google 特殊优化：UTF-8 字节顺序标记不应出现在 robots.txt 文件中，尽管如此，跳过输入的第一个字节中可能出现的 BOM 前缀
            if (bomPos < bomUtf8.length && b == bomUtf8[bomPos++]) {
                posBegin++;
                posEnd++;
                continue;
            }
            bomPos = bomUtf8.length;

            if (b != '\n' && b != '\r' && b != '\0') {
                posEnd++;
            } else {
                if (posBegin != posEnd || !previousWasCarriageReturn || b != '\n') {
                    parseLine(robotsTxtBodyBytes, posBegin, posEnd, ++lineNumber);
                }
                posBegin = posEnd = i + 1;
                previousWasCarriageReturn = b == '\r';
            }
        }

        parseHandler.handleEnd();

        return parseHandler.compute();
    }

    private String directiveHandler(String key, byte[] robotsTxtBodyBytes, int separator, int limit, int lineBegin, int lineEnd, int lineNumber) {
        DirectiveType directiveType = parseDirective(key);
        if (directiveType == DirectiveType.UNKNOWN) {
            logger.warn( "Unknown key.{}, {}, {},{}", robotsTxtBodyBytes, lineBegin, lineEnd, lineNumber);
        }

        String value;
        try {
            value = getValue(robotsTxtBodyBytes, separator, limit, lineBegin, lineEnd, lineNumber);
        } catch (final ParseException e) {
            logger.warn("No value found.{}, {}, {}, {}", robotsTxtBodyBytes, lineBegin, lineEnd, lineNumber);
            value = "";
        }
        return value;
    }

}
