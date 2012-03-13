package org.loon.framework.android.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract.Constants;

public class LSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String SMSSQLDB = "sms_SQLDB";
	public static final String SMSSQLTABLE = "sms_SQLTable";

	public static final String SMSID = "sms_id";			// 短消息序号，如 100
	public static final String SMSADDRESS = "sms_Address";	// 发信人地址，即手机号，如 +8613811810000
	public static final String SMSPERSON = "sms_Person";	// 发信人，返回一个数字就是联系人列表里的序号，陌生人为null  
	public static final String SMSBODY = "sms_Body";		// 短消息内容  
	public static final String SMSDATE = "sms_Date";		// 短消息日期，long型，如 1256539465022
	public static final String SMSTYPE = "sms_Type";		// 类型，1是接收到，2是已发出  
	

	public LSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists " 
				+ SMSSQLTABLE + "("
				+ SMSID + " integer primary key autoincrement, "
				+ SMSADDRESS + " varchar, "
				+ SMSPERSON + " varchar, "
				+ SMSBODY + " varchar, "
				+ SMSDATE + " long, "
				+ SMSTYPE + " int)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
