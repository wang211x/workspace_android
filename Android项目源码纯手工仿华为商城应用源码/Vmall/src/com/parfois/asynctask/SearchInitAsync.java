package com.parfois.asynctask;

import java.util.List;

import com.parfois.adapter.SearchAdapter;
import com.parfois.adapter.SearchInitAdapter;
import com.parfois.bean.SGoods;
import com.parfois.utils.Constant;
import com.parfois.utils.DBManager;
import com.parfois.utils.HttpUtils;
import com.parfois.utils.MyStatic;
import com.parfois.utils.PaserUtils;
import com.parfois.utils.PathUtils;
import com.parfois.vmall.SearchActivity.ViewSearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchInitAsync extends AsyncTask<String, Void, List<String>>{
	private Context context;
	private ViewSearch vs;
	private ProgressDialog pd;
	private SearchAdapter sa,sagv;
	private List<SGoods> salist;
	
	public SearchInitAsync(Context context, ViewSearch vs,SearchAdapter sa,SearchAdapter sagv,List<SGoods> salist) {
		this.context = context;
		this.vs=vs;
		this.sa=sa;
		this.sagv=sagv;
		this.salist=salist;
	}

	protected void onPreExecute() {
		pd=new ProgressDialog(context);
		pd.setTitle("Loading...");
		pd.show();
	}

	protected List<String> doInBackground(String... params) {
		List<String> list=null;
		Cursor cursor = DBManager.rawQuery(MyStatic.db, "select * from "+Constant.TABLE_NAME+" where "+Constant._ID+"='"+params[0]+"'",null);
		if (cursor.getCount() == 1) {
			byte[] buff = DBManager.CursorToByte(cursor);
			list=PaserUtils.parserHomeHotJson(buff);
		} else {
			if(HttpUtils.isHaveInternet(context)){
				byte[] buff = HttpUtils.getDataFromHttp(params[0]);
				String sql = "insert into " + Constant.TABLE_NAME + " values('" + params[0] + "','" + new String(buff, 0, buff.length)+ "')";
				DBManager.execSQL(MyStatic.db, sql);
				list=PaserUtils.parserHomeHotJson(buff);
			}
		}
		return list;
	}

	protected void onPostExecute(final List<String> result) {
		if(result!=null&&result.size()!=0){
			vs.search_etword.setHint(result.remove(0));
			SearchInitAdapter sia=new SearchInitAdapter(context, result);
			vs.search_gvword.setAdapter(sia);
			vs.search_gvword.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					vs.search_etword.setText(result.get(position));
					PathUtils.search_page=1;
					PathUtils.search_keyword=vs.search_etword.getText().toString().replace(" ", "%20");
					String path=PathUtils.search_0+PathUtils.search_page+PathUtils.search_1+
							PathUtils.search_item+PathUtils.search_2+PathUtils.search_keyword;
					new SearchAsync(context, vs,sa,sagv,salist,true).execute(path);
				}
			});
		}
		
		pd.dismiss();
	}
	
}
