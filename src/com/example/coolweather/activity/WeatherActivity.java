package com.example.coolweather.activity;

import com.example.coolweather.R;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	
	/**
	 * 切换城市按钮
	 */
	private Button switchButton;
	
	/**
	 * 刷新按钮
	 */
	private Button refreshButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//初始化控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText =(TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		
		switchButton = (Button) findViewById(R.id.switch_city);
		refreshButton = (Button) findViewById(R.id.refresh_weather);
		
		String countycode = getIntent().getStringExtra("country_code");
		//有县级代号就去查询天气
		if(!TextUtils.isEmpty(countycode)){
		publishText.setText("同步中...");
		//设置控件不可见
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		cityNameText.setVisibility(View.INVISIBLE);
		//第一次写没有写下面这个查询方法
		queryWeatherCode(countycode);
		}else{
			//没有县级代号就直接显示本地天气
			showWeather();
		}
		switchButton.setOnClickListener(this);
		refreshButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("BackToHome",true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 查询县级代号所对应的天气代号
	 * @param countyCode
	 * 					县级代号
	 */
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city"+
	countyCode + ".xml";
		queryFromServer(address, "countryCode");
	}
	/**
	 * 查询天气代号所对应的天气
	 * @param weatherCode
	 * 					天气代号
	 */
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/"+
	weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 * @param address
	 * 					网络地址
	 * @param type
	 * 					天气代号或者天气信息
	 */
	private void queryFromServer(final String address, final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("countryCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if(array !=null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new  Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				Log.d("debug_error", "Exception:"+Log.getStackTraceString(e));
				runOnUiThread(new  Runnable() {
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
		
	}
	
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", " "));
		temp1Text.setText(prefs.getString("temp1", " "));
		temp2Text.setText(prefs.getString("temp2", " "));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		String textString = "今天"+prefs.getString("publish_time","")+"发布";
		publishText.setText(textString);
		Toast.makeText(this,textString , Toast.LENGTH_LONG).show();
		currentDateText.setText(prefs.getString("current_date", ""));
		//设置控件可见
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	/**
	 * 返回按键 进入重新选择地区
	 */
//	@Override
//	public void onBackPressed() {
//		Intent intent =new Intent(this,ChooseAreaActivity.class);
//		intent.putExtra("BackToHome", true);
//		startActivity(intent);
//		finish();
//	}
	
}
