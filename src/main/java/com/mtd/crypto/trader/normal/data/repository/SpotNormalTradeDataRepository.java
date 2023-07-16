package com.mtd.crypto.trader.normal.data.repository;

import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotNormalTradeDataRepository extends JpaRepository<SpotNormalTradeData, String> {

    List<SpotNormalTradeData> findAllByTradeStatus(TradeStatus tradeStatus);

    List<SpotNormalTradeData> findAllByTradeStatusIn(List<TradeStatus> tradeStatuses);

    List<SpotNormalTradeData> findAllByTradeStatusInOrderByTradeStatusAscCreatedTimeAsc(List<TradeStatus> tradeStatuses);

    // Custom queries can be added here if needed
}
