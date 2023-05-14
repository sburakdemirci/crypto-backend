package com.mtd.crypto.parser.service;

import com.mtd.crypto.market.client.BinanceHttpClient;
import com.mtd.crypto.parser.data.dto.HalukDto;
import com.mtd.crypto.parser.data.response.TradeDataProfit;
import com.mtd.crypto.parser.data.response.TradeDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InitialTradeDataService {

    private final BinanceHttpClient binanceHttpClient;


    public List<TradeDataResponse> getTradeData(List<HalukDto> halukDtoList) {
        List<TradeDataResponse> tradeDataResponseList = new ArrayList<>();
        halukDtoList.forEach(halukDto -> {

            Double currentPriceResponse = binanceHttpClient.getPrice(halukDto.getCoin());
            Double currentPrice = currentPriceResponse;

            List<TradeDataProfit> profitList = new ArrayList<>();

            TradeDataProfit tradeDataProfit1 = new TradeDataProfit(halukDto.getEntryPrice(),currentPrice,halukDto.getTakeProfit1());
            profitList.add(tradeDataProfit1);

            if (Optional.ofNullable(halukDto.getTakeProfit2()).isPresent()) {
                TradeDataProfit tradeDataProfit2 = new TradeDataProfit(halukDto.getEntryPrice(),currentPrice,halukDto.getTakeProfit2());
                profitList.add(tradeDataProfit2);
            }

            TradeDataResponse tradeDataResponse= new TradeDataResponse(halukDto.getCoin(),halukDto.getEntryPrice(),halukDto.getStopPrice(),currentPrice,halukDto.isReverse(),profitList);
            tradeDataResponseList.add(tradeDataResponse);

        });
        return tradeDataResponseList;
    }

    public List<Object> getSheetHeaders() {
        return Arrays.asList("Coin", "Entry", "Current", "Stop", "Loss", "Current Loss", "TP1 Price", "TP1 Rate", "TP1 Current Rate","TP2 Price", "TP2 Rate", "TP2 Current Rate" );
    }
}
