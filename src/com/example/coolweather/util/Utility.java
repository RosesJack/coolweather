package com.example.coolweather.util;

/**
 * 解析服务器传回来的数据
 * 
 * @author w
 * 
 */

import com.example.coolweather.model.City;
import com.example.coolweather.model.Country;
import com.example.coolweather.model.Province;

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
					//把从服务器中得到的省级的数据存储到数据库中
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
					//第一次把Province写成了Id ->这是导致第二个错误：第二级页面一直进度条
					city.setProvinceId(provinceId);
					//把解析后的数据存储到City表中
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
			if(TextUtils.isEmpty(response)){
				String[] allCity = response.split(",");
				if(allCity != null && allCity.length > 0){
					for(String p : allCity){
						String [] arrayStrings = p.split("\\|");
						Country country = new Country();
						country.setCountryCode(arrayStrings[0]);
						country.setCountryName(arrayStrings[1]);
						country.setCityId(cityId);
						//把服务器得到的县级数据存储到数据库中
						coolWeatherDB.saveCountry(country);
					}
				return true;
				}
			}
			return false;
	}
}
