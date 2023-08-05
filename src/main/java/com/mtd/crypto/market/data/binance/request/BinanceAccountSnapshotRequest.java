package com.mtd.crypto.market.data.binance.request;

import com.mtd.crypto.market.data.binance.enumarator.BinanceAccountType;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceAccountSnapshotRequest extends BinanceRequestBase {

    private BinanceAccountType type;
    // No additional parameters required for this request
}