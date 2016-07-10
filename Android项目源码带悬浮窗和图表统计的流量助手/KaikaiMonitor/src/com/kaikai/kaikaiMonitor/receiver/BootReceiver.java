package com.kaikai.kaikaiMonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.kaikai.kaikaiMonitor.db.DbManager;
import com.kaikai.kaikaiMonitor.service.TrafficFetchService;
import com.kaikai.kaikaiMonitor.utils.TrafficUtils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/7
 * * * * * * * * * * * * * * * * * * * * * * *
 */
public class BootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                DbManager.getInstance(context).setTrafficTotal(0L);
                /**
                 * 定时更新流量
                 */
                TrafficUtils.startRepeatingService(context, TrafficUtils.INTERVAL, TrafficFetchService.class, "");
                Log.i("TAG", "流量助手：开机初始化完成");
        }
}
