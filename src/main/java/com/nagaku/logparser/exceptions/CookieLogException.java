package com.nagaku.logparser.exceptions;

public class CookieLogException extends RuntimeException {

    public CookieLogException(String message) {
        super(message);
    }

    public CookieLogException(String message, Throwable e) {
        super(message, e);
    }
}
