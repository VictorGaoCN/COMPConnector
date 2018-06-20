package cn.chinaunicom.open.common.utils;

import cn.chinaunicom.open.annotation.COMPParam;
import cn.chinaunicom.open.common.CompLogger;
import cn.chinaunicom.open.common.exceptions.COMPParamException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Victor Gao
 * @createTime 2018/6/14
 */
public class JsonUtil {

    /**
     * 将Map转换为JSONString
     * @param map
     * @return
     */
    public static String map2JsonString(Map map) {
        return JSONObject.toJSONString(map);
    }

    /**
     * 将请求参数对象按照注解中的参数转换为JSONObject
     * @param object
     * @return
     */
    public static JSONObject getJsonObjectFromObject(Object object) {
        if (object == null) {
            throw new COMPParamException("被转换对象不能为空");
        }
        COMPParam compParam = object.getClass().getAnnotation(COMPParam.class);
        if (compParam == null) {
            CompLogger.warn(object.getClass().getName() + "：class缺少能力平台参数注解配置COMPParam");
            return JsonUtil.getJsonObjectFromObjectWithAnnotation(object);
        } else {
            JSONObject reqObject = new JSONObject();
            reqObject.put(object.getClass().getAnnotation(COMPParam.class).paramName(), JsonUtil.getJsonObjectFromObjectWithAnnotation(object));
            return reqObject;
        }
    }

    /**
     * 递归实现将请求参数对象按照注解中的参数转换为JSONObject
     * @param object
     * @return
     * @throws NoSuchFieldException
     */
    public static JSONObject getJsonObjectFromObjectWithAnnotation(Object object) {
        if (object == null) {
            return null;
        }
        JSONObject node = new JSONObject();
        //Map paramMap = new HashMap();
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                COMPParam compParam =field.getAnnotation(COMPParam.class);
                if (compParam != null) {
                    String newKey = compParam.paramName();
                    Object valueObject = getFieldValueByName(field.getName(), object);
                    if (valueObject == null && !compParam.enableNull()) {
                        // 不进行任何操作
                    } else {
                        if (valueObject instanceof Map) {
                            node.put(newKey,valueObject);
                        } else if (valueObject instanceof List) {
                            List valueList = (List) valueObject;
                            List list = new ArrayList();
                            for (int i = 0; i < valueList.size(); i++) {
                                Object o = valueList.get(i);
                                if (isLeaf(o.getClass())) {
                                    list.add(o);
                                } else {
                                    list.add(getJsonObjectFromObjectWithAnnotation(o));
                                }
                            }
                            node.put(newKey, list);
                        } else if(isLeaf(valueObject) ) {
                            node.put(newKey, valueObject);
                        } else {
                            node.put(newKey,getJsonObjectFromObjectWithAnnotation(valueObject));
                        }
                    }
                }
            }
        }
        return node;
    }

    /**
     * 将返回JSONString转换为返回参数对象
     * @param clazz
     * @param jsonObject
     * @return
     */
    public static Object getObjectFromJsonString(Class clazz, JSONObject jsonObject) {
        try {
            if (Map.class.isAssignableFrom(clazz)) {
                if (jsonObject != null) {
                    return jsonObject.getInnerMap();
                } else {
                    return null;
                }
            } else {
                Annotation rspParam = clazz.getAnnotation(COMPParam.class);
                if (rspParam==null) {
                    CompLogger.warn(clazz.getName() + "：class缺少能力平台参数注解配置COMPParam");
                    return JsonUtil.getObjectFromJsonStringWithAnnotation(clazz,jsonObject);
                } else {
                    return JsonUtil.getObjectFromJsonStringWithAnnotation(clazz, jsonObject.getJSONObject(((COMPParam) clazz.getAnnotation(COMPParam.class)).paramName()));
                }
            }
        } catch (IllegalAccessException e) {
            CompLogger.error("返回报文转换对象出错", e);
            throw new COMPParamException("返回报文转换对象出错");
        } catch (InstantiationException e) {
            CompLogger.error("返回报文转换对象出错", e);
            throw new COMPParamException("返回报文转换对象出错");
        }
    }

    /**
     * 递归实现将返回JSONString转换为返回参数对象
     * @param clazz
     * @param jsonObject
     * @return
     */
    private static Object getObjectFromJsonStringWithAnnotation(Class clazz, JSONObject jsonObject) throws IllegalAccessException, InstantiationException {
        if (jsonObject == null) {
            return null;
        }
        Object rspParamObject = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                COMPParam compParam =field.getAnnotation(COMPParam.class);
                if (compParam != null) {
                    String key = compParam.paramName();
                    Object valueObject = jsonObject.get(key);
                    field.setAccessible(true);
                    if (valueObject == null) {
                        field.set(rspParamObject, null);
                    } else {
                        if (isLeaf(field.getType())) {
                            field.set(rspParamObject, valueObject);
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            JSONArray jsonArray = jsonObject.getJSONArray(key);
                            List list = new ArrayList();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                Class clazz1 = (Class) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                                if (isLeaf(clazz1)) {
                                    list.add(jsonArray.get(i));
                                } else {
                                    list.add(getObjectFromJsonStringWithAnnotation(clazz1, jsonArray.getJSONObject(i)));
                                }

                            }
                            field.set(rspParamObject, list);
                        } else if (Map.class.isAssignableFrom(field.getType())) {
                            field.set(rspParamObject, jsonObject.getJSONObject(key).getInnerMap());
                        } else {
                            field.set(rspParamObject, getObjectFromJsonStringWithAnnotation(field.getType(), jsonObject.getJSONObject(key)));
                        }
                    }
                }
            }
        }
        return rspParamObject;
    }

    /**
     * 判断对象是不是基本类型(用于判断被转换对象是不是叶子节点的对象)
     * @param object
     * @return
     */
    private static boolean isLeaf(Object object) {
        if (object instanceof String || object instanceof Integer || object instanceof Character || object instanceof Short
                || object instanceof Long || object instanceof Boolean || object instanceof Float || object instanceof Double || object instanceof Byte){
            return true;
        }
        return false;
    }

    /**
     * 判断类是不是基本类型(用于判断类是不是叶子节点的类)
     * @param clazz
     * @return
     */
    private static boolean isLeaf(Class clazz) {
        if (String.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)
                || Short.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)
                || Float.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz) || Byte.class.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }

    /**
     * 获取对象中给定名称的属性对象
     * @param fieldName
     * @param o
     * @return
     */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            CompLogger.error(e.getMessage(),e);
            return null;
        }
    }

}
