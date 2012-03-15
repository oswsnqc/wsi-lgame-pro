package org.loon.framework.android.game.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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

public class LSQLSMS {

	private Context context;
	private LSQLiteHelper sqlHelper;
	private SQLiteDatabase rDB;
	
	public LSQLSMS(Context context){
		this.context = context;
		sqlHelper = new LSQLiteHelper(context, LSQLiteHelper.SMSSQLDB, null, 1);
	}
	
	
	private Cursor getSMSInPhone(){
		final String SMS_URI_ALL = "content://sms/"; 
//		final String SMS_URI_INBOX = "content://sms/inbox";
//		final String SMS_URI_SEND = "content://sms/sent"; 
//		final String SMS_URI_DRAFT = "content://sms/draft";
//		final String SMS_URI_OUTBOX = "content://sms/outbox";
//		final String SMS_URI_FAILED = "content://sms/failed";
//		final String SMS_URI_QUEUED = "content://sms/queued";
		
		Uri uri_SMSCONTENT = Uri.parse(SMS_URI_ALL);
		String [] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
		
		Cursor cursor = context.getContentResolver().query(uri_SMSCONTENT, projection, null, null, "date desc");
		while(cursor.moveToNext()){
			System.out.println("--sms-- : " + cursor.getString(cursor.getColumnIndex("_id")) + "\t\t"
					+ cursor.getString(cursor.getColumnIndex("address")) + "\t\t"
					+ cursor.getString(cursor.getColumnIndex("person")) + "\t\t"
					+ cursor.getString(cursor.getColumnIndex("date")) + "\t\t"
					+ cursor.getString(cursor.getColumnIndex("type")));
			System.out.println("--sms-- : " + cursor.getString(cursor.getColumnIndex("body")));
		}
		
		return cursor;
	}
	
	public void insertSMSToDatebase(){
		SQLiteDatabase wDB = sqlHelper.getWritableDatabase();
		Cursor cursor = getSMSInPhone();
		
		wDB.beginTransaction();
		if(cursor.moveToFirst()){

			int index_Address = cursor.getColumnIndex("address");
			int index_Person = cursor.getColumnIndex("person");
			int index_Body = cursor.getColumnIndex("body");
			int index_Date = cursor.getColumnIndex("date");
			int index_Type = cursor.getColumnIndex("type");
			
			do {
				String strAddress = cursor.getString(index_Address);
				String strPerson = cursor.getString(index_Person);
				String strBody = cursor.getString(index_Body);
				long longDate = cursor.getLong(index_Date);
				int intType = cursor.getInt(index_Type);

				String sqlInsert = "insert into " + LSQLiteHelper.SMSSQLTABLE + " values(null, ?, ?, ?, ?, ?)";
				Object[] bindArgs = new Object[]{ strAddress, strPerson, strBody, longDate, intType};
				wDB.execSQL(sqlInsert, bindArgs);
				
			} while (cursor.moveToNext());
		} 
		wDB.setTransactionSuccessful();
		wDB.endTransaction();
		
		if(!cursor.isClosed()){	// 关闭游标
			cursor.close();
			cursor = null;
		}
		
		if(wDB.isOpen()){		// 关闭数据库
			wDB.close();
			wDB = null;
		}
	}
	
	
	public Cursor querySMSFromDataBase(){
		rDB = sqlHelper.getReadableDatabase();
		
		String sqlSelect = "select * from " + LSQLiteHelper.SMSSQLTABLE + " order by " + LSQLiteHelper.SMSDATE + " desc";
		Cursor cursor = rDB.rawQuery(sqlSelect, null);
		
		return cursor;
	}

	public Cursor querySMSFromDataBase(int topSize){
		rDB = sqlHelper.getReadableDatabase();
		String sqlSelect = "";
		
		Cursor dbCountCursor = rDB.rawQuery("select count(*) from sms", null);
		dbCountCursor.moveToFirst();
		if (topSize < dbCountCursor.getInt(0)) {		// 不足 size 条短信，则取前 size 条
			sqlSelect = "select * from sms order by date desc limit " + topSize;
		} else {
			sqlSelect = "select * from sms order by date desc";
		}
		if(!dbCountCursor.isClosed()){
			dbCountCursor.close();
			dbCountCursor = null;
		}
		
		Cursor cursor = rDB.rawQuery(sqlSelect, null);
		return cursor;
	}
	
	public void closeReaderDB(){
		if(rDB.isOpen()){
			rDB.close();
			rDB = null;
		}
	}
}
