package main;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.io.IOException;


/** Main Class creates an application for customer and appointment management. */

public class Main extends Application {

    /**
     * Initializes the LoginScreen.fxml
     *
     * @param stage
     * @throws IOException
     */

 @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginScreen.fxml"));
        stage.setTitle("Login Screen");
        stage.setScene(new Scene(root, 1080, 800));
        stage.show();
    }


    /**
     * Main method loads database connection and launches the FXML.
     *
     * @param args
     * @throws Exception
     * */

    public static void main(String[] args) {

        //Locale.setDefault(new Locale("fr"));

     ResourceBundle rb =ResourceBundle.getBundle("language/rb", Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("fr") || Locale.getDefault().getLanguage().equals("en"))
            System.out.println(rb.getString("Login"));


        DBConnection.openConnection();
        launch(args);
        DBConnection.closeConnection();

    }


}

