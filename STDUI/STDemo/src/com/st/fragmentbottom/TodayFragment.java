package com.st.fragmentbottom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.st.sliding.R;
import com.st.tools.ToolBox;
import com.st.utils.WeatherUtil;
import com.st.view.CircleProgressBar;
import com.st.web.WeatherService;

public class TodayFragment extends Fragment implements OnClickListener {
	private static final int PAGE_CHART = 0x01;
	private static final int PAGE_STEP = 0x02;
	private static final int PAGE_TIANQI = 0x03;

	public static final String NAMESPACE="http://WebXml.com.cn/";
    public static final String URL = "http://webservice.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
	
	private static final int CHAT_NUM = 12;
	private BarChart mSaltChart;
	private RelativeLayout mLayoutStep;
	private RelativeLayout mLayoutTianqi;

	private CircleProgressBar mCircleBar;

	private TextView mTvCountSteps;

	private RelativeLayout mChartTv;
	private RelativeLayout mStepTv;
	private RelativeLayout mTianqiTv;

	private ImageButton mBtnPriview;
	private ImageButton mBtnNext;
	private WeatherService mWser;
	private String[] mWeatherarr;
	private String mCityNo;
	private int mPageIndex;
	
	
	private ImageView mTodayImage;
	private TextView mToadyWeather;
	private int mWalkings = 6000;
	
	private ToolBox mMyTool;
	private boolean mIsInitWeather = false;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(null!=mWeatherarr){
				mToadyWeather.setText(mWeatherarr[5]+"\n"+mWeatherarr[6]+"\n"+mWeatherarr[7]);
				mTodayImage.setImageResource(WeatherUtil.getImage(mWeatherarr[8]));
			}
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mMyTool = ToolBox.the();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_today, container,
				false);
		mSaltChart = (BarChart) rootView.findViewById(R.id.chart_salt);
		mLayoutStep = (RelativeLayout) rootView
				.findViewById(R.id.top_layout_step);
		mLayoutTianqi = (RelativeLayout) rootView
				.findViewById(R.id.relativelayout_tianqi);
		mBtnPriview = (ImageButton) rootView
				.findViewById(R.id.pageview_left_btn);
		mBtnNext = (ImageButton) rootView.findViewById(R.id.pageview_right_btn);

		mChartTv = (RelativeLayout) rootView
				.findViewById(R.id.pageview_chart_container);
		mStepTv = (RelativeLayout) rootView
				.findViewById(R.id.pageview_steps_container);
		mTianqiTv = (RelativeLayout) rootView
				.findViewById(R.id.pageview_tianqi_container);
		mTvCountSteps = (TextView) rootView
				.findViewById(R.id.pageview_circle_big_tv);
		mCircleBar = (CircleProgressBar) rootView
				.findViewById(R.id.circleProgressBar);
		mCircleBar.setMax(7000);
		mTodayImage = (ImageView) rootView.findViewById(R.id.imageview_pm_cloud);
		mToadyWeather = (TextView) rootView.findViewById(R.id.today_weather);
		mChartTv.setOnClickListener(this);
		mStepTv.setOnClickListener(this);
		mTianqiTv.setOnClickListener(this);

		mBtnPriview.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mPageIndex = PAGE_CHART;
		initChart();
		EventBus.getDefault().register(this);
		initWeather();
		return rootView;
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onEventMainThread(String city) {
		if(mIsInitWeather)
			return;
		String cityName = city;
		if(city.contains("市"))
			cityName = city.substring(0, city.length()-1);
		getWeatherInfo(cityName);
	}
	private void getWeatherInfo(String city){
		if(null!=mCityNo&&mCityNo.equals(city))
			return;
		mCityNo = city;
		new Thread(new getWeather()).start();
	}
	
	public class getWeather implements Runnable{
		@Override
		public synchronized void run() {
			//判断是否联网
			mWser=new WeatherService(mCityNo);
			mWeatherarr=mWser.getWeather();
			if(null != mWeatherarr){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(new java.util.Date());
				mMyTool.setSetting(getString(R.string.setting_weather_date), date);
				mMyTool.setSetting(getString(R.string.setting_weather_info), mWeatherarr);
			}
			mHandler.sendMessage(mHandler.obtainMessage());
		}
	}

	private void initChart() {
		// TODO Auto-generated method stub
		mSaltChart.setUnit("ml");
		mSaltChart.setDescription("盐量监控 ml/h");
		mSaltChart.setMaxVisibleValueCount(CHAT_NUM);
		// sets the number of digits for values inside the chart
		mSaltChart.setValueDigits(2);
		// disable 3D
		mSaltChart.set3DEnabled(false);
		// scaling can now only be done on x- and y-axis separately
		mSaltChart.setPinchZoom(false);
		mSaltChart.setDrawBarShadow(false);
		mSaltChart.setDrawVerticalGrid(false);
		mSaltChart.setDrawHorizontalGrid(false);
		mSaltChart.setDrawGridBackground(false);
		XLabels xLabels = mSaltChart.getXLabels();
		xLabels.setPosition(XLabelPosition.BOTTOM);
		xLabels.setCenterXLabelText(true);
		xLabels.setSpaceBetweenLabels(0);
		mSaltChart.setDrawYLabels(true);
		mSaltChart.setDrawLegend(false);
		mSaltChart.setDrawYValues(true);
		// add a nice and smooth animation
		mSaltChart.animateY(1000);
		new chartTask().execute(50);
	}

	private void initWeather(){
		String[] info = mMyTool.updateWeather();
		if(null != info){
			mIsInitWeather = true;
			mWeatherarr = info;
			mHandler.sendMessage(mHandler.obtainMessage());
		}
	}
	
	@SuppressLint("NewApi")
	private void initCircleBar() {
		mCircleBar.setProgress(mWalkings, 700);
		mTvCountSteps.setText(String.valueOf(mWalkings));
	}

	private class chartTask extends AsyncTask<Integer, Integer, BarData> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected BarData doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

			for (int i = 0; i < CHAT_NUM; i++) {
				float mult = (30 + 1);
				float val1 = (float) (Math.random() * mult) + mult / 3;
				yVals1.add(new BarEntry((int) val1, i));
			}

			ArrayList<String> xVals = new ArrayList<String>();
			for (int i = 0; i < CHAT_NUM; i++) {
				xVals.add(i * 2 + "h");
			}

			BarDataSet set1 = new BarDataSet(yVals1, "Data Set");
			set1.setColors(ColorTemplate.createColors(getActivity(),
					ColorTemplate.VORDIPLOM_COLORS));

			ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
			dataSets.add(set1);

			BarData data = new BarData(xVals, dataSets);
			return data;
		}

		@Override
		protected void onPostExecute(BarData result) {
			// TODO Auto-generated method stub
			mSaltChart.setData(result);
			mSaltChart.invalidate();
			super.onPostExecute(result);
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.pageview_left_btn:
			if (mPageIndex == PAGE_CHART)
				return;
			else if (mPageIndex == PAGE_STEP) {
				mSaltChart.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.in_leftright));
				mLayoutStep.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.out_leftright));
				mSaltChart.setVisibility(View.VISIBLE);
				mLayoutStep.setVisibility(View.INVISIBLE);
				mPageIndex = PAGE_CHART;
				mSaltChart.animateY(1000);
			} else if (mPageIndex == PAGE_TIANQI) {
				mLayoutStep.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.in_leftright));
				mLayoutTianqi.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.out_leftright));
				mLayoutStep.setVisibility(View.VISIBLE);
				mLayoutTianqi.setVisibility(View.INVISIBLE);
				mPageIndex = PAGE_STEP;
				initCircleBar();
			}
			break;
		case R.id.pageview_right_btn:
			if (mPageIndex == PAGE_TIANQI)
				return;
			else if (mPageIndex == PAGE_STEP) {
				mLayoutTianqi.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.in_rightleft));
				mLayoutStep.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.out_rightleft));
				mLayoutStep.setVisibility(View.INVISIBLE);
				mLayoutTianqi.setVisibility(View.VISIBLE);
				mPageIndex = PAGE_TIANQI;
			} else if (mPageIndex == PAGE_CHART) {
				mLayoutStep.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.in_rightleft));
				mSaltChart.setAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.out_rightleft));
				mSaltChart.setVisibility(View.INVISIBLE);
				mLayoutStep.setVisibility(View.VISIBLE);
				mPageIndex = PAGE_STEP;
				initCircleBar();
			}
			break;
		case R.id.pageview_chart_container:
			switchPage(PAGE_CHART);
			break;
		case R.id.pageview_steps_container:
			switchPage(PAGE_STEP);
			break;
		case R.id.pageview_tianqi_container:
			switchPage(PAGE_TIANQI);
			break;
		}
		changeView();
	}

	private void switchPage(int page) {
		if (mPageIndex == page)
			return;
		switch (page) {
		case PAGE_CHART:
			mLayoutStep.setVisibility(View.INVISIBLE);
			mLayoutTianqi.setVisibility(View.INVISIBLE);
			mSaltChart.setAnimation(AnimationUtils.loadAnimation(getActivity(),
					R.anim.fade_in));
			mSaltChart.setVisibility(View.VISIBLE);
			mSaltChart.animateY(1000);
			break;
		case PAGE_STEP:
			mSaltChart.setVisibility(View.INVISIBLE);
			mLayoutTianqi.setVisibility(View.INVISIBLE);
			mLayoutStep.setAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.fade_in));
			mLayoutStep.setVisibility(View.VISIBLE);
			initCircleBar();
			break;
		case PAGE_TIANQI:
			mSaltChart.setVisibility(View.INVISIBLE);
			mLayoutStep.setVisibility(View.INVISIBLE);
			mLayoutTianqi.setAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.fade_in));
			mLayoutTianqi.setVisibility(View.VISIBLE);
			break;
		}
		mPageIndex = page;
	}

	@SuppressLint("ResourceAsColor")
	private void changeView() {
		// TODO Auto-generated method stub
		switch (mPageIndex) {
		case PAGE_CHART:
			((ImageView) mChartTv.findViewById(R.id.salt_icon))
					.setImageResource(R.drawable.day_icon_active_down);
			((ImageView) mStepTv.findViewById(R.id.step_icon))
					.setImageResource(R.drawable.day_icon_walking);
			((ImageView) mTianqiTv.findViewById(R.id.activity_icon))
					.setImageResource(R.drawable.day_timeline_icon_place);

			((TextView) mChartTv.findViewById(R.id.salt_icon_desc))
					.setTextColor(getResources().getColor(R.color.orange));
			((TextView) mStepTv.findViewById(R.id.step_icon_desc))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mTianqiTv.findViewById(R.id.activity_icon_desc))
					.setTextColor(getResources().getColor(R.color.light_grey));

			((TextView) mChartTv.findViewById(R.id.salt_value))
					.setTextColor(getResources().getColor(R.color.orange));
			((TextView) mStepTv.findViewById(R.id.step_value))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mTianqiTv.findViewById(R.id.activity_value))
					.setTextColor(getResources().getColor(R.color.light_grey));
			break;
		case PAGE_STEP:
			((ImageView) mChartTv.findViewById(R.id.salt_icon))
					.setImageResource(R.drawable.day_icon_active);
			((ImageView) mStepTv.findViewById(R.id.step_icon))
					.setImageResource(R.drawable.day_icon_walking_light);
			((ImageView) mTianqiTv.findViewById(R.id.activity_icon))
					.setImageResource(R.drawable.day_timeline_icon_place);

			((TextView) mChartTv.findViewById(R.id.salt_icon_desc))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mStepTv.findViewById(R.id.step_icon_desc))
					.setTextColor(getResources().getColor(R.color.orange));
			((TextView) mTianqiTv.findViewById(R.id.activity_icon_desc))
					.setTextColor(getResources().getColor(R.color.light_grey));

			((TextView) mChartTv.findViewById(R.id.salt_value))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mStepTv.findViewById(R.id.step_value))
					.setTextColor(getResources().getColor(R.color.orange));
			((TextView) mTianqiTv.findViewById(R.id.activity_value))
					.setTextColor(getResources().getColor(R.color.light_grey));
			break;
		case PAGE_TIANQI:
			((ImageView) mChartTv.findViewById(R.id.salt_icon))
					.setImageResource(R.drawable.day_icon_active);
			((ImageView) mStepTv.findViewById(R.id.step_icon))
					.setImageResource(R.drawable.day_icon_walking);
			((ImageView) mTianqiTv.findViewById(R.id.activity_icon))
					.setImageResource(R.drawable.day_timeline_icon_place_down);

			((TextView) mChartTv.findViewById(R.id.salt_icon_desc))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mStepTv.findViewById(R.id.step_icon_desc))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mTianqiTv.findViewById(R.id.activity_icon_desc))
					.setTextColor(getResources().getColor(R.color.orange));

			((TextView) mChartTv.findViewById(R.id.salt_value))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mStepTv.findViewById(R.id.step_value))
					.setTextColor(getResources().getColor(R.color.light_grey));
			((TextView) mTianqiTv.findViewById(R.id.activity_value))
					.setTextColor(getResources().getColor(R.color.orange));
			break;
		}
	}
}
