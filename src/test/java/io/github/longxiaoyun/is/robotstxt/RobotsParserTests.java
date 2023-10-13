package io.github.longxiaoyun.is.robotstxt;

import com.alibaba.fastjson2.JSON;
import io.github.longxiaoyun.is.robotstxt.entity.RobotsContent;
import io.github.longxiaoyun.is.robotstxt.service.Matcher;
import io.github.longxiaoyun.is.robotstxt.service.Parser;
import io.github.longxiaoyun.is.robotstxt.service.ParserHandler;
import io.github.longxiaoyun.is.robotstxt.service.impl.RobotsMatcher;
import io.github.longxiaoyun.is.robotstxt.service.impl.RobotsParseHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.github.longxiaoyun.is.robotstxt.service.impl.RobotsParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.nio.charset.StandardCharsets;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RobotsParserTests {

    @Test
    public void testParserRobotsTxtContent() throws Exception {
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

        ParserHandler parserHandler = new RobotsParseHandler();
        final Parser parser = new RobotsParser(parserHandler);
        final Matcher matcher = parser.parse(robotsTxtContent.getBytes(StandardCharsets.UTF_8));
        final RobotsContent actualContents = ((RobotsMatcher) matcher).getRobotsContent();
        System.out.println("结果: " + JSON.toJSONString(actualContents));

    }
}
