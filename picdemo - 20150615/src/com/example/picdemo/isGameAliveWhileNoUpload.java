package com.example.picdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class isGameAliveWhileNoUpload implements Runnable {
	
	long gameStartTime;
	
	public isGameAliveWhileNoUpload(long startTime){
		
		this.gameStartTime = startTime;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		long currentTime = new Date().getTime();
		long difference =  currentTime - gameStartTime;
		long checkingTimer = 1000;
		System.out.println("Difference greater than 10");
		
		while(UploadService.count==0)
		{
			
			
			if (difference >checkingTimer)
			{
			
				checkingTimer = checkingTimer+10000;
			
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet("http://"+Constants.IP_ADDRESS_AND_PORT + "/tmo/isActive");
			
			String availability_text = new String("NOK");

			try {

				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
				availability_text = getASCIIContentFromEntity(entity);
				System.out.println("Printing the is game on"+availability_text);
				String[] text = availability_text.split(":");
				if(text[1].contains("true")){
					System.out.println("Game On");
				}
				else if(text[1].contains("false")){
					System.out.println("Game Close");
					CameraActivity.stopFlag=true;
					
				}
					
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			//return Username + availability_text;
			
		}
			currentTime = new Date().getTime();
			difference = currentTime - gameStartTime;
		}
	}
	protected String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}

}
