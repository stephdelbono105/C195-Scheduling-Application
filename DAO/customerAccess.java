package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;
import model.Customers;


import java.sql.*;

public class customerAccess {


    /**
     * Creates ObservableList that gets all customer data.
     * @throws SQLException
     * @return customersObservableList
     */
    public static ObservableList<Customers> getAllCustomers(Connection connection) throws SQLException {

        String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code," +
                " customers.Phone, customers.Division_ID, first_level_divisions.Division FROM customers INNER JOIN first_level_divisions" +
                " ON customers.Division_ID = first_level_divisions.Division_ID";

PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);
ResultSet rs = ps.executeQuery();

ObservableList<Customers> customersObservableList = FXCollections.observableArrayList();

while (rs.next()) {

    int customerID = rs.getInt("Customer_ID");
    String customerName = rs.getString("Customer_Name");
    String customerAddress = rs.getString("Address");
    String customerPostalCode = rs.getString("Postal_Code");
    String customerPhone = rs.getString("Phone");
    String divisionName = rs.getString("Division");
    int divisionID = rs.getInt("Division_ID");


    Customers customer = new Customers(customerID, customerName, customerAddress, customerPostalCode, customerPhone, divisionName, divisionID);
    customersObservableList.add(customer);
}
return customersObservableList;
    }
}
