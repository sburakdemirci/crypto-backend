package com.mtd.crypto.market.data.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;

public abstract class BinanceRequest {

/*
     MultiValueMap<String,String> toMultiValueMap();
*/


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
          }
          return body;
     }

}
