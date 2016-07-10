package com.st.sliding;


import java.util.ArrayList;
import java.util.List;

import com.st.view.CakeSurfaceView;
import com.st.view.CakeSurfaceView.Gravity;
import com.st.view.CakeSurfaceView.OnItemClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class AboutActivity extends Activity {
	private CakeSurfaceView cakeSurfaceView;
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about);
		setTitle(R.string.btn_about);
		getActionBar().setIcon(R.drawable.left_aboutl);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		cakeSurfaceView = (CakeSurfaceView) findViewById(R.id.report_cake_view);
		List<CakeSurfaceView.CakeValue> cakeValues2 = new ArrayList<CakeSurfaceView.CakeValue>();
		cakeValues2.add(new CakeSurfaceView.CakeValue("猫猫猫", 10f,"我是猫,我是item0.我是猫,我是item0.我是猫,我是item0.我是猫,我是item0.我是猫,我是item0.我是猫,我是item0.我是猫,我是item0."));
		cakeValues2.add(new CakeSurfaceView.CakeValue("狗狗狗", 0f,"狗狗狗,item1.狗狗狗,item1.狗狗狗,item1.狗狗狗,item1.狗狗狗,item1.狗狗狗,item1.狗狗狗,item1.狗狗狗,item1.狗狗狗,item1.狗狗狗,item1."));
		cakeValues2.add(new CakeSurfaceView.CakeValue("人人人", 0f,"人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2.人,item2."));
		cakeValues2.add(new CakeSurfaceView.CakeValue("javaapk.com", 20f,"橡果"));
		cakeValues2.add(new CakeSurfaceView.CakeValue("瓜皮", 26f,"瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4.瓜皮,item4."));
		cakeValues2.add(new CakeSurfaceView.CakeValue("鸭嘴兽", 1f,"鸭嘴兽,item5.鸭嘴兽,item5.鸭嘴兽,item5.鸭嘴兽,item5.鸭嘴兽,item5.鸭嘴兽,item5.鸭嘴兽,item5.鸭嘴兽,item5.鸭嘴兽,item5."));
		cakeValues2.add(new CakeSurfaceView.CakeValue("面包", 0f,"面包,item6.面包,item6.面包,item6.面包,item6.面包,item6.面包,item6.面包,item6.面包,item6.面包,item6.面包,item6.面包,item6.面包,item6."));
		cakeValues2.add(new CakeSurfaceView.CakeValue("dog", 3f,"dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7.dog,item7."));
		cakeValues2.add(new CakeSurfaceView.CakeValue("cat", 13f,"cat,item8.cat,item8.cat,item8.cat,item8.cat,item8.cat,item8.cat,item8.cat,item8.cat,item8.cat,item8.cat,item8."));
		cakeSurfaceView.setData(cakeValues2);
		cakeSurfaceView.setGravity(Gravity.bottom);
		cakeSurfaceView.setDetailTopSpacing(15);
//		cakeSurfaceView.setHighLightOffset(10);
		cakeSurfaceView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(int position) {
				Toast.makeText(AboutActivity.this, "点击:" + position, 0).show();
			}
		});
		}
		
		 @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		        switch (item.getItemId()) {
		        case android.R.id.home:
		            finish();
		            return true;
		        }
		        return super.onOptionsItemSelected(item);
		    }
}
