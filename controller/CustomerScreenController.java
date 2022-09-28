package controller;

import DAO.appointmentAccess;
import DAO.countryAccess;
import DAO.customerAccess;
import javafx.scene.layout.Region;
import model.Appointments;
import model.firstLevelDivisions;
import DAO.firstLevelDivisionAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.DBConnection;
import model.Countries;
import model.Customers;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;



/**
 * Class and method to add, update, and delete customer data.
 */
public class CustomerScreenController implements Initializable {

    @FXML
    private  TextField customerIdTF;
    @FXML
    private  TextField customerNameTF;
    @FXML
    private  TextField customerPhoneTF;
    @FXML
    private  TextField customerAddressTF;
    @FXML
    private  TextField customerPostalTF;
    @FXML
    private Button customerSaveButton = null;
    @FXML
    private TableView<Customers> customersTable;
    @FXML
    private TableColumn customerIdCol;
    @FXML
    private TableColumn customerNameCol;
    @FXML
    private TableColumn customerPhoneCol;
    @FXML
    private TableColumn customerFirstLevelCol;
    @FXML
    private ComboBox<String> editState;
    @FXML
    private ComboBox<String> editCountry;


    /**
     * Initializes controls and sets initial variables and observable lists.
     * Lambda fills ObservableList with getDivisionName().
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = DBConnection.getConnection();

            ObservableList<countryAccess> allCountries = countryAccess.getCountries();
            ObservableList<String> countryNames = FXCollections.observableArrayList();

            ObservableList<firstLevelDivisionAccess> allFirstLevelDivisions = firstLevelDivisionAccess.getAllFirstLevelDivisions();
            ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();

            ObservableList<Customers> allCustomersList = customerAccess.getAllCustomers(connection);


            customersTable.setItems(allCustomersList);
            customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
            customerFirstLevelCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));


            allCountries.stream().map(Countries::getCountryName).forEach(countryNames::add);
            editCountry.setItems(countryNames);

            // LAMBDA
            allFirstLevelDivisions.forEach(firstLevelDivision -> firstLevelDivisionAllNames.add(firstLevelDivision.getDivisionName()));
            editState.setItems(firstLevelDivisionAllNames);


        } catch (Exception e) {
            e.printStackTrace();


        }


    }

    /**
     * Back Button Method to return to the Main Menu.
     * @throws Exception
     */
    public void toMainScreen(ActionEvent actionEvent) throws Exception {
        System.out.println("Back Button was clicked");

        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Main Screen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cancel Button Method to cancel changes made to a customer and reset the customer table view.
     * @throws Exception
     */
    public void customerCancelButton(ActionEvent actionEvent) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Customer Screen");
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Add Button method to add a new customer.
     * @param actionEvent
     */
    public void customerAddButton(ActionEvent actionEvent) throws Exception {


        try{
            Connection connection = DBConnection.getConnection();

            if (customerNameTF.getText().isEmpty() || customerAddressTF.getText().isEmpty() || customerPostalTF.getText().isEmpty() ||
                    customerPhoneTF.getText().isEmpty() || editCountry.getValue().isEmpty() || editState.getValue().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "To Add New Customer: Enter all information in each field under Customer Details, then click the Add Customer button");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }

           else if (!customerNameTF.getText().isEmpty() || !customerAddressTF.getText().isEmpty() || !customerPostalTF.getText().isEmpty() ||
                    !customerPhoneTF.getText().isEmpty() || !editCountry.getValue().isEmpty() || !editState.getValue().isEmpty())
            {

                Integer autoGenID = (int) (Math.random() * 1000);

                int firstLevelDivisionName = 0;
                for (firstLevelDivisionAccess firstLevelDivision : firstLevelDivisionAccess.getAllFirstLevelDivisions()) {
                    if (editState.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                        firstLevelDivisionName = firstLevelDivision.getDivisionID();
                    }
                }
                String insertStatement = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, " +
                        "Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";

                DBConnection.setPreparedStatement(DBConnection.getConnection(), insertStatement);
                PreparedStatement ps = DBConnection.getPreparedStatement();

                ps.setInt(1, autoGenID);
                ps.setString(2, customerNameTF.getText());
                ps.setString(3, customerAddressTF.getText());
                ps.setString(4, customerPostalTF.getText());
                ps.setString(5, customerPhoneTF.getText());

                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, firstLevelDivisionName);

                ps.execute();


                ObservableList<Customers> refreshCustomersList = customerAccess.getAllCustomers(connection);
                customersTable.setItems(refreshCustomersList);

                Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
                Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1080, 800);
                stage.setTitle("Customer Screen");
                stage.setScene(scene);
                stage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Method that fills in fields when a customer is selected and edit button is clicked.
     * @throws SQLException
     * @param actionEvent
     */
    public void customerEditButton(ActionEvent actionEvent) throws SQLException {

        try {
            DBConnection.getConnection();

            Customers selectedCustomer = (Customers) customersTable.getSelectionModel().getSelectedItem();
            String divisionName = "", countryName = "";

            if (selectedCustomer == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "To Edit a Customer: Select the Customer's name from the table above, click the Edit Customer Button, update the fields, then click the Save Button");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }

            else if (selectedCustomer != null) {
                ObservableList<countryAccess> getAllCountries = countryAccess.getCountries();
                ObservableList<firstLevelDivisionAccess> getFLDivisionNames = firstLevelDivisionAccess.getAllFirstLevelDivisions();
                ObservableList<String> allFLDivision = FXCollections.observableArrayList();

                editState.setItems(allFLDivision);

                customerIdTF.setText(String.valueOf((selectedCustomer.getCustomerID())));
                customerNameTF.setText(selectedCustomer.getCustomerName());
                customerAddressTF.setText(selectedCustomer.getCustomerAddress());
                customerPostalTF.setText(selectedCustomer.getCustomerPostalCode());
                customerPhoneTF.setText(selectedCustomer.getCustomerPhone());

                for (firstLevelDivisions flDivision : getFLDivisionNames) {
                    allFLDivision.add(flDivision.getDivisionName());
                    int countryIDToSet = flDivision.getCountry_ID();

                    if (flDivision.getDivisionID() == selectedCustomer.getDivisionID()) {
                        divisionName = flDivision.getDivisionName();

                        for (Countries countries : getAllCountries) {
                            if (countries.getCountryID() == countryIDToSet) {
                                countryName = countries.getCountryName();
                            }
                        }
                    }
                }

                editState.setValue(divisionName);
                editCountry.setValue(countryName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save Button Method to add new customer when clicked.
     * @param actionEvent
     */
    public void customerSaveButton(ActionEvent actionEvent) throws NullPointerException {


        try {
            Connection connection = DBConnection.getConnection();

            Customers selectedCustomer = (Customers) customersTable.getSelectionModel().getSelectedItem();

            if (selectedCustomer == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "To Save a Customer Update: Select the customer's name from the table above, click Edit Customer, then click the Save Button");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }

           else if (!customerNameTF.getText().isEmpty() || !customerAddressTF.getText().isEmpty() || !customerPostalTF.getText().isEmpty() ||
                    !customerPhoneTF.getText().isEmpty() || !editCountry.getValue().toString().isEmpty() || !editState.getValue().toString().isEmpty())
            {
                int firstLevelDivisionName = 0;
                for (firstLevelDivisionAccess firstLevelDivision : firstLevelDivisionAccess.getAllFirstLevelDivisions()) {
                    if (editState.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                        firstLevelDivisionName = firstLevelDivision.getDivisionID();
                    }
                }

                String insertStatement = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?," +
                        " Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";

                DBConnection.setPreparedStatement(DBConnection.getConnection(), insertStatement);

                PreparedStatement ps = DBConnection.getPreparedStatement();

                ps.setInt(1, Integer.parseInt(customerIdTF.getText()));
                ps.setString(2, customerNameTF.getText());
                ps.setString(3, customerAddressTF.getText());
                ps.setString(4, customerPostalTF.getText());
                ps.setString(5, customerPhoneTF.getText());

                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, firstLevelDivisionName);
                ps.setInt(11, Integer.parseInt(customerIdTF.getText()));

                ps.execute();

                ObservableList<Customers> refreshCustomersList = customerAccess.getAllCustomers(connection);
                customersTable.setItems(refreshCustomersList);

                Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
                Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1080, 800);
                stage.setTitle("Customer Screen");
                stage.setScene(scene);
                stage.show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete button Method to delete a selected customer record and all associated appointments.
     * @throws Exception
     * @param actionEvent
     */
    public void customerDeleteButton(ActionEvent actionEvent) throws Exception {

        try {

            Connection connection = DBConnection.getConnection();
            ObservableList<Appointments> getAllAppointmentsList = appointmentAccess.getAllAppointments();

            Customers selectedCustomer = (Customers) customersTable.getSelectionModel().getSelectedItem();

            if (selectedCustomer == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "To Delete a Customer: Select the Customer's name from the table above, then click the Delete Button");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }

            else if (selectedCustomer != null) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected Customer and all associated Appointments?");
                Optional<ButtonType> confirmation = alert.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    int deleteCustomerID = customersTable.getSelectionModel().getSelectedItem().getCustomerID();
                    appointmentAccess.deleteAppointment(deleteCustomerID, connection);

                    String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
                    DBConnection.setPreparedStatement(DBConnection.getConnection(), sqlDelete);
                    PreparedStatement psDelete = DBConnection.getPreparedStatement();
                    int customerFromTable = customersTable.getSelectionModel().getSelectedItem().getCustomerID();

                    // DELETE ASSOCIATED APPOINTMENTS FROM DB
                    for (Appointments appointment : getAllAppointmentsList) {
                        int customerFromAppointments = appointment.getCustomerID();
                        if (customerFromTable == customerFromAppointments) {
                            String deleteAppStatement = "DELETE FROM appointments WHERE Appointment_ID = ?";
                            DBConnection.setPreparedStatement(DBConnection.getConnection(), deleteAppStatement);
                        }
                    }
                    psDelete.setInt(1, customerFromTable);
                    psDelete.execute();

                    System.out.println("Customer and Associated Appointments successfully deleted");
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Customer: " + customersTable.getSelectionModel().getSelectedItem().getCustomerName() + " and Associated Appointments successfully deleted");
                    alert2.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert2.showAndWait();

                    ObservableList<Customers> refreshCustomersList = customerAccess.getAllCustomers(connection);
                    customersTable.setItems(refreshCustomersList);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Fills combo boxes and filters with first level division data.
     * @param actionEvent
     * @throws SQLException
     */
    public void comboBoxFilter(ActionEvent actionEvent) throws SQLException {
        try {
            DBConnection.getConnection();

            String selectedCountry = (String) editCountry.getSelectionModel().getSelectedItem();

            ObservableList<firstLevelDivisionAccess> getAllFirstLevelDivisions = firstLevelDivisionAccess.getAllFirstLevelDivisions();

            ObservableList<String> flDivUS = FXCollections.observableArrayList();
            ObservableList<String> flDivUK = FXCollections.observableArrayList();
            ObservableList<String> flDivCAN = FXCollections.observableArrayList();

            getAllFirstLevelDivisions.forEach(firstLevelDivision -> {
                if (firstLevelDivision.getCountry_ID() == 1) {
                    flDivUS.add(firstLevelDivision.getDivisionName());
                } else if (firstLevelDivision.getCountry_ID() == 2) {
                    flDivUK.add(firstLevelDivision.getDivisionName());
                } else if (firstLevelDivision.getCountry_ID() == 3) {
                    flDivCAN.add(firstLevelDivision.getDivisionName());
                }
            });

            if (selectedCountry.equals("U.S")) {
                editState.setItems(flDivUS);
            } else if (selectedCountry.equals("UK")) {
                editState.setItems(flDivUK);
            } else if (selectedCountry.equals("Canada")) {
                editState.setItems(flDivCAN);
            }
        } catch (Exception e) {
            e.printStackTrace();


        }

        }



}
