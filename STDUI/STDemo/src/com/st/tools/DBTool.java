package com.st.tools;

import java.util.List;

import android.content.Context;

import com.android.pc.ioc.db.sqlite.DbUtils;
import com.android.pc.ioc.db.sqlite.Selector;
import com.st.app.STApplaction;
import com.st.strut.STUser;

public class DBTool {
	private Context mAppContext;
	private DbUtils mDBUtil;
	private static class MyDBTool {
		public static DBTool gInstance = new DBTool();
	};

	public static DBTool the() {
		return MyDBTool.gInstance;
	}

	private DBTool() {
		mAppContext = STApplaction.mApp;
		mDBUtil = DbUtils.create(mAppContext);
	}
	
	public void saveUser(STUser user){
		mDBUtil.save(user);
	}
	
	public List<STUser> findAllUserByCls(){
		return mDBUtil.findAll(STUser.class);
	}
	
	public STUser findUserByName(String name){
		return mDBUtil.findFirst(Selector.from(STUser.class).where("name","=",name));
	}
	
	public void delUserById(int id){
		mDBUtil.deleteById(STUser.class, id);
	}
	
	public void updateUserById(STUser user){
		mDBUtil.update(user);
	}
}
