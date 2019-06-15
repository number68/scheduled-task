package com.cloud.task.util;

import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * <JSON工具类><br/>
 *
 */
public final class JSONUtil {

    private JSONUtil() {
        super();
    }

    /**
     * <JSON字符串转Java对象><br/>
     *
     * @param jsonText
     * @param classType
     * @return
     * @author zwl
     * @create 2019/04/20 15:29:07
     * @since 0.1
     */
    public static <T> T parseObject(String jsonText, Class<T> classType) {
        if (StringUtils.isEmpty(jsonText)) {
            return null;
        }
        return JSONObject.parseObject(jsonText, classType);
    }

    /**
     * <Java对象转JSON字符串><br/>
     *
     * @param object
     * @return
     * @author zwl
     * @create 2019/04/20 15:29:24
     * @since 0.1
     */
    public static String toJSONString(Object object) {
        return JSONObject.toJSONString(object);
    }

    /**
     * <JSONObject转Java对象><br/>
     *
     * @param jsonObject
     * @param classType
     * @return
     * @author zwl
     * @create 2019/04/20 16:46:18
     * @since 0.1
     */
    public static Object toJavaObject(JSONObject jsonObject, Class<?> classType) {
        // 枚举不能正常转换
        return jsonObject.toJavaObject(classType);
    }

    /**
     * <JSON字符串转Java列表对象><br/>
     *
     * @param jsonText
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseArray(String jsonText, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonText)) {
            return null;
        }
        // TODO how to use custom SerializeConfig
        return JSONArray.parseArray(jsonText, clazz);
    }
}
