package com.mmc.miaomiaoce;

import java.io.BufferedReader;
import android.telephony.CellInfoLte;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import android.test.PerformanceTestCase.Intermediates;
import org.apache.http.entity.StringEntity;

public class UploadService implements Runnable{

	String filename;
	String serverUrl;
	public static int count;
	private static final String TAG = "UploadService";
//	public DatabaseHandler db;
	String pathToOurFile;
	int threadId;
	public static int lastFileIndex;
	//Anuj : HTTP retry count 
	int retryCount = 0;
	boolean skipUploadFlag;
	int currentFileIndex;
	boolean getNewFile;
	File pictureFileDir;
	public int indexOftheFilebeingUploaded ;
	
	
	//Danny
	private int idImage=0;
	public int idConnectedIotSensor=0;	//system=0;sensor1=1;sensor2=2;sensor3=3;...
	protected int[] idSensorImage = {0,0,0,0,0} ; //Should be less than Constants.SENSOR_IMAGE_AMOUT
	
	
	
//	public UploadService(String username, DatabaseHandler db, int id){
	public UploadService(String filename, String serverUrl){		
		this.filename = filename;
		this.serverUrl = serverUrl;
//		this.db=db;
		this.lastFileIndex=-1;
		count=0;
		this.skipUploadFlag = false;
		this.currentFileIndex = 0;
		this.getNewFile=true;
		//this.indexOftheFilebeingUploaded = -1;
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		Log.d(TAG,"Inside the Upload Service- Just Started!");
		
		
//		long totalTime = CameraActivity.startTime+Constants.PLAY_TIME_DURATION+Constants.GRACE_PERIOD_TO_UPLOAD;
		long currentTime = new Date().getTime();
//		long difference = ( totalTime-currentTime);
		
//		System.out.println("Total Time"+totalTime);
		System.out.println("Current Time"+currentTime);
//		System.out.println("difference in time"+difference);
		
		

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
		     // only for gingerbread and newer versions
			
			File sdDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			pictureFileDir = new File(sdDir, "IoTDemo");
		}
		
		else{
			pictureFileDir = new File("/sdcard/IoTDemo");
			
			
		}
		
		
		//String mmcFileName = this.filename;//"IotMmcLog.txt";	//"iot_Mmc1.txt";
		//pathToOurFile = "/storage/sdcard0/IoT/"+mmcFileName;//IotMmcLog.txt";
		pathToOurFile = pictureFileDir.getPath()+ File.separator+this.filename;
		File fileToBeUpload = new File(pathToOurFile);
		
		if (fileToBeUpload.exists())
		{
					System.out.print(pathToOurFile + " existing, upload it"); 
					//pathToOurFile = "/storage/emulated/0/Pictures/CameraAPIDemo/Sensor1_SensorLog.txt";
					//pathToOurFile = "/storage/emulated/0/Pictures/CameraAPIDemo/Sensor1_201552241152999.jpg";//IotMmcLog.txt";
					
					try {
						
						boolean httpConnStatus = connectToServer( pathToOurFile);
						
						while ( !httpConnStatus  && this.retryCount < 5) {//Constants.NUMBER_OF_HTTP_RETRY_ALLOWED){
							 //System.out.println("retry Count   = " +  this.retryCount);
							Log.d(TAG,"Retry Count  for file  " + pathToOurFile + " is " +  this.retryCount) ;
							httpConnStatus = connectToServer( pathToOurFile);	 
						
						}
						
						this.retryCount = 0;
						
					} catch (Exception ex) {
						// Exception handling
						retryCount ++;
						//if (retryCount <= NUMBER_OF_HTTP_RETRY_ALLOWED){
							
						//}
						System.out.println("Exception in file upload"+ex.toString());
						Log.d(TAG,"Exception in file upload"+ex.toString()) ;
						
					}
				

		}
		
		
		try{
					Thread.sleep(2000);
		}
				catch(Exception e){
					e.printStackTrace();
				}
		}
			
	

	protected boolean  connectToServer(String pathToOurFile){
		
		//pathToOurFile = "/storage/sdcard0/Pictures/CameraAPIDemo/lte.txt";
		
		Log.d(TAG,"Connecting to Server for file " +pathToOurFile );
		System.out.println("Connecting to Server for file: "+pathToOurFile );
		
		String name[] = pathToOurFile.split("/");
		String filenameurl = name[name.length - 1];
			
		

		try{
			
//			//backup
//			HttpPost httpost = new HttpPost("http://"+Constants.IP_ADDRESS_AND_PORT+"/tmo/files/"
//					+ Username + "/");
//			//*/
//			//String actionUrl = "http://"+ Constants.IP_ADDRESS_AND_PORT + "/tmo/files/sensor1/";
			//String actionUrl = "http://"+ Constants.IP_ADDRESS_AND_PORT + "/tmo/iotimg?sensorId=" + "sensor1" +"&fileName="+"1";
			//String actionUrl = "http://"+ Constants.IP_ADDRESS_AND_PORT + "/tmo/iotfiles?sensorId=" + "sensor1" +"&fileName="+"mmc";
			//String actionUrl = "http://"+ Constants.IP_ADDRESS_AND_PORT + "/tmo/iotfiles/1/iot";
			String actionUrl;
			actionUrl = "http://"+this.serverUrl+"/upload"; //Hardcoded server address
			System.out.println("actionUrl1: "+actionUrl);
			//actionUrl = "http://192.168.1.7:8000/upload"; //Hardcoded server address
			//actionUrl = "http://10.140.186.176:8000/upload"; //Hardcoded server address
			//actionUrl = "http://10.140.161.153:8000/upload"; //Hardcoded server address
			HttpPost httpost = new HttpPost(actionUrl);
			
			System.out.println("Upload image file: actionUrl="+actionUrl);
			
			MultipartEntity entity = new MultipartEntity();
			//File uploadFileName = new File("/storage/sdcard0/Pictures/CameraAPIDemo/lte.txt");
			//File uploadFileName = new File("/storage/sdcard0/IoT/IotMmcLog.txt");
			File uploadFileName = new File(pathToOurFile);
			entity.addPart("file", new FileBody(uploadFileName));
			
			HttpClient httpclient = new DefaultHttpClient();
			
			httpclient.getParams().setParameter("http.socket.timeout", 50 * 1000);
			httpclient.getParams().setParameter("http.connection.timeout", 50 * 1000);
			
			httpost.addHeader("file-name",filenameurl);//"lte.txt");	//Danny added, reference http://www.cnblogs.com/mengdd/p/3607293.html
			httpost.setEntity(entity);								//Danny commented and replaced with below 
//			HttpEntity requestHttpEntity = prepareHttpEntity(uploadFileName);
//			if (null != requestHttpEntity) {
//				httpost.setEntity(requestHttpEntity);  
//            }
			HttpResponse response;
			response = httpclient.execute(httpost);
			HttpEntity hentity = response.getEntity();
			String availability_text = getASCIIContentFromEntity(hentity);
			
			System.out.println("httpost response: "+availability_text);
			

			
			if(availability_text.contains("File has been uploaded to")){
				count++;
				getNewFile=true;
				Log.e("UploadService", "Uploaded File "+pathToOurFile + " Upload File Count = "+count);
				
				
			}
			
		
		
		
		
		//count++;
		//System.out.println("Thread --- "+threadId + " Uploading file number"+count);
		Log.d(TAG,"Thread --- "+threadId + " Uploading file number"+count);
		
		//System.out.println("differnece "+difference);
		//new File(pathToOurFile).delete();
		//System.out.println("Deleting file"+pathToOurFile);
		
		//Thread.sleep(500);
		//count++;
		
		}
		
		catch(Exception  e) {
			this.retryCount = this.retryCount + 1;
			Log.d(TAG,"Exception Count for  file   " + pathToOurFile + "is " + this.retryCount );
			Log.d(TAG,"Exception while uploading   " + pathToOurFile + "is " + e.toString() );
			return false;
		}
		
		
		return true;
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
