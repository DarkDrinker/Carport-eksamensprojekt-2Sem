package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Material;
import app.models.Orderline;


import java.sql.*;
import java.util.HashMap;
import java.sql.Connection;
import java.util.Map;



public class MaterialMapper {


    public static Map<Integer, Material> getAllMaterial(ConnectionPool connectionPool) throws DatabaseException {

        Map<Integer, Material> materialMap = new HashMap<>();

        String sql = "SELECT * from material";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int material_id = rs.getInt("material_id");
                    String material_description = rs.getString("material_description");
                    int size = rs.getInt("size");
                    double price = rs.getDouble("price");
                    Material material = new Material(material_id, material_description, size, price);
                    materialMap.put(material_id, material);
                }

            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i MaterialMapper");
        }
        return materialMap;
    }

    public static int getMaterialIdBySize(int size, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "SELECT material_id FROM material WHERE size = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, size); // Set the size parameter
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("material_id");
                } else {
                    throw new DatabaseException("Ingen materiale for den størrelse");
                }
            }
        }
    }



}