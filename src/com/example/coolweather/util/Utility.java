package com.example.coolweather.util;

/**
 * ����������������������
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
					//�Ѵӷ������еõ���ʡ�������ݴ洢�����ݿ���
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
					//��һ�ΰ�Provinceд����Id ->���ǵ��µڶ������󣺵ڶ���ҳ��һֱ������
					city.setProvinceId(provinceId);
					//�ѽ���������ݴ洢��City����
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
			if(TextUtils.isEmpty(response)){
				String[] allCity = response.split(",");
				if(allCity != null && allCity.length > 0){
					for(String p : allCity){
						String [] arrayStrings = p.split("\\|");
						Country country = new Country();
						country.setCountryCode(arrayStrings[0]);
						country.setCountryName(arrayStrings[1]);
						country.setCityId(cityId);
						//�ѷ������õ����ؼ����ݴ洢�����ݿ���
						coolWeatherDB.saveCountry(country);
					}
				return true;
				}
			}
			return false;
	}
}
