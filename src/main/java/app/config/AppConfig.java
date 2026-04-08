package app.config;

import app.controllers.AiController;
import app.controllers.AuthController;
import app.controllers.ContentController;
import app.controllers.InteractionController;
import app.persistence.daos.UserDAO;
import app.persistence.daos.ContentDAO;
import app.persistence.daos.UserInteractionDAO;
import app.integration.openai.OpenAiClient;
import app.integration.openai.OpenAiService;
import app.exceptions.ApiException;
import app.routes.AiRoutes;
import app.routes.AuthRoutes;
import app.routes.ContentRoutes;
import app.routes.InteractionRoutes;
import app.routes.SecurityRoutes;
import app.security.JwtUtil;
import app.services.AuthService;
import app.services.ContentService;
import app.services.InteractionService;
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
    private static final String DEFAULT_JWT_SECRET = "dev-secret-change-me";

    public Javalin createApp() {
        OpenAiService openAiService = createOpenAiService();
        AiController aiController = new AiController(openAiService);
        ContentService contentService = new ContentService(new ContentDAO());
        ContentController contentController = new ContentController(contentService);
        InteractionService interactionService = new InteractionService(
                new UserInteractionDAO(),
                new UserDAO(),
                new ContentDAO()
        );
        InteractionController interactionController = new InteractionController(interactionService);
        JwtUtil jwtUtil = new JwtUtil(resolveJwtSecret());
        AuthService authService = new AuthService(new UserDAO(), jwtUtil);
        AuthController authController = new AuthController(authService);

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableRouteOverview("/routes");
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
            ContentRoutes.register(config.routes, contentController);
            InteractionRoutes.register(config.routes, interactionController);
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
                if (e instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw new RuntimeException(e);
            }
        });

        return app;
    }

    public void start() {
        createApp().start(DEFAULT_PORT);
        logger.info("ERU API started on port {}", DEFAULT_PORT);
    }

    private OpenAiService createOpenAiService() {
        String apiKey = resolveApiKey();
        if (apiKey == null) {
            logger.warn("OPENAI_API_KEY was not found. /ai/elaborate will return a configuration error until it is set.");
            return null;
        }

        String model = resolveOpenAiModel();
        logger.info("OpenAI integration enabled with model {}", model);
        OpenAiClient client = new OpenAiClient(apiKey, model);
        return new OpenAiService(client);
    }

    private String resolveApiKey() {
        String envApiKey = resolveFirstEnvironmentValue("OPENAI_API_KEY", "ERU_API_KEY", "eru_api_key");
        if (envApiKey != null && !envApiKey.isBlank()) {
            return envApiKey.trim();
        }

        String configApiKey = Utils.getOptionalPropertyValue("OPENAI_API_KEY", CONFIG_FILE);
        if (configApiKey != null && !configApiKey.isBlank()) {
            return configApiKey;
        }

        return null;
    }

    private String resolveFirstEnvironmentValue(String... keys) {
        for (String key : keys) {
            String value = System.getenv(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String resolveOpenAiModel() {
        String envModel = System.getenv("OPENAI_MODEL");
        if (envModel != null && !envModel.isBlank()) {
            return envModel.trim();
        }

        String configModel = Utils.getOptionalPropertyValue("OPENAI_MODEL", CONFIG_FILE);
        if (configModel != null && !configModel.isBlank()) {
            return configModel.trim();
        }

        return DEFAULT_MODEL;
    }

    private String resolveJwtSecret() {
        String envJwtSecret = System.getenv("JWT_SECRET");
        if (envJwtSecret != null && !envJwtSecret.isBlank()) {
            return envJwtSecret.trim();
        }

        String configJwtSecret = Utils.getOptionalPropertyValue("JWT_SECRET", CONFIG_FILE);
        if (configJwtSecret != null && !configJwtSecret.isBlank()) {
            return configJwtSecret;
        }

        logger.warn("JWT_SECRET was not found. Falling back to a local development secret.");
        return DEFAULT_JWT_SECRET;
    }
}
