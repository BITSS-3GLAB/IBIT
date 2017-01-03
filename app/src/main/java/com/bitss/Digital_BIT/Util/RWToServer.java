package com.bitss.Digital_BIT.Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class RWToServer {

    public static String doHttpGet(String url) throws IOException {
        String source = "";

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);

        HttpGet httpGet = new HttpGet(url);

        HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            source = EntityUtils.toString(httpResponse.getEntity());
        }

        return source;
    }

    public static String doHttpPost(String url, JSONObject data) throws IOException {
        String source = "";

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("charset", HTTP.UTF_8);

        try {
            httpPost.setEntity(new StringEntity(data.toString(), HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse httpResponse = httpClient.execute(httpPost);
        //Log.i("RWToServer", "Http status"+httpResponse.getStatusLine().getStatusCode());
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            
        	source = EntityUtils.toString(httpResponse.getEntity());
        
        }

        return source;
    }
}
