package com.mtd.crypto.market.data.binance.request;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BinanceCancelOrderRequestDto extends BinanceRequestBase {
    private String symbol;
    private Long orderId;

    //todo burak calculate average price


}