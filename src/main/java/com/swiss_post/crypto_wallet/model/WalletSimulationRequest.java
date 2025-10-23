package com.swiss_post.crypto_wallet.model;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WalletSimulationRequest(@NotNull List<SimulationAsset> assets) {
}