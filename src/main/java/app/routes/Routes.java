package app.routes;

import app.controllers.AiController;
import app.controllers.AuthController;
import app.controllers.ContentController;
import app.controllers.InteractionController;
import io.javalin.router.JavalinDefaultRoutingApi;

public class Routes {
    public static final String API_CONTEXT_PATH = "/api/v1";

    private final AiController aiController;
    private final AuthController authController;
    private final ContentController contentController;
    private final InteractionController interactionController;

    public Routes(
            AiController aiController,
            AuthController authController,
            ContentController contentController,
            InteractionController interactionController
    ) {
        this.aiController = aiController;
        this.authController = authController;
        this.contentController = contentController;
        this.interactionController = interactionController;
    }

    public void register(JavalinDefaultRoutingApi routingApi) {
        SecurityRoutes.register(routingApi);
        AuthRoutes.register(routingApi, authController);
        ContentRoutes.register(routingApi, contentController);
        InteractionRoutes.register(routingApi, interactionController);
        AiRoutes.register(routingApi, aiController);
    }
}
