package com.swiss_post.crypto_wallet.model;


import java.math.BigDecimal;

public record Asset(String symbol, BigDecimal quantity, BigDecimal price, BigDecimal value ) {}