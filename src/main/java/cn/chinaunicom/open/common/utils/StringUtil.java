package cn.chinaunicom.open.common.utils;

import cn.chinaunicom.open.common.CompLogger;
import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 字符串工具类
 * @author Victor Gao
 * @createTime 2018-04-02
 * @version
 */
public class StringUtil {

    /**
     * 判断字符串是否为不为空且不为空串
     * @param str
     * @return
     */
    public static boolean isNotEmpty (String str) {
        return !(str == null || "".equals(str));
    }

    /**
     * 将JSONString转换为MAP
     * @param jsonString
     * @return
     */
    public static Map jsonString2Map(String jsonString) {
        return JSON.parseObject(jsonString);
    }

    /**
     * 从InputStream中读入String
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuffer sb = new StringBuffer();
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) {
            CompLogger.error("", e);
        }
        return sb.toString();
    }

    /**
     * 将驼峰字符串转换为大写加下划线字符串
     * @param string
     * @return
     */
    public static String camelCase2UpperCaseWithUnderLine(String string) {
        if (string != null) {
            StringBuilder stringBuilder = new StringBuilder(1024);
            int start = 0;
            for (int i = 0; i < string.length(); i++) {
                char ch = string.charAt(i);
                if (ch >= 'A' && ch <= 'Z') {
                    stringBuilder.append(string.substring(start, i).toUpperCase()).append('_');
                    start = i;
                } else if (i == string.length() - 1) {
                    stringBuilder.append(string.substring(start, i + 1).toUpperCase());
                }
            }
            System.out.println(stringBuilder.toString());
            return stringBuilder.toString();
        }
        return null;
    }

    /**
     * 将大写加下划线字符串转换为驼峰字符串
     * @param string
     * @return
     */
    public static String upperCaseWithUnderLine2CamelCase(String string) {
        if (string != null) {
            StringBuilder stringBuilder = new StringBuilder(1024);
            String [] strs = string.split("_");
            for (String str : strs) {
                str = str.toLowerCase();
                stringBuilder.append((char)(str.charAt(0) - 32)).append(str.substring(1));
            }
            return (char)(stringBuilder.charAt(0) + 32) + stringBuilder.substring(1);
        }
        return null;
    }

}
