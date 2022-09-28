package DAO;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Users;
import main.DBConnection;
import java.sql.SQLException;
import java.sql.*;


public class userAccess extends Users {

    public userAccess(int userID, String userName, String userPassword)
    {super();}


    /**
     * Method validates the username and password for the login form.
     * @param password
     * @param username
     */
    public static int validateUser(String username, String password) {
        try {
            String sqlQuery = "SELECT * FROM users WHERE user_name = '" + username + "' AND password = '" + password + "'";

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();

            while (rs.next() == true) {

                if (rs.getString("User_Name").equals(username)) {

                    if (rs.getString("Password").equals(password)) {
                        return rs.getInt("User_ID");
                    }

                }

            }
            } catch(SQLException e){
                e.printStackTrace();
            }
        return -1;
    }



    /**
     * Method to get all user data to the getAllUsers ObservableList
     * @throws SQLException
     * @return usersObservableList
     */
   public static ObservableList<userAccess> getAllUsers() throws SQLException {
        ObservableList<userAccess> usersObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String userPassword = rs.getString("Password");
            userAccess user = new userAccess(userID, userName, userPassword);
            usersObservableList.add(user);
        }
        return usersObservableList;
    }

}
