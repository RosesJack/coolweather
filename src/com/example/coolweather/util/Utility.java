package com.example.coolweather.util;

/**
 * 解析服务器传回来的数据
 * 
 * @author w
 * 
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.coolweather.model.City;
import com.example.coolweather.model.Country;
import com.example.coolweather.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {
	/**
	 * 解析和处理服务器传来的省级数据
	 * 
	 * @param coolWeatherDB
	 *            实例化的数据库工具对象
	 * @param response
	 *            服务器得到的装换后String数据
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");
			if (allProvince != null && allProvince.length > 0) {
				for (String p : allProvince) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 把从服务器中得到的省级的数据存储到数据库中
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理从服务器传来的市级数据
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCity = response.split(",");
			if (allCity != null && allCity.length > 0) {
				for (String p : allCity) {
					String[] array = p.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					// 第一次把Province写成了Id ->这是导致第二个错误：第二级页面一直进度条
					city.setProvinceId(provinceId);
					// 把解析后的数据存储到City表中
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器传来的县级数据
	 */
	public static boolean handleContiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {// 这里第一次写的时候没有加上非“!”导致程序一直在进度条不动
			String[] allCity = response.split(",");
			if (allCity != null && allCity.length > 0) {
				for (String p : allCity) {
					String[] arrayStrings = p.split("\\|");
					Country country = new Country();
					country.setCountryCode(arrayStrings[0]);
					country.setCountryName(arrayStrings[1]);
					country.setCityId(cityId);
					// 把服务器得到的县级数据存储到数据库中
					coolWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 * 
	 * @param context
	 *            传入的上下文对象
	 * @param response
	 *            传人的待解析字符串
	 */
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */

	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected",true);
		editor.putString("city_name", cityName);
		editor.putString("weatherCode",weatherCode);
		editor.putString("temp1",temp1);
		editor.putString("temp2",temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
