package com.example.picdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Camera;
import android.os.Environment;
import android.util.Log;

public class FileDeleterThread implements Runnable {

	public static String TAG = "FileDeleterThread";

	public FileDeleterThread() {

	}

	public void run() {

		long totalTime = CameraActivity.startTime
				+ Constants.PLAY_TIME_DURATION
				+ Constants.GRACE_PERIOD_TO_UPLOAD;
		long currentTime = new Date().getTime();
		long difference = (totalTime - currentTime);
		File pictureFileDir;
		String pathToOurFile;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// only for gingerbread and newer versions

			File sdDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			pictureFileDir = new File(sdDir, "CameraAPIDemo");
		}

		else {
			pictureFileDir = new File("/sdcard/CameraAPIDemo");

			// pathToOurFile = pictureFileDir.getPath()+
			// File.separator+filename;
		}

		while ((difference > 0) && (CameraActivity.threadFlag)) {
			
			//Thread to wait for few pictures to be taken
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, e.toString());

			}
			
			//Deleting based on the last file index
				currentTime = new Date().getTime();
				difference= (totalTime - currentTime);
				
				String fileList[] = pictureFileDir.list();
				Log.d(TAG, "Size of the directory "+fileList.length+" After "+difference + " seconds");
				
				if(fileList.length>10){
				for(int i = 0 ; i<fileList.length; i++){	
					
				 if(!CameraActivity.imageFileNameList.get(UploadService.lastFileIndex).equals(fileList[i])){
					 pathToOurFile = pictureFileDir.getPath() + File.separator + fileList[i];
					 File toDelete = new File(pathToOurFile);
					 if(toDelete.isFile()){
						 Log.d(TAG,"File Deleted "+fileList[i]);
						 toDelete.delete();
					 }
					 
						
				 }
				 else{
					 Log.d(TAG,"Current File Uploaded could not be deleted"+fileList[i]);
				 }
				}	
				
			}
			
			
		}
	}

}
