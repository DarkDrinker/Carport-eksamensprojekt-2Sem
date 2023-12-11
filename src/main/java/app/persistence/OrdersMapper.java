package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Orderline;
import app.models.Orders;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        // Insert order lines
           /* for (Orderline orderline : orderlines) {
                insertOrderline(orderline, newOrdersId, connectionPool);
            }*/

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
