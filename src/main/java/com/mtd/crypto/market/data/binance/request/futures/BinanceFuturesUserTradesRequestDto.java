package com.mtd.crypto.market.data.binance.request.futures;

import com.mtd.crypto.market.data.binance.request.BinanceRequestBase;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class BinanceFuturesUserTradesRequestDto extends BinanceRequestBase {
    private String symbol;
    private Long orderId;
}
