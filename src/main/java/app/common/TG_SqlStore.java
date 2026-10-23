package app.common;

import java.io.InputStream;
import java.util.Properties;

public class TG_SqlStore {
    private static final Properties P = new Properties();

    static {
        try (InputStream in = TG_SqlStore.class.getResourceAsStream("/db/mapper/TableCreate.xml")) {
            P.loadFromXML(in);
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }

    // Object... args : 인자를 가변적으로 받음, Object로 인해 Integer, Boolean 등의 래퍼 타입으로 자동 형 변환됨
    public static String get(String key, Object... args) {

        return String.format(P.getProperty(key).trim(), args);
    }
}
