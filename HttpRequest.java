package com.guruinfo.scm.common.service;

import android.content.Context;
import android.os.StrictMode;

import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.Sharedpref;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HttpRequest {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    // Live Server
    //public static String domainURL = "http://192.168.0.41:8080/clicksproject/";
   /* public static String domainURL = "http://erp.eapinfra.com:8080/clicksproject/";
    public static String staticURL = "http://erp.eapinfra.com:8080/clicksproject/";
    public static String portalURL = "http://erp.eapinfra.com:8080/vendorportal/";
    public static String chatURL = "http://erp.eapinfra.com:8080/ErpServices/";
    public static String chatDomainURL = "http://192.168.0.41:8080/ErpServices/";*/
    //NEW
    /*public static String domainURL = "http://erp.eapinfra.com:8080/eapprojectlive/";
    public static String staticURL = "http://erp.eapinfra.com:8080/eapprojectlive/";
    public static String portalURL = "http://erp.eapinfra.com:8080/vendorportal/";
    public static String chatURL = "http://erp.eapinfra.com:8080/ErpServices/";
    public static String chatDomainURL = "http://192.168.0.41:8080/ErpServices/";*/
    //	Development Server

    public static String domainURL = "http://192.168.3.21:8080/ERP/";
    public static String staticURL = "http://192.168.3.21:8080/ERP/";
    public static String portalURL = "http://192.168.3.21:8080/portal/";
    public static String chatURL = "http://192.168.3.21:8080/ErpServices/";
    public static String chatDomainURL = "http://192.168.3.21:8080/ErpServices/";

    /* public static String domainURL = "http://192.168.3.32:9090/ERPLIVE/";
     public static String staticURL = "http://192.168.3.32:9090/ERPLIVE/";
     public static String portalURL = "http://192.168.3.21:8080/portal/";
     public static String chatURL = "http://erp.eapinfra.com:8080/ErpServices/";
     public static String chatDomainURL = "http://192.168.0.41:8080/ErpServices/";*/
    // Test Server
    /*public static String domainURL = "http://192.168.3.22:8080/eaptest/";
    public static String staticURL = "http://192.168.3.22:8080/eaptest/";
    public static String portalURL = "http://192.168.3.21:8080/portalTest/";
    public static String chatURL = "http://192.168.3.21:8080/ErpServices/";
    public static String chatDomainURL = "http://192.168.3.21:8080/ErpServices/";*/
    //	apportallive aperplive
    public static String postData(String URL, String paramJson, String url, Context mContext) throws Exception {
        // doCheck();
        // System.setProperty("http.keepAlive", "false");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost;
        httppost = new HttpPost(url);
        String ip = "'Connected_Ip':'" + url + "'";
        System.out.println("ip" + ip);

        paramJson = paramJson.substring(0, paramJson.length() - 1);
        paramJson = paramJson + "," + ip + "}";
        System.out.println("paramJsonafter-----" + paramJson);
        String SetServerString = "";
        try {
            if (paramJson.length() > 0) {
                System.out.println("paramJson----" + paramJson);
                JSONObject jsonParm = new JSONObject(paramJson);
                //jsonParm.put("app_type","1");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(jsonParm.length());
                @SuppressWarnings("unchecked")
                Iterator<String> subIter = jsonParm.keys();
                while (subIter.hasNext()) {
                    String key = subIter.next();
                    String paraStr = jsonParm.getString(key);
                    nameValuePairs.add(new BasicNameValuePair(key, paraStr));
                }
                if (Sharedpref.getPrefBoolean(mContext, "islogin")) {
                    Sharedpref.setPrefBoolean(mContext, "islogin", false);
                    Sharedpref.SetPrefString(mContext, "type", Sharedpref.GetPrefString(mContext, "UserType"));
                    nameValuePairs.add(new BasicNameValuePair("AId", "EX_AUTHENTICATOR"));
                } else {
                    if (Sharedpref.GetPrefString(mContext, "type").equalsIgnoreCase("employee")) {
                        nameValuePairs.add(new BasicNameValuePair("AId", "EX_SERVICES"));
                    }
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } else {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            SetServerString = httpclient.execute(httppost, responseHandler);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return SetServerString;
    }

    public static void doCheck() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}