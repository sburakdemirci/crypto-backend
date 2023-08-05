package com.mtd.crypto.trader.futures.data;

import com.mtd.crypto.market.data.binance.enumarator.BinanceFuturesPositionSide;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuturesParentTradeData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    //todo trade status


    @NotBlank(message = "Symbol is required")
    private String symbol;

    @Enumerated(EnumType.STRING)
    private BinanceFuturesPositionSide positionSide;
}
