package com.mtd.crypto.trader.service;

import com.mtd.crypto.trader.data.entity.TradeData;
import com.mtd.crypto.trader.data.repository.TradeDataRepository;
import com.mtd.crypto.trader.enumarator.TradeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TraderBotDataService {

    private final TradeDataRepository tradeDataRepository;

    @Transactional
    public TradeData save(TradeData tradeData) {
        return tradeDataRepository.save(tradeData);
    }

    @Transactional
    public List<TradeData> saveAll(List<TradeData> tradeDataList) {
        return tradeDataRepository.saveAll(tradeDataList);
    }

    @Transactional
    public void deleteById(String id) {
        tradeDataRepository.deleteById(id);
    }

    public Optional<TradeData> findById(String id) {
        return tradeDataRepository.findById(id);
    }

    public List<TradeData> findAllByStatus(TradeStatus status) {
        return tradeDataRepository.findAllByStatus(status);
    }

    @Transactional
    public void changeStatus(String id, TradeStatus status) {
        tradeDataRepository.setTradeStatus(id, status);
    }


    //save
    //update
    //changeStatus
    //delelte

    //getBinanceCurrentPrice
    //getBinance last 15 minute data. 15 minutes can be changed. It will be ensure its not a needle.
    //binance dan ya 15 dakikalik mumu al. Ama bu durumda soyle bir hata olabilir. Mumlarin baslangic ve bitis sureleri var. Ornegin 10:00 da baslaya mum 10:15 de bitiyor. Sen 10:01 de datayi cekersen
    //bu veri aslinda son 1 dakikanin verisi olur. O yuzden 1 dakikalik mumlardan son 15 mum yani son 15 dakikayi cek. Sonra butun mumlarin open ve close price'larinin giris noktasinin ustunde
    //oldugundan emin ol


}
