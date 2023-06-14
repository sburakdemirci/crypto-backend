package com.mtd.crypto.market.data.custom;

import eu.bitwalker.useragentutils.UserAgent;
import org.junit.jupiter.api.Test;

class AdjustedDecimalTest {

    @Test
    public void some() {
        System.out.println("");
    }


    @Test
    public void userAgentTest() {

        String s = "Mozilla/5.0 (Linux; Android 13; Pixel 7 Build/TQ2A.230505.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/113.0.5672.77 Mobile Safari/537.36 OktaMobile/4.24.1";

        UserAgent userAgent = UserAgent.parseUserAgentString(s);

    }

}