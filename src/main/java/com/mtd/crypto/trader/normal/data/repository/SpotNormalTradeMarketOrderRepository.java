package com.mtd.crypto.trader.normal.data.repository;

import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotNormalTradeMarketOrderRepository extends JpaRepository<SpotNormalTradeMarketOrder, String> {
    // Custom queries can be added here if needed


}
