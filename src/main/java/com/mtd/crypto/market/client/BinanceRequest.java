package com.mtd.crypto.market.client;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class BinanceRequest {


    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    private static String BINANCE_API_KEY_HEADER = "X-MBX-APIKEY";

    private String url;
    private HttpMethod httpMethod;
    private MultiValueMap<String, String> params;
    private MultiValueMap<String, String> requestBody;
    private HttpHeaders headers = new HttpHeaders();
    private Class<?> responseType;


    @Builder
    public BinanceRequest(String url, HttpMethod httpMethod, MultiValueMap<String, String> params, Class<?> responseType) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.params = params;
        this.responseType = responseType;
    }


    public BinanceRequest signRequest(String secretKey) {

        this.params.add("timestamp", String.valueOf(Instant.now().toEpochMilli()));
        String queryString = constructQueryString().replaceFirst("^\\?", "");

        try {
            Mac sha256_HMAC = Mac.getInstance(SIGNATURE_ALGORITHM);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SIGNATURE_ALGORITHM);
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(queryString.getBytes(StandardCharsets.UTF_8));
            String signature = URLEncoder.encode(new String(Hex.encode(hash)), StandardCharsets.UTF_8);
            this.params.add("signature", signature);
            return this;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature: " + e.getMessage());
        }
    }

    public BinanceRequest withApiKeyHeader(String apiKey) {
        headers.add(BINANCE_API_KEY_HEADER, apiKey);
        return this;
    }

    public BinanceRequest withQuery() {
        this.url += constructQueryString();
        return this;
    }

    public BinanceRequest withRequestBody() {
        this.requestBody = this.params;
        return this;
    }


    public HttpEntity<MultiValueMap<String, String>> getHttpEntity() {
        return new HttpEntity<>(requestBody, headers);
    }

    private String constructQueryString() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        for (Map.Entry<String, List<String>> entry : this.params.entrySet()) {
            String key = entry.getKey();
            uriBuilder.queryParam(key, entry.getValue().get(0));
        }
        return uriBuilder.toUriString();
    }

}
