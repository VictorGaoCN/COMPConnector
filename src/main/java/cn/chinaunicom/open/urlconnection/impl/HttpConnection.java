package cn.chinaunicom.open.urlconnection.impl;

import cn.chinaunicom.open.common.CompLogger;
import cn.chinaunicom.open.common.Const;
import cn.chinaunicom.open.common.utils.JsonUtil;
import cn.chinaunicom.open.common.utils.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * HTTP连接
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public class HttpConnection extends AbstractConnection {

    public HttpConnection() {
        super();
    }

    public HttpConnection(String method, String url, Map httpRequestHeaders) {
        super(method, url, httpRequestHeaders);
    }

    public HttpConnection(String method, String url, Map<String, Object> params, Map httpRequestHeaders) {
        super(method, url, JsonUtil.map2JsonString(params), httpRequestHeaders);
    }

    public HttpConnection(String method, String url, String requestContext, Map httpRequestHeaders) {
        super(method, url, requestContext, httpRequestHeaders);
    }

    /**
     * 执行请求方法
     * @return
     * @throws IOException
     */
    public String doConnect() throws IOException {
        URL reqURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)reqURL.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(method);
        connection.setUseCaches(false);
        Set<String> keys = httpRequestHeaders.keySet();
        for(String key : keys) {
            connection.setRequestProperty(key, httpRequestHeaders.get(key));
        }
        connection.connect();
        boolean enableRequestBody = (method.equals(Const.HttpMethodType.POST) || method.equals(Const.HttpMethodType.PUT)) || method.equals(Const.HttpMethodType.DELETE) && paramString != null;
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
            CompLogger.error("Request Error! ResponseCode;" + connection.getResponseCode());
            throw new IOException("Request Error! ResponseCode;" + connection.getResponseCode());
        }
    }
}
