package com.chaoxing.scaffold.common.core.utils;

import com.chaoxing.scaffold.common.core.utils.json.*;

import lombok.Getter;

import java.lang.reflect.Type;

/**
 * @author lingting 2021/2/25 20:38
 */
public final class JsonUtils {

	private static final String JACKSON_CLASS = "com.fasterxml.jackson.databind.ObjectMapper";

	private static final String GSON_CLASS = "com.google.gson.Gson";

	private static final String FAST_JSON_CLASS = "com.alibaba.fastjson.JSON";

	private JsonUtils() {
	}

	@Getter
	private static JsonTool jsonTool;

	static {
		if (classIsPresent(JACKSON_CLASS)) {
			jsonTool = new JacksonJsonToolAdapter();
		}
		else if (classIsPresent(GSON_CLASS)) {
			jsonTool = new GsonJsonToolAdapter();
		}
		else if (classIsPresent(FAST_JSON_CLASS)) {
			jsonTool = new FastjsonJsonToolAdapter();
		}
	}

	/**
	 * 切换适配器. 请注意 本方法全局生效
	 */
	public static void switchAdapter(JsonTool jsonTool) {
		JsonUtils.jsonTool = jsonTool;
	}

	public static String toJson(Object obj) {
		return jsonTool.toJson(obj);
	}

	public static <T> T toObj(String json, Class<T> r) {
		return jsonTool.toObj(json, r);
	}

	public static <T> T toObj(String json, Type t) {
		if (classIsPresent(FAST_JSON_CLASS) && t instanceof com.alibaba.fastjson.TypeReference) {
			return toObj(json, new TypeReference<T>() {
				@Override
				public Type getType() {
					return ((com.alibaba.fastjson.TypeReference<?>) t).getType();
				}
			});
		}
		else if (classIsPresent(JACKSON_CLASS) && t instanceof com.fasterxml.jackson.core.type.TypeReference) {
			return toObj(json, new TypeReference<T>() {
				@Override
				public Type getType() {
					return ((com.fasterxml.jackson.core.type.TypeReference<?>) t).getType();
				}
			});
		}

		return jsonTool.toObj(json, t);
	}

	public static <T> T toObj(String json, TypeReference<T> t) {
		return jsonTool.toObj(json, t);
	}

	private static boolean classIsPresent(String className) {
		return ClassUtils.isPresent(className, JsonUtils.class.getClassLoader());
	}

}
