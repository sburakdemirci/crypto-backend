package com.mtd.crypto.trader.spot.pipeline.visitor.buy;

import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import com.mtd.crypto.trader.spot.service.SpotNormalTradeDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpotBuyEnterCheckWalletVisitor implements SpotVisitor {


    private static final double COMMISSION_PERCENTAGE = 0.002;

    private final BinanceService binanceService;
    private final SpotNormalTradeDataService dataService;

    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {
        double walletUSDTAsset = binanceService.getBalanceBySymbol("USDT").getFree().doubleValue();

        if (context.getTradeData().getPositionAmountInDollar() * (1.0 + COMMISSION_PERCENTAGE) > walletUSDTAsset) {
            dataService.changeStateToApprovalWaiting(context.getTradeData().getId());
            throw new PipelineStopException("Insufficient wallet asset to enter position. Trade will be converted to Approval Waiting state");
        }
    }

    @Override
    public String notificationText() {
        return "";
    }
}
