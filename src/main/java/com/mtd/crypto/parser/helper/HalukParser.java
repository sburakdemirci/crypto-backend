package com.mtd.crypto.parser.helper;

import com.mtd.crypto.parser.data.dto.HalukDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HalukParser {

    public static List<HalukDto> parseData(String data) {
        String[] lines = data.split("\\n");
/*
        Pattern withoutReverse = Pattern.compile("^(\\w+)\\s-\\s([\\d.]+)\\sGİRİŞ\\s/\\s([\\d.]+)\\sSTOP\\s/\\sTP1?\\s-\\s([\\d.]+)(?:\\s/\\sTP2\\s-\\s([\\d.]+))?(?:\\s|$)");
*/

/*
        Pattern patternSingleSpace = Pattern.compile("^(\\w+)\\s-\\s([\\d.]+)\\sGİRİŞ\\s/\\s([\\d.]+)\\sSTOP\\s/\\sTP1?\\s-\\s([\\d.]+)(?:\\s/\\sTP2\\s-\\s([\\d.]+))?(?:\\s([\\w.]+))?");
*/
        Pattern patternMultipleSpaces = Pattern.compile("^(\\w+)\\s+-\\s+([\\d.]+)\\s+GİRİŞ\\s+/\\s+([\\d.]+)\\s+STOP\\s+/\\s+TP\\s?-?\\s+([\\d.]+)(?:\\s+/\\s+TP2\\s+-\\s+([\\d.]+))?(?:\\s+(.+))?");


        List<HalukDto> halukDtoList = new ArrayList<>();

        for (String line : lines) {
            Matcher matcher = patternMultipleSpaces.matcher(line);
            if (matcher.matches()) {

                HalukDto halukDto = HalukDto.builder()
                        .coin(matcher.group(1))
                        .entryPrice(Double.parseDouble(matcher.group(2)))
                        .stopPrice(Double.parseDouble(matcher.group(3)))
                        .takeProfit1(Double.parseDouble(matcher.group(4))).build();

                if (matcher.group(5) != null) {
                    halukDto.setTakeProfit2(Double.parseDouble(matcher.group(5)));
                }
                if(matcher.group(6)!= null){
                halukDto.setReverse(true);
                }

                halukDtoList.add(halukDto);

            }
        }
        return halukDtoList;
    }

}
