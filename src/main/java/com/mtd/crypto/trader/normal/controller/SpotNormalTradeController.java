package com.mtd.crypto.trader.normal.controller;

import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.normal.data.dto.SpotNormalTradeDto;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.service.SpotNormalTradeDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("spot/normal")
@RequiredArgsConstructor
public class SpotNormalTradeController {

    private final SpotNormalTradeDataService spotNormalTradeDataService;
    private final BinanceService binanceService;

    @PostMapping("data")
    public SpotNormalTradeData save(@RequestBody @Valid SpotNormalTradeDto spotNormalTradeDto) {
        Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeDto.getSymbol());

        if (spotNormalTradeDto.isPriceDropRequired() && currentPrice < spotNormalTradeDto.getEntry()) {
            throw new RuntimeException("Entry price cannot be lower than current price when price drop required!");
        }

        return spotNormalTradeDataService.createTradeData(spotNormalTradeDto);
        //todo test here with mockMVC to get expected validation result for currentPrice
        //TODO add controllerADvice for binanceException and all other exceptions. Return response entity with exception message
        //Check if coin exists in binance
        //validate stop and limit price
    }


    //Get All By Filter
    //Approve Trade
    //Cancel Trade in position. First make market sell then mark order as cancelled in position

}
