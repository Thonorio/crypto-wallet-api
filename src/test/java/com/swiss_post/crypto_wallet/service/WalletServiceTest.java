package com.swiss_post.crypto_wallet.service;

import com.swiss_post.crypto_wallet.entity.AssetEntity;
import com.swiss_post.crypto_wallet.entity.CoinCapAssetEntity;
import com.swiss_post.crypto_wallet.entity.UserEntity;
import com.swiss_post.crypto_wallet.entity.WalletEntity;
import com.swiss_post.crypto_wallet.infrastructure.exceptions.TokenNotFoundException;
import com.swiss_post.crypto_wallet.model.CreateWalletRequest;
import com.swiss_post.crypto_wallet.model.SimulationAsset;
import com.swiss_post.crypto_wallet.model.Wallet;
import com.swiss_post.crypto_wallet.model.WalletSimulationResult;
import com.swiss_post.crypto_wallet.repository.CoinCapAssetRepository;
import com.swiss_post.crypto_wallet.repository.UserRepository;
import com.swiss_post.crypto_wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CoinCapAssetRepository coinCapAssetRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWallet_existingWallet_returnsWallet() {
        WalletEntity walletEntity = WalletEntity.builder()
                .id("wallet1")
                .assets(List.of(
                        AssetEntity.builder().symbol("BTC").quantity(BigDecimal.TEN).price(BigDecimal.valueOf(100)).build()
                ))
                .build();
        when(walletRepository.findById("wallet1")).thenReturn(Optional.of(walletEntity));

        Wallet wallet = walletService.getWallet("wallet1");

        assertEquals("wallet1", wallet.id());
        assertEquals(1, wallet.assets().size());
        assertEquals("BTC", wallet.assets().get(0).symbol());
    }

    @Test
    void getWallet_nonExistingWallet_throwsException() {
        when(walletRepository.findById("wallet1")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> walletService.getWallet("wallet1"));
        assertEquals("Wallet not found", ex.getMessage());
    }

    @Test
    void createNewWallet_createsUserIfNotExists() {
        CreateWalletRequest request = new CreateWalletRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(i -> i.getArgument(0));

        Wallet wallet = walletService.createNewWallet(request);

        assertNotNull(wallet.id());
        assertEquals(BigDecimal.ZERO, wallet.total());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void addAssetToWallet_happyPath() {
        String walletId = "wallet1";
        WalletEntity walletEntity = WalletEntity.builder().id(walletId).assets(new ArrayList<>()).build();
        CoinCapAssetEntity coinCapAsset = CoinCapAssetEntity.builder().symbol("BTC").timestamp(LocalDate.now().toEpochDay()).data(List.of("50000")).build();

        when(coinCapAssetRepository.findBySymbol("BTC")).thenReturn(Optional.of(coinCapAsset));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(i -> i.getArgument(0));

        Wallet wallet = walletService.addAssetToWallet(walletId, "BTC", BigDecimal.ONE, BigDecimal.valueOf(50000));

        assertEquals(walletId, wallet.id());
        assertEquals(1, wallet.assets().size());
        assertEquals("BTC", wallet.assets().get(0).symbol());
    }

    @Test
    void addAssetToWallet_tokenNotFound_throwsException() {
        when(coinCapAssetRepository.findBySymbol("BTC")).thenReturn(Optional.empty());
        TokenNotFoundException ex = assertThrows(TokenNotFoundException.class,
                () -> walletService.addAssetToWallet("wallet1", "BTC", BigDecimal.ONE, BigDecimal.TEN));
        assertEquals("Token price not found for symbol: BTC", ex.getMessage());
    }

    @Test
    void simulateWallet_calculatesCorrectPerformance() {
        SimulationAsset btc = new SimulationAsset("BTC", BigDecimal.TEN, BigDecimal.valueOf(100));
        SimulationAsset eth = new SimulationAsset("ETH", BigDecimal.valueOf(5), BigDecimal.valueOf(50));

        CoinCapAssetEntity btcEntity = CoinCapAssetEntity.builder().symbol("BTC").timestamp(LocalDate.now().toEpochDay()).data(List.of("120")).build();
        CoinCapAssetEntity ethEntity = CoinCapAssetEntity.builder().symbol("ETH").timestamp(LocalDate.now().toEpochDay()).data(List.of("40")).build();

        when(coinCapAssetRepository.findTopBySymbolAndTimestampBetweenOrderByTimestampDesc(eq("BTC"), anyLong(), anyLong())).thenReturn(Optional.of(btcEntity));
        when(coinCapAssetRepository.findTopBySymbolAndTimestampBetweenOrderByTimestampDesc(eq("ETH"), anyLong(), anyLong())).thenReturn(Optional.of(ethEntity));

        WalletSimulationResult result = walletService.simulateWallet(List.of(btc, eth), LocalDate.now());

        assertEquals(BigDecimal.valueOf(1400.00).setScale(2), result.total());
        assertEquals("BTC", result.bestAsset());
        assertEquals("ETH", result.worstAsset());
    }

    @Test
    void simulateWallet_missingToken_throwsException() {
        SimulationAsset btc = new SimulationAsset("BTC", BigDecimal.ONE, BigDecimal.TEN);
        when(coinCapAssetRepository.findTopBySymbolAndTimestampBetweenOrderByTimestampDesc(eq("BTC"), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        TokenNotFoundException ex = assertThrows(TokenNotFoundException.class,
                () -> walletService.simulateWallet(List.of(btc), LocalDate.now()));
        assertEquals("Token price not found for symbol: BTC", ex.getMessage());
    }
}