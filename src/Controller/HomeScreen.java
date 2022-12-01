package Controller;

import Model.Appointments;
import helper.AppointmentsQuery;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Controller class for HomeScreen.fxml
 * */
public class HomeScreen implements Initializable {
    private ObservableList<Model.Appointments> appointments;
    @FXML
    private Button ViewButton;
    @FXML
    private Button LogoutButton;
    @FXML
    private RadioButton Reports;
    @FXML
    private RadioButton Customers;
    @FXML
    private RadioButton Appointments;

    /**
     * @param actionEvent ActionEvent activates on Button logout.
     * Logs the user out of the database and closes the scene and opens scene FirstScreen.fxml
     * */
    public void logout(ActionEvent actionEvent){
        try{
            Stage primaryStage = (Stage) LogoutButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/View/FirstScreen.fxml"));
            primaryStage.setTitle("Appointment Management System");
            primaryStage.setScene(new Scene(root, 400, 275));
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * @param actionEvent ActionEvent activates on Button view
     * Opens a new scene depending on which radioButton is toggled.
     *                    scene Customers.fxml, Reports.fxml, or Appointments.fxml
     * */
    public void view(ActionEvent actionEvent){
        try{
            if(Customers.isSelected()){
                Stage primaryStage = (Stage) ViewButton.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/View/Customers.fxml"));
                primaryStage.setTitle("Appointment Management System");
                primaryStage.setScene(new Scene(root, 900, 600));
                primaryStage.show();
            }
           if(Reports.isSelected()){
                Stage primaryStage = (Stage) ViewButton.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));
                primaryStage.setTitle("Appointment Management System");
                primaryStage.setScene(new Scene(root,940, 486));
                primaryStage.show();
            }
            if(Appointments.isSelected()){
                Stage primaryStage = (Stage) ViewButton.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));
                primaryStage.setTitle("Appointment Management System");
                primaryStage.setScene(new Scene(root, 1000, 600));
                primaryStage.show();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    /**
     * @param location Location to resolve relative paths
     * @param resources Resources to localize root object
     * Queries the Appointments database and calls the reminder() function.
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            appointments = AppointmentsQuery.getAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            reminder();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks appointments list and localtime of the user for appointments scheduled within 15 minutes of login.
     * Alerts user of appointments with INFORMATION alert box if appointment within 15 minutes exists.
     * */
    private void reminder() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus15Min = now.plusMinutes(15);

        FilteredList<Appointments> filteredData = new FilteredList<>(appointments);

        filteredData.setPredicate(row -> {
                    LocalDateTime rowDate = row.getStartTime();
                    return rowDate.isAfter(now.minusMinutes(1)) && rowDate.isBefore(nowPlus15Min);
                }
        );
        if (filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment Reminder");
            alert.setHeaderText("Appointment Notice:");
            alert.setContentText("No upcoming appointments.");
            alert.showAndWait();
        } else {
            String type = filteredData.get(0).getType();
            int customer =  filteredData.get(0).getCustomerId();
            LocalDateTime start = filteredData.get(0).getStartTime();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment Reminder");
            alert.setHeaderText("Reminder: You have the following appointment set for the next 15 minutes.");
            alert.setContentText("Your upcoming " + type + " appointment with Customer#" + customer +
                    " is currently set for " + start + ".");
            alert.showAndWait();
        }

    }
}

