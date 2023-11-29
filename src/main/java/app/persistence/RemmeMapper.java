package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Remme;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RemmeMapper {

    public static Map<Integer, Remme> getAllRemme(ConnectionPool connectionPool) throws DatabaseException {

        Map<Integer, Remme> remmeMap = new HashMap<>();

        String sql = "SELECT * from remme";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int remme_id = rs.getInt("remme_id");
                    String rem= rs.getString("rem");
                    int size= rs.getInt("size");
                    double price = rs.getDouble("price");
                    Remme remme = new Remme(remme_id, rem, size, price);
                    remmeMap.put(remme_id, remme);
                }

            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i RemmeMapper");
        }
        return remmeMap;
    }
}