package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class BinanceCandleStickResponse {

    private Object openTime;


    private Object open;


    private Object high;


    private Object low;


    private Object close;


    private Object volume;


    private Object closeTime;


    private Object quoteAssetVolume;


    private Object numberOfTrades;

    private Object takerBuyBaseAssetVolume;

    private Object takerBuyQuoteAssetVolume;


    //TODO burak test this


    public static List<BinanceCandleStickResponse> parse(String responseToParse) throws JSONException {

        JSONArray jsonArray = new JSONArray(responseToParse);
        List<BinanceCandleStickResponse> candles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray candleArray = jsonArray.getJSONArray(i);
            BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
            candle.setOpenTime(candleArray.getLong(0));
            candle.setOpen(candleArray.getString(1));
            candle.setHigh(candleArray.getString(2));
            candle.setLow(candleArray.getString(3));
            candle.setClose(candleArray.getString(4));
            candle.setVolume(candleArray.getString(5));
            candle.setCloseTime(candleArray.getLong(6));
            candle.setQuoteAssetVolume(candleArray.getString(7));
            candle.setNumberOfTrades(candleArray.getInt(8));
            candle.setTakerBuyBaseAssetVolume(candleArray.getString(9));
            candle.setTakerBuyQuoteAssetVolume(candleArray.getString(10));
            candles.add(candle);
        }
        return candles;
    }
}
