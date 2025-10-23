package com.swiss_post.crypto_wallet.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiss_post.crypto_wallet.model.*;
import com.swiss_post.crypto_wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class WalletControllerTest {

    private MockMvc mockMvc;
    private WalletService walletService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        WalletController controller = new WalletController(walletService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createWallet_shouldReturnCreatedWallet() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest("test@example.com");
        Wallet mockWallet = new Wallet("wallet-123", BigDecimal.ZERO, List.of());

        Mockito.when(walletService.createNewWallet(any())).thenReturn(mockWallet);

        mockMvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("wallet-123"));
    }

    @Test
    void getWallet_shouldReturnWallet() throws Exception {
        Wallet mockWallet = new Wallet("wallet-123", BigDecimal.valueOf(1000), List.of());
        Mockito.when(walletService.getWallet("wallet-123")).thenReturn(mockWallet);

        mockMvc.perform(get("/wallets/wallet-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-123"))
                .andExpect(jsonPath("$.total").value(1000));
    }

    @Test
    void addAsset_shouldReturnWalletWithNewAsset() throws Exception {
        AddAssetRequest request = new AddAssetRequest("BTC", BigDecimal.TEN, BigDecimal.valueOf(30000));
        Wallet mockWallet = new Wallet("wallet-123", BigDecimal.valueOf(300000), List.of(
                new Asset("BTC", BigDecimal.TEN, BigDecimal.valueOf(30000), BigDecimal.valueOf(300000))
        ));

        Mockito.when(walletService.addAssetToWallet(eq("wallet-123"), eq("BTC"), eq(BigDecimal.TEN), eq(BigDecimal.valueOf(30000))))
                .thenReturn(mockWallet);

        mockMvc.perform(post("/wallets/wallet-123/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-123"))
                .andExpect(jsonPath("$.total").value(300000));
    }


    @Test
    void simulateWallet_shouldReturnSimulationResult() throws Exception {
        WalletSimulationRequest request = new WalletSimulationRequest(List.of(
                new SimulationAsset("BTC", BigDecimal.TEN, BigDecimal.valueOf(30000))
        ));
        WalletSimulationResult result = new WalletSimulationResult(
                BigDecimal.valueOf(310000), "BTC", BigDecimal.valueOf(3.33),
                "BTC", BigDecimal.valueOf(-2.5)
        );

        Mockito.when(walletService.simulateWallet(any(), eq(LocalDate.of(2025, 10, 23))))
                .thenReturn(result);

        mockMvc.perform(post("/wallets/simulate/2025-10-23")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(310000))
                .andExpect(jsonPath("$.bestAsset").value("BTC"))
                .andExpect(jsonPath("$.worstAsset").value("BTC"));
    }
}