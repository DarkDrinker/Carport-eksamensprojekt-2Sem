package app.persistence;
import app.entities.User;
import app.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {
    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException{
       String sql = "select * from \"user\" where email=? and password=?";

       try(Connection connection = connectionPool.getConnection())
       {
           try(PreparedStatement ps = connection.prepareStatement(sql))
           {
               ps.setString(1,email);
               ps.setString(2,password);
               ResultSet rs = ps.executeQuery();
               if(rs.next()){
                   int id = rs.getInt("id");
                   return new User(id, email, password);
               } else {
                throw new DatabaseException("Login er desværre forkert");
               }
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

    }

    public static void createuser(String name, String password, String adresse, String email, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "insert into \"user\" (name, password, adresse, email) values (?,?,?,?)";

        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                ps.setString(1, name);
                ps.setString(2, password);
                ps.setString(3, adresse);
                ps.setString(4, email);
                int rowsAffected =  ps.executeUpdate();
                if (rowsAffected != 1)
                {
                    throw new DatabaseException("Fejl ved oprettelse af ny bruger");
                }
            }
        }
        catch (SQLException e)
        {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value "))
            {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }

            throw new DatabaseException(msg);
        }
    }

    public int updateBalance(int userId, int balance, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET balance = ? WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection()){

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setInt(2, balance);

                int rowAffected = ps.executeUpdate();
                return rowAffected;

            } catch (SQLException ex) {
                throw new DatabaseException(ex.getMessage());
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Forbindelse kunne ikke oprettes");
        }
    }


}
