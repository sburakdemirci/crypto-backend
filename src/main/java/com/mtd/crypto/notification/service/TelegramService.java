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

    public void sendMessage(String chatId, String message) {
        String url = telegramSecretProperties.getBaseUrl() + telegramSecretProperties.getApiKey() + "/sendMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("chat_id", chatId);
        multiValueMap.add("text", message);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueMap, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error occurred");
        }

    }


}
