package app.persistence;
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
    
    //SQL handler to execute, this case inserting an order into our DB
    public static int insertOrders(Orders orders, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (user_id, date, carport_length, carport_width, shed_length, shed_width, status) VALUES (?, ?, ?, ?, ?, ?, ?) returning id";

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
                
                //checks to se if any rows were affected
                if (rowsAffected == 0) {
                    throw new DatabaseException("Inserting order fejlede, ingen rækker affected.");
                }

                // Retrieve the generated id and returns it
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

    //SQL handler to execute, this case inserting a orderLine into our DB
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

    public static List<Orders> getSize(int id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> sizelist = new ArrayList<>();
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

    //Pulls a specific order by its id. 
    public static Orders getOrderById(int id, ConnectionPool connectionPool) throws DatabaseException {
        Orders order = null;
        String sql = "SELECT orders.id, orders.date, orders.user_id, public.user.name, public.user.email, public.user.city, public.user.role, public.orders.carport_length, public.orders.carport_width, public.orders.shed_length, public.orders.shed_width, public.orders.status\n" +
                "FROM Orders\n" +
                "INNER JOIN public.user\n" +
                "ON orders.user_id = public.user.id where orders.id = ? ORDER BY id";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Date date = rs.getDate("date");
                    int user_id = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String city = rs.getString("city");
                    double carport_length = rs.getDouble("carport_length");
                    double carport_width = rs.getDouble("carport_width");
                    double shed_length = rs.getDouble("shed_length");
                    double shed_width = rs.getDouble("shed_width");
                    String status = rs.getString("status");
                    order = new Orders(id, date, user_id, name, email, city, carport_length, carport_width, shed_length, shed_width, status);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i getOrderById: " + e.getMessage());
        }

        return order;
    }

    //pulls all orders in our DB and returns them as a MAP
    public static Map<Integer, Orders> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        Map<Integer, Orders> ordersMap = new HashMap<>();
        String sql1 = "SELECT orders.id, orders.date, orders.user_id, public.user.name, public.user.email, public.user.city, public.user.role, public.orders.carport_length, public.orders.carport_width, public.orders.shed_length, public.orders.shed_width, public.orders.status\n" +
                "FROM Orders\n" +
                "INNER JOIN public.user\n" +
                "ON orders.user_id = public.user.id ORDER BY id";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql1)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date date = rs.getDate("date");
                    int user_id = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String city = rs.getString("city");
                    double carport_length = rs.getDouble("carport_length");
                    double carport_width = rs.getDouble("carport_width");
                    double shed_length = rs.getDouble("shed_length");
                    double shed_width = rs.getDouble("shed_width");
                    String status = rs.getString("status");
                    String role = rs.getString("role");
                    Orders order = new Orders(id, date, user_id, name, email, city, carport_length, carport_width, shed_length, shed_width, status);
                    if(role.equals("customer")) {
                        ordersMap.put(id, order);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i getAllOrders 333: " + e.getMessage());
        }

        return ordersMap;
    }

    //pulls specific orderlines by their associated orderId
    public static List<Orderline> getOrderlinesByOrderId(int orders_id, ConnectionPool connectionPool) throws DatabaseException, SQLException {
        String sql = "SELECT id, material_id, quantity, total_price FROM orderline WHERE orders_id = ? ORDER BY id DESC";
        List<Orderline> orderlines = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orders_id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int material_id = rs.getInt("material_id");
                    int quantity = rs.getInt("quantity");
                    int total_price = rs.getInt("total_price");


                    Material material = MaterialMapper.getMaterialById(material_id, connectionPool);

                    Orderline orderline = new Orderline(id, material, quantity, total_price);
                    orderlines.add(orderline);
                }
            } catch (SQLException e) {
                throw new DatabaseException("fejl i orderlinesbyorderID"+e.getMessage());
            }
        }
        return orderlines;
    }
    //SQl Handler to update the orders status of a specific order
    public static void updateorderstatus(String status, int id, ConnectionPool connectionPool) throws DatabaseException, SQLException {
        String sql = "update orders\n" +
                "set status=?\n" +
                "where id=?;";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
            }
        }

        //Pulls specific orders by their associated UserId
    public static List<Orders> getOrdersByUserId(int user_id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> ListOfOrders = new ArrayList<>();
        String sql = "select * from orders where user_id=?";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, user_id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date date = rs.getDate("date");
                    double carport_length = rs.getDouble("carport_length");
                    double carport_width = rs.getDouble("carport_width");
                    double shed_length = rs.getDouble("shed_length");
                    double shed_width = rs.getDouble("shed_width");
                    String status = rs.getString("status");
                    ListOfOrders.add(new Orders(id, date, user_id ,carport_length, carport_width, shed_length, shed_width, status));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl i getOrdersByCustomerId " + e);
        }
        return ListOfOrders;
    }
}