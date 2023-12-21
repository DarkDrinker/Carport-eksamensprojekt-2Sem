package app;
import app.controllers.OrderController;
import app.entities.User;
import app.models.Orderline;
import app.models.Orders;
import app.persistence.OrdersMapper;
import app.util.EmailSender;
import config.ThymeleafConfig;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.List;

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
                int orderId = OrderController.allOrders(ctx, connectionPool);
                ctx.sessionAttribute("orderId", orderId);
                ctx.redirect("/order-confirmation");
            } else {
                String email = ctx.formParam("email");
                // Process guest order and redirect to order-confirmation page with guest email
                OrderController.processGuestOrder(ctx, connectionPool, email);
                ctx.attribute("email", email);
                ctx.redirect("/order-confirmation");
            }
        });

        app.get("/order-confirmation", ctx -> {
            int orderId = ctx.sessionAttribute("orderId");
            // Fetch the order and orderline details
            Orders order = OrdersMapper.getOrderById(orderId, connectionPool);
            List<Orderline> orderlines = OrdersMapper.getOrderlinesByOrderId(orderId, connectionPool);
            ctx.attribute("SessionOrder", order);
            ctx.attribute("orderlines", orderlines);
            ctx.render("order-confirmation.html");
        });

        app.post("/order-conformation", ctx -> {
            OrderController.allOrders(ctx, connectionPool);
            ctx.render("order-conformation.html");
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
        app.get("/saleswindow/{orderId}", ctx -> {
            User currentUser = ctx.sessionAttribute("currentUser");
            if (currentUser != null && "admin".equals(currentUser.getRole())) {
                int orderId = Integer.parseInt(ctx.pathParam("orderId"));
                Orders order = OrdersMapper.getOrderById(orderId, connectionPool);
                List<Orderline> orderlines = OrdersMapper.getOrderlinesByOrderId(orderId, connectionPool);
                ctx.attribute("SessionOrder", order);
                ctx.attribute("orderlines", orderlines);
                ctx.render("sale.html");
            } else {
                ctx.redirect("/frontpage");
            }
        });

        app.get("/cart", ctx -> ctx.render("cart.html"));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> UserController.login(ctx, connectionPool));
        app.get("/logout", UserController::logout);
    }
}

//Excempel på at man kan sende en mail, kan dog ikke få den sat fast på en knap.
/*
 });


try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
