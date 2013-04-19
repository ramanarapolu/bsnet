package com.jda.bsnet.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule



class JsonUtils {
	public static <T> T fromJson(String jsonString, Class<T> argClass){
		ObjectMapper objectMapper = new ObjectMapper()
		objectMapper.registerModule(new JodaModule())
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		return objectMapper.readValue(jsonString, argClass)
	}

	public static void writeJsonToFile(String fileName, Object obj){
		ObjectMapper objectMapper = new ObjectMapper()
		objectMapper.registerModule(new JodaModule())
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		objectMapper.writeValue(new File(fileName), obj)
	}

	public static <T>T readFromJsonFile(String fileName, Class<T> argClass){
		ObjectMapper objectMapper = new ObjectMapper()
		objectMapper.registerModule(new JodaModule())
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		return objectMapper.readValue(new File(fileName), argClass)
	}

}
