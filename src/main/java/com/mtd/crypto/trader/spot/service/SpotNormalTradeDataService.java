package com.mtd.crypto.trader.spot.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.BinanceOrderResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeSource;
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
    public SpotNormalTradeData createTradeData(@Valid SpotNormalTradeCreateRequest spotNormalTradeCreateRequest) {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .symbol(spotNormalTradeCreateRequest.getSymbol())
                .quoteAsset(spotNormalTradeCreateRequest.getQuoteAsset())
                .entry(spotNormalTradeCreateRequest.getEntry())
                .enterCurrentPrice(spotNormalTradeCreateRequest.isEnterCurrentPrice())
                .priceDropRequired(spotNormalTradeCreateRequest.isPriceDropRequired())
                .gradualProfit(spotNormalTradeCreateRequest.isGradualSelling())
                .takeProfit(spotNormalTradeCreateRequest.getTakeProfit())
                .stop(spotNormalTradeCreateRequest.getStop())
                .source(spotNormalTradeCreateRequest.isBurak() ? TradeSource.BURAK : TradeSource.HALUK)
                .walletPercentage(((double) spotNormalTradeCreateRequest.getWalletPercentage() / 100))
                .tradeStatus(TradeStatus.APPROVAL_WAITING)
                .notes(spotNormalTradeCreateRequest.getNotes())
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
                .commission(getUsdtCommission(binanceOrderResponse))
                .build();
        return marketOrderRepository.save(spotNormalTradeMarketOrder);
    }

    /**
     * If commission asset includes USD for example its USDT or BUSD etc. It will count as a commission
     * If commission asset is not in dollars, it will fetch the price and calculate price in dollars.
     *
     * @param binanceOrderResponse
     * @return
     */
    private double getUsdtCommission(BinanceOrderResponse binanceOrderResponse) {
        return binanceOrderResponse.getFills().stream().map(fill -> {
            if (!fill.getCommissionAsset().contains("USD")) {
                return binanceService.getCurrentPrice(fill.getCommissionAsset() + "USDT") * fill.getCommission();
            } else {
                return fill.getCommission();
            }
        }).mapToDouble(d -> d).sum();
    }


    public SpotNormalTradeData approveTrade(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        if (spotNormalTradeData.getTradeStatus() != TradeStatus.APPROVAL_WAITING) {
            throw new RuntimeException("Trade cannot be approved! Current status is: " + spotNormalTradeData.getTradeStatus().toString());
        }
        spotNormalTradeData.setApprovedAt(Instant.now());
        spotNormalTradeData.setTradeStatus(TradeStatus.POSITION_WAITING);
        return tradeDataRepository.save(spotNormalTradeData);
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
    public SpotNormalTradeData cancelTradeBeforePosition(String tradeDataId) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        if (spotNormalTradeData.getTradeStatus() == TradeStatus.POSITION_WAITING || spotNormalTradeData.getTradeStatus() == TradeStatus.APPROVAL_WAITING) {
            spotNormalTradeData.setCancelledAt(Instant.now());
            spotNormalTradeData.setTradeStatus(TradeStatus.CANCELLED_BEFORE_POSITION);
            return tradeDataRepository.save(spotNormalTradeData);
        } else {
            throw new RuntimeException("Trade is not before position");
        }

    }

    @Transactional
    public SpotNormalTradeData cancelTradeInPosition(String tradeDataId, SpotNormalTradeMarketOrder marketSellOrder) {
        SpotNormalTradeData spotNormalTradeData = findById(tradeDataId);
        spotNormalTradeData.setQuantityLeftInPosition(spotNormalTradeData.getQuantityLeftInPosition() - marketSellOrder.getQuantity());
        spotNormalTradeData.setTradeStatus(TradeStatus.CLOSED_IN_POSITION);
        spotNormalTradeData.setPositionFinishedAt(Instant.now());
        spotNormalTradeData.setCancelledAt(Instant.now());
        return tradeDataRepository.save(spotNormalTradeData);
    }

    @Transactional
    public SpotNormalTradeData adjustTradeInPosition(String tradeId, @Valid SpotNormalTradeAdjustRequest spotNormalTradeAdjustRequest) {
        SpotNormalTradeData tradeData = findById(tradeId);
        tradeData.setStop(spotNormalTradeAdjustRequest.getStop());
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

    public List<SpotNormalTradeData> findAllByOrderByCreatedTimeDesc() {
        return tradeDataRepository.findAllByOrderByCreatedTimeDesc();
    }


    public List<SpotNormalTradeData> findTradesByStatusesOrderByStatusAndCreatedTime(List<TradeStatus> tradeStatusList) {
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
