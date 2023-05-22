package com.mtd.crypto.market.client;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.configuration.BinanceSecretProperties;
import com.mtd.crypto.market.data.request.BinanceRequestBase;
import com.mtd.crypto.market.exception.BinanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@LoggableClass
@RequiredArgsConstructor
public class BinanceRequestHandler {


    private static final int MAX_RETRY_COUNT = 5;
    private final RestTemplate restTemplate;
    private final BinanceSecretProperties binanceSecretProperties;


//todo burak log loglog log
    //todo burak save request and response to database or log. But do not log the signature or any secret related thing
    //TODO retry with other endpoints if request fails. api1, api2, api3

    public <T> BinanceRequest createGetRequest(BinanceRequestBase binanceRequestBase, String url, boolean isSigned) {
        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .params(binanceRequestBase.toMultiValueMap())
                .build();

        if (isSigned) {
            binanceRequest.signRequest(binanceSecretProperties.getSecretKey());
            binanceRequest.withApiKeyHeader(binanceSecretProperties.getApiKey());
        }

        binanceRequest.withQuery();

        return binanceRequest;

    }

    public <T> BinanceRequest createPostRequest(BinanceRequestBase binanceRequestBase, String url) {

        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .params(binanceRequestBase.toMultiValueMap())
                .build()
                .signRequest(binanceSecretProperties.getSecretKey())
                .withRequestBody()
                .withApiKeyHeader(binanceSecretProperties.getApiKey());

        return binanceRequest;

    }


    public BinanceRequest createDeleteRequest(BinanceRequestBase binanceRequestBase, String url) {
        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.DELETE)
                .params(binanceRequestBase.toMultiValueMap())
                .build()
                .signRequest(binanceSecretProperties.getSecretKey())
                .withQuery()
                .withApiKeyHeader(binanceSecretProperties.getApiKey());

        return binanceRequest;

    }


    public <T> ResponseEntity<T> sendRequest(BinanceRequest binanceRequest, Class<T> responseEntity) {
        //todo burak save request and response to database
        //TODO Add retry mechanism except for the HttpClientErrorException.BadRequest. If server gives 500 or otheer than bad request retry 3 times

        try {
            return restTemplate.exchange(binanceRequest.getUrl(), binanceRequest.getHttpMethod(), binanceRequest.getHttpEntity(), responseEntity);
        } catch (HttpClientErrorException.BadRequest e) {
            e.printStackTrace();
            throw new BinanceException(e.getMessage(), e.getStatusCode());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new BinanceException(e.getMessage(), e.getStatusCode());
        }
    }


}


