package cn.chinaunicom.open.common.utils;

/**
 * @author Victor Gao
 * @createTime 2018-06-11
 * @version
 */
public class UrlUtil {

    /**
     * 从URL中根据参数名获取参数
     * @param url
     * @param paramName
     * @return
     */
    public static String getParamFromUrlByName(String url, String paramName) {
        if (StringUtil.isNotEmpty(url) && url.indexOf('?') >= 0) {
            String[] paramArray = url.substring(url.indexOf('?') + 1).split("&");
            for (String param : paramArray) {
                String[] paramKV = param.split("=");
                if (paramName.equals(paramKV[0])) {
                    return paramKV[1];
                }
            }
        }
        return null;
    }

}
