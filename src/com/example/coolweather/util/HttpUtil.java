package com.example.coolweather.util;

/**
 * 网络请求工具包
 * @author w
 *
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.text.TextUtils;
import java.net.SocketTimeoutException;
import java.io.EOFException;


public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL	url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					// 网络请求方式
					connection.setRequestMethod("GET");
					// 网络请求超时限制 /ms
					connection.setConnectTimeout(8000);
					// 网络读取超时限制/ms
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder sBuilder = new StringBuilder();
					String line = null;

					while ((line = reader.readLine()) != null ) {
						sBuilder.append(line);
					}

					if (listener != null) {
						// 回调onFinish方法
//						String string = sBuilder.toString();
//						if(TextUtils.isEmpty(string)){
//							string="21|bineg";
//						}
//						listener.onFinish(string);
//						System.out.println("这是onfinish后,传过去的string是："+string);
						listener.onFinish(sBuilder.toString());
					}
				} catch (Exception e) {
					if(listener != null){
						//回调onError()方法
						listener.onError(e);
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
