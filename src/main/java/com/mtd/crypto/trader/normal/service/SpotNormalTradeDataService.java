package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderType;
import com.mtd.crypto.market.data.response.BinanceOCOOrderResponse;
import com.mtd.crypto.market.data.response.BinanceOCOOrderResponse_OrderReport;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.dto.SpotNormalTradeDto;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeOcoOrder;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeDataRepository;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeMarketOrderRepository;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeOcoOrderRepository;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeOcoOrderCancelReason;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeOcoOrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@LoggableClass
@Slf4j
@Service
@RequiredArgsConstructor
public class SpotNormalTradeDataService {

    private final SpotNormalTradeDataRepository spotNormalTradeDataRepository;
    private final SpotNormalTradeMarketOrderRepository spotNormalTradeMarketOrderRepository;
    private final SpotNormalTradeOcoOrderRepository spotNormalTradeOcoOrderRepository;


    //TODO validate if coin exists in binance before this method
    @Transactional(rollbackFor = Exception.class)
    public SpotNormalTradeData saveTradeData(SpotNormalTradeDto spotNormalTradeDto) {

        //Check if pricedropRequred is false and current price greater than %2 of entry price
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .symbol(spotNormalTradeDto.getSymbol())
                .entry(spotNormalTradeDto.getEntry())
                .takeProfit(spotNormalTradeDto.getTakeProfit())
                .stop(spotNormalTradeDto.getStop())
                .source(spotNormalTradeDto.getSource())
                .walletPercentage(spotNormalTradeDto.getWalletPercentage())
                .build();
        return spotNormalTradeDataRepository.save(spotNormalTradeData);
    }


    @Transactional
    public void approveTrade(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        if (spotNormalTradeData.getTradeStatus() != TradeStatus.APPROVAL_WAITING) {
            throw new RuntimeException("Trade cannot be approved");
        }
        spotNormalTradeData.setApprovedAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_WAITING);
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }

    @Transactional
    public void cancelTradeBeforePosition(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        if (spotNormalTradeData.getTradeStatus() != TradeStatus.POSITION_WAITING) {
            throw new RuntimeException("Only position waiting trades can cancelled");
        }
        spotNormalTradeData.setCancelledAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.CANCELLED_BEFORE_POSITION);
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }

    @Transactional
    public void cancelTradeInPosition(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        if (spotNormalTradeData.getTradeStatus() != TradeStatus.POSITION_WAITING) {
            throw new RuntimeException("Only position waiting trades can cancelled");
        }
        spotNormalTradeData.setCancelledAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.CANCELLED_IN_POSITION);
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }

    @Transactional
    public SpotNormalTradeMarketOrder saveMarketOrder(String parentTradeId, BinanceOrderResponse binanceOrderResponse, SpotNormalTradeMarketOrderType spotNormalTradeMarketOrderType) {

        Double averagePrice = binanceOrderResponse.getCummulativeQuoteQty() / binanceOrderResponse.getExecutedQty();
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.getReferenceById(parentTradeId);

        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = SpotNormalTradeMarketOrder.builder()
                .parentTrade(spotNormalTradeData)
                .binanceOrderId(binanceOrderResponse.getOrderId())
                .side(binanceOrderResponse.getSide())
                .quantity(binanceOrderResponse.getExecutedQty())
                .averagePrice(averagePrice)
                .type(spotNormalTradeMarketOrderType)
                .build();
        return spotNormalTradeMarketOrderRepository.save(spotNormalTradeMarketOrder);
    }


    @Transactional
    public SpotNormalTradeOcoOrder saveOcoOrder(String parentTradeId, BinanceOCOOrderResponse binanceOCOOrderResponse) {

        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.getReferenceById(parentTradeId);

        HashMap<BinanceOrderType, BinanceOCOOrderResponse_OrderReport> orders = new HashMap<>();

        binanceOCOOrderResponse.getOrderReports().forEach(orderReport -> {
            orders.put(orderReport.getType(), orderReport);
        });
        BinanceOCOOrderResponse_OrderReport takeProfit = orders.get(BinanceOrderType.LIMIT_MAKER);
        BinanceOCOOrderResponse_OrderReport stopLoss = orders.get(BinanceOrderType.STOP_LOSS_LIMIT);


        SpotNormalTradeOcoOrder spotNormalTradeOcoOrder = SpotNormalTradeOcoOrder
                .builder()
                .parentTrade(spotNormalTradeData)
                .binanceOcoOrderListId(binanceOCOOrderResponse.getOrderListId())
                .binanceTakeProfitOrderId(takeProfit.getOrderId())
                .binanceStopLossOrderId(stopLoss.getOrderId())
                .quantity(takeProfit.getOrigQty())
                .takeProfitPrice(takeProfit.getPrice())
                .stopPrice(stopLoss.getStopPrice())
                .stopLimitPrice(stopLoss.getPrice())
                .status(SpotNormalTradeOcoOrderStatus.ACTIVE)
                .build();
        return spotNormalTradeOcoOrderRepository.save(spotNormalTradeOcoOrder);
    }

    @Transactional
    public SpotNormalTradeOcoOrder cancelOcoOrder(String parentTradeId, Long binanceOrderListId, SpotNormalTradeOcoOrderCancelReason spotNormalTradeOcoOrderCancelReason) {
        SpotNormalTradeOcoOrder spotNormalTradeOcoOrder = spotNormalTradeOcoOrderRepository.findByParentTradeIdAndBinanceOcoOrderListId(parentTradeId, binanceOrderListId).orElseThrow(() -> new RuntimeException("Binance Oco Order cannot found in database"));
        spotNormalTradeOcoOrder.setCancelReason(spotNormalTradeOcoOrderCancelReason);
        spotNormalTradeOcoOrder.setCancelledAt(Instant.now());
        spotNormalTradeOcoOrder.setStatus(SpotNormalTradeOcoOrderStatus.CANCELLED);
        return spotNormalTradeOcoOrderRepository.save(spotNormalTradeOcoOrder);
    }

    public List<SpotNormalTradeData> findParentTradeByStatus(TradeStatus tradeStatus) {
        return spotNormalTradeDataRepository.findAllByTradeStatus(tradeStatus);
    }

    public List<SpotNormalTradeOcoOrder> findOcoOrdersByStatus(SpotNormalTradeOcoOrderStatus status) {
        return spotNormalTradeOcoOrderRepository.findAllByStatus(status);
    }


    //changeTradeStatus
    //findOrdersInPosition
    //findOrdersAwaitingPosition


}
