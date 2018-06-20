package cn.chinaunicom.open.nlgxptconnection.impl;

import cn.chinaunicom.open.common.CompLogger;
import cn.chinaunicom.open.common.Const;
import cn.chinaunicom.open.common.exceptions.COMPCompressException;
import cn.chinaunicom.open.common.utils.CompressUtil;
import cn.chinaunicom.open.common.utils.JsonUtil;
import cn.chinaunicom.open.common.utils.StringUtil;
import cn.chinaunicom.open.common.utils.UrlUtil;
import cn.chinaunicom.open.nlgxptconnection.COMPConnectionContext;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public class COMPJsonConnection extends AbstractCOMPConnection {

    public COMPJsonConnection(){
        super();
        this.contentType = Const.ContentType.JSON;
    }

    public COMPJsonConnection(String appId, String appSecret) {
        super(appId, appSecret);
        this.contentType = Const.ContentType.JSON;
    }

    /**
     * 从请求参数中获取请求报文
     * @param params        参数对象
     * @param isCompress    是否压缩报文
     * @return
     */
    @Override
    protected String getRequestContext(Object params, boolean isCompress) {
        JSONObject busiParamObject = params instanceof Map ?  (JSONObject) JSONObject.toJSON(params) : JsonUtil.getJsonObjectFromObject(params);
        JSONObject compParamObject = new JSONObject();

        // 构建UNI_BSS_HEAD节点
        JSONObject uniBssHead = new JSONObject();
        // 获取当前时间，根据此时间生成TIMPSTAMP和TRANS_ID
        Date date = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(date);
        String transId = new SimpleDateFormat("yyyyMMddHHmmssSSS" + getRandomNumber()).format(date);
        uniBssHead.put("APP_ID", this.appId);
        uniBssHead.put("TIMESTAMP", timeStamp);
        uniBssHead.put("TRANS_ID", transId);
        uniBssHead.put("TOKEN", getToken(this.appId, this.appSecret, timeStamp, transId));

        compParamObject.put("UNI_BSS_HEAD", uniBssHead);

        if (isCompress) {
            try {
                String compress = CompressUtil.compress(busiParamObject.toJSONString());
                JSONObject mediaInfo = new JSONObject();
                mediaInfo.put("MEDIA_INFO",compress);
                compParamObject.put("UNI_BSS_ATTACHED", mediaInfo);
                compParamObject.put("UNI_BSS_BODY", "");
            } catch (IOException e) {
                CompLogger.error("请求参数对象压缩失败", e);
                throw new COMPCompressException("压缩报文失败");
            }
        } else {
            compParamObject.put("UNI_BSS_BODY", busiParamObject);
        }
        return compParamObject.toJSONString();
    }

    /**
     * 处理返回报文
     * @param responseContext   返回报文
     * @return
     */
    @Override
    protected String getResponseContext(String responseContext) {
        if (StringUtil.isNotEmpty(responseContext)) {
            JSONObject responseObj = JSONObject.parseObject(responseContext);
            if (responseObj.getJSONObject(Const.COMPParamNodeName.UNI_BSS_ATTACHED) != null
                    && StringUtil.isNotEmpty(responseObj.getJSONObject(Const.COMPParamNodeName.UNI_BSS_ATTACHED).getString(Const.COMPParamNodeName.MEDIA_INFO))) {
                String mediaInfo = responseObj.getJSONObject(Const.COMPParamNodeName.UNI_BSS_ATTACHED).getString(Const.COMPParamNodeName.MEDIA_INFO);
                String paramString = null;
                try {
                    paramString = CompressUtil.uncompress(mediaInfo);
                } catch (IOException e) {
                    CompLogger.error("响应报文解压缩出错：", e);
                    throw new COMPCompressException("响应报文解压缩失败");
                }
                JSONObject paramObject = JSONObject.parseObject(paramString);
                responseObj.put(Const.COMPParamNodeName.UNI_BSS_BODY, paramObject);
                responseObj.getJSONObject(Const.COMPParamNodeName.UNI_BSS_ATTACHED).put(Const.COMPParamNodeName.MEDIA_INFO, "");
                return responseObj.toString();
            }
        }
        return responseContext;
    }

    /**
     * 从请求报文、返回报文中获取相关参数信息
     * @param requestContext       请求报文
     * @param responseContext      响应报文
     * @return
     */
    @Override
    protected COMPConnectionContext parseContext(String url, String requestContext, String responseContext) {
        COMPConnectionContext compConnectionContext = new COMPConnectionContext();
        if (StringUtil.isNotEmpty(requestContext)) {
            //请求JSON
            JSONObject requestObj = JSONObject.parseObject(requestContext);
            //请求头信息
            JSONObject header = requestObj.getJSONObject("UNI_BSS_HEAD");
            compConnectionContext.setRequestContext(requestContext);
            compConnectionContext.setTransId(header.getString("TRANS_ID"));
            compConnectionContext.setTimestamp(header.getString("TIMESTAMP"));
            //响应JSON
            JSONObject responseObj = JSONObject.parseObject(responseContext);
            //响应头信息
            header = responseObj.getJSONObject("UNI_BSS_HEAD");
            compConnectionContext.setResponseContext(responseContext);
            compConnectionContext.setRespCode(header.getString("RESP_CODE"));
            compConnectionContext.setRespDesc(header.getString("RESP_DESC"));
        } else {
            if (StringUtil.isNotEmpty(url)) {
                compConnectionContext.setTransId(UrlUtil.getParamFromUrlByName(url, "TRANS_ID"));
                compConnectionContext.setTimestamp(UrlUtil.getParamFromUrlByName(url, "TIMESTAMP"));
                compConnectionContext.setResponseContext(responseContext);
                JSONObject responseObj = JSONObject.parseObject(responseContext);
                compConnectionContext.setRespCode(responseObj.getString("RESP_CODE"));
                compConnectionContext.setRespDesc(responseObj.getString("RESP_DESC"));
            }
        }
        return compConnectionContext;
    }

}
