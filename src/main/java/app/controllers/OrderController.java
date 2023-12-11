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

public class OrderController {

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

        // Extract form parameters for order details
        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));
        String status = ctx.formParam("status");

        // Create an Orders object with the extracted details
        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), user.getId(), carportLength, carportWidth, shedLength, shedWidth, status);

        try {
            // Call the insertOrders method to insert the order into the database and get the generated id
            int generatedOrderId = OrdersMapper.insertOrders(orders, connectionPool);

            // Use the generatedOrderId for further processing or pass it to other methods as needed
            // For example, pass it to the calculateAndRender method
            calculateAndRender(ctx, generatedOrderId, connectionPool);

        } catch (DatabaseException e) {
            // Handle any database exception by rethrowing or logging
            throw new DatabaseException("Fejl i allOrders: " + e.getMessage());
        }
    }


    public static void calculateAndRender(Context ctx, int generatedOrderId, ConnectionPool connectionPool) throws DatabaseException {
        // Use the generatedOrderId for further processing
        double numberOfPosts = Calculator.calculatePost(generatedOrderId, connectionPool);
        double numberOfRafters = Calculator.calculateRafter(generatedOrderId, connectionPool);
        double numberOfStraps = Calculator.calculateStraps(generatedOrderId, connectionPool);

        ctx.attribute("numberOfPosts", (int) numberOfPosts);
        ctx.attribute("numberOfRafters", (int) numberOfRafters);
        ctx.attribute("numberOfStraps", (int) numberOfStraps);


            ctx.render("salesperson.html");
        }
    public static int insertOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        User user = ctx.sessionAttribute("currentUser");

        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));


        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), user.getId(), carportLength, carportWidth, shedLength, shedWidth, "Pending");

        // Call the insertOrders method to insert the order into the database and get the generated id
        return OrdersMapper.insertOrders(orders, connectionPool);
    }


   /* public static void calcPosts(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.formParam("id"));
        int post = (int) Calculator.calculatePost(id, connectionPool);
        ctx.attribute("post", post);
        ctx.render("salesperson.html");
    }

    public static void calcRafters(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.formParam("id"));
        int rafter = (int) Calculator.calculateRafter(id, connectionPool);
        ctx.attribute("rafter", rafter);
        ctx.render("salesperson.html");
    }

    public static void calcStraps(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.formParam("id"));
        int strap = (int) Calculator.calculateStraps(id, connectionPool);
        ctx.attribute("strap", strap);
        ctx.render("salesperson.html");
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

