package app.persistence;

import app.controllers.OrderController;
import app.exceptions.DatabaseException;
import app.models.Material;
import app.models.Orderline;
import app.models.Orders;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersMapper {


        public static int insertOrders(Orders orders, ConnectionPool connectionPool) throws DatabaseException {
            String sql = "INSERT INTO orders (user_id, date, carport_length, carport_width, shed_length, shed_width, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, orders.getUser_id());
                    ps.setTimestamp(2, new Timestamp(orders.getDate().getTime()));
                    ps.setDouble(3, orders.getCarport_length());
                    ps.setDouble(4, orders.getCarport_width());
                    ps.setDouble(5, orders.getShed_length());
                    ps.setDouble(6, orders.getShed_width());
                    ps.setString(7, orders.getStatus());

                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new DatabaseException("Inserting order fejlede, ingen rækker affected.");
                    }

                    // Retrieve the generated id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        } else {
                            throw new DatabaseException("Inserting order fejlede, ingen ID udtaget.");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Fejl i insertOrders: " + e.getMessage());
            }
        }

        // Insert orderlines
           /* for (Orderline orderline : orderlines) {
                insertOrderline(orderline, newOrdersId, connectionPool);
            }*/

    public static void insertOrderline(Orderline orderline, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "INSERT INTO orderline (orders_id, material_id, quantity, total_price) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderline.getOrders_id());
                ps.setInt(2, orderline.getMaterial().getMaterial_id());
                ps.setInt(3, orderline.getQuantity());
                ps.setDouble(4, orderline.getTotal_price());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new DatabaseException("Inserting orderline fejlede, ingen rækker affected.");
                }
            }
        }
    }

        public static List<Orders> getSize(int id, ConnectionPool connectionPool) throws DatabaseException{

            List<Orders> sizelist= new ArrayList<>();
            String sql = "select carport_length, carport_width, shed_length, shed_width from orders where id=?";

            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        double carport_length = rs.getDouble("carport_length");
                        double carport_width = rs.getDouble("carport_width");
                        double shed_length = rs.getDouble("shed_length");
                        double shed_width = rs.getDouble("shed_width");
                        sizelist.add(new Orders(carport_length, carport_width, shed_length, shed_width));
                    }
                }

            } catch (SQLException e) {
                throw new DatabaseException("Fejl i getSize " + e);
            }
            return sizelist;
        }

        public static Orders getOrderById(int id, ConnectionPool connectionPool) throws DatabaseException {
            Orders order = null;
            String sql = "SELECT * FROM orders WHERE id=?";

            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Date date = rs.getDate("date");
                        int user_id = rs.getInt("user_id");
                        double carport_length = rs.getDouble("carport_length");
                        double carport_width = rs.getDouble("carport_width");
                        double shed_length = rs.getDouble("shed_length");
                        double shed_width = rs.getDouble("shed_width");
                        String status = rs.getString("status");
                        order = new Orders(id, date, user_id, carport_length, carport_width, shed_length, shed_width, status);
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Fejl i getOrderById: " + e.getMessage());
            }

            return order;
        }
    public static Map<Integer, Orders> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        Map<Integer, Orders> ordersMap = new HashMap<>();
        String sql = "SELECT * FROM orders ORDER BY id DESC";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date date = rs.getDate("date");
                    int user_id = rs.getInt("user_id");
                    double carport_length = rs.getDouble("carport_length");
                    double carport_width = rs.getDouble("carport_width");
                    double shed_length = rs.getDouble("shed_length");
                    double shed_width = rs.getDouble("shed_width");
                    String status = rs.getString("status");
                    Orders order = new Orders(id, date, user_id, carport_length, carport_width, shed_length, shed_width, status);
                    ordersMap.put(id, order);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i getAllOrders 333: " + e.getMessage());
        }

        return ordersMap;
    }

    }
