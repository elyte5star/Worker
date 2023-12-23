package org.elyte.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilityFunctions {
    static SecureRandom rnd = new SecureRandom();
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static String generateUuidString() {
        return UUID.randomUUID().toString();
    }

    public static String timeNow() {
        LocalDateTime current = LocalDateTime.now();
        return current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    }

    public static String convertHashMapToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    public static Map<String, Map<String, Object>> jsonToMap(String jsonObject)
            throws JsonMappingException, JsonProcessingException {
        return new ObjectMapper().readValue(jsonObject,
                new TypeReference<Map<String, Map<String, Object>>>() {
                });
    }

    public static Map<String, Map<String, Map<String, Object>>> jsonToMap2(String jsonObject)
            throws JsonMappingException, JsonProcessingException {
        return new ObjectMapper().readValue(jsonObject,
                new TypeReference<Map<String, Map<String, Map<String, Object>>>>() {
                });
    }

    public static Map<String, Object> objectToMap(Object object) {
        return new ObjectMapper().convertValue(object,
                new TypeReference<Map<String, Object>>() {
                });

    }

}
