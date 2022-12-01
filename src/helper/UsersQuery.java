package helper;

import Model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * helper class that Queries the users in the database.
 * */
public abstract class UsersQuery {

    /**
     * Queries the database to check username and password exist and match
     * @param password String
     * @param userName String
     * @return true if a match is found/else returns false
     * @throws SQLException Catches SQLException and prints stacktrace.
     * */
    public static boolean login(String userName, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE User_Name=? AND Password=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ps.setString(2, password);

            ps.execute();
            ResultSet resultSet = ps.getResultSet();
            while(resultSet.next()) {
                String dbUserName = resultSet.getString("User_Name");
                String dbPassword = resultSet.getString("Password");
                int dbUserId = resultSet.getInt("User_ID");

                if (userName.equals(dbUserName) && password.equals(dbPassword)) {
                    return true;
                }
        }
        return false;
    }

    /**
     * Queries the database and creates an ObservableList of Model.Users
     * @return ObservableList of Model.Users
     * @throws SQLException Catches SQLException and prints stacktrace.
     * */
    public static ObservableList<Users> getUserList() throws SQLException {

        ObservableList<Users> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();
            ;

            while (resultSet.next()) {
                Users newUser = new Users(
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getString("Password")
                );

                users.add(newUser);
            }
            return users;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

}

