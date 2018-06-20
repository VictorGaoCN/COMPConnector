package cn.chinaunicom.open.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 * @author Victor Gao
 * @createTime 2018-04-03
 */
public class ExceptionUtil {

    /**
     * 获取异常的堆栈信息
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTraceStr = sw.toString();
        pw.close();
        return stackTraceStr;
    }

}
