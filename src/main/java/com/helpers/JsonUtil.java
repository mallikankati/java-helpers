package com.helpers;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Json helper functions to convert from and to json
 * 
 * Underneath it will use Jackson jar to handle json payload
 * 
 * @author mallik
 * 
 */
public final class JsonUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,
				false);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	public static ObjectMapper getObjectMapper() {
		return mapper;
	}

	public static <T> T fromJson(String jsonStr, Class<T> clazz) {
		T value = null;
		try {
			value = getObjectMapper().readValue(jsonStr, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return value;
	}

	public static <T> T fromJson(String jsonStr, TypeReference<T> reference) {
		T value = null;
		try {
			value = getObjectMapper().readValue(jsonStr,
					new TypeReference<T>() {
					});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return value;
	}

	public static String toJson(Object source) {
		String json = null;
		try {
			json = getObjectMapper().writeValueAsString(source);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return json;
	}
}
