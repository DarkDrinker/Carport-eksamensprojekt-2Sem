package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Spaer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SpaerMapper {

    public static Map<Integer, Spaer> getAllSpaer(ConnectionPool connectionPool) throws DatabaseException {

        Map<Integer, Spaer> spaerMap = new HashMap<>();

        String sql = "SELECT * from spaer";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int spaer_id = rs.getInt("spaer_id");
                    String spaertrae= rs.getString("spaertrae");
                    int size= rs.getInt("size");
                    double price = rs.getDouble("price");
                    Spaer spaer = new Spaer(spaer_id, spaertrae, size, price);
                    spaerMap.put(spaer_id, spaer);
                }

            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i SpaerMapper");
        }
        return spaerMap;
    }
}
