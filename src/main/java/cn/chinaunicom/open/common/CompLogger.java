package cn.chinaunicom.open.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 能力平台日志类
 * @author Victor Gao
 * @createTime 2018-04-04
 * @version
 */
public class CompLogger {

    private static final Log logger = LogFactory.getLog("sdk.comp");

    /**
     * INFO级别
     * @param message
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * INFO级别
     * @param message
     * @param e
     */
    public static void info(String message, Exception e){
        logger.info(message, e);
    }

    /**
     * DEBUG级别
     * @param message
     */
    public static void debug(String message) {
        logger.debug(message);
    }

    /**
     * DEBUG级别
     * @param message
     * @param e
     */
    public static void debug(String message, Exception e) {
        logger.debug(message, e);
    }

    /**
     * WARN级别
     * @param message
     */
    public static void warn(String message) {
        logger.warn(message);
    }

    /**
     * WARN级别
     * @param message
     * @param e
     */
    public static void warn(String message, Exception e) {
        logger.warn(message, e);
    }

    /**
     * ERROR级别
     * @param message
     */
    public static void error(String message) {
        logger.error(message);
    }

    /**
     * ERROR级别
     * @param message
     * @param e
     */
    public static void error(String message, Exception e) {
        logger.error(message, e);
    }

}
