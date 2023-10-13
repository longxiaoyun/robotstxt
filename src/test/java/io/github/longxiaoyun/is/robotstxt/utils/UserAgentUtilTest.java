package io.github.longxiaoyun.is.robotstxt.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserAgentUtilTest {



    @Test
    public void testParseUserAgent() {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36";
        String res = UserAgentUtil.parseUserAgent(ua);
        Assert.assertEquals("Mozilla", res);
    }
}
