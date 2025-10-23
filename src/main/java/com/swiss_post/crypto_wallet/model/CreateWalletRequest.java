package com.swiss_post.crypto_wallet.model;

import jakarta.validation.constraints.NotNull;

public record CreateWalletRequest(@NotNull String email) {}