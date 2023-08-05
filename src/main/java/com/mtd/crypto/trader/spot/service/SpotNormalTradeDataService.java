package com.mtd.crypto.trader.spot.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.BinanceOrderResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.spot.data.repository.SpotNormalTradeDataRepository;
import com.mtd.crypto.trader.spot.data.repository.SpotNormalTradeMarketOrderRepository;
import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeAdjustRequest;
import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeCreateRequest;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@LoggableClass
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class SpotNormalTradeDataService {

    private final SpotNormalTradeDataRepository tradeDataRepository;
    private final SpotNormalTradeMarketOrderRepository marketOrderRepository;
    private final BinanceService binanceService;

    @Transactional(rollbackFor = Exception.class)
    public SpotNormalTradeData save(Optional<String> tradeId, @Valid SpotNormalTradeCreateRequest spotNormalTradeCreateRequest) {

        SpotNormalTradeData tradeData = tradeId.map(this::findById).orElse(new SpotNormalTradeData());

        tradeData.setSymbol(spotNormalTradeCreateRequest.getSymbol());
        tradeData.setEntry(spotNormalTradeCreateRequest.getEntry());
        tradeData.setEntryAlgorithm(spotNormalTradeCreateRequest.getEntryAlgorithm());
        tradeData.setGradualProfit(spotNormalTradeCreateRequest.isGradualSelling());
        tradeData.setTakeProfit(spotNormalTradeCreateRequest.getTakeProfit());
        tradeData.setCurrentStop(spotNormalTradeCreateRequest.getStop());
        tradeData.setInitialStop(spotNormalTradeCreateRequest.getStop());
        tradeData.setPositionAmountInDollar(spotNormalTradeCreateRequest.getPositionAmountInDollar());
        tradeData.setTradeStatus(TradeStatus.APPROVAL_WAITING);
        tradeData.setNotes(spotNormalTradeCreateRequest.getNotes());

        return tradeDataRepository.save(tradeData);
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
                .commission(binanceOrderResponse.getCommission())
                .build();
        return marketOrderRepository.save(spotNormalTradeMarketOrder);
    }


    public SpotNormalTradeData approveTrade(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setApprovedAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_WAITING);
        return tradeDataRepository.save(spotNormalTradeData);
    }

    public SpotNormalTradeData changeStateToApprovalWaiting(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setApprovedAt(null);
        spotNormalTradeData.setTradeStatus(TradeStatus.APPROVAL_WAITING);
        return tradeDataRepository.save(spotNormalTradeData);
    }

    public SpotNormalTradeData increaseStop(String tradeDataId, double newStopPrice) {
        SpotNormalTradeData tradeData = findById(tradeDataId);
        tradeData.setCurrentStop(newStopPrice);
        return tradeDataRepository.save(tradeData);
    }


    public void enterPosition(String tradeDataId, SpotNormalTradeMarketOrder marketBuyOrder) {
        SpotNormalTradeData tradeData = findById(tradeDataId);
        tradeData.setStartedAt(Instant.now());
        tradeData.setTradeStatus(TradeStatus.IN_POSITION);
        tradeData.setAverageEntryPrice(marketBuyOrder.getAveragePrice());
        tradeData.setTotalQuantity(marketBuyOrder.getQuantity());
        tradeData.setQuantityLeftInPosition(marketBuyOrder.getQuantity());
        tradeDataRepository.save(tradeData);
    }

    public void partialSell(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        tradeDataRepository.save(spotNormalTradeData);
    }

    public void finishPosition(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_FINISHED);
        spotNormalTradeData.setFinishedAt(Instant.now());
        tradeDataRepository.save(spotNormalTradeData);
    }

    //TODO exception handling. Make sure you are sending messages via ControllerAdvice

    public void deleteTradeById(String tradeDataId) {
        tradeDataRepository.deleteById(tradeDataId);
    }


    public SpotNormalTradeData closeTradeManually(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.MANUALLY_CLOSED);
        spotNormalTradeData.setFinishedAt(Instant.now());
        return tradeDataRepository.save(spotNormalTradeData);
    }

    public SpotNormalTradeData adjustTradeInPosition(String tradeId, @Valid SpotNormalTradeAdjustRequest spotNormalTradeAdjustRequest) {
        SpotNormalTradeData tradeData = findById(tradeId);
        tradeData.setCurrentStop(spotNormalTradeAdjustRequest.getStop());
        tradeData.setTakeProfit(spotNormalTradeAdjustRequest.getTakeProfit());
        tradeData.setGradualProfit(spotNormalTradeAdjustRequest.isGradualSelling());
        return tradeDataRepository.save(tradeData);
    }


    public List<SpotNormalTradeData> findAllByTradeStatus(TradeStatus tradeStatus) {
        return tradeDataRepository.findAllByTradeStatus(tradeStatus);
    }


    public List<SpotNormalTradeData> findAllByOrderByTradeStatusAscCreatedTimeAsc() {
        return tradeDataRepository.findAllByOrderByTradeStatusAscCreatedTimeAsc();
    }

    public List<SpotNormalTradeData> findAllByTradeStatusInOrderByTradeStatusAscCreatedTimeAsc(List<TradeStatus> tradeStatusList) {
        return tradeDataRepository.findAllByTradeStatusInOrderByTradeStatusAscCreatedTimeAsc(tradeStatusList);
    }


    public SpotNormalTradeData findById(String tradeId) {
        return tradeDataRepository.findById(tradeId).orElseThrow(() -> new RuntimeException("Trade not found with given id: " + tradeId));
    }

    public List<SpotNormalTradeMarketOrder> findAllMarketOrderByParentTradeAndType(String parentTradeId, SpotNormalTradeMarketOrderType spotNormalTradeMarketOrderType) {
        return marketOrderRepository.findAllByParentTradeIdAndType(parentTradeId, spotNormalTradeMarketOrderType);
    }


    public List<SpotNormalTradeMarketOrder> findAllMarketOrdersByParentTradeIdAndSide(String parentTradeId, BinanceOrderSide side) {
        return marketOrderRepository.findAllByParentTradeIdAndSide(parentTradeId, side);
    }


    public List<SpotNormalTradeMarketOrder> findAllMarketOrderByParentTrade(String parentTradeId) {
        return marketOrderRepository.findAllByParentTradeId(parentTradeId);
    }


}
