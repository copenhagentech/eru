package app.routes;

import app.security.AppRole;
import io.javalin.router.JavalinDefaultRoutingApi;

import java.util.Map;

public class SecurityRoutes {

    private SecurityRoutes() {
    }

    public static void register(JavalinDefaultRoutingApi routes) {
        routes.get("/protected/open_demo", ctx -> ctx.json(Map.of("message", "Hello from open endpoint")), AppRole.ANYONE);
        routes.get("/protected/user_demo", ctx -> ctx.json(Map.of("message", "Hello from USER protected endpoint")), AppRole.USER);
        routes.get("/protected/admin_demo", ctx -> ctx.json(Map.of("message", "Hello from ADMIN protected endpoint")), AppRole.ADMIN);
    }
}
