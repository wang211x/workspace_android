package com.st.app;

import android.app.Application;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.event.EventBus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.st.sliding.R;

public class STApplaction extends Application implements AMapLocationListener{
	public static STApplaction mApp;
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation mLocation;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mApp = this;
		aMapLocManager = LocationManagerProxy.getInstance(this);
		startLocation();
		Ioc.getIoc().init(this);
		DisplayImageOptions defaultOptions = new DisplayImageOptions
				.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo) 
				.showImageOnFail(R.drawable.empty_photo) 
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(100)//缓存一百张图片
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
		super.onCreate();
	}
	
	
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		stoptLocation();
		super.onTerminate();
	}



	public AMapLocation getBDLocation(){
		return mLocation;
	}
	private void startLocation(){
		if(null!=aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER)){
			mLocation = aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER);
		}else if(null!=aMapLocManager.getLastKnownLocation(LocationManagerProxy.NETWORK_PROVIDER)){
			mLocation = aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER);
		}
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 10000, 10, this);
	}
	private void stoptLocation(){
			if (aMapLocManager != null) {
				aMapLocManager.removeUpdates(this);
				aMapLocManager.destory();
			}
			aMapLocManager = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation mapLocation) {
		// TODO Auto-generated method stub
		if(null != mapLocation){
			mLocation = mapLocation;
			EventBus.getDefault().post(mLocation.getCity());
		}
		
		
	}
}
