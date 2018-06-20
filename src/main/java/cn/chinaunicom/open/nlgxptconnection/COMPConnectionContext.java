package cn.chinaunicom.open.nlgxptconnection;

import cn.chinaunicom.open.common.exceptions.COMPContextException;
import cn.chinaunicom.open.common.utils.JsonUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Victor Gao
 * @createTime 2018-04-08
 * @version
 */
public class COMPConnectionContext {

    /**
     * 请求报文
     */
    String requestContext;

    /**
     * 响应报文
     */
    String responseContext;

    /**
     * 响应码
     */
    String respCode;

    /**
     * 响应描述
     */
    String respDesc;

    /**
     * 时间戳
     */
    String timestamp;

    /**
     * 流水
     */
    String transId;

    /**
     * 返回参数对象
     */
    Object respObj;

    public COMPConnectionContext() {
    }

    public COMPConnectionContext(String requestContext, String responseContext, String respCode, String respDesc, String timestamp, String transId, Object respObj) {
        this.requestContext = requestContext;
        this.responseContext = responseContext;
        this.respCode = respCode;
        this.respDesc = respDesc;
        this.timestamp = timestamp;
        this.transId = transId;
        this.respObj = respObj;
    }

    public String getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(String requestContext) {
        this.requestContext = requestContext;
    }

    public String getResponseContext() {
        return responseContext;
    }

    public void setResponseContext(String responseContext) {
        this.responseContext = responseContext;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Object getRespObject(Class clazz) {
        if (respObj == null) {
            if (responseContext == null) {
                throw new COMPContextException("获取返回报文出错");
            }
            JSONObject rspJson = JSONObject.parseObject(responseContext).getJSONObject("UNI_BSS_BODY");
            this.respObj = JsonUtil.getObjectFromJsonString(clazz, rspJson);
        }
        return respObj;
    }
}
