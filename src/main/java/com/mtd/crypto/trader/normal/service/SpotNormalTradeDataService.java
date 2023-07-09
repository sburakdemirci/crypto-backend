package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
import com.mtd.crypto.trader.common.enumarator.TradeSource;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.dto.SpotNormalTradeDto;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeDataRepository;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeMarketOrderRepository;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import jakarta.validation.Valid;
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

    private final SpotNormalTradeDataRepository tradeDataRepository;
    private final SpotNormalTradeMarketOrderRepository marketOrderRepository;

    @Transactional(rollbackFor = Exception.class)
    public SpotNormalTradeData createTradeData(@Valid SpotNormalTradeDto spotNormalTradeDto) {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .symbol(spotNormalTradeDto.getSymbol())
                .quoteAsset(spotNormalTradeDto.getQuoteAsset())
                .entry(spotNormalTradeDto.getEntry())
                .isPriceDropRequired(spotNormalTradeDto.isPriceDropRequired())
                .takeProfit(spotNormalTradeDto.getTakeProfit())
                .stop(spotNormalTradeDto.getStop())
                .source(spotNormalTradeDto.isBurak()? TradeSource.BURAK:TradeSource.HALUK)
                .walletPercentage(((double) spotNormalTradeDto.getWalletPercentage() / 100))
                .tradeStatus(TradeStatus.APPROVAL_WAITING)
                .build();
        return tradeDataRepository.save(spotNormalTradeData);
    }


    public SpotNormalTradeMarketOrder saveMarketOrder(String parentTradeId, BinanceOrderResponse binanceOrderResponse, SpotNormalTradeMarketOrderType spotNormalTradeMarketOrderType) {

        Double averagePrice = binanceOrderResponse.getCummulativeQuoteQty() / binanceOrderResponse.getExecutedQty();
        SpotNormalTradeData spotNormalTradeData = tradeDataRepository.getReferenceById(parentTradeId);

        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = SpotNormalTradeMarketOrder.builder()
                .parentTrade(spotNormalTradeData)
                .binanceOrderId(binanceOrderResponse.getOrderId())
                .side(binanceOrderResponse.getSide())
                .quantity(binanceOrderResponse.getExecutedQty())
                .averagePrice(averagePrice)
                .type(spotNormalTradeMarketOrderType)
                .build();
        return marketOrderRepository.save(spotNormalTradeMarketOrder);
    }

    public void approveTrade(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        if (spotNormalTradeData.getTradeStatus() != TradeStatus.APPROVAL_WAITING) {
            throw new RuntimeException("Trade cannot be approved! Current status is: " + spotNormalTradeData.getTradeStatus().toString());
        }
        spotNormalTradeData.setApprovedAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_WAITING);
        tradeDataRepository.save(spotNormalTradeData);
    }

    public void enterPosition(String tradeDataId, SpotNormalTradeMarketOrder marketBuyOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setPositionStartedAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.IN_POSITION);
        spotNormalTradeData.setAverageEntryPrice(marketBuyOrder.getAveragePrice());
        spotNormalTradeData.setTotalQuantity(marketBuyOrder.getQuantity());
        spotNormalTradeData.setQuantityLeftInPosition(marketBuyOrder.getQuantity());
        tradeDataRepository.save(spotNormalTradeData);
    }


    public void partialProfit(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        tradeDataRepository.save(spotNormalTradeData);
    }


    public void fullProfitExit(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_FINISHED_WITH_PROFIT);
        spotNormalTradeData.setPositionFinishedAt(Instant.now());
        tradeDataRepository.save(spotNormalTradeData);
    }

    public void fullStopLossExit(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_FINISHED_WITH_LOSS);
        spotNormalTradeData.setPositionFinishedAt(Instant.now());
        tradeDataRepository.save(spotNormalTradeData);
    }

    public void fullStopLossExitAfterProfit(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_FINISHED_WITH_PROFIT);
        spotNormalTradeData.setPositionFinishedAt(Instant.now());
        tradeDataRepository.save(spotNormalTradeData);
    }

    //TODO exception handling. Make sure you are sending messages via ControllerAdvice

    public void cancelTradeBeforePosition(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        if (spotNormalTradeData.getTradeStatus() != TradeStatus.POSITION_WAITING) {
            throw new RuntimeException("Only position waiting trades can cancelled");
        }
        spotNormalTradeData.setCancelledAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.CANCELLED_BEFORE_POSITION);
        tradeDataRepository.save(spotNormalTradeData);
    }

    @Transactional
    public void cancelTradeInPosition(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);

        spotNormalTradeData.setCancelledAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.CANCELLED_IN_POSITION);
        tradeDataRepository.save(spotNormalTradeData);
    }


    public List<SpotNormalTradeData> findAllByTradeStatus(TradeStatus tradeStatus) {
        return tradeDataRepository.findAllByTradeStatus(tradeStatus);
    }


    public List<SpotNormalTradeData> findTradesByStatusesOrderByStatusAndCreatedTime(List<TradeStatus> tradeStatusList) {
        return tradeDataRepository.findAllByTradeStatusInOrderByTradeStatusAscCreatedTimeAsc(tradeStatusList);
    }


    public SpotNormalTradeData findById(String tradeId) {
        return tradeDataRepository.findById(tradeId).orElseThrow(() -> new RuntimeException("Trade not found with given id: " + tradeId));
    }

    public List<SpotNormalTradeMarketOrder> findMarketOrderByParentTradeAndType(String parentTradeId, SpotNormalTradeMarketOrderType spotNormalTradeMarketOrderType) {
        return marketOrderRepository.findAllByParentTradeIdAndType(parentTradeId, spotNormalTradeMarketOrderType);
    }


    public List<SpotNormalTradeMarketOrder> findMarketOrderByParentTrade(String parentTradeId) {
        return marketOrderRepository.findAllByParentTradeId(parentTradeId);
    }


}
