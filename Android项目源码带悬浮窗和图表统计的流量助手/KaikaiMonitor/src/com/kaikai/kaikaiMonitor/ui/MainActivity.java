package com.kaikai.kaikaiMonitor.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.kaikai.kaikaiMonitor.R;
import com.kaikai.kaikaiMonitor.service.TrafficFetchService;
import com.kaikai.kaikaiMonitor.utils.AndroidUtils;
import com.kaikai.kaikaiMonitor.utils.TrafficUtils;

public class MainActivity extends Activity {

        private TextView cur_type;
        private TextView cur_interval;
        private TextView total_mobile;
        private TextView day_mobile;
        private TextView total_wifi;
        private TextView day_wifi;

        /**
         * Called when the activity is first created.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);

                initView();
                /**
                 * 设置当前网络类型
                 */
                setCurNetType();

                /**
                 * 更新流量界面
                 */
                new InitTotalInterfaceTask().execute();

                /**
                 * 定时更新流量
                 */
                TrafficUtils.startRepeatingService(this, TrafficUtils.INTERVAL, TrafficFetchService.class, "");
                /**
                 * 监听网络变化 和 流量更新
                 */
                IntentFilter mFilter = new IntentFilter();
                mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mFilter.addAction(TrafficUtils.ACTION_UPDATE_TRAFFIC);
                registerReceiver(mReceiver, mFilter);
        }

        @Override
        protected void onStart() {
                super.onStart();

        }

        @Override
        protected void onResume() {
                super.onResume();
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
                unregisterReceiver(mReceiver);
        }

        private void initView() {
                cur_type = AndroidUtils.findViewById(this, R.id.cur_type);
                cur_interval = AndroidUtils.findViewById(this, R.id.cur_interval);
                total_mobile = AndroidUtils.findViewById(this, R.id.total_mobile);
                day_mobile = AndroidUtils.findViewById(this, R.id.day_mobile);
                total_wifi = AndroidUtils.findViewById(this, R.id.total_wifi);
                day_wifi = AndroidUtils.findViewById(this, R.id.day_wifi);

                cur_interval.setText(String.format(getString(R.string.cur_interval), TrafficUtils.INTERVAL));
        }

        private void setCurNetType() {
                String type = TrafficUtils.netWorkTypeToString(this);
                cur_type.setText(String.format(getString(R.string.cur_type), type));
        }

        public void onViewClick(View view) {
                switch (view.getId()) {
                case R.id.setting:
                        AndroidUtils.startActivity(this, SettingActivity.class);
                        break;
                case R.id.allapp_detail:
                        AndroidUtils.startActivity(this, AllAppActivity.class);
                        break;
                }
        }

        private BroadcastReceiver mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                        if (TrafficUtils.ACTION_UPDATE_TRAFFIC.equals(intent.getAction())) {
                                new InitTotalInterfaceTask().execute();
                        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                                setCurNetType();
                        }
                }
        };

        private class InitTotalInterfaceTask extends AsyncTask<Void, Void, long[]> {

                @Override
                protected long[] doInBackground(Void... params) {
                        return TrafficUtils.getMobileAndWifiData(MainActivity.this);
                }

                @Override
                protected void onPostExecute(long[] datas) {
                        Log.i("TAG", "InitTotalInterfaceTask:onPostExecute() 界面更新");
                        total_mobile.setText(TrafficUtils.dataSizeFormat(datas[TrafficUtils.INDEX_TOTAL_MOBILE]));
                        day_mobile.setText(TrafficUtils.dataSizeFormat(datas[TrafficUtils.INDEX_DAY_MOBILE]));
                        total_wifi.setText(TrafficUtils.dataSizeFormat(datas[TrafficUtils.INDEX_TOTAL_WIFI]));
                        day_wifi.setText(TrafficUtils.dataSizeFormat(datas[TrafficUtils.INDEX_DAY_WIFI]));
                }
        }

}
