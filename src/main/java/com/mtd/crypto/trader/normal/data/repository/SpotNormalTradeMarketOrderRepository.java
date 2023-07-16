package com.mtd.crypto.trader.normal.data.repository;

import com.mtd.crypto.market.data.binance.binance.BinanceOrderSide;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotNormalTradeMarketOrderRepository extends JpaRepository<SpotNormalTradeMarketOrder, String> {
    // Custom queries can be added here if needed

    List<SpotNormalTradeMarketOrder> findAllByParentTradeIdAndType(String parentTradeId, SpotNormalTradeMarketOrderType spotNormalTradeMarketOrderType);

    List<SpotNormalTradeMarketOrder> findAllByParentTradeIdAndSide(String parentTradeId, BinanceOrderSide side);

    List<SpotNormalTradeMarketOrder> findAllByParentTradeId(String parentTradeId);

}
