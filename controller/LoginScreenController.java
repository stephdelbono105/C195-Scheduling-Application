package controller;

import DAO.appointmentAccess;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import model.Appointments;
import model.Users;
import DAO.userAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class and methods to verify login, add timestamp to login_activity log, and sets Locale to translate language based on the user's operating system language setting.
 */
public class LoginScreenController implements Initializable {

    @FXML
    public Label schedAppLabel;
    @FXML
    private TextField loginPasswordField;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private Button loginButton;
    @FXML
    private Label locationLabel;
    @FXML
    private TextField loginLocationField;
    @FXML
    private Label loginLabel;
    ResourceBundle rb;


    /**
     * Initialize upon login screen start. Gets the locale info and sets text language on fields.
     * @param rb
     * @param url
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("LoginScreen Initialized");

        try {

            loginLocationField.setText(ZoneId.systemDefault().toString());

            rb = ResourceBundle.getBundle("language/rb", Locale.getDefault());

            schedAppLabel.setText(rb.getString("Title"));
            locationLabel.setText(rb.getString("Location"));
            loginPasswordField.setPromptText(rb.getString("password"));
            loginUsernameField.setPromptText(rb.getString("username"));
            loginLabel.setText(rb.getString("Login"));
            loginButton.setText(rb.getString("Login"));

        } catch(MissingResourceException e) {
            System.out.println("Missing Resource file: " + e);
        }
        catch(Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Login Button for the Login Screen.
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
@FXML
    public void onLoginButton(ActionEvent actionEvent) throws IOException, SQLException {

      try {
          // DEFINITIONS FOR 15 MIN APPOINTMENT CHECKS

          ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();
          LocalDateTime currTimeMinus15 = LocalDateTime.now().minusMinutes(15);
          LocalDateTime currTimePlus15 = LocalDateTime.now().plusMinutes(15);
          LocalDateTime startTime;
          int getAppointmentID = 0;
          LocalDateTime displayTime = null;
          boolean appointmentWithin15Min = false;


          rb = ResourceBundle.getBundle("language/rb", Locale.getDefault());

          String usernameInput = loginUsernameField.getText();
          String passwordInput = loginPasswordField.getText();
          int userId = userAccess.validateUser(usernameInput, passwordInput);

          FileWriter fileWriter = new FileWriter("login_activity.txt", true);
          PrintWriter outputFile = new PrintWriter(fileWriter);

          if (userId > 0) {
              FXMLLoader loader = new FXMLLoader();
              loader.setLocation(getClass().getResource("/view/MainScreen.fxml"));
              Parent root = loader.load();
              Stage stage = (Stage) loginButton.getScene().getWindow();
              Scene scene = new Scene(root);
              stage.setScene(scene);
              stage.show();
              System.out.println("Login Successful!");

              outputFile.print("user: " + usernameInput + " logged in successfully at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

              for (Appointments appointment: getAllAppointments) {
                  startTime = appointment.getStart();
                  if ((startTime.isAfter(currTimeMinus15) || startTime.isEqual(currTimeMinus15)) && (startTime.isBefore(currTimePlus15) ||
                          (startTime.isEqual(currTimePlus15)))) {
                      getAppointmentID = appointment.getAppointmentID();
                      displayTime = startTime;
                      appointmentWithin15Min = true;
                  }
              }

              if (appointmentWithin15Min != false) {
                  Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have an Appointment within 15 minutes." + "\n" +
                          "Appointment ID: " + getAppointmentID + "\n" + "Appointment Start Time: " + displayTime);
                  alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                  alert.showAndWait();
                  System.out.println("There is an Appointment within 15 minutes");
              }

              else {
                  Alert alert = new Alert(Alert.AlertType.INFORMATION, "No Upcoming Appointments");
                  alert.showAndWait();
                  System.out.println("No Upcoming Appointments");
              }


          } else if (userId < 0) {
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setTitle(rb.getString("Error"));
              alert.setContentText(rb.getString("Incorrect"));
              alert.show();

              outputFile.print("User: " + usernameInput + " failed login attempt at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
          }

          outputFile.close();

      }catch (IOException ioException) {
          ioException.printStackTrace();
      }


    }
}





