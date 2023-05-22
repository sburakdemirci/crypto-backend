package com.mtd.crypto.sheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GoogleSheetsConfiguration {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final GoogleSheetsConfigurationProperties googleSheetsConfigurationProperties;

    @Bean
    public GoogleCredentials getCredentials()
            throws IOException {

        InputStream resourceAsStream = GoogleSheetsService.class.getResourceAsStream(googleSheetsConfigurationProperties.getServiceAccountPath());

        assert resourceAsStream != null;
        return GoogleCredentials.fromStream(resourceAsStream)
                .createScoped(SCOPES);
    }


    @Bean
    public Sheets getSheets() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredentials credential = getCredentials();
        HttpCredentialsAdapter httpCredentialsAdapter = new HttpCredentialsAdapter(credential);
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, httpCredentialsAdapter)
                .setApplicationName(googleSheetsConfigurationProperties.getAppName())
                .build();
    }
}
