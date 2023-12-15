package app;
import app.controllers.OrderController;
import app.entities.User;
import app.models.Orders;
import app.util.EmailSender;
import config.ThymeleafConfig;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import static app.controllers.UserController.checkUserLoggedIn;


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
        app.get("/order", ctx -> {
            boolean isLoggedIn = UserController.checkUserLoggedIn(ctx);
            ctx.attribute("isLoggedIn", isLoggedIn);
            ctx.render("order.html");
        });
        app.post("/order", ctx -> {
            boolean isLoggedIn = UserController.checkUserLoggedIn(ctx);
            if (isLoggedIn) {
                OrderController.allOrders(ctx, connectionPool);
               {
                    ctx.redirect("/sale"); // Redirect non-admin users to a confirmation page
                }
            } else {
                String email = ctx.formParam("email");
                OrderController.processGuestOrder(ctx, connectionPool, email);
                ctx.attribute("email", email);
                ctx.render("sale.html"); // Render order confirmation for guests
            }
        });
        app.get("/sale", ctx -> {
            Orders orderDetails = ctx.sessionAttribute("orderDetails");
            ctx.attribute("orderDetails", orderDetails);
            ctx.render("sale.html");
        });
        app.post("/sale", ctx -> {
            OrderController.allOrders(ctx, connectionPool);
            ctx.render("sale.html");
        });
        app.get("/saleswindow", ctx -> {
            User currentUser = ctx.sessionAttribute("currentUser");
            if (currentUser != null && "admin".equals(currentUser.getRole())) {
                OrderController.GrabAllOrders(ctx, connectionPool); // Fetch all orders
                ctx.render("saleswindow.html");
            } else {
                ctx.redirect("/frontpage");
            }
        });
        app.get("/saleswindow/:orderId", ctx -> {
            User currentUser = ctx.sessionAttribute("currentUser");
            if (currentUser != null && "admin".equals(currentUser.getRole())) {
                OrderController.GrabOneOrder(ctx, connectionPool); // Fetch details of a specific order
                ctx.render("saleswindow.html"); // Render the same page with details of the specific order
            } else {
                ctx.redirect("/frontpage");
            }
        });

        app.post("/saleswindow", ctx -> {
            OrderController.allOrders(ctx, connectionPool);
            ctx.render("saleswindow.html");
        });
        app.get("/cart", ctx -> ctx.render("cart.html"));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> UserController.login(ctx, connectionPool));
        app.get("/logout", ctx -> UserController.logout(ctx));
        //app.get("/orders/{id}", ctx -> OrderController.getorders(ctx, connectionPool));
    }
}

//Excempel på at man kan sende en mail, kan dog ikke få den sat fast på en knap.
/*try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }*/