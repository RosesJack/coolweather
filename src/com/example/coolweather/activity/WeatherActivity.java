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
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	/**
	 * ������ʾ��ǰ����
	 */
	private TextView currentDateText;
	
	/**
	 * �л����а�ť
	 */
	private Button switchButton;
	
	/**
	 * ˢ�°�ť
	 */
	private Button refreshButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//��ʼ���ؼ�
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
		//���ؼ����ž�ȥ��ѯ����
		if(!TextUtils.isEmpty(countycode)){
		publishText.setText("ͬ����...");
		//���ÿؼ����ɼ�
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		cityNameText.setVisibility(View.INVISIBLE);
		//��һ��дû��д���������ѯ����
		queryWeatherCode(countycode);
		}else{
			//û���ؼ����ž�ֱ����ʾ��������
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
			publishText.setText("ͬ����...");
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
	 * ��ѯ�ؼ���������Ӧ����������
	 * @param countyCode
	 * 					�ؼ�����
	 */
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city"+
	countyCode + ".xml";
		queryFromServer(address, "countryCode");
	}
	/**
	 * ��ѯ������������Ӧ������
	 * @param weatherCode
	 * 					��������
	 */
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/"+
	weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 * @param address
	 * 					�����ַ
	 * @param type
	 * 					�������Ż���������Ϣ
	 */
	private void queryFromServer(final String address, final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("countryCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//�ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if(array !=null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//�������������ص�������Ϣ
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
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
		
	}
	
	
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", " "));
		temp1Text.setText(prefs.getString("temp1", " "));
		temp2Text.setText(prefs.getString("temp2", " "));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		String textString = "����"+prefs.getString("publish_time","")+"����";
		publishText.setText(textString);
		Toast.makeText(this,textString , Toast.LENGTH_LONG).show();
		currentDateText.setText(prefs.getString("current_date", ""));
		//���ÿؼ��ɼ�
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	/**
	 * ���ذ��� ��������ѡ�����
	 */
//	@Override
//	public void onBackPressed() {
//		Intent intent =new Intent(this,ChooseAreaActivity.class);
//		intent.putExtra("BackToHome", true);
//		startActivity(intent);
//		finish();
//	}
	
}