package com.mtd.crypto.trader.spot.pipeline.visitor.buy;

import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import org.springframework.stereotype.Component;

@Component
public class SpotBuyEnterLastPriceVisitor implements SpotVisitor {
    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {

        throw new RuntimeException("Not implemented!");
        //todo later
    }

    @Override
    public String notificationText() {
        return null;
    }


}
