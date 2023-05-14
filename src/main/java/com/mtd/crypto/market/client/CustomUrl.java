package com.mtd.crypto.market.client;

import lombok.Data;

import java.net.MalformedURLException;
import java.net.URL;

@Data
public class CustomUrl {
    private String protocol;
    private String host;
    private String path;
    private String query;


    public CustomUrl(String urlString) {

        try {
            URL url = new URL(urlString);
            this.protocol = url.getProtocol();
            this.host = url.getHost();
            this.path = url.getPath();
            this.query = url.getQuery();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://").append(host);

        if (path != null) {
            sb.append(path);

        }
        if (query != null) {
            sb.append("?").append(query);
        }
        return sb.toString();
    }
}

