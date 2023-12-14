package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.models.Material;
import app.models.Orderline;
import app.models.Orders;
import app.services.Calculator;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import app.persistence.OrdersMapper;
import io.javalin.http.Context;

import java.sql.Date;
import java.util.*;

import static app.persistence.OrdersMapper.getAllOrders;
import static app.persistence.OrdersMapper.getOrderById;

public class OrderController {
    private static final int GUEST_USER_ID = 0;
    public static void initializeMaterialMap(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        Map<Integer, Material> materialMap = ctx.sessionAttribute("materialMap");

        if (materialMap == null) {
            // Hent alle materialer fra DB og gem i Hashmap
            materialMap = MaterialMapper.getAllMaterial(connectionPool);
            ctx.sessionAttribute("materialMap", materialMap);

            List<Material> materialList = new ArrayList<>(materialMap.values());

            // Sort the materialList by material_id
            materialList.sort(Comparator.comparing(Material::getMaterial_id));

            ctx.sessionAttribute("materialList", materialList);
        }
    }


    public static void allOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        // Retrieve the current user from the session
        User user = ctx.sessionAttribute("currentUser");

        // If the user is logged in, we will use their ID and if not we will use a default ID (0 as a placeholder)
        int userId = (user != null) ? user.getId() : GUEST_USER_ID;

        // Extract form parameters for order details
        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));
        String status = ctx.formParam("status");

        // Create an Orders object with the extracted details
        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), userId, carportLength, carportWidth, shedLength, shedWidth, status);

        try {
            // Call the insertOrders method to insert the order into the database and get the generated id
            int generatedOrderId = OrdersMapper.insertOrders(orders, connectionPool);

            // Retrieve the inserted order details
            Orders insertedOrder = getOrderById(generatedOrderId, connectionPool);

            // Use the insertedOrder for further processing or pass it to other methods as needed
            calculateAndRender(ctx, insertedOrder, connectionPool);

        } catch (DatabaseException e) {
            // Handle any database exception by rethrowing or logging
            throw new DatabaseException("Fejl i allOrders: " + e.getMessage());
        }
    }

    public static void calculateAndRender(Context ctx, Orders orders, ConnectionPool connectionPool) throws DatabaseException {
        // Use the orders for further processing
        double numberOfPosts = Calculator.calculatePost(orders.getId(), connectionPool);
        double numberOfRafters = Calculator.calculateRafter(orders.getId(), connectionPool);
        double numberOfStraps = Calculator.calculateStraps(orders.getId(), connectionPool);

        ctx.attribute("orderDetails", orders);
        ctx.attribute("numberOfPosts", (int) numberOfPosts);
        ctx.attribute("numberOfRafters", (int) numberOfRafters);
        ctx.attribute("numberOfStraps", (int) numberOfStraps);

        ctx.render("sale.html");
    }
    public static void GrabAllOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        try {
            Map<Integer, Orders> allOrders = getAllOrders(connectionPool);
            ctx.sessionAttribute("allorders", allOrders);
        } catch (DatabaseException e) {
            // Handle any database exception by rethrowing or logging
            throw new DatabaseException("Fejl i GrabAllOrders: " + e.getMessage());
        }
    }

    public static void GrabOneOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.pathParam("orderId"));
        try {
            Orders order = getOrderById(id, connectionPool);
            ctx.sessionAttribute("SessionOrder", order);
        } catch (DatabaseException e) {
            e.getMessage();
        }
    }
    public static void processGuestOrder(Context ctx, ConnectionPool connectionPool, String guestEmail) throws DatabaseException {        // Retrieve the current user from the session
        User user = ctx.sessionAttribute("currentUser");
        // If the user is logged in, we will use their ID and if not we will use a default ID (0 as a placeholder)
        int userId = (user != null) ? user.getId() : GUEST_USER_ID;

        // Extract form parameters for order details
        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));
        String status = ctx.formParam("status");

        // Create an Orders object with the extracted details
        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), userId, carportLength, carportWidth, shedLength, shedWidth, status);

        try {
            // Call the insertOrders method to insert the order into the database and get the generated id
            int generatedOrderId = OrdersMapper.insertOrders(orders, connectionPool);

            // Retrieve the inserted order details
            Orders insertedOrder = getOrderById(generatedOrderId, connectionPool);

            // Use the insertedOrder for further processing or pass it to other methods as needed
            calculateAndRender(ctx, insertedOrder, connectionPool);

        } catch (DatabaseException e) {
            // Handle any database exception by rethrowing or logging
            throw new DatabaseException("Fejl i processGuestOrder: " + e.getMessage());
        }
    }

   /* public static int insertOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        User user = ctx.sessionAttribute("currentUser");

        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));


        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), user.getId(), carportLength, carportWidth, shedLength, shedWidth, "Pending");

        // Call the insertOrders method to insert the order into the database and get the generated id
        return OrdersMapper.insertOrders(orders, connectionPool);
    }
    public static void showOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            Orders order = getOrderById(orderId, connectionPool);

            if (order != null) {
                ctx.attribute("orderDetails", order);
                ctx.render("sale.html");
            } else {
                ctx.status(404).result("Order not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid order ID format");
        } catch (DatabaseException e) {
            throw new DatabaseException("Fejl i showOrders: " + e.getMessage());
        }
    }
    public static void initializeOrdersMap(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        Map<Integer, Orders> ordersMap = ctx.sessionAttribute("ordersMap");

        if (ordersMap == null) {
            ordersMap = getAllOrders(connectionPool); // Fetch all orders
            ctx.sessionAttribute("ordersMap", ordersMap);

            List<Orders> ordersList = new ArrayList<>(ordersMap.values());
            ordersList.sort(Comparator.comparing(Orders::getId));
            ctx.sessionAttribute("ordersList", ordersList);
        }
    }

    public static void calcPosts(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.formParam("id"));
        int post = (int) Calculator.calculatePost(id, connectionPool);
        ctx.attribute("post", post);
        ctx.render("sale.html");
    }

    public static void calcRafters(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.formParam("id"));
        int rafter = (int) Calculator.calculateRafter(id, connectionPool);
        ctx.attribute("rafter", rafter);
        ctx.render("sale.html");
    }

    public static void calcStraps(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.formParam("id"));
        int strap = (int) Calculator.calculateStraps(id, connectionPool);
        ctx.attribute("strap", strap);
        ctx.render("sale.html");
    }*/


        /*public static void processOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
            User user = (User) ctx.sessionAttribute("currentUser");
            Cart cart = (Cart) ctx.sessionAttribute("cart");

            double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
            double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
            double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
            double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));

            Orders orders = new Orders(0, new Date(System.currentTimeMillis()), user.getId(), carportLength, carportWidth, shedLength, shedWidth, "Pending");

           try {
                OrdersMapper.insertOrders(orders, cart.getCartItems(), connectionPool);
                ctx.render("order-confirmation.html");
            } catch (DatabaseException e) {
                throw new DatabaseException("Fejl i processOrders");
            }
        }*/
}

