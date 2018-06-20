package cn.chinaunicom.open.common;

/**
 * 常量类
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public class Const {

    /**
     * HTTP请求头Content-Type
     */
    public static class ContentType {
        public final static String JSON = "application/json;charset=UTF-8";
        public final static String XML= "text/xml";
    }

    /**
     * HTTP请求方法类型常量
     * @author Victor Gao
     * @createTime 2018-04-01
     * @version
     */
    public static class HttpMethodType {

        public final static String GET = "GET";

        public final static String POST = "POST";

        public final static String PUT = "PUT";

        public final static String DELETE = "DELETE";
    }

    /**
     * HTTP/HTTPS协议类型常量
     * @author Victor Gao
     * @createTime 2018-04-01
     * @version
     */
    public static class HttpProtocolType {

        public final static String HTTP = "HTTP";

        public final static String HTTPS = "HTTPS";

    }

    /**
     * 请求头名称
     * @author Victor Gao
     * @createTime 2018-04-01
     * @version
     */
    public static class HttpHeader {
        public final static String ACCEPT_ENCODING = "Accept-Encoding";
        public final static String CONTENT_TYPE = "Content-Type";
        public final static String ENCODING = "Encoding";
    }

    /**
     * 能力平台参数节点名称
     */
    public static class COMPParamNodeName {
        public final static String UNI_BSS_HEAD = "UNI_BSS_HEAD";
        public final static String UNI_BSS_BODY = "UNI_BSS_BODY";
        public final static String UNI_BSS_ATTACHED = "UNI_BSS_ATTACHED";
        public final static String MEDIA_INFO = "MEDIA_INFO";
    }

}
