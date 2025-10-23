package com.swiss_post.crypto_wallet.infrastructure.exceptions;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String symbol) {
        super("Token price not found for symbol: " + symbol);
    }
}
