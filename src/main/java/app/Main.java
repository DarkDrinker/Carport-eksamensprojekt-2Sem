package app;

import app.controllers.OrderController;
import config.ThymeleafConfig;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            JavalinThymeleaf.init(ThymeleafConfig.templateEngine());
        }).start(7070);

        // Routing

        app.get("/", ctx -> ctx.render("frontpage.html"));
        app.get("/frontpage", ctx -> ctx.render("frontpage.html"));
        app.post("/frontpage", ctx -> ctx.render("frontpage.html"));
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.post("/createuser",ctx -> UserController.createuser(ctx, connectionPool ));
        app.get("/materials", ctx -> {
            OrderController.initializeMaterialMap(ctx, connectionPool);
            ctx.render("materials.html");
        });
        app.get("/order", ctx -> ctx.render("order.html"));
        app.post("/order", ctx -> {
            OrderController.initializeMaterialMap(ctx, connectionPool);
            OrderController.allOrders(ctx, connectionPool);
            ctx.render("cart.html");
        });

        app.get("/cart", ctx -> ctx.render("cart.html"));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> UserController.login(ctx, connectionPool));
        app.get("/logout", ctx -> UserController.logout(ctx));
        //app.get("/orders/{id}", ctx -> OrderController.getorders(ctx, connectionPool));
    }
}