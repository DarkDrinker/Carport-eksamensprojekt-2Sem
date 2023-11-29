package app.persistence;

import app.models.Stolper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

import static app.Main.connectionPool;


public class StolperMapper {


    public static List<Stolper> getAllStolper(ConnectionPool connectionPool) {

        List<Stolper> stolperList = new ArrayList<>();

        String sql = "SELECT * from stolper";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int stolper_id = rs.getInt("stolper_id");
                    String stolpe= rs.getString("stolpe");
                    int size= rs.getInt("size");
                    double price = rs.getDouble("price");
                    Stolper stolper = new Stolper(stolper_id, stolpe, size, price);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stolperList;
    }
}