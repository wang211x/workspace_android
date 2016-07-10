package com.st.utils;

import com.st.sliding.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

public class WeatherUtil {

	// ��ʾ�Ի���
	public static Dialog showDialog(Context context, View view, String title,
			String content) {
		Dialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
				.setMessage(content).create();
		return alertDialog;
	}

	
	/*-------------------
	 * �洢������Ϣ
	 * @parame int type 1-string,2-boolean,3-long,4-int,5-float
	 * --------------
	 */

	public static void saveShare(Context context, int type, String name,Object value) {
		Editor editor = context.getSharedPreferences("weather",
				Context.MODE_PRIVATE).edit();
		switch (type) {
			case 1:
				editor.putString(name,(String) value);
				break;
			case 2:
				editor.putBoolean(name,(Boolean)value);
				break;
			case 3:
				editor.putLong(name, (Long)value);
				break;
			case 4:
				editor.putInt(name, (Integer)value);
				break;
			case 5:
				editor.putFloat(name,(Float)value);
				break;
			default:
				break;
		}
		editor.commit();
	}
	
	/**
	 * ��ȡ�洢��Ϣ
	 * @param context-������,type-��������,name-������Ϣ����
	 */
	
	public static Object getShare(Context context,int type, String name) {
		
		SharedPreferences share = context.getSharedPreferences("weather",
				Context.MODE_PRIVATE);
		switch (type) {
			case 1:
				return share.getString(name, "");
			case 2:
				return share.getBoolean(name, false);
			case 3:
				return share.getLong(name, 0);
			case 4:
				return share.getInt(name, 0);
			case 5:
				return share.getFloat(name,Float.parseFloat("0"));
			default:
				return null;
		}
		
	}
	
	
	/**
	 * @author Administrator
	 * @param param-ͼƬ�ַ���
	 */
	
	public static int getImage(String param){
		int currentid=0;
		String[] arr=param.split("\\.");
		int tag;
		if(arr[0].equals("nothing")){
			tag=0;
		}else{
			tag=Integer.parseInt(arr[0]);
		}
		switch(tag){
			case 0:
				currentid=R.drawable.w01;
				break;
			case 1:
				currentid=R.drawable.w02;
				break;
			case 2:
				currentid=R.drawable.w03;
				break;
			case 3:
				currentid=R.drawable.w04;
				break;
			case 4:
				currentid=R.drawable.w05;
				break;
			case 5:
				currentid=R.drawable.w06;
				break;
			case 6:
				currentid=R.drawable.w07;
				break;
			case 7:
				currentid=R.drawable.w19;
				break;
			case 8:
				currentid=R.drawable.w09;
				break;
			case 9:
				currentid=R.drawable.w10;
				break;
			case 10:
				currentid=R.drawable.w11;
				break;
			case 11:
				currentid=R.drawable.w12;
				break;
			case 12:
				currentid=R.drawable.w13;
				break;
			case 13:
				currentid=R.drawable.w14;
				break;
			case 14:
				currentid=R.drawable.w15;
				break;
			case 15:
				currentid=R.drawable.w16;
				break;
			case 16:
				currentid=R.drawable.w17;
				break;
			case 17:
				currentid=R.drawable.w18;
				break;
			case 18:
				currentid=R.drawable.w19;
				break;
			case 19:
				currentid=R.drawable.w20;
				break;
			case 20:
				currentid=R.drawable.w21;
				break;
			case 21:
				currentid=R.drawable.w22;
				break;
			case 22:
				currentid=R.drawable.w23;
				break;
			case 23:
				currentid=R.drawable.w24;
				break;
			case 24:
				currentid=R.drawable.w25;
				break;
			case 25:
				currentid=R.drawable.w26;
				break;
			case 26:
				currentid=R.drawable.w27;
				break;
			case 27:
				currentid=R.drawable.w28;
				break;
			case 28:
				currentid=R.drawable.w29;
				break;
			case 29:
				currentid=R.drawable.w30;
				break;
			case 30:
				currentid=R.drawable.w31;
				break;
			case 31:
				currentid=R.drawable.w32;
				break;
			case 32:
				currentid=R.drawable.w33;
				break;
			default:
				currentid=R.drawable.w01;
				break;
		
		}
		
		return currentid;
	}
	
	/**
	 * �ж��ֻ��Ƿ�����
	 * @param context
	 * @return
	 */
	
	public static boolean isconnect(final Context context) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();    

			
			if (info!=null && info.isAvailable()) {
				return true;
			}else{
				return false;
			}
	}

}
