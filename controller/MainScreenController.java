package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Main Screen class for main menu navigation to Customers, Appointments, or Reports.
 */
public class MainScreenController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialized");
    }


    /**
     * Exit Button that closes the program.
     * @param actionEvent
     */
    public void exitProgramAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        alert.setContentText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }


    /**
     * Go to the Customers Screen.
     * @param actionEvent
     * @throws Exception
     */
    public void toCustomers(ActionEvent actionEvent) throws Exception {
        System.out.println("Customer Button was clicked");

        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Customer Screen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Go to the Appointments Screen.
     * @param actionEvent
     * @throws Exception
     */
    public void toAppointments(ActionEvent actionEvent) throws Exception {
        System.out.println("Appointment Button was clicked");

        Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Appointment Screen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Go to the Reports Screen.
     * @param actionEvent
     * @throws Exception
     */
    public void toReports(ActionEvent actionEvent) throws Exception {
        System.out.println("Appointment Button was clicked");

        Parent root = FXMLLoader.load(getClass().getResource("/view/ReportScreen.fxml"));
        Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 800);
        stage.setTitle("Report Screen");
        stage.setScene(scene);
        stage.show();
    }


}


