package app.config.hibernate;

import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;

import java.util.Properties;

public final class HibernateConfig {

    private static volatile EntityManagerFactory emf;

    private HibernateConfig() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (HibernateConfig.class) {
                if (emf == null) {
                    emf = HibernateEmfBuilder.build(buildProps());
                }
            }
        }
        return emf;
    }

    private static Properties buildProps() {
        Properties props = HibernateBaseProperties.createBase();

        // Keep the schema in sync without dropping existing development data
        props.put("hibernate.hbm2ddl.auto", "update");

        if (System.getenv("DEPLOYED") != null) {
            setDeployedProperties(props);
        } else {
            setDevProperties(props);
        }
        return props;
    }

    private static void setDeployedProperties(Properties props) {
        String dbName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + dbName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
    }

    private static void setDevProperties(Properties props) {
        String dbName = resolveSetting("DB_NAME", "eru");
        String username = resolveSetting("DB_USERNAME", "postgres");
        String password = resolveSetting("DB_PASSWORD", "postgres");
        String jdbcUrl = resolveSetting("DB_URL", "jdbc:postgresql://localhost:5432/" + dbName);

        props.put("hibernate.connection.url", jdbcUrl);
        props.put("hibernate.connection.username", username);
        props.put("hibernate.connection.password", password);
    }

    private static String resolveSetting(String key, String defaultValue) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        String configValue = Utils.getOptionalPropertyValue(key, "config.properties");
        if (configValue != null && !configValue.isBlank()) {
            return configValue.trim();
        }

        return defaultValue;
    }
}
