package io.github.longxiaoyun.is.service;

public abstract class Parser {
    protected ParserHandler parseHandler;

    /**
     * Parser must follow specific {@link ParserHandler} rules in order to parse. Thus it requires an
     * instance of it upon creation.
     *
     * @param parseHandler handler to follow during parsing process.
     */
    protected Parser(ParserHandler parseHandler) {
        this.parseHandler = parseHandler;
    }

    /**
     * Method to parse robots.txt file into a matcher.
     *
     * @param robotsTxtBodyBytes body of robots.txt file to parse
     * @return matcher representing given robots.txt file
     */
    public abstract Matcher parse(final byte[] robotsTxtBodyBytes);


}
