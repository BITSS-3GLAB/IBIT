package com.bitss.Digital_BIT.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



public class HttpPictureAsker { 
	public Bitmap getPicture(String url){
    	try {
			URL myFileURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)myFileURL.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10000);
			InputStream in = conn.getInputStream();
			byte[] b = readInputStream(in);
				
			Bitmap map = BitmapFactory.decodeByteArray(b, 0, b.length);
			return map;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	public Bitmap getSecondHandUrl(String url)
	{
		Bitmap bitmap = null ;
		HttpGet httpRequest = new HttpGet(url);
		//取得HttpClient 对象
		HttpClient httpclient = new DefaultHttpClient();
		try {
			//请求httpClient ，取得HttpRestponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				//取得相关信息 取得HttpEntiy
				HttpEntity httpEntity = httpResponse.getEntity();
				//获得一个输入流
				InputStream is = httpEntity.getContent();
				System.out.println(is.available());
				System.out.println("Get, Yes!");
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
				
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
        
	}
    
    private byte[] readInputStream(InputStream in) throws Exception{
    	int len = 0;
    	byte buf[] = new byte[1024];
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	while ((len = in.read(buf)) != -1){
    		out.write(buf, 0, len);
    	}
    	out.close();
    	return out.toByteArray();
    }
}