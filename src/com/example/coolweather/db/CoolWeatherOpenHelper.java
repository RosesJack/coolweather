package com.example.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	/**
	 * Province 表建表
	 */
	public static final String CREATE_PROVINCE = "create table Province("
			+ "id integer primary key autoincrement," + "city_name text,"
			+ "city_code text" + "province_id integer)";

	/**
	 * City 建表
	 */
	public static final String CREATE_CITY = "create table City ("
			+ " id integer primary key autoincrement ," + "city_name  text ,"
			+ "city_code text," + "province integer	)";
	/**
	 * Country 建表
	 */
	public static final String CREATE_COUNTRY = "create table Country("
			+ "id integer primary key autoincrement," + "country_name text,"
			+ "country_code text" + "country_id integer)";

	/**
	 * 重写构造方法
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
	 * 重写建表方法 PS:在检测到没有这些表名的时候，调用onCreate方法创建这些表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * 创建三个表
		 */
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTRY);
		db.execSQL(CREATE_PROVINCE);
	}

	/**
	 * 重写数据库升级方法 PS:只要传入的newVersion 大于当前版本号就会执行这个方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
