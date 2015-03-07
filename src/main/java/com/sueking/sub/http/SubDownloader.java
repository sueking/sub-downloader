package com.sueking.sub.http;

import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import com.sueking.sub.filehash.FileHashCalculator;

public class SubDownloader {
	
	private static final String REQUEST_ADDRESS = "https://www.shooter.cn/api/subapi.php";

	public static void download(String fileName) throws Exception {
		File file = new File(fileName);
		File directory = file.getParentFile();

		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				return true;
			}
		}).build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
				.build();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("filehash", FileHashCalculator.getHash(fileName)));
		formparams.add(new BasicNameValuePair("pathinfo", fileName));
		formparams.add(new BasicNameValuePair("format", "json"));
		URLEncodedUtils.format(formparams, Consts.UTF_8);
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
		HttpPost httppost = new HttpPost(REQUEST_ADDRESS);
		httppost.setEntity(entity);
		CloseableHttpResponse response = httpClient.execute(httppost);
		System.out.println(response);
		// http请求
		// 获得response
		// 解析
		// 保存
	}

	public static void main(String[] args) throws Exception {
		String filePath = ClassLoader.getSystemResource("testidx.avi").getFile();
		download(filePath);
	}

}
