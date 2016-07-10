package com.st.tools;

import java.text.SimpleDateFormat;
import java.util.Set;

import android.content.Context;
import android.widget.Toast;

import com.st.app.STApplaction;
import com.st.sliding.R;

public class ToolBox {
	private Context mAppContext;
	private static class MyToolBox {
		public static ToolBox gInstance = new ToolBox();
	};

	public static ToolBox the() {
		return MyToolBox.gInstance;
	}

	private ToolBox() {
		mAppContext = STApplaction.mApp;
	}

	public String[] updateWeather() {
		String weatherDate = getSetting(mAppContext.getString(R.string.setting_weather_date));
		if(null == weatherDate)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new java.util.Date());
		if(weatherDate.equals(date))
			return getArraySetting(mAppContext.getString(R.string.setting_weather_info));
		else
			return null;
	}

	//show toast
	
	public void showToast(String message){
		Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT).show();
	}
	
	public void showToast(int message){
		Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT).show();
	}
	// setting params-------
	public String getSetting(String key) {
		return MySetting.the(mAppContext).getSetting(key, null);
	}

	public String getSetting(String key, String defaultValue) {
		return MySetting.the(mAppContext).getSetting(key, defaultValue);
	}

	public boolean getSetting(String key, boolean denfultValue) {
		return MySetting.the(mAppContext).getSetting(key, denfultValue);
	}
	
	public String[] getArraySetting(String key) {
		int arraySize = getSetting(mAppContext.getString(R.string.setting_weather_size),0);
		String[] value = new String[arraySize];
		for(int i = 0;i<arraySize;i++){
			value[i] = getSetting(mAppContext.getString(R.string.setting_weather_info)+i);
		}
		return value;
	}

	public int getSetting(String key, int denfultValue) {
		return MySetting.the(mAppContext).getSetting(key, denfultValue);
	}

	public void setSetting(String key, String value) {
		MySetting.the(mAppContext).setSetting(key, value);
	}

	public void setSetting(String key, boolean value) {
		MySetting.the(mAppContext).setSetting(key, value);
	}

	public void setSetting(String key, int value) {
		MySetting.the(mAppContext).setSetting(key, value);
	}
	
	public void setSetting(String key,String[] value){
		MySetting.the(mAppContext).setSetting(mAppContext.getString(R.string.setting_weather_size),value.length);
		for(int i = 0;i<value.length;i++)
			MySetting.the(mAppContext).setSetting(key+i, value[i]);
	}

}
