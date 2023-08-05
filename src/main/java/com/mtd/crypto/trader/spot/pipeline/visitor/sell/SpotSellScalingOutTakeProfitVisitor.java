package com.mtd.crypto.trader.spot.pipeline.visitor.sell;

import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import com.mtd.crypto.trader.spot.service.SpotNormalTradeDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SpotSellScalingOutTakeProfitVisitor implements SpotVisitor {

    private final SpotNormalTradeDataService dataService;

    //todo pozisyonun bir kisminin
    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {

        if (context.getTradeData().getCurrentStop() >= context.getTradeData().getTakeProfit()) {
            //daha once yapilmis
            double trailingStopPrice = context.getTradeData().getCurrentStop() * 1.01;
            if (context.getCurrentPrice() > trailingStopPrice) {
                dataService.increaseStop(context.getTradeData().getId(), trailingStopPrice);
            }
        } else if (context.getCurrentPrice() >= context.getTradeData().getTakeProfit()) {
            dataService.increaseStop(context.getTradeData().getId(), context.getTradeData().getTakeProfit());
        }

    }


    @Override
    public String notificationText() {
        return "Scaling out profit started for position";
    }

}
