package com.stt.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具类
 */
public class JsonUtil {
	private static volatile ObjectMapper mapper = new ObjectMapper();

	private JsonUtil() {
	};

	public static ObjectMapper getInstance() {
		return mapper;
	}

	public static byte[] toJsonBytes(Object obj) {
		try {
			return mapper.writeValueAsBytes(obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJsonString(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		if (json == null)
			return null;
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 若有泛型需要使用 TypeReference 来引用泛型，否则会转换失败
	 * <p>
	 * 如：Result<Account> toObject2 = JsonUtil.toObject(jsonStr, new
	 * TypeReference<Result<Account>>() { });
	 * <p>
	 * 如：HashMap<String,Account> 如果要转换为对应的Map，则必须使用 TypeReference
	 * 
	 * @param json
	 * @param typeReference
	 * @return
	 */
	public static <T> T toObject(String json, TypeReference<T> typeReference) {
		if (json == null)
			return null;
		try {
			return mapper.readValue(json, typeReference);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(byte[] json, Class<T> clazz) {
		if (json == null)
			return null;
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(byte[] json, TypeReference<T> typeReference) {
		if (json == null)
			return null;
		try {
			return mapper.readValue(json, typeReference);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 转换为 List<T>
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toObjects(String json, Class<T> clazz) {
		if (json == null) {
			return null;
		}
		try {
			// 第一个Class 是集合类型，第二个Class是集合类型中的泛型，要返回的类型
			// 如果已知返回类型，可以使用 new TypeReference<List<T>>()
			JavaType javaType = mapper.getTypeFactory()
					.constructParametricType(List.class, clazz);
			List<T> readValue = (List<T>) mapper.readValue(json, javaType);
			return readValue;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toJsonToObject(Object from, Class<T> clazz) {
		return JsonUtil.toObject(JsonUtil.toJsonString(from), clazz);
	}

}
