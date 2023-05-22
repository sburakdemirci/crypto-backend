package com.mtd.crypto.market.data.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;


public abstract class BinanceRequestBase {

    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(this);

                if (fieldValue != null) {
                    body.add(fieldName, fieldValue.toString());
                }
            }
        } catch (IllegalAccessException e) {
            // Handle the exception
            throw new RuntimeException("There is a problem while parsing the binance request");
        }
        return body;
    }

}
