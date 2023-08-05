package com.mtd.crypto.trader.spot.pipeline.visitor;

import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;

public interface SpotVisitor {
    void visit(SpotTradeContext context) throws PipelineStopException;

    String notificationText();

    //TODO String telegram message or implement an abstract class from this to

}
