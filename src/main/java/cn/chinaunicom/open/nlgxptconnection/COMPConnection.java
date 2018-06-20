package cn.chinaunicom.open.nlgxptconnection;

/**
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public interface COMPConnection {

    /**
     * 请求能力共享平台接口
     * @param url           请求地址
     * @param method        请求方法 GET/POST/PUT/DELETE
     * @return              返回能力平台的返回报文(JSON格式)
     */
    public COMPConnectionContext excute(String method, String url);

    /**
     * 请求能力共享平台接口
     * @param method            请求方法 GET/POST/PUT/DELETE
     * @param url               请求地址
     * @param requestContext    请求参数REQ及以下部分字符串
     * @return
     */
    public COMPConnectionContext excute(String method, String url, String requestContext);

    /**
     * 请求能力共享平台接口
     * @param method            请求方法 GET/POST/PUT/DELETE
     * @param url               请求地址
     * @param requestContext    请求参数REQ及以下部分字符串
     * @param isCompressed      是否压缩请求报文
     * @return
     */
    public COMPConnectionContext excute(String method, String url, String requestContext, boolean isCompressed);

    /**
     * 请求能力共享平台接口，获取相关信息
     * @param method        请求方法 GET/POST/PUT/DELETE
     * @param url           请求地址
     * @param params        业务参数MAP,父节点为UNI_BSS_BODY
     * @return              返回能力平台的返回报文(JSON格式)
     */
    public COMPConnectionContext excute(String method, String url, Object params);

    /**
     * 请求能力共享平台接口，获取相关信息
     * @param method        请求方法 GET/POST/PUT/DELETE
     * @param url           请求地址
     * @param params        业务参数MAP,父节点为UNI_BSS_BODY
     * @param isCompressed  是否压缩请求报文
     * @return              返回能力平台的返回报文(JSON格式)
     */
    public COMPConnectionContext excute(String method, String url, Object params, boolean isCompressed);

}
