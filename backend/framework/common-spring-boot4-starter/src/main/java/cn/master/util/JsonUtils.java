package cn.master.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.TypeFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
public class JsonUtils {
    private static final JsonMapper jsonMapper = new JsonMapper();
    private static final TypeFactory typeFactory = jsonMapper.getTypeFactory();

    public static String toJSONString(Object value) {
        return jsonMapper.writeValueAsString(value);
    }

    public static <T> List<T> parseArray(String content, TypeReference<T> valueType) {
        JavaType subType = typeFactory.constructType(valueType);
        CollectionType javaType = typeFactory.constructCollectionType(List.class, subType);
        return jsonMapper.readValue(content, javaType);
    }

    public static <T> List<T> parseArray(String content, Class<T> valueType) {
        CollectionType javaType = typeFactory.constructCollectionType(List.class, valueType);
        return jsonMapper.readValue(content, javaType);
    }

    public static <T> Function<Object, T> objectToType(Class<T> clazz) {
        return object -> jsonMapper.convertValue(object, clazz);
    }

    public static byte[] toJSONBytes(Object object) {
        return jsonMapper.writeValueAsBytes(object);
    }

    public static <T> Function<Object, T> objectToType(TypeReference<T> targetType) {
        return object -> jsonMapper.convertValue(object, targetType);
    }
    public static <K,V> Map<K,V> parseMap(String mapJsonStr, Class<K> kClazz, Class<V> vClazz) {
        if (StringUtils.isBlank(mapJsonStr) || kClazz == null || vClazz == null) {
            return Collections.emptyMap();
        } 
        return jsonMapper.readValue(mapJsonStr, typeFactory.constructParametricType(Map.class, kClazz, vClazz));
    }
}
