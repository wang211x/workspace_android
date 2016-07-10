package com.kaikai.kaikaiMonitor.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kaikai.kaikaiMonitor.R;
import com.kaikai.kaikaiMonitor.model.DateTrafficModel;
import com.kaikai.kaikaiMonitor.utils.ProgressDialogUtils;
import com.kaikai.kaikaiMonitor.utils.TrafficUtils;
import com.kaikai.kaikaiMonitor.widget.MyMarkerView;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/10
 * * * * * * * * * * * * * * * * * * * * * * *
 */
public class ChartActivity extends Activity {

        public static final String TAG_TITLE = "title";
        public static final String TAG_UID = "uid";
        public static final int DATA_SIZE = 5;

        private int uid;
        private LineChart mChart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.chart_activity);
                initView();
                new FetchDataTask().execute();
        }

        private void initView() {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                        if (!TextUtils.isEmpty(bundle.getString(TAG_TITLE))) {
                                setTitle(bundle.getString(TAG_TITLE));
                        }
                        uid = bundle.getInt(TAG_UID, 0);
                } else {
                        Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
                        finish();
                }

                mChart = (LineChart) findViewById(R.id.chart1);
                mChart.setNoDataTextDescription("未加载数据");
        }

        private void initChart() {
                //                mChart.setOnChartGestureListener(this);
                //                mChart.setOnChartValueSelectedListener(this);
                mChart.setDrawGridBackground(false);
                mChart.setDescription("");
                mChart.setHighlightEnabled(true);
                mChart.setTouchEnabled(true);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                // mChart.setScaleXEnabled(true);
                // mChart.setScaleYEnabled(true);
                mChart.setPinchZoom(true);

                MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
                mChart.setMarkerView(mv);
                mChart.setHighlightIndicatorEnabled(false);

                mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
                Legend l = mChart.getLegend();
                l.setForm(Legend.LegendForm.LINE);
        }

        private void setData(List<DateTrafficModel> datas) {

                ArrayList<String> xVals = new ArrayList<String>();
                for (int i = 0; i < datas.size(); i++) {
                        xVals.add(new SimpleDateFormat("M.d").format(new Date(datas.get(i).getDate())));
                }

                ArrayList<Entry> yVals = new ArrayList<Entry>();

                for (int i = 0; i < datas.size(); i++) {

                        yVals.add(new Entry(TrafficUtils.dataToMB(datas.get(i).getTraffic()), i));
                }

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(yVals, DATA_SIZE + "天流量情况（单位：MB）");
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);

                // set the line to be drawn like this "- - - - - -"
                set1.enableDashedLine(10f, 5f, 0f);
                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleSize(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(9f);
                set1.setFillAlpha(65);
                set1.setFillColor(Color.BLACK);
                //        set1.setDrawFilled(true);
                // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
                // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

                ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
                dataSets.add(set1); // add the datasets

                // create a data object with the datasets
                LineData data = new LineData(xVals, dataSets);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                //                leftAxis.addLimitLine(ll1);
                //                leftAxis.addLimitLine(ll2);
                Collections.sort(datas, new Comparator<DateTrafficModel>() {
                        @Override
                        public int compare(DateTrafficModel lhs, DateTrafficModel rhs) {
                                return (int) (rhs.getTraffic() - lhs.getTraffic());
                        }
                });
                leftAxis.setAxisMaxValue(TrafficUtils.dataToMB(datas.get(0).getTraffic()) + 10.0f);
                leftAxis.setAxisMinValue(0.0f);
                leftAxis.setStartAtZero(false);
                leftAxis.enableGridDashedLine(10f, 10f, 0f);

                // limit lines are drawn behind data (and not on top)
                leftAxis.setDrawLimitLinesBehindData(true);

                mChart.getAxisRight().setEnabled(false);

                // set data
                mChart.setData(data);
        }

        private class FetchDataTask extends AsyncTask<Void, Void, List<DateTrafficModel>> {
                ProgressDialogUtils pDlgUtl;

                @Override
                protected void onPreExecute() {
                        if (pDlgUtl == null) {
                                pDlgUtl = new ProgressDialogUtils(ChartActivity.this);
                                pDlgUtl.show();
                        }
                }

                @Override
                protected List<DateTrafficModel> doInBackground(Void... params) {
                        return TrafficUtils.fetchDayTrrafic(ChartActivity.this, uid, DATA_SIZE);
                }

                @Override
                protected void onPostExecute(List<DateTrafficModel> datas) {
                        initChart();
                        setData(datas);
                        if (pDlgUtl != null) {
                                pDlgUtl.hide();
                        }
                        //                        mChart.animateXY(1000, 1000);
                }
        }
}
