package com.mtd.crypto.market.data.request;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BinanceCancelOCOOrderRequestDto extends BinanceRequestBase {

    private String symbol;
    private Long orderListId;


}