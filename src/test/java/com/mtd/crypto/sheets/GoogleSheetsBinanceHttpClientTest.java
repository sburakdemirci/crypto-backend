package com.mtd.crypto.sheets;

import com.mtd.crypto.parser.data.dto.HalukDto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GoogleSheetsBinanceHttpClientTest {

    @Test
    public void saveGoogleSheets() throws GeneralSecurityException, IOException {

        String input = "ETC - 22.58 GİRİŞ / 22.35 STOP / TP - 23 reverse";
        Pattern pattern = Pattern.compile("^(\\w+)\\s-\\s([\\d.]+)\\sGİRİŞ\\s/\\s([\\d.]+)\\sSTOP\\s/\\sTP1?\\s-\\s([\\d.]+)(?:\\s/\\sTP2\\s-\\s([\\d.]+))?(?:\\s([\\w.]+))?");
/*
        Pattern pattern = Pattern.compile("^(\\w+)\\s-\\s([\\d.]+)\\sGİRİŞ\\s/\\s([\\d.]+)\\sSTOP\\s/\\sTP1?\\s-\\s([\\d.]+)(?:\\s/\\sTP2\\s-\\s([\\d.]+))");
*/

        List<HalukDto> halukDtoList = new ArrayList<>();


        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            HalukDto halukDto = HalukDto.builder()
                    .coin(matcher.group(1))
                    .entryPrice(Double.parseDouble(matcher.group(2)))
                    .stopPrice(Double.parseDouble(matcher.group(3)))
                    .takeProfit1(Double.parseDouble(matcher.group(4))).build();

            if (matcher.group(5) != null) {
                halukDto.setTakeProfit2(Double.parseDouble(matcher.group(5)));
            }

            halukDtoList.add(halukDto);

        }

        System.out.println("");

    }

}