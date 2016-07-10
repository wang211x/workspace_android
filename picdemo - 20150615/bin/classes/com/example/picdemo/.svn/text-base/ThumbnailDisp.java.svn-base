package com.example.picdemo;



import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class ThumbnailDisp extends Activity {

	DatabaseHandler db;
	int totalImages = 0;
	int totalUploadedImages = 0;
	TextView totalNumberImages;
	TextView totalNumberUploadedImages;
	TextView username;
	String Username;
	
    ProgressBar progBar;
    
    Button button1, button2;
    int typeBar;                     // Determines type progress bar: 1 = horizontal
    int delay = 200;                  // Milliseconds of delay in the update loop
    int totalTakenPictures;           // Maximum value of picture to be on horizontal progress bar
    String startTime;
	
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thumbnail_disp);
		db = DatabaseHandler.getHelper(this);

		Bundle bundle = getIntent().getExtras();
		this.Username = bundle.getString("USERNAME");
		username = (TextView) findViewById(R.id.textView4);
		username.setText("Contestant Id : " +Username);
		
		boolean winnerUsername = Boolean.valueOf(bundle.getString("isWinner"));
		
		totalNumberImages = (TextView) findViewById(R.id.textView5);
		if(winnerUsername){
			
			String winnersPos = bundle.getString("winnersPosition");
			if(winnersPos.equals("1")){
				totalNumberImages.setText("You are the Winner");
			}
			else{
			//int winnersPosition = Integer.getInteger(winnersPos);
			//winnersPosition--;
			totalNumberImages.setText("You are the "+winnersPos+"  Runner Up");
			}			
		}
		else{
			totalNumberImages.setText("Thanks for your Participation");
		}
		
		//totalNumberUploadedImages = (TextView) findViewById(R.id.textView6);
		
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thumbnail_disp, menu);
		return true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
	public void onUserLeaveHint() {
	    finish();
	}
	public void finish(){
		System.exit(0);
	}
	
}
