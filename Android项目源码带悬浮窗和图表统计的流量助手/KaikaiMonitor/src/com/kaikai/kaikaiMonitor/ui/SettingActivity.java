package com.kaikai.kaikaiMonitor.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.kaikai.kaikaiMonitor.R;
import com.kaikai.kaikaiMonitor.service.FloatBarService;
import com.kaikai.kaikaiMonitor.utils.AndroidUtils;
import com.kaikai.kaikaiMonitor.utils.PreferencesUtils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/9
 * * * * * * * * * * * * * * * * * * * * * * *
 */
public class SettingActivity extends Activity {

        public static final String TAG_FLOAT = "tag_float";

        private CheckBox float_check;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.setting_activity);

                initView();
        }

        private void initView() {
                float_check = AndroidUtils.findViewById(this, R.id.float_check);

                float_check.setChecked(PreferencesUtils.getInstance().isChecked(this, TAG_FLOAT));

                setListener();
        }

        private void setListener() {
                float_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                PreferencesUtils.getInstance().setChecked(SettingActivity.this, TAG_FLOAT, isChecked);
                                if (isChecked) {
                                        Intent serviceIntent = new Intent(SettingActivity.this, FloatBarService.class);
                                        startService(serviceIntent);
                                } else {
                                        Intent brodcastIntent = new Intent(FloatBarService.ACTION_SERVICE);
                                        sendBroadcast(brodcastIntent);
                                }
                        }
                });
        }
}
