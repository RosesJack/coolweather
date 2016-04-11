package com.example.coolweather.util;

/**
 * ����������������������
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
	 * �����ʹ��������������ʡ������
	 * 
	 * @param coolWeatherDB
	 *            ʵ���������ݿ⹤�߶���
	 * @param response
	 *            �������õ���װ����String����
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
					// �Ѵӷ������еõ���ʡ�������ݴ洢�����ݿ���
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ���ӷ������������м�����
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
					// ��һ�ΰ�Provinceд����Id ->���ǵ��µڶ������󣺵ڶ���ҳ��һֱ������
					city.setProvinceId(provinceId);
					// �ѽ���������ݴ洢��City����
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ���������������ؼ�����
	 */
	public static boolean handleContiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {// �����һ��д��ʱ��û�м��Ϸǡ�!�����³���һֱ�ڽ���������
			String[] allCity = response.split(",");
			if (allCity != null && allCity.length > 0) {
				for (String p : allCity) {
					String[] arrayStrings = p.split("\\|");
					Country country = new Country();
					country.setCountryCode(arrayStrings[0]);
					country.setCountryName(arrayStrings[1]);
					country.setCityId(cityId);
					// �ѷ������õ����ؼ����ݴ洢�����ݿ���
					coolWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 * 
	 * @param context
	 *            ����������Ķ���
	 * @param response
	 *            ���˵Ĵ������ַ���
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
	 * �����������ص�����������Ϣ�洢��SharedPreferences�ļ���
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyy��M��d��", Locale.CHINA);
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
