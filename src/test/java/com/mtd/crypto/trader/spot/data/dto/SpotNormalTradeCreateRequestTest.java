package com.mtd.crypto.trader.spot.data.dto;

import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeCreateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class SpotNormalTradeCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    public void symbolNotBlank() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("")
                .quoteAsset("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(90.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void baseTradingSymbolNotBlank() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(90.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void entryPositive() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(-10.0)
                .takeProfit(120.0)
                .stop(90.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void takeProfitPositive() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(100.0)
                .takeProfit(0.0)
                .stop(90.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void stopPositive() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(-5.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isTakeProfitHigherThanStop() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(100.0)
                .takeProfit(10.0)
                .stop(15.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isEntryHigherThanStop() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(20.0)
                .takeProfit(120.0)
                .stop(15.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isTakeProfitHigherThanEntry() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(10.0)
                .takeProfit(5.0)
                .stop(90.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isHighLoss() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(85.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void happyPath() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .quoteAsset("USDT")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(95.0)
                .priceDropRequired(true)
                .burak(true)
                .walletPercentage(20)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertTrue(violations.isEmpty());
    }

}
