package com.mtd.crypto.trader.spot.pipeline.visitor.buy;


import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.BinanceUserAssetResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.service.SpotNormalTradeDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpotBuyEnterCheckWalletVisitorTest {

    @Mock
    private BinanceService binanceService;

    @Mock
    private SpotNormalTradeDataService dataService;

    private SpotBuyEnterCheckWalletVisitor visitor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        visitor = new SpotBuyEnterCheckWalletVisitor(binanceService, dataService);
    }

    @Test
    void visit_InsufficientWalletAsset_ThrowsPipelineStopException() throws PipelineStopException {
        // Arrange
        SpotOperationDecision decision = new SpotOperationDecision(BinanceOrderSide.BUY, 1000.0, SpotNormalTradeMarketOrderType.ENTRY);
        SpotTradeContext context = new SpotTradeContext(new SpotNormalTradeData(), 1000.0);
        context.getTradeData().setPositionAmountInDollar(1000);
        BinanceUserAssetResponse userAssetResponse = new BinanceUserAssetResponse();
        userAssetResponse.setAsset("USDT");
        userAssetResponse.setFree(BigDecimal.valueOf(500.0));
        when(binanceService.getBalanceBySymbol("USDT")).thenReturn(userAssetResponse);

        // Act & Assert
        assertThrows(PipelineStopException.class, () -> visitor.visit(context));
        verify(dataService, times(1)).changeStateToApprovalWaiting(context.getTradeData().getId());
    }

    @Test
    void visit_SufficientWalletAsset_DoesNotThrowPipelineStopException() throws PipelineStopException {
        // Arrange
        SpotOperationDecision decision = new SpotOperationDecision(BinanceOrderSide.BUY, 1000.0, SpotNormalTradeMarketOrderType.ENTRY);
        SpotTradeContext context = new SpotTradeContext(new SpotNormalTradeData(), 1000.0);
        context.getTradeData().setPositionAmountInDollar(1000);
        BinanceUserAssetResponse userAssetResponse = new BinanceUserAssetResponse();
        userAssetResponse.setAsset("USDT");
        userAssetResponse.setFree(BigDecimal.valueOf(1500.0));
        when(binanceService.getBalanceBySymbol("USDT")).thenReturn(userAssetResponse);

        // Act & Assert
        assertDoesNotThrow(() -> visitor.visit(context));
        verify(dataService, never()).changeStateToApprovalWaiting(context.getTradeData().getId());
    }

    @Test
    void notificationText_ReturnsEmptyString() {
        // Act
        String notificationText = visitor.notificationText();

        // Assert
        assertEquals("", notificationText);
    }
}