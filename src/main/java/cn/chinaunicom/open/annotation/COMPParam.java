package cn.chinaunicom.open.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 能力平台参数对象注解
 * @author Victor Gao
 * @createTime 2018-06-07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface COMPParam {

    /**
     * 目标参数名
     * @return
     */
    String paramName();

    /**
     * 参数为NULL时是否传此节点，默认不传。
     * @return
     */
    boolean enableNull() default false;

}
