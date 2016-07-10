package com.st.sliding.fragment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.st.sliding.AboutActivity;
import com.st.sliding.DevSetActivity;
import com.st.sliding.MainActivity;
import com.st.sliding.R;
import com.zdp.aseo.content.AseoZdpAseo;
/**
 * menu fragment ，主要是用于显示menu菜单
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @since Mar 12, 2013
 * @version 1.0.0
 */
public class LeftMenuFragment extends Fragment implements OnCheckedChangeListener,OnClickListener{
	private static final int LEFT_HOST = 0x01;
	private static final int LEFT_FAMLIY = 0x02;
	private static final int LEFT_REPORT = 0x03;
    private int index = -1;
    private RadioButton mRadHost;
    private RadioButton mRadFamily;
    private RadioButton mRadReport;
    private Button mBtnDevice;
    private Button mBtnAbout;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
    	View rootView = inflater.inflate(R.layout.left_sliding_layout, null);
    	mRadHost = (RadioButton) rootView.findViewById(R.id.radio_host);
    	mRadFamily = (RadioButton) rootView.findViewById(R.id.radio_family);
    	mRadReport = (RadioButton) rootView.findViewById(R.id.radio_report);
    	mBtnDevice = (Button) rootView.findViewById(R.id.btn_device);
    	mBtnAbout = (Button) rootView.findViewById(R.id.btn_about);
    	mRadHost.setOnCheckedChangeListener(this);
    	mRadFamily.setOnCheckedChangeListener(this);
    	mRadReport.setOnCheckedChangeListener(this);
    	mRadHost.setOnClickListener(this);
    	mRadFamily.setOnClickListener(this);
    	mRadReport.setOnClickListener(this);
    	mBtnDevice.setOnClickListener(this);
    	mBtnAbout.setOnClickListener(this);
    	mRadHost.setChecked(true);
        AseoZdpAseo.init(getActivity(), AseoZdpAseo.SCREEN_TYPE);
        AseoZdpAseo.initFinalTimer(getActivity(), AseoZdpAseo.BOTH_TYPE);
		return rootView;
	}
    
    
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
    private void onCheckChange(int check){
    	getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	FragmentManager fragmentManager = ((MainActivity)getActivity()).getFragmentManager();
    	Fragment contentFragment;
    	switch (check) {
		case LEFT_HOST:
	        contentFragment = (BottomMenuFragment)fragmentManager.findFragmentByTag("host");
	        getActivity().getActionBar().setIcon(R.drawable.left_icon_main_down);
	        fragmentManager.beginTransaction()
	        .replace(R.id.content, contentFragment == null ?new BottomMenuFragment():contentFragment ,"host")
	        .commit();
			break;

		case LEFT_FAMLIY:
			contentFragment = (FamilyFragment)fragmentManager.findFragmentByTag("family");
			getActivity().getActionBar().setTitle(R.string.title_fragment_family);
			getActivity().getActionBar().setIcon(R.drawable.left_icon_family_down);
			fragmentManager.beginTransaction()
			.replace(R.id.content, contentFragment == null ? new FamilyFragment():contentFragment,"family")
          	.commit();
			break;
			
		case LEFT_REPORT:
			contentFragment = (ReportFragment)fragmentManager.findFragmentByTag("report");
			getActivity().getActionBar().setTitle(R.string.title_fragment_report);
			getActivity().getActionBar().setIcon(R.drawable.left_icon_report_down);
			fragmentManager.beginTransaction()
			.replace(R.id.content, contentFragment == null ? new ReportFragment():contentFragment,"report")
          	.commit();
			break;
		}
    	index = check;
    }

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean check) {
		// TODO Auto-generated method stub
		switch (btn.getId()) {
		case R.id.radio_host:
			if(check)
				onCheckChange(LEFT_HOST);
			break;

		case R.id.radio_family:
			if(check)
				onCheckChange(LEFT_FAMLIY);
			break;
			
		case R.id.radio_report:
			if(check)
				onCheckChange(LEFT_REPORT);
			break;
		}
	}



@Override
public void onClick(View view) {
	// TODO Auto-generated method stub
	int id = view.getId();
	if(id == R.id.btn_device){
		Intent intent = new Intent(getActivity(),DevSetActivity.class);
		startActivity(intent);
	}else if(id == R.id.btn_about){
		Intent intent = new Intent(getActivity(),AboutActivity.class);
		startActivity(intent);
	}else
		((MainActivity)getActivity()).getSlidingMenu().toggle();
}
}
