package com.mtd.crypto.market.client;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

class CustomUrlTest {

    @Test
    public void test() throws MalformedURLException {

        URL url = new URL("https://www.binance.com/abc/def?filter=1");

        CustomUrl customUrl = new CustomUrl("https://www.binance.com/abc?filter=1");
    }

}