package com.guruinfo.scm.common.service;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.view.ViewGroup;
import com.guruinfo.scm.R;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.irozon.sneaker.Sneaker;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.guruinfo.scm.common.AppContants.DASHBOARDOFFLINEMODE;
/**
 * Created by Kannan G on 03/06/2017
 */
public class RestClientHelper {
    private static final String LOG_TAG = "RestClientHelper";
    public static String defaultBaseUrl = "";
    private static final Object lockObject = new Object();
    private static RestClientHelper restClientHelper;
    private final Handler handler = new Handler(Looper.getMainLooper());
    Context mContext;
    private RestClientHelper() {
    }
    public interface RestClientListener {
        void onSuccess(String response);
        void onError(String error);
    }
    public static RestClientHelper getInstance() {
        if (restClientHelper == null)
            synchronized (lockObject) {
                if (restClientHelper == null)
                    restClientHelper = new RestClientHelper();
            }
        return restClientHelper;
    }
    private final Executor executor;
    {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
                5L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull final Runnable r) {
                        return new Thread(r, LOG_TAG + "Thread");
                    }
                });
        executor.allowCoreThreadTimeOut(true);
        this.executor = executor;
    }
    private void addHeaders(Request.Builder builder, @NonNull ArrayMap<String, String> headers) {
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
    }
    public void get(@NonNull String serviceUrl, Context context, @NonNull RestClientListener restClientListener) {
        get(serviceUrl, null, null, restClientListener);
        this.mContext = context;
    }
    public void getURL(@NonNull String serviceUrl, Context context, @NonNull RestClientListener restClientListener) {
        this.mContext = context;
        getURL(serviceUrl, null, null, restClientListener);
    }
    public void getChatURL(@NonNull String serviceUrl, Context context, @NonNull RestClientListener restClientListener) {
        this.mContext = context;
        getChatURL(serviceUrl, null, null, restClientListener);
    }
    public void get(@NonNull String serviceUrl, ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        get(serviceUrl, null, params, restClientListener);
    }
    public void get(@NonNull String serviceUrl, ArrayMap<String, String> headers, ArrayMap<String, Object> params, RestClientListener restClientListener) {
        final Request.Builder builder = new Request.Builder();
        if (headers != null)
            addHeaders(builder, headers);
        builder.url(generateUrlParams(serviceUrl, params));
        execute(builder, restClientListener);
    }
    public void getURL(@NonNull String serviceUrl, ArrayMap<String, String> headers, ArrayMap<String, Object> params, RestClientListener restClientListener) {
        final Request.Builder builder = new Request.Builder();
        serviceUrl = ApiCalls.getURLfromJson(serviceUrl, mContext);
        System.out.println("Request--> " + serviceUrl);
        if (headers != null)
            addHeaders(builder, headers);
        builder.url(generateUrlParams(serviceUrl, params));
        execute(builder, restClientListener);
    }
    public void getChatURL(@NonNull String serviceUrl, ArrayMap<String, String> headers, ArrayMap<String, Object> params, RestClientListener restClientListener) {
        final Request.Builder builder = new Request.Builder();
        serviceUrl = ApiCalls.getChatURLfromJson(serviceUrl,mContext);
        System.out.println("Request--> " + serviceUrl);
        if (headers != null)
            addHeaders(builder, headers);
        builder.url(generateUrlParams(serviceUrl, params));
        execute(builder, restClientListener);
    }
    public void post(@NonNull Context context, @NonNull JSONObject params, @NonNull RestClientListener restClientListener) {
        post(params, restClientListener);
        this.mContext = context;
    }
    public void post(@NonNull JSONObject params, @NonNull RestClientListener restClientListener) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpRequest.staticURL).newBuilder();
        Iterator<String> subIter = params.keys();
        while (subIter.hasNext()) {
            String key = subIter.next();
            try {
                String paraStr = params.getString(key);
                urlBuilder.addQueryParameter(key, paraStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        postExecute(request, restClientListener);
    }
    public void put(@NonNull String serviceUrl, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        put(serviceUrl, null, params, restClientListener);
    }
    public void put(@NonNull String serviceUrl, ArrayMap<String, String> headers, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        final Request.Builder builder = new Request.Builder();
        if (headers != null)
            addHeaders(builder, headers);
        StringBuffer urls = new StringBuffer();
        if (defaultBaseUrl.length() > 0)
            urls.append(defaultBaseUrl);
        urls.append(serviceUrl);
        builder.url(urls.toString());
        builder.put(generateRequestBody(params));
        execute(builder, restClientListener);
    }
    public void delete(@NonNull String serviceUrl, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        delete(serviceUrl, null, params, restClientListener);
    }
    public void delete(@NonNull String serviceUrl, ArrayMap<String, String> headers, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        final Request.Builder builder = new Request.Builder();
        if (headers != null)
            addHeaders(builder, headers);
        StringBuffer urls = new StringBuffer();
        if (defaultBaseUrl.length() > 0)
            urls.append(defaultBaseUrl);
        urls.append(serviceUrl);
        builder.url(urls.toString());
        builder.delete(generateRequestBody(params));
        execute(builder, restClientListener);
    }
    public void postMultipart(@NonNull String serviceUrl, @NonNull ArrayMap<String, File> files, @NonNull RestClientListener restClientListener) {
        postMultipart(serviceUrl, null, null, files, restClientListener);
    }
    public void postMultipart(@NonNull String serviceUrl, ArrayMap<String, Object> params, @NonNull ArrayMap<String, File> files, @NonNull RestClientListener restClientListener) {
        postMultipart(serviceUrl, null, params, files, restClientListener);
    }
    public void postMultipart(@NonNull String serviceUrl, ArrayMap<String, String> headers, ArrayMap<String, Object> params, @NonNull ArrayMap<String, File> files, @NonNull RestClientListener restClientListener) {
        final Request.Builder builder = new Request.Builder();
        if (headers != null)
            addHeaders(builder, headers);
        StringBuffer urls = new StringBuffer();
        if (defaultBaseUrl.length() > 0)
            urls.append(defaultBaseUrl);
        urls.append(serviceUrl);
        builder.url(urls.toString());
        builder.post(generateMultipartBody(params, files));
        execute(builder, restClientListener);
    }
    public void setToast(String str) {
        int headerSize, msgSize;
        if (mContext.getResources().getBoolean(R.bool.isTablet)) {
            headerSize = 25;
            msgSize = 20;
        } else {
            headerSize = 18;
            msgSize = 14;
        }
        Sneaker.with((Activity) mContext)
                .setTextSize(headerSize, msgSize)
                .setTitle("Message", R.color.black) // Title and title color
                .setMessage(str, R.color.content_gray) // Message and message color
                .setDuration(4000) // Time duration to show
                .autoHide(true) // Auto hide Sneaker view
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT) // Height of the Sneaker layout
                .setIcon(R.drawable.progress_bar_icon) // Icon, icon tint color and circular icon view
                .sneak(R.color.toast_bg);
    }
    private void execute(final Request.Builder builder, final RestClientListener restClientListener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES).readTimeout(2, TimeUnit.MINUTES).build();
                try {
                   // if (!(Sharedpref.getPrefBoolean(mContext, DASHBOARDOFFLINEMODE))) {
                        if (BaseActivity.isInternetAvailable()) {
                            final Response response = client.newCall(builder.build()).execute();
                            final String responseData = response.body().string();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.code() == 200) {
                                        restClientListener.onSuccess(responseData);
                                    } else {
                                        restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                                    }
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    restClientListener.onError(mContext.getResources().getString(R.string.no_net));
                                }
                            });
                        }
                  /*  } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                restClientListener.onError("Offline Mode Enabled...");
                            }
                        });
                    }*/
                } catch (HttpResponseException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                }
            }
        });
    }
    private void postExecute(final Request request, final RestClientListener restClientListener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES).writeTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build();
                try {
                    if (BaseActivity.isInternetAvailable()) {
                        final Response response = client.newCall(request).execute();
                        final String responseData = response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.code() == 200) {
                                    restClientListener.onSuccess(responseData);
                                } else {
                                    restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                                }
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                restClientListener.onError(mContext.getResources().getString(R.string.no_net));
                            }
                        });
                    }
                } catch (HttpResponseException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            restClientListener.onError(mContext.getResources().getString(R.string.no_server));
                        }
                    });
                }
            }
        });
    }
    private String generateUrlParams(String serviceUrl, ArrayMap<String, Object> params) {
        final StringBuffer urls = new StringBuffer();
        if (defaultBaseUrl.length() > 0)
            urls.append(defaultBaseUrl);
        urls.append(serviceUrl);
        if (params != null) {
            int i = 0;
            for (String key : params.keySet()) {
                if (i == 0) {
                    urls.append("?" + key + "=" + params.get(key));
                } else {
                    urls.append("&" + key + "=" + params.get(key));
                }
                i++;
            }
        }
        return urls.toString();
    }
    private RequestBody generateRequestBodyJson(JSONObject jsonObj) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; "), jsonObj.toString());
        return requestBody;
    }
    private RequestBody generateRequestBody(ArrayMap<String, Object> params) {
        final JSONObject jsonObj = new JSONObject();
        if (params != null) {
            for (String key : params.keySet()) {
                try {
                    jsonObj.put(key, params.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; "), jsonObj.toString());
        return requestBody;
    }
    private RequestBody generateMultipartBody(ArrayMap<String, Object> params, ArrayMap<String, File> files) {
        final MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, String.valueOf(params.get(key)));
            }
        }
        if (files != null) {
            for (String key : files.keySet()) {
                builder.addFormDataPart(key, key, RequestBody.create(MediaType.parse("image/png"), files.get(key)));
            }
        }
        return builder.build();
    }
}
