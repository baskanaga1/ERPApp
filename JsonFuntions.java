package com.guruinfo.scm.common.service;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
public class JsonFuntions {
	private static final int TIMEOUT = 50000;
	// the timeout until a connection is established
	private static final int CONNECTION_TIMEOUT = 50000; 
	// the timeout for waiting for data
	private static final int SOCKET_TIMEOUT = 50000;
	// from ClientConnectionRequest
	private static final long MCC_TIMEOUT = 50000; 
	
	public final String getJSONfromURL(String url){
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		
		 DefaultHttpClient client;
		
	      //prepare for the https connection
	      //call this in the constructor of the class that does the connection if
	      //it's used multiple times
		 KeyStore trustStore;
		 SSLSocketFactory sf = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sf = new CustomSSLSocketFactory(trustStore);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        // Setting up parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.expect-continue", false);
        // Setting timeout
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        // Registering schemes for both HTTP and HTTPS
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));
        // Creating thread safe client connection manager
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
        // Creating HTTP client
        client = new DefaultHttpClient(ccm, params);
        // Registering user name and password for authentication
        client.getCredentialsProvider().setCredentials(
                new AuthScope(null, -1),
                new UsernamePasswordCredentials("", ""));	       		
		
		//http post
	    try{
			System.setProperty("http.keepAlive", "false");
	    	HttpGet get = new HttpGet(url);
	    	setTimeouts(get.getParams());
	    	HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
	    }catch(Exception e){
	     	Log.e("log_tag", "Error in http connection " + e.toString());
	    }
	    
	  //convert response to string
	    try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, HTTP.UTF_8),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
            	sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
	    }catch(Exception e){
	    	Log.e("log_tag", "Error converting result " + e.toString());
	    }
	    return result;
	}
	
	private static void setTimeouts(HttpParams params) {
	    params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
	        CONNECTION_TIMEOUT);
	    params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
	    params.setLongParameter(ConnManagerPNames.TIMEOUT, MCC_TIMEOUT);
	}
	
	private class CustomSSLSocketFactory extends SSLSocketFactory {
	    public SSLContext sslContext = SSLContext.getInstance("TLS");
	    public CustomSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);
	        TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	        }
	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }
	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        sslContext.init(null, new TrustManager[] {tm}, null);
	    }
	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }
	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
	
}
