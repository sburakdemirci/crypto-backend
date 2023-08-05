package com.mtd.crypto.trader.spot.data.dto;

import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeCreateRequest;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeEntryAlgorithm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class SpotNormalTradeCreateRequestTest {

    private Validator validator;

    @BeforeEach
    public void setupValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void symbolNotBlank() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(90.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CURRENT_PRICE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void entryPositive() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(-10.0)
                .takeProfit(120.0)
                .stop(90.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CURRENT_PRICE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void takeProfitPositive() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(0.0)
                .stop(90.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CURRENT_PRICE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void stopPositive() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(-5.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CURRENT_PRICE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isTakeProfitHigherThanStop() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(10.0)
                .stop(15.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CANDLE_4_HOURS_CLOSE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isEntryHigherThanStop() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(20.0)
                .takeProfit(120.0)
                .stop(15.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CANDLE_4_HOURS_CLOSE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isTakeProfitHigherThanEntry() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(10.0)
                .takeProfit(5.0)
                .stop(90.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.LAST_PRICE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void isHighLoss() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(85.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.PRICE_DROP)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void happyPath() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(95.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CURRENT_PRICE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void entryAlgorithmNotNull() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(95.0)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void positionAmountInDollarNotNull() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(95.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CURRENT_PRICE)
                .gradualSelling(true)
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void validTrade() {
        SpotNormalTradeCreateRequest trade = SpotNormalTradeCreateRequest.builder()
                .symbol("BTC")
                .entry(100.0)
                .takeProfit(120.0)
                .stop(95.0)
                .entryAlgorithm(SpotNormalTradeEntryAlgorithm.CURRENT_PRICE)
                .gradualSelling(true)
                .positionAmountInDollar(1000)
                .notes("Test notes")
                .build();

        Set<ConstraintViolation<SpotNormalTradeCreateRequest>> violations = validator.validate(trade);
        Assertions.assertTrue(violations.isEmpty());
    }
}
