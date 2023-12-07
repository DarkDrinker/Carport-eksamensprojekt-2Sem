package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.models.Material;
import app.models.Orderline;
import app.models.Orders;
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

