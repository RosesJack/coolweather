package com.example.coolweather.util;

/**
 * �������󹤾߰�
 * @author w
 *
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				URL url;
				try {
					url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					// ��������ʽ
					connection.setRequestMethod("GET");
					// ��������ʱ���� /ms
					connection.setConnectTimeout(8000);
					// �����ȡ��ʱ����/ms
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder sBuilder = new StringBuilder();
					String line;

					while ((line = reader.readLine()) != null) {
						sBuilder.append(line);
					}

					if (listener != null) {
						// �ص�onFinish����
						listener.onFinish(sBuilder.toString());
					}
				} catch (Exception e) {
					if(listener != null){
						//�ص�onError()����
						listener.onError(e);
					}
				}
			}
		}).start();
	}
}