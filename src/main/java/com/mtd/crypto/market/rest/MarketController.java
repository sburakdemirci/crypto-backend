package com.mtd.crypto.market.rest;

import com.mtd.crypto.market.data.response.BinanceCurrentPriceResponse;
import com.mtd.crypto.market.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("market/data")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://192.168.4.22:8082")

public class MarketController {

    private final BinanceService binanceService;


    @GetMapping
    public List<BinanceCurrentPriceResponse> getAllCoinInfo() {
        return binanceService.getAllCoinPrices().stream().filter(coin -> coin.getSymbol().contains("USDT")).toList();
    }
}
