package com.mtd.crypto.parser.rest;

import com.mtd.crypto.parser.data.dto.HalukDto;
import com.mtd.crypto.parser.data.response.TradeDataResponse;
import com.mtd.crypto.parser.helper.ExcelHeaderProvider;
import com.mtd.crypto.parser.helper.HalukParser;
import com.mtd.crypto.parser.service.InitialTradeDataService;
import com.mtd.crypto.sheets.GoogleSheetsConfigurationProperties;
import com.mtd.crypto.sheets.GoogleSheetsException;
import com.mtd.crypto.sheets.GoogleSheetsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class ParserController {

    private final InitialTradeDataService initialTradeDataService;
    private final GoogleSheetsService googleSheetsService;
    private final GoogleSheetsConfigurationProperties googleSheetsConfigurationProperties;


    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, value = "parser")
    public List<TradeDataResponse> parse(@RequestBody String value) throws GoogleSheetsException, IOException {

        List<HalukDto> halukDtoList = HalukParser.parseData(value);
        List<TradeDataResponse> tradeDataResponseList = initialTradeDataService.getTradeData(halukDtoList);

        List<List<Object>> excelData = new ArrayList<>();
        excelData.add(ExcelHeaderProvider.getInitialDataHeaders());

        tradeDataResponseList.forEach(tradeDataResponse -> {
            excelData.add(tradeDataResponse.convertToSheetData());
        });

        String sheetName = LocalDate.now(ZoneId.of(googleSheetsConfigurationProperties.getDateZone())).toString();

        googleSheetsService.createNewSheet(googleSheetsConfigurationProperties.getSheetId(), sheetName);
        googleSheetsService.write(excelData, googleSheetsConfigurationProperties.getSheetId(), sheetName, Optional.empty());

        return tradeDataResponseList;
    }
}
