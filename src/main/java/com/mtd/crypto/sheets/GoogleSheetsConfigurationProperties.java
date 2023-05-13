package com.mtd.crypto.sheets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.sheets")
public class GoogleSheetsConfigurationProperties {

    private String sheetId;
    private String dateZone;
    private String appName;
    private String serviceAccountPath;

}
