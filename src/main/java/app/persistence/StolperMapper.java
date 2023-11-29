package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Stolper;

import java.sql.*;
import java.util.HashMap;
import java.sql.Connection;
import java.util.Map;



public class StolperMapper {


    public static Map<Integer, Stolper> getAllStolper(ConnectionPool connectionPool) throws DatabaseException {

        Map<Integer, Stolper> stolperMap = new HashMap<>();

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
                    stolperMap.put(stolper_id, stolper);
                }

            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i StolperMapper");
        }
        return stolperMap;
    }
}