package io.github.longxiaoyun.is.exception;

public class ParseException extends RuntimeException{
    public ParseException(String message) {
        super(message);
    }

    public ParseException() {
        super();
    }
}
