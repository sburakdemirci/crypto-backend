package com.mtd.crypto.market.exception;

import org.springframework.http.HttpStatusCode;

public class BinanceException extends RuntimeException {

    public BinanceException(String message, HttpStatusCode statusCode) {
        super(message);
    }
}