package com.parfois.asynctask;

import java.lang.ref.SoftReference;
import java.util.Map;

import com.parfois.utils.HttpUtils;
import com.parfois.utils.MyStatic;
import com.parfois.utils.ReadWriteUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class DownSaveImgAsync extends AsyncTask<String, Integer, Bitmap> {
	private Context context;
	private CallBack cb;
	private Map<String, Bitmap> map;
	private String key;
	
	public DownSaveImgAsync(Context context, CallBack cb,Map<String,Bitmap> map) {
		this.context = context;
		this.cb = cb;
		this.map=map;
	}

	protected Bitmap doInBackground(String... params) {
		key=params[0];
		params[0]=params[0].replace(" ", "%20");
		Bitmap bm = MyStatic.lru.get(params[0]);
		if(bm==null){
			SoftReference<Bitmap> sr = MyStatic.hashMap.get(params[0]);
			if (sr != null) {
				bm = sr.get();
				MyStatic.hashMap.remove(params[0]);
				MyStatic.lru.put(params[0], bm);
			}else{
				String str = params[0].replace("/", "").replace(":", "").replace(".", "");
				if (ReadWriteUtils.hasFile(str)) {
					byte[] buff = ReadWriteUtils.readImg(str);
					bm=BitmapFactory.decodeByteArray(buff, 0, buff.length);
					MyStatic.lru.put(params[0], bm);
				} else {
					if (HttpUtils.isHaveInternet(context)) {
						byte[] buff = HttpUtils.getDataFromHttp(params[0]);
						ReadWriteUtils.writeImg(buff, str);
						bm=BitmapFactory.decodeByteArray(buff, 0, buff.length);
						MyStatic.lru.put(params[0], bm);
					}
				}
			}
		}
		
		return bm;
	}

	protected void onPostExecute(Bitmap result) {
		if (result != null && result.getByteCount() != 0) {
			map.put(key, result);			
			cb.sendImage(result,key);
		}
	}

	public interface CallBack {
		public abstract void sendImage(Bitmap bm,String key);
	}
}
