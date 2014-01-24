package com.jda.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JsonUtils {
    public static <T> T fromJson(String jsonString, Class<T> argClass) throws JsonParseException, JsonMappingException, IOException{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return objectMapper.readValue(jsonString, argClass);
    }

    public static void writeJsonToFile(String fileName, Object obj) throws JsonGenerationException, JsonMappingException, IOException{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.writeValue(new File(fileName), obj);
    }

    public static String convertObjToJsonString(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
    	 ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.registerModule(new JodaModule());
         objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
         objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
         StringWriter stringObj = new StringWriter();
         objectMapper.writeValue(stringObj, obj);
         return stringObj.toString();
    }
    
    public static <T>T readFromJsonFile(String fileName, Class<T> argClass) throws JsonParseException, JsonMappingException, IOException{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return objectMapper.readValue(new File(fileName), argClass);
    }

}
