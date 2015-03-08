package com.sueking.sub.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sueking.sub.filehash.FileHashCalculator;

public class SubDownloader {
	
	private static final String REQUEST_ADDRESS = "https://www.shooter.cn/api/subapi.php";
	
	public static final String SUB_LANG_ENG = "eng";
	public static final String SUB_LANG_CHN = "chn";
	

	public static void download(String fileName, String subLang) throws Exception {
		
		CloseableHttpClient httpClient = getHttpClient();
		
		SubInfo[] subInfos = getSubInfos(fileName, httpClient, subLang);
		
		httpClient.close();
		
		int index = 1;
		for (SubInfo subInfo : subInfos) {
			System.out.println(subInfo);
			for (FileInfo fileInfo : subInfo.Files) {
				downloadSubFile(fileName, fileInfo, subLang, index++);
			}
		}
	}

	private static void downloadSubFile(String filePath, FileInfo fileInfo, String subLang, int index) throws IOException, Exception  {
		File file = new File(filePath);
		String fileName = file.getName();
		String fileNameNoSuffix = fileName.substring(0, fileName.lastIndexOf('.'));
		String directory = file.getParent();
		String subFileName = buildSubFileName(fileInfo, subLang, index, fileNameNoSuffix);
		File subFile = new File(directory, subFileName);
		subFile.createNewFile();
		CloseableHttpClient client = getHttpClient();
		HttpGet httpGet = new HttpGet(fileInfo.Link);
		HttpResponse response = client.execute(httpGet);
		response.getEntity().writeTo(new FileOutputStream(subFile));
		client.close();
		System.out.println(subFile.getName() + "has been downloaded");
	}

	private static String buildSubFileName(FileInfo fileInfo, String subLang,
			int index, String fileNameNoSuffix) {
		StringBuilder subFileName = new StringBuilder();
		subFileName.append(fileNameNoSuffix);
		subFileName.append('.');
		subFileName.append(subLang);
		if (index > 1) {
			subFileName.append(index);
		}
		subFileName.append('.');
		subFileName.append(fileInfo.Ext);
		return subFileName.toString();
	}

	private static SubInfo[] getSubInfos(String fileName,
			CloseableHttpClient httpClient, String subLang) throws Exception,
			UnsupportedEncodingException, IOException, ClientProtocolException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("filehash", FileHashCalculator.getHash(fileName)));
		formparams.add(new BasicNameValuePair("pathinfo", fileName));
		formparams.add(new BasicNameValuePair("format", "json"));
		formparams.add(new BasicNameValuePair("lang", subLang));
		URLEncodedUtils.format(formparams, Consts.UTF_8);
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
		HttpPost httppost = new HttpPost(REQUEST_ADDRESS);
		httppost.setEntity(entity);
		SubInfo[] subInfos = httpClient.execute(httppost, new ResponseHandler<SubInfo[]>(){

			public SubInfo[] handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				StatusLine statusLine = response.getStatusLine();

				HttpEntity entity = response.getEntity();
				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(statusLine.getStatusCode(),
							statusLine.getReasonPhrase());
				}
				if (entity == null) {
					throw new ClientProtocolException(
							"Response contains no content");
				}
				Gson gson = new GsonBuilder().create();
				ContentType contentType = ContentType.getOrDefault(entity);
				Charset charset = contentType.getCharset();
				Reader reader = new InputStreamReader(entity.getContent(), charset);
				return gson.fromJson(reader, SubInfo[].class);
			}
			
		});
		return subInfos;
	}

	private static CloseableHttpClient getHttpClient()
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException {
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
		return httpClient;
	}

//	public static void main(String[] args) throws Exception {
//		download(args[0], SUB_LANG_CHN);
//		download(args[0], SUB_LANG_ENG);
//	}
	
	static class FileInfo{
		String Ext;
		String Link;
		@Override
		public String toString() {
			return "FileInfo [Ext=" + Ext + ", Link=" + Link + "]";
		}
	}
	
	static class SubInfo{
		String Desc;
		int Delay;
		FileInfo[] Files;
		@Override
		public String toString() {
			return "SubInfo [Desc=" + Desc + ", Delay=" + Delay + ", Files="
					+ Arrays.toString(Files) + "]";
		}
	}

}
