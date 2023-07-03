package com.mtd.crypto.notification.service;

import com.mtd.crypto.notification.configuration.TelegramSecretProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final RestTemplate restTemplate;
    private final TelegramSecretProperties telegramSecretProperties;

    /**
     * @param chatId
     * @param message Blue text `Hello`
     *                Bold text *SomeText*
     *                But you need to set parsemode to markdownV2.
     *                But its not accepting characters like '-' which is used in your String database ids. So don't use it if its not necessary
     */
    public void sendMessage(String chatId, String message) {
        String url = telegramSecretProperties.getBaseUrl() + telegramSecretProperties.getApiKey() + "/sendMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("chat_id", chatId);
        multiValueMap.add("text", message);
/*
        multiValueMap.add("parse_mode", "MarkdownV2");
*/

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueMap, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error occurred");
        }

    }


}
