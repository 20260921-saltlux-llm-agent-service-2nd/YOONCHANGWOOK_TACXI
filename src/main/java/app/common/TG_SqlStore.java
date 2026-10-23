package app.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TG_SqlStore {
    private static final Properties P = new Properties();

    static {
        try (InputStream in = new FileInputStream("src/main/java/resources/db/mapper/TG_TableCreate.xml")) {
            P.loadFromXML(in);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Object... args : 인자를 가변적으로 받음, Object로 인해 Integer, Boolean 등의 래퍼 타입으로 자동 형 변환됨
    public static String get(String key, Object... args) {

        // TG_TableCreate.xml의 key에 해당하는 sql을 가져와 %s 자리에 args 인자값들이 들어감
        return String.format(P.getProperty(key).trim(), args);
    }
}
