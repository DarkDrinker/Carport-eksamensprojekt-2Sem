package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Orderline;
import app.models.Orders;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    public class OrdersMapper {


        public static Orders insertOrders(Orders orders, List<Orderline> orderlines, ConnectionPool connectionPool) throws DatabaseException {
            String sqlOrders = "INSERT INTO orders (date, user_id, carport_length, carport_width, shed_length, shed_width, status) VALUES (?,?,?,?,?,?,?)";

            int newOrdersId = 0;

            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sqlOrders, Statement.RETURN_GENERATED_KEYS)) {
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
                        newOrdersId = keys.getInt(1);
                    }
                } catch (SQLException e) {
                    throw new DatabaseException("Error in insertOrders with setDate");
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error in insertOrders with getConnection");
            }

            // Insert order lines
            for (Orderline orderline : orderlines) {
                insertOrderline(orderline, newOrdersId, connectionPool);
            }

            orders.setId(newOrdersId);
            return orders;
        }

        public static Orderline insertOrderline(Orderline orderline, int orderId, ConnectionPool connectionPool) throws DatabaseException {
            String sqlOrderline = "INSERT INTO orderline (order_id, material_id, quantity, total_price) VALUES (?,?,?,?)";

            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(sqlOrderline)) {
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
    }
