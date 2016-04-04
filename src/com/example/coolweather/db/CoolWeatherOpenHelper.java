package com.example.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	/**
	 * Province ����
	 */
	public static final String CREATE_PROVINCE = "create table Province("
			+ "id integer primary key autoincrement," + "city_name text,"
			+ "city_code text" + "province_id integer)";

	/**
	 * City ����
	 */
	public static final String CREATE_CITY = "create table City ("
			+ " id integer primary key autoincrement ," + "city_name  text ,"
			+ "city_code text," + "province integer	)";
	/**
	 * Country ����
	 */
	public static final String CREATE_COUNTRY = "create table Country("
			+ "id integer primary key autoincrement," + "country_name text,"
			+ "country_code text" + "country_id integer)";

	/**
	 * ��д���췽��
	 * 
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * ��д������ PS:�ڼ�⵽û����Щ������ʱ�򣬵���onCreate����������Щ��
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * ����������
		 */
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTRY);
		db.execSQL(CREATE_PROVINCE);
	}

	/**
	 * ��д���ݿ��������� PS:ֻҪ�����newVersion ���ڵ�ǰ�汾�žͻ�ִ���������
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
