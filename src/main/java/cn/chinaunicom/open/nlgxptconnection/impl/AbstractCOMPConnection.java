package cn.chinaunicom.open.nlgxptconnection.impl;

import cn.chinaunicom.open.common.CompLogger;
import cn.chinaunicom.open.common.Const;
import cn.chinaunicom.open.common.utils.MD5Util;
import cn.chinaunicom.open.nlgxptconnection.COMPConnection;
import cn.chinaunicom.open.nlgxptconnection.COMPConnectionContext;
import cn.chinaunicom.open.urlconnection.Connection;
import cn.chinaunicom.open.urlconnection.impl.HttpConnection;
import cn.chinaunicom.open.urlconnection.impl.HttpsConnection;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public abstract class AbstractCOMPConnection implements COMPConnection {

    /**
     * APP_ID
     */
    protected String appId;

    /**
     * APP_SECRET
     */
    protected String appSecret;

    /**
     * 接口类型 JSON/XML(当前只支持JSON)
     */
    protected String contentType;

    public AbstractCOMPConnection(){}

    public AbstractCOMPConnection(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    /**
     * (抽象方法)从请求参数中获取请求报文
     * @param params            请求参数对象
     * @param isCompress        是否压缩请求报文
     * @return
     */
    protected abstract String getRequestContext(Object params,boolean isCompress);

    /**
     * (抽象接口)处理返回报文
     * @param responseContext   返回报文
     * @return
     */
    protected abstract String getResponseContext(String responseContext);

    /**
     * (抽象方法)从请求报文、返回报文中获取相关参数信息
     * @param url                  请求路径
     * @param requestContext       请求报文
     * @param responseContext      响应报文
     * @return
     */
    protected abstract COMPConnectionContext parseContext(String url, String requestContext,String responseContext);

    /**
     * 请求能力共享平台接口，获取相关信息
     * @param method        请求方法 GET/POST/PUT/DELETE
     * @param url           请求地址
     * @return
     */
    public COMPConnectionContext excute(String method, String url) {
        return excute(method, url, "", false);
    }

    /**
     * 请求能力共享平台接口
     * @param method            请求方法 GET/POST/PUT/DELETE
     * @param url               请求地址
     * @param requestContext    请求能力平台的报文
     * @return
     */
    public COMPConnectionContext excute(String method, String url, String requestContext) {
        return excute(method, url, requestContext, false);
    }

    /**
     * 请求能力共享平台接口
     * @param method            请求方法 GET/POST/PUT/DELETE
     * @param url               请求地址
     * @param requestContext    请求能力平台的报文
     * @param isCompressed      是否压缩业务字段参数
     * @return
     */
    public COMPConnectionContext excute(String method, String url, String requestContext, boolean isCompressed) {
        Map selfDefHeaders = new HashMap();
        selfDefHeaders.put(Const.HttpHeader.CONTENT_TYPE, this.contentType);
        selfDefHeaders.put(Const.HttpHeader.ACCEPT_ENCODING, isCompressed ? "gzip" : "");
        CompLogger.info("[COMP-REQUEST] " + "Request-Time: " + new Date() + " Request-Method: "
                + method + " Request-URL: " + url + ((requestContext == null) ? "" : (" Request-Context: " + requestContext)));
        Connection connection = getConnection(method, url, requestContext, selfDefHeaders);
        COMPConnectionContext compConnectionContext = null;
        try {
            String responseContext = getResponseContext(connection.doConnect());
            compConnectionContext = parseContext(url, requestContext, responseContext);
            CompLogger.info("[COMP-RESPONSE] " + "Response-Time: " + new Date() + " Response-Context: " + responseContext);
        } catch (IOException e) {
            CompLogger.error("REQUEST COMP ERROR!", e);
        }
        return compConnectionContext;
    }

    /**
     * 请求能力共享平台接口，获取相关信息
     * @param method        请求方法 GET/POST/PUT/DELETE
     * @param url           请求地址
     * @param params        业务参数MAP,父节点为UNI_BSS_BODY
     * @return              返回能力平台的返回报文
     */
    public COMPConnectionContext excute(String method, String url, Object params) {
        return excute(method, url, params, false);
    }

    /**
     * 请求能力共享平台接口，获取相关信息
     * @param method        请求方法 GET/POST/PUT/DELETE
     * @param url           请求地址
     * @param params        业务参数MAP,父节点为UNI_BSS_BODY
     * @param isCompressed  是否压缩报文
     * @return              返回能力平台的返回报文
     */
    public COMPConnectionContext excute(String method, String url, Object params, boolean isCompressed) {
        return excute(method, url, getRequestContext(params, isCompressed), isCompressed);
    }

    /**
     * 获取网络连接
     * @param method            请求方法
     * @param url               连接地址
     * @param requestContext    能力平台参数Map
     * @param selfDefHeaders    添加的请求报文头
     * @return
     */
    protected Connection getConnection(String method, String url, String requestContext, Map selfDefHeaders) {
        // 获取协议类型
        int index = url.indexOf("://");
        String protocolType = url.substring(0, index).toUpperCase();
        // 根据不同的协议类型构造不同的Connection
        Connection connection;
        if (requestContext == null) {
            connection = protocolType.equals(Const.HttpProtocolType.HTTP)
                    ? new HttpConnection(method, url, selfDefHeaders)
                    : new HttpsConnection(method, url, selfDefHeaders);
        } else {
            connection = protocolType.equals(Const.HttpProtocolType.HTTP)
                    ? new HttpConnection(method, url, requestContext, selfDefHeaders)
                    : new HttpsConnection(method, url, requestContext, selfDefHeaders);
        }
        return connection;
    }

    /**
     * 获取六位随机数
     * @return
     */
    protected static int getRandomNumber() {
        return (int)((Math.random() * 9 + 1) * 100000);
    }

    /**
     * 根据鉴权信息计算TOKEN：将鉴权参数按照参数名字母顺序将参数名和参数值拼接成字符串并进行MD5加密(32位小写)得出TOKEN值
     * @param appId         APP_ID
     * @param appSecret     APP_SECRET
     * @param timeStamp     TIMESTAMP
     * @param transId       TRANS_ID
     * @return              返回根据及安全信息计算出的TOKEN
     */
    protected static Object getToken(String appId, String appSecret, String timeStamp, String transId) {
        String beforeEncode = "APP_ID" + appId + "TIMESTAMP" + timeStamp + "TRANS_ID" + transId + appSecret;
        return MD5Util.md5encode(beforeEncode);
    }

}
