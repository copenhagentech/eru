package app.config.hibernate;

import jakarta.persistence.EntityManagerFactory;

import java.util.Properties;

public final class HibernateTestUtil {

    private HibernateTestUtil() {
    }

    public static EntityManagerFactory createEntityManagerFactory(
            String jdbcUrl,
            String username,
            String password
    ) {
        Properties props = HibernateBaseProperties.createBase();
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.connection.url", jdbcUrl);
        props.put("hibernate.connection.username", username);
        props.put("hibernate.connection.password", password);
        return HibernateEmfBuilder.build(props);
    }
}
