package app;

import config.ThymeleafConfig;
import java.util.List;
import app.controllers.OrderController;
import app.entities.User;
import app.models.Orderline;
import app.models.Orders;
import app.util.EmailSender;
import app.persistence.OrdersMapper;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;



public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres"; //safpiq-rutqiv-6Xugna
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
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.get("/order-confirmation", ctx -> ctx.render("order-conformation.html"));
        app.get("/standardcarport", ctx -> ctx.render("standardcarport.html"));
        app.get("/find-fog", ctx -> ctx.render("find-fog.html"));
        app.get("/cart", ctx -> ctx.render("cart.html"));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.get("/logout", UserController::logout);

        app.post("/login", ctx -> UserController.login(ctx, connectionPool));
        app.post("/frontpage", ctx -> ctx.render("frontpage.html"));
        app.post("/createuser", ctx -> UserController.createuser(ctx, connectionPool));
        app.post("/order", ctx -> OrderController.processOrder(ctx, connectionPool));

        app.get("/materials", ctx -> {
            OrderController.initializeMaterialMap(ctx, connectionPool);
            ctx.render("materials.html");
        });

        app.get("/order", ctx -> {
            boolean isLoggedIn = UserController.checkUserLoggedIn(ctx);
            ctx.attribute("isLoggedIn", isLoggedIn);
            ctx.render("order.html");
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
                double totalPrice = orderlines.stream()
                        .mapToDouble(Orderline::getTotal_price)
                        .sum();
                ctx.attribute("totalPrice", totalPrice);
                ctx.attribute("SessionOrder", order);
                ctx.attribute("orderlines", orderlines);
                ctx.render("sale.html");
            } else {
                ctx.redirect("/frontpage");
            }
        });

        app.post("/update-status", ctx -> {
            OrderController.updatestatus(ctx, connectionPool);
            ctx.result("Status updated succesfully");
        });

        app.get("/customerOrders/{id}", ctx -> {
            List<Orders> ListOfOrders = OrdersMapper.getOrdersByUserId(Integer.parseInt(ctx.pathParam("id")), connectionPool);
            ctx.sessionAttribute("AllOrderByCustomer",ListOfOrders);
            ctx.render("customerOrders.html");
        });
        app.get("/customerOrder/{orderId}", ctx -> {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            Orders order = OrdersMapper.getOrderById(orderId, connectionPool);
            List<Orderline> orderlines = OrdersMapper.getOrderlinesByOrderId(orderId, connectionPool);
            ctx.attribute("SessionOrder",order);
            ctx.attribute("orderlines", orderlines);
            ctx.render("customersale.html");
        });
    }
}


/*
//Excempel på at man kan sende en mail, kan dog ikke få den sat fast på en knap.
try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
