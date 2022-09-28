package controller;

import DAO.appointmentAccess;
import DAO.contactAccess;
import DAO.reportAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;

import java.sql.SQLException;
import java.time.Month;
import java.util.Collections;


/**
 * Class and Methods to display generated reports
 */
public class ReportScreenController  {
    @FXML private TableView<Reports> customerByCountry;
    @FXML private TableColumn countryName;
    @FXML private TableColumn countryCount;
    @FXML private TableView<ReportType> appTotalsTypeTable;
    @FXML private TableColumn appTypeTotalsCol;
    @FXML private TableColumn appTotalsCol;
    @FXML private TableView<ReportMonth> appMonthTotalTable;
    @FXML private TableColumn appTotalMonthCol;
    @FXML private TableColumn appTotalMonthTotalCol;
    @FXML private TableColumn customerIdCol;
    @FXML private TableColumn appEndCol;
    @FXML private TableColumn appDescriptionCol;
    @FXML private TableColumn appStartCol;
    @FXML private TableColumn appIdCol;
    @FXML private TableColumn appTypeCol;
    @FXML private TableColumn appTitleCol;
    @FXML private ComboBox<String> appContactBox;
    @FXML private TableView<Appointments> allAppsContactTable;


    /**
     * Initializes and populates fields on the report form.
     * @throws SQLException
     */
    public void initialize() throws SQLException {

        // CONTACT SCHEDULE REPORT
        appIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appTitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        appTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        appStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        appEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        //APPOINTMENT TOTALS REPORT
        appTypeTotalsCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appTotalsCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTotal"));
        appTotalMonthCol.setCellValueFactory(new PropertyValueFactory<>("appointmentMonth"));
        appTotalMonthTotalCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTotal"));

        // CUSTOMER BY COUNTRY LOCATION REPORT
        countryName.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        countryCount.setCellValueFactory(new PropertyValueFactory<>("countryCount"));


        ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        appContactBox.setItems(allContactsNames);

    }


    /**
     * Populates report form with appointment data based on contact.
     */
    @FXML
    public void appByContact(ActionEvent actionEvent) {
        try {
            int contactID = 0;

            ObservableList<Appointments> getAllAppointmentData = appointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentInfo = FXCollections.observableArrayList();
            ObservableList<Contacts> getAllContacts = contactAccess.getAllContacts();

            Appointments contactAppointmentInfo;

            String contactName = appContactBox.getSelectionModel().getSelectedItem();

            for (Contacts contact: getAllContacts) {
                if (contactName.equals(contact.getContactName())) {
                    contactID = contact.getContactID();
                }
            }
            for (Appointments appointment: getAllAppointmentData) {
                if (appointment.getContactID() == contactID) {
                    contactAppointmentInfo = appointment;
                    appointmentInfo.add(contactAppointmentInfo);
                }
            }
            allAppsContactTable.setItems(appointmentInfo);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Populates report form with Total number of customer appointments based on Type and Month.
     * @throws SQLException
     */
    public void appTotalsTab(Event event) throws SQLException {
        try {
            ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();
            ObservableList<Month> appointmentMonths = FXCollections.observableArrayList();
            ObservableList<Month> monthOfAppointments = FXCollections.observableArrayList();

            ObservableList<String> appointmentType = FXCollections.observableArrayList();
            ObservableList<String> uniqueAppointment = FXCollections.observableArrayList();

            ObservableList<ReportType> reportType = FXCollections.observableArrayList();
            ObservableList<ReportMonth> reportMonths = FXCollections.observableArrayList();

            // LAMBDA
            getAllAppointments.forEach(appointments -> {
                appointmentType.add(appointments.getAppointmentType());
            });

            //LAMBDA
            getAllAppointments.stream().map(appointment -> {
                return appointment.getStart().getMonth();
            }).forEach(appointmentMonths::add);

            //LAMBDA
            appointmentMonths.stream().filter(month -> {
                return !monthOfAppointments.contains(month);
            }).forEach(monthOfAppointments::add);

            for (Appointments appointments: getAllAppointments) {
                String appointmentsAppType = appointments.getAppointmentType();
                if (!uniqueAppointment.contains(appointmentsAppType)) {
                    uniqueAppointment.add(appointmentsAppType);
                }
            }

            for (Month month: monthOfAppointments) {
                int totalMonth = Collections.frequency(appointmentMonths, month);
                String monthName = month.name();
                ReportMonth appointmentMonth = new ReportMonth(monthName, totalMonth);
                reportMonths.add(appointmentMonth);
            }
            appMonthTotalTable.setItems(reportMonths);

            for (String type: uniqueAppointment) {
                String typeToSet = type;
                int typeTotal = Collections.frequency(appointmentType, type);
                ReportType appointmentTypes = new ReportType(typeToSet, typeTotal);
                reportType.add(appointmentTypes);
            }
            appTotalsTypeTable.setItems(reportType);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * My custom generated report to display the total customers per country.
     * @throws SQLException
     */
    public void customerByLocationTab(Event event) throws SQLException {
        try {
            ObservableList<Reports> aggregatedCountries = reportAccess.getCountries();
            ObservableList<Reports> countriesToAdd = FXCollections.observableArrayList();

            aggregatedCountries.forEach(countriesToAdd::add);
            customerByCountry.setItems(countriesToAdd);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Back button to return to main menu.
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


}



