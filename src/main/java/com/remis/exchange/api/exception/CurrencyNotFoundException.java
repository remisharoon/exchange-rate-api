package com.remis.exchange.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "exchange rates not found")
public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException() {
        super();
    }

    public CurrencyNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CurrencyNotFoundException(final String message) {
        super(message);
    }

    public CurrencyNotFoundException(final Throwable cause) {
        super(cause);
    }
}