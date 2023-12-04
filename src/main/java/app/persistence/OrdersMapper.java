package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Orderline;
import app.models.Orders;

import java.sql.*;

public class OrdersMapper {



    public static Orders insertOrders(Orders orders, ConnectionPool connectionPool) throws DatabaseException {

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

                // Hent det nye id for ordren
                if (rowsAffected == 1) {
                    ResultSet keys = ps.getGeneratedKeys();
                    keys.next();
                    newOrderId = keys.getInt(1);
                }

            } catch (SQLException e) {
                throw new DatabaseException("Fejl i insertOrders med setDate");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl i insertOrders med getConnection");
        }
        /*for (Orderline orderline : orderlines) {
            insertOrderline(orderline, newOrderId, connectionPool);
        }*/
        orders.setId(newOrderId);
        return orders;

    }

    public static Orderline insertOrderline(Orderline orderline, int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orderline (order_id, quantity, top_id, bottom_id, total_price) VALUES (?,?,?,?,?)";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, orderline.getQuantity());

                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DatabaseException("Fejl i insertOrderline med SQL query: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i insertOrderline med forbindelse til database: " + e.getMessage());
        }

        return orderline;
    }
}