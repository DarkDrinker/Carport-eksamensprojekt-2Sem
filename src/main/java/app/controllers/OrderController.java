package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.models.Material;
import app.models.Orders;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import app.persistence.OrdersMapper;
import io.javalin.http.Context;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderController {


    public static void allMaterial(Context ctx, ConnectionPool connectionPool){

        try {
            List<Material> materialList =  new ArrayList<>( MaterialMapper.getAllMaterial(connectionPool).values());
            ctx.attribute("materialList", materialList);

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
    public static void allOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        User user = (User) ctx.sessionAttribute("currentUser");
        Cart cart = (Cart) ctx.sessionAttribute("cart");
        double carportLength = Double.parseDouble(ctx.formParam("carport_length"));
        double carportWidth = Double.parseDouble(ctx.formParam("carport_width"));
        double shedLength = Double.parseDouble(ctx.formParam("shed_length"));
        double shedWidth = Double.parseDouble(ctx.formParam("shed_width"));

        Orders orders = new Orders(0, new Date(System.currentTimeMillis()), user.getId(), carportLength, carportWidth, shedLength, shedWidth, "Pending");
        try {
            orders = OrdersMapper.insertOrders(orders, cart.getCartItems(), connectionPool );
            ctx.render("cart.html");

        } catch (DatabaseException e) {
            throw new DatabaseException("Fejl i allOrders"+e);
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

