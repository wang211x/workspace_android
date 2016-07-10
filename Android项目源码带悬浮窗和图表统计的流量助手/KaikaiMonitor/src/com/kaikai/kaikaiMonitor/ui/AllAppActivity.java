package com.kaikai.kaikaiMonitor.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.kaikai.kaikaiMonitor.R;
import com.kaikai.kaikaiMonitor.model.AppModel;
import com.kaikai.kaikaiMonitor.utils.AndroidUtils;
import com.kaikai.kaikaiMonitor.utils.ProgressDialogUtils;
import com.kaikai.kaikaiMonitor.utils.TrafficUtils;
import com.kaikai.kaikaiMonitor.utils.ViewHolder;

import java.util.List;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/8
 * * * * * * * * * * * * * * * * * * * * * * *
 */
public class AllAppActivity extends Activity {

        private ListView app_list;
        private List<AppModel> dataList;
        private boolean isFirstIn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.allapp_activity);

                /**
                 * 控制加载对话框显示
                 */
                isFirstIn = true;

                initView();

                /**
                 * 获得流量数据
                 */
                new FetchAllAppDataTask().execute();

                /**
                 * 监听流量更新
                 */
                IntentFilter mFilter = new IntentFilter();
                mFilter.addAction(TrafficUtils.ACTION_UPDATE_TRAFFIC);
                registerReceiver(mReceiver, mFilter);
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
                unregisterReceiver(mReceiver);
        }

        private void initView() {
                app_list = AndroidUtils.findViewById(this, R.id.app_list);

                app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle b = new Bundle();
                                b.putString(ChartActivity.TAG_TITLE, dataList.get(position).getAppName());
                                b.putInt(ChartActivity.TAG_UID, dataList.get(position).getUid());
                                AndroidUtils.startActivity(AllAppActivity.this, ChartActivity.class, b);
                        }
                });
        }

        private BroadcastReceiver mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                        new FetchAllAppDataTask().execute();
                }
        };

        private class FetchAllAppDataTask extends AsyncTask<Void, Void, List<AppModel>> {

                ProgressDialogUtils pDlgUtl;

                @Override
                protected void onPreExecute() {
                        if (pDlgUtl == null && isFirstIn) {
                                isFirstIn = false;
                                pDlgUtl = new ProgressDialogUtils(AllAppActivity.this);
                                pDlgUtl.show();
                        }
                }

                @Override
                protected List<AppModel> doInBackground(Void... params) {
                        return TrafficUtils.getAllAppTraffic(AllAppActivity.this);
                }

                @Override
                protected void onPostExecute(List<AppModel> datas) {
                        dataList = datas;
                        ListAdapter adapter = app_list.getAdapter();
                        if (adapter != null
                                && app_list.getAdapter() instanceof AllAppAdapter) {
                                ((AllAppAdapter) adapter).notifyDataSetChanged();
                        } else {
                                app_list.setAdapter(new AllAppAdapter());
                        }
                        if (pDlgUtl != null) {
                                pDlgUtl.hide();
                        }
                        Log.i("TAG", "FetchAllAppDataTask():onPostExecute() 界面更新");
                }
        }

        private class AllAppAdapter extends BaseAdapter {

                @Override
                public int getCount() {
                        return dataList.size();
                }

                @Override
                public AppModel getItem(int position) {
                        return dataList.get(position);
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                                convertView = getLayoutInflater().from(AllAppActivity.this)
                                        .inflate(R.layout.allapp_item, null);
                        }
                        TextView app_name = ViewHolder.get(convertView, R.id.app_name);
                        TextView app_traffic = ViewHolder.get(convertView, R.id.app_traffic);
                        ImageView app_icon = ViewHolder.get(convertView, R.id.app_icon);

                        AppModel item = getItem(position);
                        app_name.setText(item.getAppName());
                        app_traffic.setText(TrafficUtils.dataSizeFormat(item.getTraffic()));

                        try {
                                PackageManager pManager = getPackageManager();
                                Drawable icon = pManager.getApplicationIcon(item.getPkgName());
                                app_icon.setImageDrawable(icon);
                        } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                        }

                        return convertView;
                }
        }

}
