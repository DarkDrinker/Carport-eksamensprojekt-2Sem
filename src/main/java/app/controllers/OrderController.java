package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.models.Material;
import app.models.Orderline;
import app.models.Orders;
import app.persistence.UserMapper;
import app.services.Calculator;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import app.persistence.OrdersMapper;
import io.javalin.http.Context;

import java.sql.Date;
import java.sql.SQLException;
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



    public static int allOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        // Retrieve the current user from the session
        User user = ctx.sessionAttribute("currentUser");
        int userId = (user != null) ? user.getId() : GUEST_USER_ID; // Use user ID or default ID

        // Extract form parameters for order details
        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));
        String status = ctx.formParam("status");

        // Create an Orders object
        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), userId, carportLength, carportWidth, shedLength, shedWidth, status);

        try {
            // Insert the order and get the generated ID
            int generatedOrderId = OrdersMapper.insertOrders(orders, connectionPool);
            //Orders insertedOrder = getOrderById(generatedOrderId, connectionPool);
            // Additional processing with insertedOrder
            //calculateAndRender(ctx, insertedOrder, connectionPool);

            // Return the generated order ID
            return generatedOrderId;
        } catch (DatabaseException e) {
            // Handle database exceptions
            throw new DatabaseException("Fejl i allOrders: " + e.getMessage());
        }
    }


    public static void calculateAndRender(Context ctx, Orders orders, ConnectionPool connectionPool) throws DatabaseException, SQLException {
        // Retrieve all materials from the database
        Map<Integer, Material> materials = MaterialMapper.getAllMaterial(connectionPool);

        // Calculate the quantities
        int numberOfPosts = (int) Calculator.calculatePost(orders.getId(), connectionPool);
        int totalRafters = (int) Calculator.calculateRafter(orders.getId(), connectionPool);
        int totalStraps = (int) Calculator.calculateStraps(orders.getId(), connectionPool);

        // Retrieve the Material objects directly using their IDs
        Material postMaterial = materials.get(16); // For posts
        int[] availableSizes = {600, 540, 480, 420, 360, 300};
        int closestRafterSize = Calculator.getClosestSize((int)orders.getCarport_width(), availableSizes);
        int closestStrapSize = Calculator.getClosestSize((int)orders.getCarport_length(), availableSizes);

        // Retrieve the Material objects using the calculated sizes
        int rafterMaterialId = MaterialMapper.getMaterialIdBySize(closestRafterSize, connectionPool);
        int strapMaterialId = MaterialMapper.getMaterialIdBySize(closestStrapSize, connectionPool);
        Material rafterMaterial = materials.get(rafterMaterialId);
        Material strapMaterial = materials.get(strapMaterialId);

        // Create orderlines with quantity and total price
        Orderline postOrderline = new Orderline(0, orders.getId(), postMaterial, numberOfPosts, postMaterial.getPrice() * numberOfPosts);
        Orderline rafterOrderline = new Orderline(0, orders.getId(), rafterMaterial, totalRafters, rafterMaterial.getPrice() * totalRafters);
        Orderline strapOrderline = new Orderline(0, orders.getId(), strapMaterial, totalStraps, strapMaterial.getPrice() * totalStraps);

        // Add these orderlines to a list for the template
        List<Orderline> orderlines = Arrays.asList(postOrderline, rafterOrderline, strapOrderline);

        OrdersMapper.insertOrderline(postOrderline, connectionPool);
        OrdersMapper.insertOrderline(rafterOrderline, connectionPool);
        OrdersMapper.insertOrderline(strapOrderline, connectionPool);

        double totalPricePosts = postMaterial.getPrice() * numberOfPosts;
        double totalPriceRafters = rafterMaterial.getPrice() * totalRafters;
        double totalPriceStraps = strapMaterial.getPrice() * totalStraps;
        double totalPrice = totalPriceStraps+totalPricePosts+totalPriceRafters;

        ctx.sessionAttribute("totalPrice", totalPrice);
        ctx.sessionAttribute("totalPricePosts", totalPricePosts);
        ctx.sessionAttribute("totalPriceRafters", totalPriceRafters);
        ctx.sessionAttribute("totalPriceStraps", totalPriceStraps);
        ctx.sessionAttribute("numberOfPosts", numberOfPosts);
        ctx.sessionAttribute("totalRafters", totalRafters);
        ctx.sessionAttribute("totalStraps", totalStraps);

        ctx.attribute("SessionOrder", orders);
        ctx.attribute("orderlines", orderlines);
        ctx.render("order-conformation.html");

    }

    //Grabs all available orders and sets them into ctx.sessionAttribute
    public static void GrabAllOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        try {
            //Checks against if the Attribute has already been filled before.
            if(ctx.sessionAttribute("allorders") != null ) {

            } else {
                Map<Integer, Orders> allOrders = getAllOrders(connectionPool);
                ctx.sessionAttribute("allorders", allOrders);
            }

        } catch (DatabaseException e) {
            // Handle any database exception by rethrowing or logging
            throw new DatabaseException("Fejl i GrabAllOrders: " + e.getMessage());
        }
    }

    public static void processOrder(Context ctx, ConnectionPool connectionPool) throws Exception {
        String email;
        String name;
        String city;
        int generated_userID;
        User user = ctx.sessionAttribute("currentUser");
        // Retrieve the personal information from the form.
        if(user != null){
            email = user.getEmail();
            name = user.getName();
            city = user.getCity();
            generated_userID = user.getId();
        } else {
            email = ctx.formParam("email");
            name = ctx.formParam("name");
            city = ctx.formParam("city");
            generated_userID = UserMapper.CreateMiniUser(ctx, email, name, city, connectionPool);
            if(generated_userID == 0) {
                throw new Exception("didnt create a new user, probaly already have an account?");
            }
        }


        // Extract form parameters for order details
        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));
        String status = ctx.formParam("status");
        //Creates a mini user that almost has all the things.



        // Create an Orders object with the extracted details
        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), generated_userID, carportLength, carportWidth, shedLength, shedWidth, status);
        try {
            // Call the insertOrders method to insert the order into the database and get the generated id
            int generatedOrderId = OrdersMapper.insertOrders(orders, connectionPool);

            // Retrieve the inserted order details
            Orders insertedOrder = getOrderById(generatedOrderId, connectionPool);

            // Use the insertedOrder for further processing or pass it to other methods as needed
            calculateAndRender(ctx, insertedOrder, connectionPool);
        } catch (DatabaseException | SQLException e) {
            // Handle any database exception by rethrowing or logging
            throw new DatabaseException("Fejl i processGuestOrder: " + e.getMessage());
        }
    }

    //Updates status of an order.
    public static void updatestatus(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        String status = ctx.formParam("status");
        int id = Integer.parseInt(ctx.formParam("orderId"));
        System.out.println(id);
        try {
            OrdersMapper.updateorderstatus(status, id, connectionPool);
        } catch (DatabaseException | SQLException e){
            throw new DatabaseException("Error i Ordercontroller updatestatus" + e.getMessage());
        }
    }

}

