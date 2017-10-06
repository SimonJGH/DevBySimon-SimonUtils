package com.simon.utils.xutis3;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.simon.utils.MyApplication;
import com.simon.utils.utils.NetworkUtils;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;
import org.xutils.common.Callback.Cancelable;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Simon on 2017/10/2.
 * <p>
 * 对xutils3.0简单封装，需要获得MyApplication的context，可以设置https和http两种请求，使用时要配合NetworkUtils一起使用！
 */

@SuppressWarnings("all")
public class XHttps {

    /*Https 证书验证对象*/
    private static SSLContext s_sSLContext = null;
    /*储存cookie*/
    public static String PREFERENCE_NAME = "COOKIES";

    private XHttps() {
    }

    public static XHttps getInstance() {
        return SafeMode.mXHttps;
    }

    /**
     * static final 保证了实例的唯一和不可改变
     */
    private static class SafeMode {
        private static final XHttps mXHttps = new XHttps();
    }

    /**
     * https get请求
     *
     * @param params    请求参数 get请求使用 addQueryStringParameter方法添加参数
     * @param mCallback 回调对象
     * @return 网络请求的Cancelable 可以中断请求
     */
    public Cancelable GET(RequestParams params, boolean cookie, final XHttpsCallBack mCallback) {
        return HttpRequest(HttpMethod.GET, params, cookie, mCallback);
    }

    /**
     * https post请求
     *
     * @param params    请求参数 post请求使用 addBodyParameter方法添加参数
     * @param mCallback 回调对象
     * @return 网络请求的Cancelable 可以中断请求
     */
    public Cancelable POST(RequestParams params, boolean cookie, final XHttpsCallBack mCallback) {
        return HttpRequest(HttpMethod.POST, params, cookie, mCallback);
    }

    /**
     * 异步Https请求
     *
     * @param method   GET/POST
     * @param params   RequestParams
     * @param cookie   true=https  false=http
     * @param callBack
     * @return Cancelable终止网络请求
     */
    private Cancelable HttpRequest(HttpMethod method, RequestParams params, boolean cookie, final XHttpsCallBack callBack) {
        /**网络请求之前先检查网络是否可用*/
        if (!NetworkUtils.isMobileConnected(MyApplication.getInstance())) {
            callBack.onFinished();
            callBack.onFailure("网络连接失败，请重试");
            Toast.makeText(MyApplication.getInstance(), "请检查网络！", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (params == null) {
            params = new RequestParams();
        }
        // 为请求添加缓存时间
        params.setCacheMaxAge(1000 * 0);
        // 超时时间60s
        params.setConnectTimeout(60 * 1000);
        // 有上传文件时使用multipart表单, 否则上传原始文件流.getAbsoluteFile
        params.setMultipart(true);
        /** Https请求设置 */
        if (cookie) {
            // 设置Cookie
            params.setHeader("Cookie", getCookie());
            // 判断https证书是否成功验证
            SSLContext sslContext = getSSLContext(MyApplication.getInstance());
            if (null == sslContext) {
                Log.i("Simon", "SSLContext  --  验证SSL证书失败");
                return null;
            }
            // 绑定SSL证书(https请求)
            params.setSslSocketFactory(sslContext.getSocketFactory());
        }
        Cancelable cancelable = x.http().request(method, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                callBack.onResponse(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callBack.onFailure(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                callBack.onFinished();
            }
        });

        return cancelable;
    }

    /**
     * 异步Https文件下载
     *
     * @param filePath 文件储存路径
     * @param params   RequestParams
     * @param cookie   true=https  false=http
     * @param callback
     * @return Cancelable终止网络请求
     */
    public Callback.Cancelable downloadFile(String filePath, RequestParams params, boolean cookie, final XHttpsDownLoadCallBack callBack) {
        /**网络请求之前先检查网络是否可用*/
        if (!NetworkUtils.isMobileConnected(MyApplication.getInstance())) {
            callBack.onFinished();
            callBack.onFailure("网络连接失败，请重试");
            Toast.makeText(MyApplication.getInstance(), "请检查网络！", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (params == null) {
            params = new RequestParams();
        }
        // 为请求添加缓存时间
        params.setCacheMaxAge(1000 * 0);
        // 超时时间60s
        params.setConnectTimeout(60 * 1000);
        // 有上传文件时使用multipart表单, 否则上传原始文件流.getAbsoluteFile
        params.setMultipart(true);
        /** Https请求设置 */
        if (cookie) {
            // 设置Cookie
            params.setHeader("Cookie", getCookie());
            // 判断https证书是否成功验证
            SSLContext sslContext = getSSLContext(MyApplication.getInstance());
            if (null == sslContext) {
                Log.i("Simon", "SSLContext  --  验证SSL证书失败");
                return null;
            }
            // 绑定SSL证书(https请求)
            params.setSslSocketFactory(sslContext.getSocketFactory());
        }
        // 断点续传
        params.setAutoRename(true);
        params.setSaveFilePath(filePath);
        Cancelable cancelable = x.http().post(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                callBack.onResponse(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callBack.onFailure(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                callBack.onFinished();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                callBack.onLoading(total, current, isDownloading);
            }
        });
        return cancelable;
    }


    /**
     * 中断网络请求
     *
     * @param mCancelable Cancelable
     */
    public <T> void interrupt(Cancelable mCancelable) {
        if (mCancelable != null && !mCancelable.isCancelled()) {
            mCancelable.cancel();
        }
    }


    /**
     * 网络请求回掉
     */
    public interface XHttpsCallBack {
        void onResponse(String result);

        void onFailure(String result);

        void onFinished();
    }

    /**
     * 下载文件回掉
     */
    public interface XHttpsDownLoadCallBack extends XHttpsCallBack {
        void onResponse(File result);

        void onLoading(long total, long current, boolean isDownloading);

        void onFailure(String result);

        void onFinished();
    }

    /**
     * 获取cookie
     *
     * @return
     */
    private String getCookie() {
        SharedPreferences cookies = MyApplication.getInstance().getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        return cookies.getString("cookie", "");
    }

    /**
     * 获取Https的证书
     *
     * @param context Activity（fragment）的上下文
     * @return SSL的上下文对象
     */
    private SSLContext getSSLContext(Context context) {
        CertificateFactory certificateFactory = null;
        InputStream inputStream = null;
        Certificate cer = null;
        KeyStore keystore = null;
        TrustManagerFactory trustManagerFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            /**这里导入SSL证书文件*/
            inputStream = context.getAssets().open("baidu.crt");
            /*读取证书*/
            try {
                cer = certificateFactory.generateCertificate(inputStream);
                Log.i("Simon", "读取证书 = " + cer.getPublicKey().toString());
            } finally {
                inputStream.close();
            }
           /*创建一个证书库，并将证书导入证书库*/
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            /*双向验证时使用*/
            keystore.load(null, null);
            keystore.setCertificateEntry("trust", cer);
            /*实例化信任库*/
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            /*初始化信任库*/
            trustManagerFactory.init(keystore);

            s_sSLContext = SSLContext.getInstance("TLS");
            s_sSLContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            /*信任所有证书 （官方不推荐使用）*/
            /*s_sSLContext.init(null, new TrustManager[]{new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {

                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {

                }
            }}, new SecureRandom());*/
            return s_sSLContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
