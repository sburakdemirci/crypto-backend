package com.mtd.crypto.trader.normal.data.dto;

import com.mtd.crypto.trader.common.enumarator.TradeSource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class SpotNormalTradeDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    public void symbolNotBlank() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("")
                .baseTradingSymbol("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(90.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void baseTradingSymbolNotBlank() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(90.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void entryPositive() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(-10.0)
                .takeProfit(120.0)
                .stop(90.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void takeProfitPositive() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(100.0)
                .takeProfit(0.0)
                .stop(90.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void stopPositive() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(-5.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isTakeProfitHigherThanStop() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(100.0)
                .takeProfit(10.0)
                .stop(15.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isEntryHigherThanStop() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(20.0)
                .takeProfit(120.0)
                .stop(15.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isTakeProfitHigherThanEntry() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(10.0)
                .takeProfit(5.0)
                .stop(90.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isHighLoss() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(85.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void happyPath() {
        SpotNormalTradeDto trade = SpotNormalTradeDto.builder()
                .symbol("BTC")
                .baseTradingSymbol("USDT")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(95.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeDto>> violations = validator.validate(trade);
        Assertions.assertTrue(violations.isEmpty());
    }
}
