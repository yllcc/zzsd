package cn.com.fotic.eimp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * 
 * @author liugj
 *
 */
public class HttpUtil {
	
	private static SslContextUtils sslContextUtils = new SslContextUtils();

	public static String sendXMLDataByPost(String postUrl, String xmlData) {
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(postUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

			if (httpConn instanceof HttpsURLConnection) {
				sslContextUtils.initHttpsConnect((HttpsURLConnection) httpConn);
			}
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Content-type", "text/xml");
			httpConn.setConnectTimeout(60000);
			httpConn.setReadTimeout(60000);
			// 发送请求
			httpConn.getOutputStream().write(xmlData.getBytes("utf-8"));
			httpConn.getOutputStream().flush();
			httpConn.getOutputStream().close();
			// 获取输入流
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));
			char[] buf = new char[1024];
			int length = 0;
			while ((length = reader.read(buf)) > 0) {
				sb.append(buf, 0, length);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
