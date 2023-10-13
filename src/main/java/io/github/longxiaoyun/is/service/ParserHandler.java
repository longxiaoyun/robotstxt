package io.github.longxiaoyun.is.service;

import io.github.longxiaoyun.is.enums.DirectiveType;

public interface ParserHandler {

    /**
     * 解析过程开始时的处理逻辑。该方法将在任何调用之前执行一次
     */
    void handleStart();

    /**
     * 指令接收器。每个指令由类型和值组成。该方法将在 {@link this#handleStart()} 之后调用，而不会在 {@link this#handleEnd()} 之后调用。可能会被调用多次。
     * @param directiveType 指令类型
     * @param directiveValue 指令值
     */
    void handleDirective(final DirectiveType directiveType, final String directiveValue);


    /**
     * 解析过程结束的处理程序。该方法将在 {@link this#handleStart()} 或 {@link this#handleDirective(DirectiveType, String)} 之后调用一次
     */
    void handleEnd();


    /**
     * 调用此方法会根据之前通过 {@link this#handleDirective(DirectiveType, String)} 方法接收到的所有信息生成匹配器。
     * 因此，它返回具有匹配功能的 robots.txt 文件的序列化视图。该方法将在 {@link this#handleEnd()} 之后调用。可能会被调用多次。
     * @return 表示原始 robots.txt 文件的匹配器
     */
    Matcher compute();
}
