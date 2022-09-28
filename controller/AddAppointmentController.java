package controller;

import DAO.appointmentAccess;
import DAO.contactAccess;
import DAO.customerAccess;
import DAO.userAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.DBConnection;
import model.Appointments;
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


/**
 * Class and methods to add new appointments.
 */
public class AddAppointmentController extends AppointmentScreenController {

    @FXML private DatePicker addAppEndDate;
    @FXML
    private TextArea addAppDescriptionTF;
    @FXML
    private TextField addAppTitleTF;
    @FXML
    private TextField addAppIdTF;
    @FXML
    private DatePicker addAppStartDate;
    @FXML
    private TextField addAppTypeTF;
    @FXML
    private TextField addAppLocationTF;
    @FXML
    private TextField addAppCustIdTF;
    @FXML
    private TextField addAppUserIdTF;
    @FXML
    private ComboBox<String> addAppContact;
    @FXML
    private ComboBox<String> addAppStartTime;
    @FXML
    private ComboBox<String> addAppEndTime;


    /**
     * Initialize controls and fills start/end time combo boxes in 15 min increments.
     * Lambda replaced for loop to add allContactsNames to ObservableList.
     * @throws SQLException
     */
    @FXML
    public void initialize() throws SQLException {

        ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();

        // LAMBDA
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

        LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
        LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);

        if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
            while (firstAppointment.isBefore(lastAppointment)) {
                appointmentTimes.add(String.valueOf(firstAppointment));
                firstAppointment = firstAppointment.plusMinutes(15);
            }
        }
        addAppStartTime.setItems(appointmentTimes);
        addAppEndTime.setItems(appointmentTimes);
        addAppContact.setItems(allContactsNames);
    }


    /**
     * Cancel Button Method to cancel adding an appointment and returns to AppointmentScreen.
     * @throws IOException
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        System.out.println("Cancel Button was clicked");

        Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Appointment Screen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Save Button method Adds a new appointment.
     * @throws IOException
     * @param actionEvent
     */
    @FXML
    void addAppointmentSave(ActionEvent actionEvent) throws IOException {

        try {
            Connection connection = DBConnection.getConnection();


            if (!addAppTitleTF.getText().isEmpty() && !addAppDescriptionTF.getText().isEmpty()
                    && !addAppLocationTF.getText().isEmpty() && !addAppTypeTF.getText().isEmpty()
                    && addAppStartDate.getValue() != null && addAppEndDate.getValue() != null &&
                    !addAppStartTime.getValue().isEmpty() && !addAppEndTime.getValue().isEmpty()
                    && !addAppCustIdTF.getText().isEmpty() && !addAppUserIdTF.getText().isEmpty()) {

                ObservableList<Customers> getAllCustomers = customerAccess.getAllCustomers(connection);
                ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
                ObservableList<userAccess> getAllUsers = userAccess.getAllUsers();
                ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();


                getAllCustomers.stream().map(Customers::getCustomerID).forEach(storeCustomerIDs::add);
                getAllUsers.stream().map(Users::getUserID).forEach(storeUserIDs::add);

                LocalDate localDateEnd = addAppEndDate.getValue();
                LocalDate localDateStart = addAppStartDate.getValue();

                DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");


                LocalTime localTimeStart = LocalTime.parse(addAppStartTime.getValue(), minHourFormat);
                LocalTime localTimeEnd = LocalTime.parse(addAppEndTime.getValue(), minHourFormat);

                LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, localTimeEnd);

                ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                LocalTime startAppTimeCheck = convertStartEST.toLocalTime();
                LocalTime endAppTimeCheck = convertEndEST.toLocalTime();

                DayOfWeek startAppDayCheck = convertStartEST.toLocalDate().getDayOfWeek();
                DayOfWeek endAppDayCheck = convertEndEST.toLocalDate().getDayOfWeek();

                int startAppDayCheckInt = startAppDayCheck.getValue();
                int endAppDayCheckInt = endAppDayCheck.getValue();

                int workWeekStart = DayOfWeek.MONDAY.getValue();
                int workWeekEnd = DayOfWeek.FRIDAY.getValue();

                LocalTime estBusinessStart = LocalTime.of(8, 0, 0);
                LocalTime estBusinessEnd = LocalTime.of(22, 0, 0);

                //WORKING
                if (startAppDayCheckInt < workWeekStart || startAppDayCheckInt > workWeekEnd || endAppDayCheckInt < workWeekStart || endAppDayCheckInt > workWeekEnd) {
                    System.out.println("Date is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Date is outside of business hours (Monday - Friday)");
                    alert.showAndWait();
                    return;
                }
                //WORKING
                if (startAppTimeCheck.isBefore(estBusinessStart) || startAppTimeCheck.isAfter(estBusinessEnd) || endAppTimeCheck.isBefore(estBusinessStart)
                        || endAppTimeCheck.isAfter(estBusinessEnd)) {
                    System.out.println("Time is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Time is outside of business hours (8:00am - 10:00pm EST): " + startAppTimeCheck + " - " + endAppTimeCheck + " EST");
                    alert.showAndWait();
                    return;
                }

                int newAppointmentID = Integer.parseInt(String.valueOf((int) (Math.random() * 1000)));

                int customerID = Integer.parseInt(addAppCustIdTF.getText());



                //WORKING
                if (dateTimeStart.isAfter(dateTimeEnd)) {
                    System.out.println("Appointment start time is after end time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment start time is after end time");
                    alert.showAndWait();
                    return;
                }

                //WORKING
                if (dateTimeStart.isEqual(dateTimeEnd)) {
                    System.out.println("Appointment has same start/end time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment has same start/end time");
                    alert.showAndWait();
                    return;
                }


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //LocalDateTime dateTimeStart2 = LocalDateTime.parse(startUTC, formatter);

                LocalDateTime dateTimeStart2 = dateTimeStart;

                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //LocalDateTime dateTimeEnd2 = LocalDateTime.parse(endUTC, formatter);
                LocalDateTime dateTimeEnd2 = dateTimeEnd;

                for (Appointments appointment : getAllAppointments)
                {
                    LocalDateTime checkStart = appointment.getStart();
                    LocalDateTime checkEnd = appointment.getEnd();

                    if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
                            (dateTimeStart2.isBefore(checkStart)) && (dateTimeEnd2.isAfter(checkEnd))) {
                        System.out.println("Appointment overlaps with an existing appointment");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment overlaps with an existing appointment");
                        alert.showAndWait();
                        return;
                    }

                    else if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&

                            (dateTimeStart2.isEqual(checkStart) || dateTimeStart2.isAfter(checkStart)) &&
                            (dateTimeStart2.isEqual(checkEnd) || dateTimeStart2.isBefore(checkEnd))) {

                        //(dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                        System.out.println("Appointment Start Time overlaps with an existing appointment");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment Start Time overlaps with an existing appointment");
                        alert.showAndWait();
                        return;
                    }

                    else if (customerID == appointment.getCustomerID() && (newAppointmentID != appointment.getAppointmentID()) &&
                            (dateTimeEnd2.isEqual(checkStart) || dateTimeEnd2.isAfter(checkStart)) &&
                            (dateTimeEnd2.isEqual(checkEnd) || dateTimeEnd2.isBefore(checkEnd))) {


                        //(dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                        System.out.println("Appointment End Time overlaps with an existing appointment");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment End Time overlaps with an existing appointment");
                        alert.showAndWait();
                        return;
                    }

                   if (Integer.parseInt(addAppUserIdTF.getText()) >= 3) {
                        System.out.println("User ID not found");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "User ID not found");
                        alert.showAndWait();
                        return;

                    }

                }


                String insertStatement = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                DBConnection.setPreparedStatement(DBConnection.getConnection(), insertStatement);
                PreparedStatement ps = DBConnection.getPreparedStatement();

                ps.setInt(1, newAppointmentID);
                ps.setString(2, addAppTitleTF.getText());
                ps.setString(3, addAppDescriptionTF.getText());
                ps.setString(4, addAppLocationTF.getText());
                ps.setString(5, addAppTypeTF.getText());
                ps.setTimestamp(6, Timestamp.valueOf(dateTimeStart));
                ps.setTimestamp(7, Timestamp.valueOf(dateTimeEnd));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(11, 1);
                ps.setInt(12, Integer.parseInt(addAppCustIdTF.getText()));
                ps.setInt(13, Integer.parseInt(addAppUserIdTF.getText()));
                ps.setInt(14, Integer.parseInt(contactAccess.findContactID(addAppContact.getValue())));

                ps.execute();

            }

            Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
            Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1080, 800);
            stage.setTitle("Appointment Screen");
            stage.setScene(scene);
            stage.show();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}



