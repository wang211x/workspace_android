package com.example.picdemo;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import com.example.picdemo.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import android.test.PerformanceTestCase.Intermediates;


public class UploadService implements Runnable{

	String Username;
	public static int count;
	private static final String TAG = "UploadService";
	public DatabaseHandler db;
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
	
	
	public UploadService(String username, DatabaseHandler db, int id){
		
		this.Username = username;
		this.db=db;
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
		
		
		long totalTime = CameraActivity.startTime+Constants.PLAY_TIME_DURATION+Constants.GRACE_PERIOD_TO_UPLOAD;
		long currentTime = new Date().getTime();
		long difference = ( totalTime-currentTime);
		
		System.out.println("Total Time"+totalTime);
		System.out.println("Current Time"+currentTime);
		System.out.println("difference in time"+difference);
		
		

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
		     // only for gingerbread and newer versions
			
			File sdDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			pictureFileDir = new File(sdDir, "CameraAPIDemo");
		}
		
		else{
			pictureFileDir = new File("/sdcard/CameraAPIDemo");
			
			//pathToOurFile = pictureFileDir.getPath()+ File.separator+filename;
		}
		
		
		
		
		while((difference > 0)&&(CameraActivity.threadFlag))
		{
			String filename = " ";	
			
			if(CameraActivity.imageFileNameList.size()>0)
			{
				
				
				if((lastFileIndex !=(CameraActivity.imageFileNameList.size()-1) ))
				{
					Log.d(TAG," Getting the last file from the list ");
					skipUploadFlag = true;
					lastFileIndex = CameraActivity.imageFileNameList.size()-1;
					filename = CameraActivity.imageFileNameList.get(lastFileIndex);
					
					getNewFile=false;
					
				}
				
				pathToOurFile = pictureFileDir.getPath() + File.separator
						+ filename;
				
				
				
				if(skipUploadFlag && new File(pathToOurFile).isFile()){
					//Toggle flag
					skipUploadFlag = false;
					
					//if(filepathforUpload.size()!=0)
					{
						//File sdDir = Environment
						//		.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
						//File pictureFileDir = new File(sdDir, "CameraAPIDemo");
						//pathToOurFile = pictureFileDir.getPath() + File.separator
						//		+ filepathforUpload.get(count);
						//pathToOurFile = filepathforUpload.get(count);
						System.out.println("inside upload"+pathToOurFile);
						try {
							
							boolean httpConnStatus = connectToServer( pathToOurFile);
							
							while ( !httpConnStatus  && this.retryCount < Constants.NUMBER_OF_HTTP_RETRY_ALLOWED){
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
						//filepathforUpload = db.getImageNotUploadCount();
						
					}
						
					}
					
				
				
			}
			
			else{
				try{
					Thread.sleep(200);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
			
			
			difference = (totalTime - new Date().getTime());
		}
		
		
	}


	protected boolean  connectToServer(String pathToOurFile){
		
		Log.d(TAG,"Connecting to Server for file " +pathToOurFile );
		
		String name[] = pathToOurFile.split("/");
		String filenameurl = name[name.length - 1];
			
		

		try{
			
			
			HttpPost httpost = new HttpPost("http://"+Constants.IP_ADDRESS_AND_PORT+"/tmo/files/"
					+ Username + "/");
			MultipartEntity entity = new MultipartEntity();
			File uploadFileName = new File(pathToOurFile);
			
			entity.addPart("file", new FileBody(uploadFileName));
			
			HttpClient httpclient = new DefaultHttpClient();
			
			httpclient.getParams().setParameter("http.socket.timeout", 50 * 1000);
			httpclient.getParams().setParameter("http.connection.timeout", 50 * 1000);
			
			httpost.setEntity(entity);
			HttpResponse response;
			response = httpclient.execute(httpost);
			HttpEntity hentity = response.getEntity();
			String availability_text = getASCIIContentFromEntity(hentity);
			
			
			if(availability_text.contains("File has been uploaded to")){
				count++;
				getNewFile=true;
				Log.e("UploadService", "Uploaded File "+pathToOurFile + " Upload File Count = "+count);
				
				
			}
			else if(availability_text.contains("STOP")){
				
				CameraActivity.stopFlag=true;
				int winnercount =0 ;
				String[] winners = availability_text.split(";");
				for(String winner:winners){
					
					if(winner.equals(Username)){
						CameraActivity.winnerFlag=true;
						CameraActivity.winnerNumber=winnercount;
					}
					winnercount++;
					//count++;
				}
				
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
	
	public void deleteEarlierFiles(int lastFileIndex){
		
		int indexforFiletobeDeleted = lastFileIndex ;
		File pictureDirectory;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
		     // only for gingerbread and newer versions
			
			File sdDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			pictureDirectory = new File(sdDir, "CameraAPIDemo");
		}
		
		else{
			pictureDirectory = new File("/sdcard/CameraAPIDemo");
			
			//pathToOurFile = pictureFileDir.getPath()+ File.separator+filename;
		}
		
		
		
		
		String pathToDeleteFile = pictureDirectory.getPath()+ File.separator + CameraActivity.imageFileNameList.get(lastFileIndex);
		
		while(new File(pathToOurFile).isFile()){
			//
			//Log.d(TAG, " Size of the Image List"+CameraActivity.imageFileNameList.size());
			new File(pathToOurFile).delete();
			indexforFiletobeDeleted--;
			if(indexforFiletobeDeleted>=0){
				pathToDeleteFile = pictureDirectory.getPath()+ File.separator + CameraActivity.imageFileNameList.get(indexforFiletobeDeleted);
			}
			
			 
			
			for(int i =0 ; i< 10;i++){
				
				String filetoDelete = CameraActivity.imageFileNameList.get(i);
				File fileToDelete = new File(pictureFileDir.getPath()+ File.separator+filetoDelete);
				if(fileToDelete.isFile())
				{
					fileToDelete.delete();
				}
				CameraActivity.imageFileNameList.remove(i);
				
				Log.d("UploadService", "Deleted File since number of files exceeded 10"+filetoDelete);
				
			}
			
			
			
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
