package com.mtd.crypto.market.rest;

import com.mtd.crypto.market.data.binance.response.BinanceCurrentPriceResponse;
import com.mtd.crypto.market.data.binance.response.BinanceUserAssetResponse;
import com.mtd.crypto.market.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("market/spot")
@RequiredArgsConstructor

public class SpotMarketController {

    private final BinanceService binanceService;

    @GetMapping("data")
    public List<BinanceCurrentPriceResponse> getAllCoinInfo() {
        return binanceService.getAllCoinPrices().stream().filter(coin -> coin.getSymbol().contains("USDT")).toList();
    }

    @GetMapping("health")
    public void testBinanceHealth() {
        binanceService.executeHealthCheck();
    }

    @GetMapping("wallet")
    public List<BinanceUserAssetResponse> getWallet() {
        return binanceService.getWallet();
    }

    @GetMapping("wallet/{symbol}")
    public BinanceUserAssetResponse getBalanceBySymbol(@PathVariable String symbol) {
        return binanceService.getBalanceBySymbol(symbol);
    }

}
