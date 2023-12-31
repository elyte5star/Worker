package elyte.util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import elyte.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AppConfig {

    static SecureRandom rnd = new SecureRandom();

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private static final String CONFIG_FILE = "src/config.yml";

    private Properties properties;

    public AppConfig() {

        try (FileInputStream configInput = new FileInputStream(CONFIG_FILE)) {
            this.properties = new Properties();
            this.properties.load(configInput);
        } catch (FileNotFoundException e) {
            log.error("[+] Config Exception ", e.getLocalizedMessage());
        } catch (IOException e) {
            log.error("[+] Config Exception ", e.getLocalizedMessage());
        }

    }

    public String getConfigValue(String property) {
        String value = this.properties.getProperty(property);
        if (value == null) {
            throw new RuntimeException("Config Error. Invalid property key!");
        }
        return value;
    }

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public String generateUuidString() {
        return UUID.randomUUID().toString();
    }

    public String timeNow() {
        LocalDateTime current = LocalDateTime.now();
        return current.format(dtf);
    }

    public long timeDiff(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        return Math.abs(duration.toMillis());
    }

    public String convertObjectToJson(Object object) {
        String result = null;
        try {
            result = new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.err.println(e);

        }
        return result;
    }

    public Map<String, Object> objectToMap(Object object) {
        return new ObjectMapper().convertValue(object,
                new TypeReference<Map<String, Object>>() {
                });

    }

    public String convertObjectToGson(Object object) {
        if (object == null) {
            return null;
        }
        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }

    public long diff(String start, String end) {
        LocalDateTime dateTime1 = LocalDateTime.parse(start, dtf);
        LocalDateTime dateTime2 = LocalDateTime.parse(end, dtf);
        Duration duration = Duration.between(dateTime1, dateTime2);
        return Math.abs(duration.toMillis());
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
