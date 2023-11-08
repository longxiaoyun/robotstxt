项目基于 https://github.com/google/robotstxt-java/tree/master 和https://developers.google.com/search/docs/crawling-indexing/robots/create-robots-txt?hl=zh-cn进行修改
***

A robots.txt parser which aims to be complaint with the draft specification.
用于robots.txt解析的java库，遵循google robots.txt规范

支持以下方法：
* `parse(byte[] bytes)` - 解析robots.txt内容，返回分组对象
* `isAllowed(String userAgent, String url)` - 匹配robots.txt内容，返回是否允许访问
* `isAllowed(List<String> userAgents, String url)` - 匹配链接是否在允许访问的User-Agent列表中
* `isAllowedIgnoreGlobal(List<String> userAgents, String url)` - 匹配链接是否在允许访问的User-Agent列表中(忽略"*")


### 安装
maven

```xml
<dependency>
  <groupId>io.github.longxiaoyun.is</groupId>
  <artifactId>robotstxt</artifactId>
  <version>1.0.1</version>
</dependency>
```

### 使用

解析
```java
    // 1. 创建robots.txt解析器
    String robotsText = "User-agent: Baiduspider\n" +
            "Disallow: /baidu\n" +
            "Disallow: /s?\n" +
            "Disallow: /ulink?\n" +
            "Disallow: /link?\n" +
            "Disallow: /home/news/data/\n" +
            "Disallow: /bh\n";
    ParserHandler parserHandler = new RobotsParseHandler();
    Parser parser = new RobotsParser(parserHandler);
    Matcher matcher = parser.parse(robotsText.getBytes(StandardCharsets.UTF_8));
    RobotsContent actualContents = ((RobotsMatcher) matcher).getRobotsContent();
    System.out.println("结果: " + JSON.toJSONString(actualContents));
```

匹配
```java
    String robotsText = "User-agent: Baiduspider\n" +
        "Disallow: /baidu\n" +
        "Disallow: /s?\n" +
        "Disallow: /ulink?\n" +
        "Disallow: /link?\n" +
        "Disallow: /home/news/data/\n" +
        "Disallow: /bh\n";
    ParserHandler parserHandler = new RobotsParseHandler();
    final Parser parser = new RobotsParser(parserHandler);
    final Matcher matcher = parser.parse(robotsTxtContent.getBytes(StandardCharsets.UTF_8));

    String userAgent = UserAgentUtil.parseUserAgent("Baiduspider");
    boolean isMatch = matcher.isAllowed(userAgent, url);
    Assert.assertTrue(isMatch);
```
