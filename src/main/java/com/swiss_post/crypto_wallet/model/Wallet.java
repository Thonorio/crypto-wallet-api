package com.swiss_post.crypto_wallet.model;


import java.math.BigDecimal;
import java.util.List;

public record Wallet(String id, BigDecimal total, List<Asset> assets) {}