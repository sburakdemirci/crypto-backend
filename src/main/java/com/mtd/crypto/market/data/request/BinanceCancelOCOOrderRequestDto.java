package com.mtd.crypto.market.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
public class BinanceCancelOCOOrderRequestDto extends BinanceRequestBase {

    private String symbol;
    private Long orderListId;


}