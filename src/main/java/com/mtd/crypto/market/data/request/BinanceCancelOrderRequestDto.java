package com.mtd.crypto.market.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceCancelOrderRequestDto extends BinanceRequestBase {
    private String symbol;
    private Long orderId;

    //todo burak calculate average price


}