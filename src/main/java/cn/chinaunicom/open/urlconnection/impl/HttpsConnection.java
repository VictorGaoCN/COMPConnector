package cn.chinaunicom.open.urlconnection.impl;

import cn.chinaunicom.open.common.Const;
import cn.chinaunicom.open.common.utils.JsonUtil;
import cn.chinaunicom.open.common.utils.StringUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

/**
 * 忽略证书校验的HTTPS连接
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public class HttpsConnection extends AbstractConnection {

    public HttpsConnection() {
        super();
    }

    public HttpsConnection(String method, String url, Map httpRequestHeaders) {
        super(method, url, httpRequestHeaders);
    }

    public HttpsConnection(String method, String url, Map<String, Object> params, Map httpRequestHeaders) {
        super(method, url, JsonUtil.map2JsonString(params), httpRequestHeaders);
    }

    public HttpsConnection(String method, String url, String requestContext, Map httpRequestHeaders) {
        super(method, url, requestContext, httpRequestHeaders);
    }

    static {
        try {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new HostnameVerifier() {
                        public boolean verify(String urlHostName, SSLSession session){
                            return true;
                        }
                    }
            );
        } catch (Exception e)  {}
    }

    HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
        public boolean verify(String s, SSLSession sslsession) {
            return true;
        }
    };

    private static void trustAllHttpsCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[1];
        trustAllCerts[0] = new TrustAllManager();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public String doConnect() throws IOException {
        URL reqURL = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection)reqURL.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(method);
        connection.setUseCaches(false);
        Set<String> keys = httpRequestHeaders.keySet();
        for(String key : keys) {
            connection.setRequestProperty(key, httpRequestHeaders.get(key));
        }
        connection.setHostnameVerifier(ignoreHostnameVerifier);
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(createSSL());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        connection.connect();
        boolean enableRequestBody = (method.equals(Const.HttpMethodType.POST) || method.equals(Const.HttpMethodType.PUT)) && paramString != null;
        if (enableRequestBody) {
            OutputStream out = connection.getOutputStream();
            String content = paramString;
            out.write(content.getBytes("UTF-8"));
            out.flush();
            out.close();
        }
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String responeStr = StringUtil.readStream(connection.getInputStream());
            return responeStr;
        }else{
            throw new IOException("请求失败 HTTP CODE:" + connection.getResponseCode());
        }
    }

    private static SSLSocketFactory createSSL() throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException {
        TrustManager[] tm = new TrustManager[]{myX509TrustManager};
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, tm, null);
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        return ssf;
    }

    /**
     * https的SSL，去掉证书校验功能
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static TrustManager myX509TrustManager = new X509TrustManager() {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    };

    /**
     *
     */
    private static class TrustAllManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }
        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }
    }
}
