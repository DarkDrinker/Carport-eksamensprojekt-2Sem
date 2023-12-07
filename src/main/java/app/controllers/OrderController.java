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
        User user = (User) ctx.sessionAttribute("currentUser");

        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));
        String status = ctx.formParam("status");

        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), user.getId(), carportLength, carportWidth, shedLength, shedWidth, status);

        List<Orderline> orderlines = new ArrayList<>();

        // Add your logic to populate the orderlines list based on the form parameters or other data

        try {
            // Call the insertOrders method to insert the order and order lines
            orders = OrdersMapper.insertOrders(orders, orderlines, connectionPool);

            // Redirect or render your desired page
            ctx.render("cart.html");

        } catch (DatabaseException e) {
            throw new DatabaseException("Fejl i allOrders" + e);
        }
    }

    public static void calculateAndRender(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int id = Integer.parseInt(ctx.formParam("id"));
        double numberOfPosts = Calculator.calculatePost(id, connectionPool);
        double numberOfRafters = Calculator.calculateRafter(id, connectionPool);
        double numberOfStraps = Calculator.calculateStraps(id, connectionPool);

        ctx.attribute("numberOfPosts", (int) numberOfPosts);
        ctx.attribute("numberOfRafters", (int) numberOfRafters);
        ctx.attribute("numberOfStraps", (int) numberOfStraps);

        ctx.render("salesperson.html");
    }
    public static void insertOrders(Context ctx, List<Orderline> orderlines, ConnectionPool connectionPool) throws DatabaseException {
        User user = ctx.sessionAttribute("currentUser");

        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));

        // Create Orders object with the additional parameters
        Orders orders = new Orders();
        orders.setUser_id(user.getId());
        orders.setCarport_length(carportLength);
        orders.setCarport_width(carportWidth);
        orders.setShed_length(shedLength);
        orders.setShed_width(shedWidth);
        orders.setStatus("Pending");

        // Assuming you have a method in OrdersMapper to insert orders with orderlines
        Orders insertedOrder = OrdersMapper.insertOrders(orders, orderlines, connectionPool);

        // Add the dimensions to the context for Thymeleaf
        ctx.attribute("carportLength", orders.getCarport_length());
        ctx.attribute("carportWidth", orders.getCarport_width());
        ctx.attribute("shedLength", orders.getShed_length());
        ctx.attribute("shedWidth", orders.getShed_width());

        // Redirect or render as needed
        ctx.redirect("/salesperson"); // Example redirect
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

