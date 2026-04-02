package app.config.hibernate;

import app.entities.Content;
import app.entities.Role;
import app.entities.User;
import app.entities.UserInteraction;
import org.hibernate.cfg.Configuration;

public final class EntityRegistry {

    private EntityRegistry() {
    }

    public static void registerEntities(Configuration configuration) {

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(Content.class);
        configuration.addAnnotatedClass(UserInteraction.class);

    }
}
