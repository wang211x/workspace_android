package com.st.sliding.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.slidingmenu.lib.SlidingMenu;
import com.st.fragmentbottom.TodayFragment;
import com.st.fragmentbottom.GroupFragment;
import com.st.fragmentbottom.AdviceFragment;
import com.st.sliding.MainActivity;
import com.st.sliding.R;

/**
 * menu fragment ，主要是用于显示menu菜单
 * 
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @since Mar 12, 2013
 * @version 1.0.0
 */
public class BottomMenuFragment extends Fragment implements
		OnCheckedChangeListener {
	private MainActivity mActivity = null;
	private RadioGroup mRadGrp;
	private int mIndex = -1;
	private static final int INDEX_MAIN = 0x01;
	private static final int INDEX_GROUP = 0x02;
	private static final int INDEX_ADVCE = 0x03;
	private TodayFragment mTodayFg;
	private GroupFragment mGroupFg;
	private AdviceFragment mAdvceFg;
	private Fragment mFragment;
	private FragmentManager mFragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mActivity = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frame_viewpager, null);
		mRadGrp = (RadioGroup) view.findViewById(R.id.tab_rg_menu);
		mRadGrp.setOnCheckedChangeListener(this);
		initTabs();
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initTabs() {
		ActionBar actionBar = mActivity.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mFragmentManager = ((MainActivity)getActivity()).getFragmentManager();
		mRadGrp.check(R.id.tab_rb_mian);
		switchFragment(INDEX_MAIN);
		((MainActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	private void switchFragment(int index) {
		if(index == mIndex)
			return;
		switch (index) {
		case INDEX_GROUP:
			if(null == mGroupFg)
				mGroupFg = new GroupFragment(mActivity);
			mFragment = mGroupFg;
			getActivity().getActionBar().setTitle(R.string.title_bottom_group);
			break;
		case INDEX_MAIN:
			if(null == mTodayFg)
				mTodayFg = new TodayFragment();
			mFragment = mTodayFg;
			getActivity().getActionBar().setTitle(R.string.title_bottom_host);
			break;
		case INDEX_ADVCE:
			if(null == mAdvceFg)
				mAdvceFg = new AdviceFragment();
			mFragment = mAdvceFg;
			getActivity().getActionBar().setTitle(R.string.content_bottom_advice);
			break;
		default:
			break;
		}
		mFragmentManager.beginTransaction().replace(R.id.bottom_view, mFragment).commit();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkId) {
		// TODO Auto-generated method stub
		switch (checkId) {
		case R.id.tab_rb_group:
			switchFragment(INDEX_GROUP);
			break;
		case R.id.tab_rb_mian:
			switchFragment(INDEX_MAIN);
			break;
		case R.id.tab_rb_advise:
			switchFragment(INDEX_ADVCE);
			break;
		default:
			break;
		}
	}

}
