package com.mtd.crypto.market.client.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

@Data
@Builder
public class BinanceRequest {
    private HttpMethod httpMethod;
    private String url;
    private boolean isSigned;
    private long timestamp;
    private String signature;
    @Getter(AccessLevel.NONE)
    private HttpEntity<MultiValueMap<String, String>> requestEntity;


    public HttpEntity<MultiValueMap<String, String>> getRequestEntity() {
        if(isSigned){
            requestEntity.getBody().add("timestamp",String.valueOf(timestamp));
            requestEntity.getBody().add("signature",signature);
        }
        return requestEntity;
    }

}
