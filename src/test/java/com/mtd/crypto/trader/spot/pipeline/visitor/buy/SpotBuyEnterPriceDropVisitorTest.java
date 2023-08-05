package com.mtd.crypto.trader.spot.pipeline.visitor.buy;


import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SpotBuyEnterPriceDropVisitorTest {

    @Mock
    private BinanceService binanceService;

    private SpotBuyEnterPriceDropVisitor visitor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        visitor = new SpotBuyEnterPriceDropVisitor(binanceService);
    }

    @Test
    void visit_CurrentPriceBelowSafeEntryPrice_SetsDecisionToBuy() throws PipelineStopException {
        // Arrange
        SpotTradeContext context = new SpotTradeContext(new SpotNormalTradeData(), 1000.0);
        context.getTradeData().setEntry(100.0);
        context.getTradeData().setPositionAmountInDollar(1000);
        when(binanceService.getCurrentPrice(context.getTradeData().getSymbol())).thenReturn(99.0);

        // Act
        visitor.visit(context);

        // Assert
        SpotOperationDecision decision = context.getDecision();
        assertNotNull(decision);
        assertEquals(BinanceOrderSide.BUY, decision.getSide());
        assertEquals(10.1010101010101, decision.getQuantity(), 0.0001);
        assertEquals(SpotNormalTradeMarketOrderType.ENTRY, decision.getType());
    }

    @Test
    void visit_CurrentPriceAboveSafeEntryPrice_DoesNotSetDecisionToBuy() throws PipelineStopException {
        // Arrange
        SpotTradeContext context = new SpotTradeContext(new SpotNormalTradeData(), 1000.0);
        context.getTradeData().setEntry(100.0);
        context.getTradeData().setPositionAmountInDollar(1000);
        when(binanceService.getCurrentPrice(context.getTradeData().getSymbol())).thenReturn(101.0);

        // Act
        visitor.visit(context);

        // Assert
        SpotOperationDecision decision = context.getDecision();
        assertNull(decision);
    }

    @Test
    void notificationText_ReturnsPositionStartedWithPriceDropStrategy() {
        // Act
        String notificationText = visitor.notificationText();

        // Assert
        assertEquals("Position started with price drop strategy", notificationText);
    }
}