package com.sueking.sub.filehash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class asd {

	public String subscribe(String dist, String userid, String password,
			String email, String name, String expirydate) throws Exception {
		String resp = "";
		String urlString = "https://<secureserver>/";
		URL url;
		URLConnection urlConn;
		DataOutputStream printout;
		DataInputStream input;
		String str = "";
		int flag = 1;

		try {
			Properties sysProperties = System.getProperties();
			// change proxy settings if required and enable the below lines
			// sysProperties.put("proxyHost", "proxy.starhub.net.sg");
			// sysProperties.put("proxyPort", "8080");
			// sysProperties.put("proxySet", "true");
			// Now you are telling the JRE to ignore the hostname
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					System.out.println("Warning: URL Host: " + urlHostName
							+ " vs. " + session.getPeerHost());
					return true;
				}
			};
			// Now you are telling the JRE to trust any https server.
			// If you know the URL that you are connecting to then this should
			// not be a problem
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);

			url = new URL(urlString);
			urlConn = url.openConnection();
			urlConn.setDoInput(true);
			Object object;
			urlConn.setUseCaches(false);

			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			input = new DataInputStream(urlConn.getInputStream());

			while (null != ((str = input.readLine()))) {
				if (str.length() > 0) {
					str = str.trim();
					if (!str.equals("")) {
						// System.out.println(str);
						resp += str;
					}
				}
			}
			input.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return resp;
	}

	// Just add these two functions in your program
	public static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

	private static void trustAllHttpsCertificates() throws Exception {

		// Create a trust manager that does not validate certificate chains:

		javax.net.ssl.TrustManager[] trustAllCerts =

		new javax.net.ssl.TrustManager[1];

		javax.net.ssl.TrustManager tm = new miTM();

		trustAllCerts[0] = tm;

		javax.net.ssl.SSLContext sc =

		javax.net.ssl.SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, null);

		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

		sc.getSocketFactory());

	}

}
