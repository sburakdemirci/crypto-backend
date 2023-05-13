package com.mtd.crypto.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetsService {

    private final Sheets sheets;

    /**
     * @param data          : Data to write
     * @param spreadsheetId : Google SpreadSheet id ex. https://docs.google.com/spreadsheets/d/HERE IS THE ID/edit#gid=0
     * @param sheet         : Sheet name. ex. Sheet1
     * @param dataRange     : Range of Data ex: A1:C2 .
     * @throws IOException
     */
    public void write(List<List<Object>> data, String spreadsheetId, String sheet, Optional<String> dataRange) throws GoogleSheetsException {

        ValueRange body = new ValueRange().setValues(data);

        try {
            Spreadsheet spreadsheet = new Spreadsheet()
                    .setProperties(new SpreadsheetProperties()
                            .setTitle(new Date().toString()));
            //TODO maybe check body and result.updatedData()
            UpdateValuesResponse result = sheets.spreadsheets().values()
                    .update(spreadsheetId, buildRange(sheet, dataRange), body)
                    .setValueInputOption("RAW")
                    .execute();

        } catch (IOException e) {
            throw new GoogleSheetsException("Unexpected error while saving the data to Google Sheets " + e.getMessage());
        }

        log.info("Google Sheets successfully updated!");
    }


    /**
     * @param spreadsheetId : Google SpreadSheet id ex. https://docs.google.com/spreadsheets/d/HERE IS THE ID/edit#gid=0
     * @param sheet         : Sheet name. ex. Sheet1
     * @param dataRange     : Range of Data ex: A1:C2 .
     * @return The data as 2 dimensional list from google sheets.
     * @throws IOException
     */
    public List<List<Object>> read(String spreadsheetId, String sheet, Optional<String> dataRange) throws GoogleSheetsException {
        try {
            ValueRange valueRange = sheets.spreadsheets().values()
                    .get(spreadsheetId, buildRange(sheet, dataRange))
                    .execute();

            return valueRange.getValues();

        } catch (IOException e) {
            e.printStackTrace();
            throw new GoogleSheetsException("Unexpected error while reading the data to Google Sheets " + e.getMessage());

        }
    }

    public void createNewSheet(String spreadsheetId, String newSheetName) throws GoogleSheetsException {
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties()
                .setTitle(newSheetName))));
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);

        try {
            sheets.spreadsheets().batchUpdate(spreadsheetId, body)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GoogleSheetsException("Error while creating spreadsheet");
        }
    }


    private String buildRange(String sheet, Optional<String> dataRange) {
        String range = sheet;
        if (dataRange.isPresent()) {
            range += "!" + dataRange;
        }
        return range;
    }

}
