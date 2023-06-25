package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.dto.SpotNormalTradeDto;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeDataRepository;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeMarketOrderRepository;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@LoggableClass
@Slf4j
@Service
@RequiredArgsConstructor
public class SpotNormalTradeDataService {

    private final SpotNormalTradeDataRepository spotNormalTradeDataRepository;
    private final SpotNormalTradeMarketOrderRepository spotNormalTradeMarketOrderRepository;


    //TODO validate if coin exists in binance before this method
    @Transactional(rollbackFor = Exception.class)
    public SpotNormalTradeData createTradeData(SpotNormalTradeDto spotNormalTradeDto) {

        //Check if pricedropRequred is false and current price greater than %2 of entry price
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .symbol(spotNormalTradeDto.getSymbol())
                .baseTradingSymbol(spotNormalTradeDto.getBaseTradingSymbol())
                .entry(spotNormalTradeDto.getEntry())
                .takeProfit(spotNormalTradeDto.getTakeProfit())
                .stop(spotNormalTradeDto.getStop())
                .source(spotNormalTradeDto.getSource())
                .walletPercentage(spotNormalTradeDto.getWalletPercentage())
                .build();
        return spotNormalTradeDataRepository.save(spotNormalTradeData);
    }


    public void approveTrade(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        if (spotNormalTradeData.getTradeStatus() != TradeStatus.APPROVAL_WAITING) {
            throw new RuntimeException("Trade cannot be approved! Current status is: " + spotNormalTradeData.getTradeStatus().toString());
        }
        spotNormalTradeData.setApprovedAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_WAITING);
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }


    public void enterPosition(String tradeDataId, SpotNormalTradeMarketOrder marketBuyOrder) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        spotNormalTradeData.setPositionStartedAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.IN_POSITION);
        spotNormalTradeData.setAverageEntryPrice(marketBuyOrder.getAveragePrice());
        spotNormalTradeData.setTotalQuantity(marketBuyOrder.getQuantity());
        spotNormalTradeData.setQuantityLeftInPosition(marketBuyOrder.getQuantity());
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }


    public void partialProfit(String tradeDataId, SpotNormalTradeMarketOrder marketBuyOrder) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketBuyOrder.getQuantity());
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }


    public void fullProfitExit(String tradeDataId, SpotNormalTradeMarketOrder marketBuyOrder) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketBuyOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_FINISHED_WITH_PROFIT);
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }

    public void fullStopLossExit(String tradeDataId, SpotNormalTradeMarketOrder marketBuyOrder) {
        SpotNormalTradeData spotNormalTradeData = spotNormalTradeDataRepository.findById(tradeDataId).orElseThrow(() -> new RuntimeException("Cannot found trade with id :" + tradeDataId));
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketBuyOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_FINISHED_WITH_LOSS);
        spotNormalTradeDataRepository.save(spotNormalTradeData);
    }

    //TODO exception handling. Make sure you are sending messages via ControllerAdvice

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


    public List<SpotNormalTradeData> findTradesByStatus(TradeStatus tradeStatus) {
        return spotNormalTradeDataRepository.findAllByTradeStatus(tradeStatus);
    }

    public List<SpotNormalTradeMarketOrder> findMarketOrderByParentTradeAndType(String parentTradeId, SpotNormalTradeMarketOrderType spotNormalTradeMarketOrderType) {
        return spotNormalTradeMarketOrderRepository.findAllByParentTradeIdAndType(parentTradeId, spotNormalTradeMarketOrderType);
    }


    public List<SpotNormalTradeMarketOrder> findMarketOrderByParentTrade(String parentTradeId) {
        return spotNormalTradeMarketOrderRepository.findAllByParentTradeId(parentTradeId);
    }


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

}
