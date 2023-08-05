package com.mtd.crypto.trader.spot.pipeline.algorithm;

import com.mtd.crypto.trader.spot.pipeline.visitor.sell.SpotSellStopLossInsuranceVisitor;
import com.mtd.crypto.trader.spot.pipeline.visitor.sell.SpotSellStopLossVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PriceDropAlgorithm {

    private final SpotSellStopLossInsuranceVisitor spotSellStopLossInsuranceVisitor;
    private final SpotSellStopLossVisitor spotSellStopLossVisitor;

}
