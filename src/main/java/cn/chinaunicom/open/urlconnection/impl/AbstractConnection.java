package cn.chinaunicom.open.urlconnection.impl;

import cn.chinaunicom.open.urlconnection.Connection;

import java.util.Map;

/**
 * 连接q抽象类
 * @author Victor Gao
 * @createTime 2018-04-02
 */
public abstract class AbstractConnection implements Connection {

    /**
     * HTTP请求方法类型
     */
    String method;

    /**
     * 请求地址
     */
    String url;

    /**
     * 请求参数字符串
     */
    String paramString;

    /**
     * HTTP请求头
     */
    Map<String, String> httpRequestHeaders;

    public AbstractConnection() {
    }

    public AbstractConnection(String method, String url, Map httpRequestHeaders) {
        this.method = method;
        this.url = url;
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public AbstractConnection(String method, String url, String paramString, Map<String, String> httpRequestHeaders) {
        this.method = method;
        this.url = url;
        this.paramString = paramString;
        this.httpRequestHeaders = httpRequestHeaders;
    }

}
