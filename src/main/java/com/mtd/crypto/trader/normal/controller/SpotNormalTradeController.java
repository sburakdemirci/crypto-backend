package com.mtd.crypto.trader.normal.controller;

import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.normal.service.SpotNormalTradeDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("spot/normal")
@RequiredArgsConstructor
public class SpotNormalTradeController {

    private final BinanceService binanceService;
    private final SpotNormalTradeDataService spotNormalTradeDataService;

/*
    @PostMapping
    public void save(@RequestBody List<SpotNormalTradeDto> spotNormalTradeDtoList) {

        spotNormalTradeDtoList.forEach(spotNormalTradeDto -> {
            binanceService.

        });


    }
*/

}
