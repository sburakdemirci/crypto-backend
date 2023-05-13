package com.mtd.crypto.trader.data.repository;

import com.mtd.crypto.trader.data.entity.TradeData;
import com.mtd.crypto.trader.enumarator.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TradeDataRepository extends JpaRepository<TradeData, String> {


    List<TradeData> findAllByStatus(TradeStatus status);


    @Modifying
    @Query("update TradeData td set td.status=:status where td.id=:id")
    void setTradeStatus(@Param(value = "id") String id, @Param(value = "status") TradeStatus status);

}
