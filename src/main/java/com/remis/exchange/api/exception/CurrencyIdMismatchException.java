package com.remis.exchange.api.exception;

public class CurrencyIdMismatchException extends RuntimeException {

    public CurrencyIdMismatchException() {
        super();
    }

    public CurrencyIdMismatchException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CurrencyIdMismatchException(final String message) {
        super(message);
    }

    public CurrencyIdMismatchException(final Throwable cause) {
        super(cause);
    }
}