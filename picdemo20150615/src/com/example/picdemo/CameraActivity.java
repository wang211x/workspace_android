package com.example.picdemo;

import android.media.AudioManager;
import android.os.Bundle;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.util.List;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

public class CameraActivity extends Activity {
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	private ArrayList<File> imageFileList = null;
	public static ArrayList<String> imageFileNameList = null;
	public static String Username;
	public  static String FileType;
	public  int no_uploads_on_fileSize = Constants.NUMBER_OF_UPLOADS_PER_PICTURE;
	Date start;
	public static long startTime;
	public static boolean threadFlag;
	private Thread[] threadPool ;
	public Context myContext;
	private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
	private static final int PREVIEW_SIZE_MAX_WIDTH = 640;
	public DatabaseHandler db;
	TextView countdown;
	TextView netTypeInfo;
	TextView netTpInfo;
	RelativeLayout relativeLayoutSensorsData;
	public static int uploadImagesCount=0;
	public int numberofclicks=0;
	private static final String TAG = "CameraActivity";

	boolean sleepFlag; // Flag to make the camera be alive for few more time to
						// save the photo
	boolean startedEarlier; // Flag to check if the app had accidentally been
							// exited or pressed home button
							// false if started normally, true if exited
							// accidentally and begun again
	
	public static boolean stopFlag = false; // Flag to check if the stop has been signalled
	public static boolean winnerFlag = false; // Flag to check if user is the winner
	
	public static int winnerNumber = 0;
	
	TextView contestantId ;
	
	UploadService upload;
	ArrayList <UploadService> uploadServiceThreadList;
	ArrayList <Thread> otherThreadsList;
	
	private AutoFocusCallback myAutoFocusCallback = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.w(TAG,"w77wang::enter onCreate");
		
		setContentView(R.layout.activity_picture_demo);
		db = DatabaseHandler.getHelper(this);
		sleepFlag = false;
		DatabaseHandler.DATABASE_VERSION = DatabaseHandler.DATABASE_VERSION + 1;

		imageFileNameList = new ArrayList<String>();
		threadFlag=true;
		myContext = this;
		
		uploadServiceThreadList = new ArrayList<UploadService>();
		otherThreadsList = new ArrayList<Thread>();
		
		Bundle bundle = getIntent().getExtras();
		this.Username = bundle.getString("USERNAME");
		this.FileType = bundle.getString("FILETYPE");
		System.out.print("Camera : FileType is " + this.FileType);
		this.start = new Date(bundle.getLong("STARTTIME"));
		this.startedEarlier = Boolean.valueOf(bundle.getString("EARLIER_START_TIME"));

		// Anuj : Added for displaying the User Id
		contestantId = (TextView) findViewById(R.id.Userid);
		contestantId.setText("Contestant Id  "+ this.Username );
		
		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		relativeLayoutSensorsData = (RelativeLayout) findViewById(R.id.sensors_data_layout);
		relativeLayoutSensorsData.bringToFront();
		relativeLayoutSensorsData.setVisibility(View.INVISIBLE);
		countdown = (TextView) relativeLayoutSensorsData.findViewById(R.id.Xc);
		countdown.setText("3");
		startTime = new Date().getTime();
		Log.w(TAG,"w77wang::enter 1.5");	
		netTypeInfo = (TextView) relativeLayoutSensorsData.findViewById(R.id.NetTypeInfo);
		netTpInfo = (TextView) relativeLayoutSensorsData.findViewById(R.id.NetTpInfo);
		//new showNetTypeInfo().execute(getNetTypeInfo());
		netTypeInfo.setText(getNetTypeInfo());
		netTpInfo.setText("");

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myAutoFocusCallback = new AutoFocusCallback() {
            
            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                if(success)
                {
                   camera.setOneShotPreviewCallback(null);
                }
                else
                {
                    //fail
                }
            }
        };
        
		try {
			//Initialising File Deleter Thread 
			Thread t1 = new Thread(new FileDeleterThread());
			t1.start();
			otherThreadsList.add(t1);
			
			// Thread to check if no uploads have happend and the game has finished
			
			Thread t2 = new Thread(new isGameAliveWhileNoUpload(startTime));
			t2.start();
			otherThreadsList.add(t2);
			
			Log.d(TAG,"Starting is Game Alive check while no upload");
			
			for(int i =0; i < no_uploads_on_fileSize; i++){
				
				upload = new UploadService(Username, db, i+1);
				uploadServiceThreadList.add(upload);
				new Thread(upload).start();
				//System.out.println("Thread starting" +i +" Starting");
				Log.d(TAG,"Thread starting" + i );
				
			}
		} catch (Exception e) {
			System.out.println( "Error in Upload Thread " + e.toString());
		}

		try {
			new Thread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub

					int count = 0;
					long lastRxInfo = 0;
					long lastTxInfo = 0;
					long difference = (new Date()).getTime() - start.getTime();
					// set the difference in milliseconds to prolong photo
					// taking for that many milliseconds
					while (((difference + 3000) < Constants.PLAY_TIME_DURATION)&& CameraActivity.stopFlag==false) {
						try {
							Thread.sleep(Constants.PICTURE_TAKING_INTERVAL);
							// Count Down Timer before the click 3...2...1..
							// click

							// The begining time of the game.
							if (count == 0) {
								try{
									db.addTime(String.valueOf(new Date().getTime()));
								}
								catch(Exception e){
									
								}
								
								startTime = new Date().getTime();
							}

							 new countdownTimerVisibility().execute(new
							 Boolean(true));
							String message="0/0";
							 try{
								 int uploadedfilecount=0;
								
								 for(int i=0 ; i<uploadServiceThreadList.size();i++){
									 uploadedfilecount +=  uploadServiceThreadList.get(i).count;
									 //System.out.println("upload file count - "+uploadedfilecount);
										Log.d(TAG,"upload file count - "+uploadedfilecount );
								 }
	  						     message = String.valueOf(numberofclicks)+ " / "+uploadedfilecount; 
							 }
							 catch(Exception e){
								 Log.d(TAG,"Error while counting uploads");
							 }
							 new countdownTimer().execute(message);

							 int intervalMs =  Constants.PICTURE_TAKING_INTERVAL/1000;
							 //get Tx Kbit/s
							 long currentTxInfo = getNetTpTxInfo();
							 long txKBitRate = ((currentTxInfo-lastTxInfo)*8/intervalMs)/1000;
							 lastTxInfo = currentTxInfo;
							 
							 //get Rx Kbit/s
							 long currentRxInfo = getNetTpRxInfo();
							 long rxKBitRate = ((currentRxInfo-lastRxInfo)*8/intervalMs)/1000;
							 lastRxInfo = currentRxInfo;
								 
							 String tpinfoMessage = "UL:"+txKBitRate+"kbps"+" DL:"+rxKBitRate+"kbps";
							 new showNetTpInfo().execute(tpinfoMessage);
							 
							 //netTypeInfo.setText(getNetTypeInfo());
							 //netTpInfo.setText(getNetTpInfo());

							 //new showNetInfo().execute(getNetInfo());
							 //netInfo.setText("netInfo");
							 //new showNetInfo().execute("netinfo");
							// Thread.sleep(500);
							// new countdownTimerVisibility().execute(new
							// Boolean(false));
							// Thread.sleep(500);
							// new countdownTimerVisibility().execute(new
							// Boolean(true));
							// new countdownTimer().execute("2");
							// Thread.sleep(500);
							// new countdownTimerVisibility().execute(new
							// Boolean(false));
							// Thread.sleep(500);
							// new countdownTimerVisibility().execute(new
							// Boolean(true));
							// new countdownTimer().execute("1");
							// Thread.sleep(500);
							// new countdownTimerVisibility().execute(new
							// Boolean(false));

						} catch (InterruptedException exception) {
							exception.printStackTrace();
						}

						difference = (new Date()).getTime() - start.getTime();

						if (inPreview) {
							count++;

							camera.takePicture(shutterCallback, null,
									photoCallback);
							inPreview = false;
						}
						sleepFlag = true;

					}

					try {
						if (sleepFlag) {
							Thread.sleep(1000);
						}

					} catch (Exception e) {

					}
					if (!startedEarlier) {
						Intent myIntent = new Intent(CameraActivity.this,
								ThumbnailDisp.class);
						myIntent.putExtra("USERNAME", Username);
						CameraActivity.this.startActivity(myIntent);

						//System.out.println("started new thumbnail display");
					}
					if(CameraActivity.stopFlag==true){
						
						//Stop all Threads
						
						//CameraActivity.threadFlag=false;
						
						Intent myIntent = new Intent(CameraActivity.this,
								ThumbnailDisp.class);
						myIntent.putExtra("USERNAME", Username);
						
						Log.d(TAG,"packing winnerflag"+CameraActivity.winnerFlag);
						
						myIntent.putExtra("isWinner", Boolean.toString(CameraActivity.winnerFlag));
						myIntent.putExtra("winnersPosition", Integer.toString(CameraActivity.winnerNumber));
						CameraActivity.this.startActivity(myIntent);
						
						//Stop all threads
					
						CameraActivity.this.finish();
					}
				
					// CameraActivity.this.finish();

				}

			}).start();

		} catch (Exception e) {
			//System.out.print(e.toString());
			Log.d(TAG,e.toString());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.picture_demo, menu);
//		return true;
    	menu.add(0,0,0,"About"); 
    	menu.add(0,1,1,"Quit"); 
    	return super.onCreateOptionsMenu(menu);
	}
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	super.onOptionsItemSelected(item);//get menu
    	switch(item.getItemId())
    	{
    		case 0://about
    		{
    			new AlertDialog.Builder(this)
    			.setTitle("About")
    			.setMessage("Nokia Pic Demo APP " + Constants.APP_Version)
    			
    			.setPositiveButton
    			(
    				"Get It",
    				new DialogInterface.OnClickListener()
    				{						
    					@Override
    					public void onClick(DialogInterface dialog, int which) 
    					{
    					}
    				}
    			)
    			.show();
    		}
			break;
    		case 1://quit
	    		{
					android.os.Process.killProcess(android.os.Process.myPid());
	    		}
    			break;
    	}    	
    	return true;
    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		threadFlag = false;
		android.os.Process.killProcess(android.os.Process.myPid());			
		super.onBackPressed();
	}
	
//	public String getNetInfo(){
//		//PackageManager pm = null;
//		String netInfo = " ";
//		PackageManager pm = (PackageManager) this.getSystemService(ACTIVITY_SERVICE);
//        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
//        for (PackageInfo info : packinfos) {
//            String[] premissions = info.requestedPermissions;
//            netInfo = info.packageName + ':';
//            if (premissions != null && premissions.length > 0) {
//                for (String premission : premissions) {
//                    if("android.permission.INTERNET".equals(premission)){
//                        //System.out.println(info.packageName+"��������");
//                        int uid = info.applicationInfo.uid;
//                        long rx = TrafficStats.getUidRxBytes(uid);
//                        long tx = TrafficStats.getUidTxBytes(uid);
//                        if(rx<0||tx<0){
//                        	netInfo += "no net info";
//                        }else{
//                        	netInfo += "DL:" + Formatter.formatFileSize(this, rx)+ " UL:" + Formatter.formatFileSize(this,tx );
//                        }
//                    }
//                }
//            }
//            netInfo += '\n';
//        }
//        return netInfo;
//	}
	
	public String getNetTypeInfo(){
		ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		String type = "NULL";
		
		if (info ==null){
			type = "null";
		} 
		else if (info.getType() ==  ConnectivityManager.TYPE_WIFI){
			type = "WIFI";
		}
		else if (info.getType() ==  ConnectivityManager.TYPE_MOBILE){
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS 
	                || subType == TelephonyManager.NETWORK_TYPE_EDGE) { 
	            type = "2G"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSPA 
	        		|| subType == TelephonyManager.NETWORK_TYPE_HSPA || subType == TelephonyManager.NETWORK_TYPE_HSPAP
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0 
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) { 
	            type = "3G"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) { 
	            type = "LTE"; 
	        } 
		}

		return type;
	}
	
	public long getNetTpRxInfo(){
		ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		ApplicationInfo info = getApplicationInfo();
		NetworkInfo netinfo = connectMgr.getActiveNetworkInfo();
		long tpInfo = 0;
		
		String name = info.packageName;
		if (name.equals("com.example.picdemo")	|| name == "com.example.picdemo") {
			int uid = info.uid;
			tpInfo = TrafficStats.getUidRxBytes(uid);
		}
		return tpInfo;
	}

	public long getNetTpTxInfo(){
		ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		ApplicationInfo info = getApplicationInfo();
		NetworkInfo netinfo = connectMgr.getActiveNetworkInfo();
		long tpInfo = 0;
		
		String name = info.packageName;
		if (name.equals("com.example.picdemo")	|| name == "com.example.picdemo") {
			int uid = info.uid;
			tpInfo = TrafficStats.getUidTxBytes(uid);
		}
		return tpInfo;
	}

	public void showAlert(String msg){		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder
	    .setTitle(msg)
	    .setMessage("Are you sure?")
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {       
	        	System.exit(0);
	    }
	    });             
	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {   
	         dialog.dismiss();	         
	        }
	    });         
	    AlertDialog alert = builder.create();
	    alert.show();	
	}
	
	public void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Camera.CameraInfo info = new Camera.CameraInfo();

			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, info);

				if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					camera = Camera.open(i);

				}
			}
			int rotation = getWindowManager().getDefaultDisplay().getRotation();
			int degrees = 0;

			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;

			case Surface.ROTATION_90:
				degrees = 90;
				break;

			case Surface.ROTATION_180:
				degrees = 180;
				break;

			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}

			int displayOrientation;

			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				displayOrientation = (info.orientation + degrees) % 360;
				displayOrientation = (360 - displayOrientation) % 360;
			} else {
				displayOrientation = (info.orientation - degrees + 360) % 360;
			}

			camera.setDisplayOrientation(displayOrientation);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		}

		if (camera == null) {
			camera = Camera.open();
		}

		startPreview();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;
		// This is not the correct way to handle . But added this to provide a  quick fix.
		System.exit(0);
		

		//super.onPause();
	}

	private void initPreview(int width, int height) {

		System.out.println("Inside Preview 1 ");
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(CameraActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
			System.out.println("Inside Preview 2 ");

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);
				Camera.Size pictureSize = getSmallestPictureSize(parameters);
				
				try {
				if (size != null && pictureSize != null) {
					
					parameters.setPreviewSize(size.width, size.height);
					//parameters.setPictureSize(pictureSize.width , pictureSize.height);
					parameters.setJpegQuality(10);
					parameters.setPictureFormat(ImageFormat.JPEG);
					parameters.setJpegQuality(10);

					camera.setParameters(parameters);
					cameraConfigured = true;
				}
				}catch (Exception ex){
					System.out.print("Exception " + ex.toString());
				}
				}
			}
		
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {

			camera.startPreview();
			inPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};

	
	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG,"Calling Save Photo Task Callback");
			new SavePhotoTask().execute(data);

			camera.startPreview();
			camera.autoFocus(myAutoFocusCallback);
			inPreview = true;
		}
	};

	private final ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
		}
	};

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size bestSize = null;

		for (Camera.Size currentSize : parameters.getSupportedPreviewSizes()) {
			System.out.println("Suported Width is "+ currentSize.width + "Suported Height is " + currentSize.height); 
			boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
			boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
			boolean isInBounds = currentSize.width <= PICTURE_SIZE_MAX_WIDTH;

			if (isDesiredRatio && isInBounds && isBetterSize) {
				bestSize = currentSize;
			}
		}

		return (bestSize);
	}

	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (result == null) {
				result = size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;

				if (newArea < resultArea) {
					result = size;
				}
			}
		}

		return (result);
	}

	class SavePhotoTask extends AsyncTask<byte[], String, String> {

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

		@Override
		protected String doInBackground(byte[]... jpeg) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
			String date = dateFormat.format(new Date());
			String photoFile = "Picture_" + date + ".jpg";
			// Anuj : Added for file compression
			
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 6;
			
			File photo;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				// only for gingerbread and newer versions

				File sdDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				File pictureFileDir = new File(sdDir, "CameraAPIDemo");

				pictureFileDir.mkdir();
				String filename = pictureFileDir.getPath() + File.separator
						+ photoFile;

				photo = new File(filename);
				

				try {
					photo.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			else {
				File dir = new File("/sdcard/CameraAPIDemo");
				dir.mkdir();
				photo = new File("/sdcard/CameraAPIDemo" + File.separator+ photoFile);
			}

			try {
				//Write only if number of files is greater than ten
				//if(imageFileNameList.size()<=10){
					
				
				FileOutputStream fos = new FileOutputStream(photo, true);
				//fos.write(jpeg[0]);
				//fos.flush();
				//fos.close();
				
				
				
				
				//if(Integer.parseInt(FileType)>1)
				{
					//FileInputStream fis ;
					Bitmap b = null;
				//but to avoid major code change overriding the existing file
				
				//Decode with inSampleSize
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        //System.out.println("Scale is " + scale);
		        
		        o2.inSampleSize = Integer.parseInt(FileType);
		        Log.d(TAG,"Sampling Size of Picture is " +  o2.inSampleSize);
		        b= BitmapFactory.decodeByteArray(jpeg[0], 0,jpeg[0].length, o2);
		        Log.d(TAG,"New bitmap " );
		        b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		        fos.flush();
		        fos.close();
		        b.recycle();
		        
		        /*
		        fis = new FileInputStream(photo);
		        b = BitmapFactory.decodeStream(fis, null, o2);
		       
		        fis.close();
		        OutputStream outStream = null;
		        
		        
		         outStream = new FileOutputStream(photo);
		         
		         outStream.flush();
		         outStream.close();
				 b.recycle();
				 */
				}
				 imageFileNameList.add(photoFile);
				 Log.e("CameraActivity", "Saved Photo"+photoFile);
				
				
			} catch (Exception e) {
				Log.d("CameraActivity", "Exception in photoCallback while saving file "+photoFile +" \n Exception "+ e);
			}
			numberofclicks++;
			Log.e("CameraActivity", "Total Clicks"+ numberofclicks);
			try {
				//db.addContact(new imageFile(photoFile, "false", "0"));
			} catch (Exception e) {
				// e.printStackTrace();
			}

			return (photo.getPath());

		}

		protected void onPostExecute(String results) {

		}
	}

	class countdownTimer extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			return arg0[0];
		}

		protected void onPostExecute(String results) {

			countdown.setText(results);

		}

	}

	class showNetTypeInfo extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			return arg0[0];
		}

		protected void onPostExecute(String results) {
			netTypeInfo.setText(results);
		}
	}

	class showNetTpInfo extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return arg0[0];
		}

		protected void onPostExecute(String results) {
			netTpInfo.setText(results);
		}
	}

	class countdownTimerVisibility extends AsyncTask<Boolean, String, String> {

		@Override
		protected String doInBackground(Boolean... arg0) {
			// TODO Auto-generated method stub

			return String.valueOf(arg0[0]);
		}

		protected void onPostExecute(String results) {

			boolean visibility = Boolean.valueOf(results);
			if (visibility) {
				relativeLayoutSensorsData.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutSensorsData.setVisibility(View.INVISIBLE);
			}

		}

	}
 }
