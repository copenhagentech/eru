package app.config;

import app.controllers.AiController;
import app.controllers.AuthController;
import app.persistence.daos.UserDAO;
import app.integration.openai.OpenAiClient;
import app.integration.openai.OpenAiService;
import app.exceptions.ApiException;
import app.routes.AiRoutes;
import app.routes.AuthRoutes;
import app.routes.SecurityRoutes;
import app.security.JwtUtil;
import app.services.AuthService;
import app.utils.Utils;
import io.javalin.Javalin;
import io.javalin.util.legacy.LegacyAccessManagerKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    private static final int DEFAULT_PORT = 7070;
    private static final String DEFAULT_MODEL = "gpt-4.1-mini";
    private static final String CONFIG_FILE = "config.properties";

    public Javalin createApp() {
        String apiKey = resolveApiKey();

        OpenAiClient client = new OpenAiClient(apiKey, DEFAULT_MODEL);
        OpenAiService openAiService = new OpenAiService(client);
        AiController aiController = new AiController(openAiService);
        JwtUtil jwtUtil = new JwtUtil(resolveJwtSecret());
        AuthService authService = new AuthService(new UserDAO(), jwtUtil);
        AuthController authController = new AuthController(authService);

        Javalin app = Javalin.create(config -> {
            config.routes.beforeMatched(ctx ->
                    logger.info("-> {} {}", ctx.method(), ctx.path())
            );
            config.routes.afterMatched(ctx ->
                    logger.info("<- {} {} {}", ctx.method(), ctx.path(), ctx.statusCode())
            );

            config.routes.exception(ApiException.class, (e, ctx) -> {
                logger.warn("API error {} {} {} {}", ctx.method(), ctx.path(), e.getCode(), e.getErrorCode());
                ctx.status(e.getCode()).json(Map.of(
                        "errorCode", e.getErrorCode().name(),
                        "message", e.getMessage()
                ));
            });
            config.routes.exception(IllegalStateException.class, (e, ctx) -> {
                logger.error("Illegal state on {} {}", ctx.method(), ctx.path(), e);
                ctx.status(500).json(Map.of(
                        "errorCode", "INTERNAL_ERROR",
                        "message", e.getMessage()
                ));
            });
            config.routes.exception(Exception.class, (e, ctx) -> {
                logger.error("Unhandled error on {} {}", ctx.method(), ctx.path(), e);
                ctx.status(500).json(Map.of(
                        "errorCode", "INTERNAL_ERROR",
                        "message", "Internal server error"
                ));
            });
            AiRoutes.register(config.routes, aiController);
            AuthRoutes.register(config.routes, authController);
            SecurityRoutes.register(config.routes);
        });

        LegacyAccessManagerKt.legacyAccessManager(app, (handler, ctx, routeRoles) -> {
            Set<String> allowedRoles = routeRoles.stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());
            ctx.attribute("allowed_roles", allowedRoles);
            authController.authenticate(ctx);
            authController.authorize(ctx);
            try {
                handler.handle(ctx);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return app;
    }

    public void start() {
        createApp().start(DEFAULT_PORT);
        logger.info("ERU API started on port {}", DEFAULT_PORT);
    }

    private String resolveApiKey() {
        String envApiKey = System.getenv("OPENAI_API_KEY");
        if (envApiKey != null && !envApiKey.isBlank()) {
            return envApiKey.trim();
        }

        try {
            String configApiKey = Utils.getPropertyValue("OPENAI_API_KEY", CONFIG_FILE);
            if (configApiKey != null && !configApiKey.isBlank()) {
                return configApiKey;
            }
        } catch (RuntimeException ignored) {
        }

        throw new IllegalStateException(
                "Missing OPENAI_API_KEY. Set environment variable OPENAI_API_KEY "
                        + "or add OPENAI_API_KEY to src/main/resources/" + CONFIG_FILE
        );
    }

    private String resolveJwtSecret() {
        String envJwtSecret = System.getenv("JWT_SECRET");
        if (envJwtSecret != null && !envJwtSecret.isBlank()) {
            return envJwtSecret.trim();
        }

        try {
            String configJwtSecret = Utils.getPropertyValue("JWT_SECRET", CONFIG_FILE);
            if (configJwtSecret != null && !configJwtSecret.isBlank()) {
                return configJwtSecret;
            }
        } catch (RuntimeException ignored) {
        }
        throw new IllegalStateException(
                "Missing JWT_SECRET. Set environment variable JWT_SECRET "
                        + "or add JWT_SECRET to src/main/resources/" + CONFIG_FILE
        );
    }
}
