package com.st.sliding;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class DevSetActivity extends Activity {

	
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_device);
		setTitle(R.string.btn_dev_set);
		getActionBar().setIcon(R.drawable.left_btn_devsetting);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }
}
