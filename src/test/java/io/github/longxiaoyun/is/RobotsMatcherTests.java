package io.github.longxiaoyun.is;

import com.alibaba.fastjson2.JSON;
import io.github.longxiaoyun.is.entity.RobotsContent;
import io.github.longxiaoyun.is.service.Matcher;
import io.github.longxiaoyun.is.service.impl.RobotsMatcher;
import io.github.longxiaoyun.is.service.impl.RobotsParseHandler;
import io.github.longxiaoyun.is.utils.UserAgentUtil;
import io.github.longxiaoyun.is.service.Parser;
import io.github.longxiaoyun.is.service.impl.RobotsParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RobotsMatcherTests {


    @Test
    public void testRobotsMatcher() {
        String robotsTxtContent = "User-agent: Baiduspider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Googlebot\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: MSNBot\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Baiduspider-image\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: YoudaoBot\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Sogou web spider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Sogou inst spider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Sogou spider2\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Sogou blog\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Sogou News Spider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Sogou Orion spider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: ChinasoSpider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: Sosospider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "\n" +
                "User-agent: yisouspider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: EasouSpider\n" +
                "Disallow: /baidu\n" +
                "Disallow: /s?\n" +
                "Disallow: /shifen/\n" +
                "Disallow: /homepage/\n" +
                "Disallow: /cpro\n" +
                "Disallow: /ulink?\n" +
                "Disallow: /link?\n" +
                "Disallow: /home/news/data/\n" +
                "Disallow: /bh\n" +
                "\n" +
                "User-agent: *\n" +
                "Disallow: /";
        String url = "http://www.baidu.com/";

        final Parser parser = new RobotsParser(new RobotsParseHandler());
        final Matcher matcher = parser.parse(robotsTxtContent.getBytes(StandardCharsets.UTF_8));

        final RobotsContent actualContents = ((RobotsMatcher) matcher).getRobotsContent();
        log.info("RobotsTxtContent: {}", JSON.toJSONString(actualContents));
        String userAgent = UserAgentUtil.parseUserAgent("Baiduspider");
        boolean isMatch = matcher.isAllowed(userAgent, url);
        Assert.assertTrue(isMatch);

        userAgent = UserAgentUtil.parseUserAgent("*");
        boolean isMatch2 = matcher.isAllowed(userAgent, url);
        Assert.assertFalse(isMatch2);

        url = "https://www.baidu.com/";
        userAgent = UserAgentUtil.parseUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");

        boolean isMatch3 = matcher.isAllowedIgnoreGlobal(Collections.singletonList(userAgent), url);
        Assert.assertTrue(isMatch3);

        boolean isMatch4 = matcher.isAllowed(Collections.singletonList(userAgent), url);
        Assert.assertFalse(isMatch4);
    }
}
