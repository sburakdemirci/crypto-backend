package com.mtd.crypto.trader.normal.data.dto;

import com.mtd.crypto.trader.normal.enumarator.SpotNormalMarketOrderPositionCommandType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpotNormalMarketOrderPositionCommand {
    private Double newStopPrice; //this will be avg price
    private Double quantityToSell;
    private SpotNormalMarketOrderPositionCommandType command;
}
