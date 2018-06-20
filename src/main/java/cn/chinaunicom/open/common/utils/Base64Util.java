package cn.chinaunicom.open.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * BASE64工具类
 * @author Victor Gao
 * @createTime 2018-04-02
 */
public class Base64Util {

    private static BASE64Encoder encoder = new BASE64Encoder();

    private static BASE64Decoder decoder = new BASE64Decoder();

    /**
     * BASE64加密
     * @param input
     * @return
     * @throws Exception
     */
    public static String encodeBase64(byte[]input) throws Exception {
        return encoder.encode(input).replaceAll("\r\n", "");
    }

    /**
     * BASE64解密
     * @param input
     * @return
     * @throws Exception
     */
    public static byte[] decodeBase64(String input) throws Exception {
        return decoder.decodeBuffer(input);
    }

}
