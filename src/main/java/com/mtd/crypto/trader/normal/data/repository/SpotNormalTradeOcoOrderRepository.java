package com.mtd.crypto.trader.normal.data.repository;

import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeOcoOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeOcoOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotNormalTradeOcoOrderRepository extends JpaRepository<SpotNormalTradeOcoOrder, String> {

    Optional<SpotNormalTradeOcoOrder> findByParentTradeIdAndBinanceOcoOrderListId(String parentTradeId, Long binanceOcoOrderListId);

    List<SpotNormalTradeOcoOrder> findAllByStatus(SpotNormalTradeOcoOrderStatus status);
}
