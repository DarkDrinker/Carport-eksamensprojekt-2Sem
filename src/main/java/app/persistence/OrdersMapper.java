package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Orderline;
import app.models.Orders;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersMapper {


    public static Orders insertOrders(Orders orders, List<Orderline> orderlines, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (date, user_id, carport_length, carport_width, shed_length, shed_width, status) VALUES (?,?,?,?,?,?,?)";

        int newOrderId = 0;

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, orders.getDate());
                ps.setInt(2, orders.getUser_id());
                ps.setDouble(3, orders.getCarport_length());
                ps.setDouble(4, orders.getCarport_width());
                ps.setDouble(5, orders.getShed_length());
                ps.setDouble(6, orders.getShed_width());
                ps.setString(7, orders.getStatus());

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 1) {
                    ResultSet keys = ps.getGeneratedKeys();
                    keys.next();
                    newOrderId = keys.getInt(1);
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error in insertOrders with setDate");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error in insertOrders with getConnection");
        }

        // Insert order lines
        for (Orderline orderline : orderlines) {
            insertOrderline(orderline, newOrderId, connectionPool);
        }

        orders.setId(newOrderId);
        return orders;
    }

    public static Orderline insertOrderline(Orderline orderline, int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orderline (order_id, material_id, quantity, total_price) VALUES (?,?,?,?)";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, orderline.getMaterial_id());
                ps.setInt(3, orderline.getQuantity());
                ps.setDouble(4, orderline.getTotal_price());

                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DatabaseException("Error in insertOrderline with SQL query");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error in insertOrderline with database connection");
        }

        return orderline;
    }
}