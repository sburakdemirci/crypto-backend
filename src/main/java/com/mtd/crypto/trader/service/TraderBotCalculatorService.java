package com.mtd.crypto.trader.service;


public class TraderBotCalculatorService {
    //static calculator methods


    private Double calculateLimitPrice(Double stopPrice) {
        return stopPrice - (stopPrice / 1000);
    }


}
