package com.bitss.Digital_BIT.Util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Crawler {
    public static byte[] crawlUrl(String url, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection con = null;
        InputStream is = null;
        URL u = new URL(url);

        con = (HttpURLConnection) u.openConnection();
        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(readTimeout);
        con.setUseCaches(false);
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestMethod("GET");
        con.setDoInput(true);

        con.connect();
        is = con.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int size = 0;

        while ((size = bis.read(buffer)) != -1) {
          out.write(buffer, 0, size);
        }

        bis.close();
        is.close();
        con.disconnect();

        return out.toByteArray();
    }

    public static byte[] crawlUrl(String url) throws IOException {
        return crawlUrl(url, 5 * 60 * 1000, 60 * 1000);
    }

}
