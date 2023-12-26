package org.elyte.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import org.elyte.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UtilityFunctions {
    static SecureRandom rnd = new SecureRandom();
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Logger log = LoggerFactory.getLogger(UtilityFunctions.class);

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

    public static String convertObjectToJson(Object object) {
        String result = null;
        try {
            result = new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.err.println(e);

        }
        return result;
    }

    
    public static Map<String, Object> objectToMap(Object object) {
        return new ObjectMapper().convertValue(object,
                new TypeReference<Map<String, Object>>() {
                });

    }

    public static String convertObjectToGson(Object object) {
        if (object == null) {
            return null;
        }
        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }


     public Object entityToObject(Status taskStatus) {

        byte[] data = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(taskStatus);
            oos.flush();
            oos.close();
            baos.close();
            data = baos.toByteArray();
        } catch (IOException ex) {
            data = null;
            log.error("ERROR :" + ex.getLocalizedMessage());

        }

        return data;

    }


}
