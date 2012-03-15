package org.loon.framework.android.game.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract.Constants;

/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project 	:	wsi-lgame-pro 
 * @author 	:	yanggang, chenpeng
 * @email 	:	yanggang2050@gmail.com
 * @site		:	http://code.google.com/p/wsi-lgame-pro/
 * @version 	:	v-0.0.1
 */

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
