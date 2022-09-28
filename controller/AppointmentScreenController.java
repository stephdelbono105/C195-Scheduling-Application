package controller;

import DAO.appointmentAccess;
import DAO.contactAccess;
import DAO.customerAccess;
import DAO.userAccess;
import javafx.scene.layout.Region;
import main.DBConnection;
import model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Contacts;
import model.Customers;
import model.Users;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import static Util.timeUtil.convertTimeDateUTC;
import static java.time.ZoneId.systemDefault;


/**
 * AppointmentScreen class contains methods for filtering appointments by week, month, or all. Also contains methods to verify overlapping appointments.
 */
public class AppointmentScreenController {

    @FXML private TextField appIdTF;
    @FXML private TextField appTitleTF;
    @FXML private TextField appDescriptionTF;
    @FXML private TextField appTypeTF;
    @FXML private ComboBox<String> appContact;
    @FXML private TextField appUserIdTF;
    @FXML private TextField appCustIdTF;
    @FXML private TextField appLocationTf;
    @FXML private ComboBox<String> appEndTime;
    @FXML private ComboBox<String> appStartTime;
    @FXML private DatePicker appStartDate;
    @FXML private DatePicker appEndDate;

    @FXML private ToggleGroup appointmentsTG;
    @FXML private TableColumn appUserCol;
    @FXML private TableView<Appointments> allAppointmentsTable;
    @FXML private TableColumn appIdCol;
    @FXML private TableColumn appTitleCol;
    @FXML private TableColumn appDescriptionCol;
    @FXML private TableColumn appLocationCol;
    @FXML private TableColumn appTypeCol;
    @FXML private TableColumn appStartCol;
    @FXML private TableColumn appEndCol;
    @FXML private TableColumn appCustomerIdCol;
    @FXML private TableColumn appContactCol;
    @FXML private RadioButton weeklyAppRB;
    @FXML private RadioButton monthlyAppRB;
    @FXML private RadioButton allAppRB;


    /**
     * Initializes appointment controls and populates allAppointmentsList
     * @throws SQLException
     */
    public void initialize() throws SQLException {

 ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();

        appIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appTitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        appDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        appLocationCol.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
        appTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        appEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        appCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appContactCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appUserCol.setCellValueFactory(new PropertyValueFactory<>("userID"));

        allAppointmentsTable.setItems(allAppointmentsList);

    }

    /**
     * Back Button Method to return to the Main Menu.
     * @throws Exception
     */
    public void toMainScreen(ActionEvent actionEvent) throws Exception {
        System.out.println("Back Button was clicked");

        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Main Screen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Add Appointment Button method loads the Add Appointment Screen.
     * @param actionEvent
     * @throws Exception
     */
    public void toAddAppointment(ActionEvent actionEvent) throws Exception {
        System.out.println("Add Button was clicked");

        Parent root = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Add Appointment Screen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cancel Button Method to cancel changes made to an appointment and reset the appointments table view.
     * @throws IOException
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        System.out.println("Cancel Button Clicked");
        Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Appointment Screen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads appointment data upon customer selection.
     * Lambda to fill the combo box with observableList of contact data.
     */
    @FXML
    public void loadAppointment() {
        try {
            DBConnection.openConnection();
            Appointments selectedAppointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

            if (selectedAppointment != null) {
                ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
                ObservableList<String> allContactNames = FXCollections.observableArrayList();
                String displayContactName = "";

                //LAMBDA
                contactsObservableList.forEach(contacts -> allContactNames.add(contacts.getContactName()));
                appContact.setItems(allContactNames);

                for (Contacts contact: contactsObservableList) {
                    if (selectedAppointment.getContactID() == contact.getContactID()) {
                        displayContactName = contact.getContactName();
                    }
                }


                appIdTF.setText(String.valueOf(selectedAppointment.getAppointmentID()));
                appTitleTF.setText(String.valueOf(selectedAppointment.getAppointmentTitle()));
                appDescriptionTF.setText(String.valueOf(selectedAppointment.getAppointmentDescription()));
                appLocationTf.setText(String.valueOf(selectedAppointment.getAppointmentLocation()));
                appTypeTF.setText(String.valueOf(selectedAppointment.getAppointmentType()));
                appCustIdTF.setText(String.valueOf(selectedAppointment.getCustomerID()));
                appStartDate.setValue(selectedAppointment.getStart().toLocalDate());
                appEndDate.setValue(selectedAppointment.getEnd().toLocalDate());

               //POSSIBLY WHERE I NEED TO ADD CONVERSION TO LOCAL TIME FOR COMBO BOXES

                appStartTime.setValue(String.valueOf(selectedAppointment.getStart().toLocalTime()));
                appEndTime.setValue(String.valueOf(selectedAppointment.getEnd().toLocalTime()));

                appUserIdTF.setText(String.valueOf(selectedAppointment.getUserID()));
                appContact.setValue(displayContactName);

                ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

                LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
                LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);

                if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
                    while (firstAppointment.isBefore(lastAppointment)) {
                        appointmentTimes.add(String.valueOf(firstAppointment));
                        firstAppointment = firstAppointment.plusMinutes(15);
                    }

                }
                appStartTime.setItems(appointmentTimes);
                appEndTime.setItems(appointmentTimes);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Update Button Method saves appointment field changes.
     * @param actionEvent
     */
    public void updateAppointment(ActionEvent actionEvent) {
        try {


            Connection connection = DBConnection.getConnection();

            Appointments selectedAppointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "To Edit an Appointment: Select an Appointment from the table above, edit Appointment fields, then click the Update Button");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            } else if (selectedAppointment != null) {


                if (!appTitleTF.getText().isEmpty() && !appDescriptionTF.getText().isEmpty() && !appLocationTf.getText().isEmpty() && !appTypeTF.getText().isEmpty()
                        && appStartDate.getValue() != null && appEndDate.getValue() != null && !appStartTime.getValue().isEmpty() && !appEndTime.getValue().isEmpty() &&
                        !appCustIdTF.getText().isEmpty() && appContact.getValue() != null) {
                    ObservableList<Customers> getAllCustomers = customerAccess.getAllCustomers(connection);
                    ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
                    ObservableList<userAccess> getAllUsers = userAccess.getAllUsers();
                    ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
                    ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();

                    getAllCustomers.stream().map(Customers::getCustomerID).forEach(storeCustomerIDs::add);
                    getAllUsers.stream().map(Users::getUserID).forEach(storeUserIDs::add);

                    LocalDate localDateEnd = appEndDate.getValue();
                    LocalDate localDateStart = appStartDate.getValue();


                    DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");

                    LocalTime localTimeStart = LocalTime.parse(appStartTime.getValue(), minHourFormat);
                    LocalTime localTimeEnd = LocalTime.parse(appEndTime.getValue(), minHourFormat);

                    LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                    LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, localTimeEnd);

                    ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, systemDefault());
                    ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, systemDefault());

                    ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                    ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));


                    //  WORKING
                    if (convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) ||
                            convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) ||
                            convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) ||
                            convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue())) {
                        System.out.println("Date is outside of business hours");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Date is outside of business hours (Monday - Friday)");
                        alert.showAndWait();
                        return;
                    }


                    if (convertStartEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) ||
                            convertStartEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0)) ||
                            convertEndEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) ||
                            convertEndEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0))) {
                        System.out.println("Time is outside of business hours");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Time is outside of business hours (8:00am - 10:00pm EST): "
                                + convertStartEST.toLocalTime() + " - " + convertEndEST.toLocalTime() + " EST");
                        alert.showAndWait();
                        return;
                    }

                    int newCustomerID = Integer.parseInt(appCustIdTF.getText());
                    int appointmentID = Integer.parseInt(appIdTF.getText());

                    //  WORKING
                    if (dateTimeStart.isAfter(dateTimeEnd)) {
                        System.out.println("Appointment start time is after end time");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment start time is after end time");
                        alert.showAndWait();
                        return;
                    }
                    //  WORKING
                    if (dateTimeStart.isEqual(dateTimeEnd)) {
                        System.out.println("Appointment has same start/end time");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment has same start/end time");
                        alert.showAndWait();
                        return;
                    }


                    for (Appointments appointment : getAllAppointments) {

                        LocalDateTime checkStart = appointment.getStart();
                        LocalDateTime checkEnd = appointment.getEnd();

                        if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&
                                (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                            System.out.println("Appointment overlaps with an existing appointment");
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment overlaps with an existing appointment");
                            alert.showAndWait();
                            return;
                        } else if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&

                                (dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) &&
                                (dateTimeStart.isEqual(checkEnd) || dateTimeStart.isBefore(checkEnd))) {

                            //(dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                            System.out.println("Appointment Start Time overlaps with an existing appointment");
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment Start Time overlaps with an existing appointment");
                            alert.showAndWait();
                            return;
                        } else if (newCustomerID == appointment.getCustomerID() && (appointmentID != appointment.getAppointmentID()) &&
                                (dateTimeEnd.isEqual(checkStart) || dateTimeEnd.isAfter(checkStart)) &&
                                (dateTimeEnd.isEqual(checkEnd) || dateTimeEnd.isBefore(checkEnd))) {

                            //(dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                            System.out.println("Appointment End Time overlaps with an existing appointment");
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment End Time overlaps with an existing appointment");
                            alert.showAndWait();
                            return;
                        }

                    }


                    String startDate = appStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String startTime = appStartTime.getValue();
                    String endDate = appEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String endTime = appEndTime.getValue();

                    String startUTC = convertTimeDateUTC(startDate + " " + startTime + ":00");
                    String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");

                    String insertStatement = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, " +
                            "Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

                    DBConnection.setPreparedStatement(DBConnection.getConnection(), insertStatement);
                    PreparedStatement ps = DBConnection.getPreparedStatement();

                    ps.setInt(1, Integer.parseInt(appIdTF.getText()));
                    ps.setString(2, appTitleTF.getText());
                    ps.setString(3, appDescriptionTF.getText());
                    ps.setString(4, appLocationTf.getText());
                    ps.setString(5, appTypeTF.getText());
                    ps.setString(6, startUTC);
                    ps.setString(7, endUTC);
                    ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(9, "admin");
                    ps.setInt(10, Integer.parseInt(appCustIdTF.getText()));
                    ps.setInt(11, Integer.parseInt(appUserIdTF.getText()));
                    ps.setInt(12, Integer.parseInt(contactAccess.findContactID(appContact.getValue())));
                    ps.setInt(13, Integer.parseInt(appIdTF.getText()));

                    System.out.println("ps " + ps);
                    ps.execute();

                    ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
                    allAppointmentsTable.setItems(allAppointmentsList);


                    Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1080, 800);
                    stage.setTitle("Appointment Screen");
                    stage.setScene(scene);
                    stage.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Delete Button method to delete an appointment.
     * @throws Exception
     * @param actionEvent
     */
    @FXML
    public void deleteAppointment(ActionEvent actionEvent)  throws Exception  {
        try {

            Connection connection = DBConnection.getConnection();


            Appointments selectedAppointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

            if (selectedAppointment == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "To Delete an Appointment, Select an Appointment from the table above, then click the Delete Button");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            } else if (selectedAppointment != null) {


                int deleteAppID = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentID();
                String deleteAppType = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentType();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected appointment with ID: " + deleteAppID + " and appointment type: " + deleteAppType);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                Optional<ButtonType> confirmation = alert.showAndWait();

                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    appointmentAccess.deleteAppointment(deleteAppID, connection);

                    ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
                    allAppointmentsTable.setItems(allAppointmentsList);

                    Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1080, 800);
                    stage.setTitle("Appointment Screen");
                    stage.setScene(scene);
                    stage.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * Filters appointments by Week when Weekly radio button is selected.
     * @throws SQLException
     */
    public void weeklyAppSelected(ActionEvent actionEvent) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsWeek = FXCollections.observableArrayList();

            LocalDateTime currentWeekStart = LocalDateTime.now().minusWeeks(1);
            LocalDateTime currentWeekEnd = LocalDateTime.now().plusWeeks(1);

            if (allAppointmentsList != null)
                allAppointmentsList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(currentWeekStart) && appointment.getEnd().isBefore(currentWeekEnd)) {
                        appointmentsWeek.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsWeek);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Filters appointments by Month when Monthly radio button is selected.
     * @throws SQLException
     */
    public void monthlyAppSelected(ActionEvent actionEvent) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsMonth = FXCollections.observableArrayList();

            LocalDateTime currentMonthStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime currentMonthEnd = LocalDateTime.now().plusMonths(1);

            if (allAppointmentsList != null)
                allAppointmentsList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(currentMonthStart) && appointment.getEnd().isBefore(currentMonthEnd)) {
                        appointmentsMonth.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsMonth);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Filters appointments by All when All Appointments radio button is selected.
     * @throws SQLException
     */
    public void allAppSelected(ActionEvent actionEvent) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();

            if (allAppointmentsList != null)
                for (model.Appointments appointment : allAppointmentsList) {
                    allAppointmentsTable.setItems(allAppointmentsList);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
