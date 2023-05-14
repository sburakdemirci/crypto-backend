package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.BinanceNewOrderResponseType;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
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