package com.example.coolweather.activity;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.R;
import com.example.coolweather.model.City;
import com.example.coolweather.model.Country;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.CoolWeatherDB;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	// 省列表
	private List<Province> provinceList;
	// 市列表
	private List<City> cityList;
	// 县列表
	private List<Country> countriesList;
	// 选中的省份
	private Province selectedProvince;
	// 选中的城市
	private City selectedCity;
	// 当前选中的级别
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = coolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					// 加载市级数据
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					// 加载县级数据
					queryCounties();
				}
			}
		});
		// 加载省级数据
		queryProvinces();
	}

	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到就从服务器上查询
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvince();
		if (provinceList != null && provinceList.size() > 0) {
			// 集合中清除数据
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			// 更新数据适配器
			adapter.notifyDataSetChanged();

			// listview.setselection(position)，表示将列表移动到指定的Position处。
			// 举例说明：
			// 1、listView中有100条记录，如果想定位到某一条上面去就可以直接调用listView.setSelection(position);
			// 2、记录listView滚动到的position的坐标，然后利用listView.scrollTo精确的进行恢复
			// 3、记录listView显示在屏幕上的item的位置，然后利用listView.setSelection恢复
			// 4、通知listView的适配器数据变更，这种适用于listView追加数据的情况，严格说不是恢复listView滚动的位置，只是保持滚动位置不错
			listView.setSelection(0);
			titleText.setText("中国");

			// 用于级别限定来进行不同级别的分类从而实现在不同分类下的点击
			currentLevel = LEVEL_PROVINCE;
		} else {
			// 从服务器上搜索
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询选中省下的所有市，优先从数据库查询，如果没有查询到就从数据库中进行查询
	 */

	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList != null && cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询选中市下的所有县，优先从数据库中查询，如果没有查询到就从数据库中进行查询
	 */
	private void queryCounties() {
		countriesList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countriesList != null && countriesList.size() > 0) {
			dataList.clear();
			for (Country country : countriesList) {
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	/**
	 * 从数据库中进行搜索
	 * 
	 * @param code
	 *            地点对应的编码
	 * @param type
	 *            三个级别的类型判断
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		// 进度对话框
		showProgressDialog();

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = Utility.handleContiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}

				if (result) {
					// 通过runOnUIThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("country".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runONUiThread()
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT);
					}
				});
			}
		});
	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}

	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
	 */
	@Override
	public void onBackPressed() {
//		super.onBackPressed();//不用父类返回方法
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		}else {
			finish();
		}
	}
}
