package com.swiss_post.crypto_wallet.controller;

import com.swiss_post.crypto_wallet.service.WalletService;
import com.swiss_post.crypto_wallet.model.AddAssetRequest;
import com.swiss_post.crypto_wallet.model.CreateWalletRequest;
import com.swiss_post.crypto_wallet.model.WalletSimulationRequest;
import com.swiss_post.crypto_wallet.model.WalletSimulationResult;
import com.swiss_post.crypto_wallet.model.Wallet;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(
            @Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createNewWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @PostMapping("/{walletId}/assets")
    public ResponseEntity<?> addAsset(
            @PathVariable String walletId,
            @Valid @RequestBody AddAssetRequest request
    ) {
        return ResponseEntity.ok(walletService.addAssetToWallet(walletId, request.symbol(), request.quantity(), request.price()));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<?> getWallet(@PathVariable String walletId) {
        return ResponseEntity.ok(walletService.getWallet(walletId));
    }

    @PostMapping("/simulate/{date}")
    public ResponseEntity<WalletSimulationResult> simulateWallet(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody WalletSimulationRequest request
    ) {
        return ResponseEntity.ok(walletService.simulateWallet(request.assets(), date));
    }
}
