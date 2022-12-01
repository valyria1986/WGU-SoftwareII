package Controller;

import helper.AppointmentsQuery;
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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
/**
 * Appointments controller for Appointments.fxml
 *
 * */
public class Appointments implements Initializable {

    static ObservableList<Model.Appointments> appointments;
    @FXML
    private TextField textField;
    @FXML
    private TableView<Model.Appointments> appointTable;
    @FXML
    private TableColumn<?, ?> appointID;
    @FXML
    private TableColumn<?, ?> title;
    @FXML
    private TableColumn<?, ?> description;
    @FXML
    private TableColumn<?, ?> locationCol;
    @FXML
    private TableColumn<?, ?> contact;
    @FXML
    private TableColumn<?, ?> type;
    @FXML
    private TableColumn<?, ?> startDateTime;
    @FXML
    private TableColumn<?, ?> endDateTime;
    @FXML
    private TableColumn<?, ?> customerID;
    @FXML
    private TableColumn<?, ?> userID;
    @FXML
    private RadioButton allRadio;
    @FXML
    private RadioButton weekRadio;
    @FXML
    private RadioButton monthRadio;
    @FXML
    private Button Exit;

    /**
     * @param actionEvent activates on Button Exit and closes current window and returns to HomeScreen.fxml
     * */
    public void previousScreen(ActionEvent actionEvent){
        try{
            Stage primaryStage = (Stage) Exit.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/View/HomeScreen.fxml"));
            primaryStage.setTitle("Appointment Management System Home Screen");
            primaryStage.setScene(new Scene(root, 600, 300));
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * @param event activates on All Radio button toggle and queries all appointments in database and lists them in the
     *              appointment table.
     * */
    public void allToggle(ActionEvent event) {
        try{
            if (allRadio.isSelected()) {
                appointments = AppointmentsQuery.getAppointments();
                appointTable.setItems(appointments);
                appointTable.refresh();
                monthRadio.setSelected(false);
                weekRadio.setSelected(false);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

    }
    /**
    * @param event activates on monthRadio button and uses helper class AppointmentsQuery to query all
    *               appointments for the current month and populates the table.
    * */
    public void monthToggle(ActionEvent event){
        if (monthRadio.isSelected()) {
            try {
                appointments = AppointmentsQuery.monthlyAppointments();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            appointTable.setItems(appointments);
            appointTable.refresh();
            weekRadio.setSelected(false);
            allRadio.setSelected(false);

        }
    }
    /**
     * @param event activates on weekRadio toggle and uses helper class AppointmentsQuery to query all appointments in
    *              the current week.
    * */
    public void weekToggle(ActionEvent event){
        if (weekRadio.isSelected()) {
            try {
                appointments = AppointmentsQuery.weeklyAppointments();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            appointTable.setItems(appointments);
            appointTable.refresh();
            monthRadio.setSelected(false);
            allRadio.setSelected(false);
        }
    }
    /**
     * @param location Location to resolve relative paths
     * @param resources Resources to localize root object
     * Initializes the table view and populates it with an Observable List from all queried appoints.
     * Uses helper class AppointmentsQuery
     * Uses Model class Appointments
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            appointments = AppointmentsQuery.getAppointments();

            appointTable.setItems(appointments);
            appointID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            title.setCellValueFactory(new PropertyValueFactory<>("title"));
            description.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            contact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
            type.setCellValueFactory(new PropertyValueFactory<>("type"));
            startDateTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            endDateTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
            customerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            userID.setCellValueFactory(new PropertyValueFactory<>("userId"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
    * @param actionEvent on button press of search
    * Calls findAppointments(String s) function and populates observable list with searched appointment titles or IDs
    * */
    public void search(ActionEvent actionEvent) {
            ObservableList<Model.Appointments> searchTable = findAppointments(textField.getText());
            appointTable.setItems(searchTable);
        }
    /**
    * @param input String text from user. Populates appointments if String matches appointmentID or appointmentTitle
    * @return ObservableList appointmentList
    * */
    private static ObservableList<Model.Appointments> findAppointments(String input) {

        ObservableList<Model.Appointments> appointmentList = FXCollections.observableArrayList();

            for (Model.Appointments appointment: appointments) {
                if (appointment.getTitle().contains(input)) {
                    appointmentList.add(appointment);
                } else if (Integer.toString(appointment.getAppointmentId()).contains(input)) {
                    appointmentList.add(appointment);
                }
            }
            return appointmentList;
        }
    /**
     * @param actionEvent ActionEvent activates on Button create.
     * Closes current window and opens scene CreateAppointment.fxml
     * */
    public void createApp(ActionEvent actionEvent) {
        try{
            Stage primaryStage = (Stage) Exit.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/View/CreateAppointment.fxml"));
            primaryStage.setTitle("Appointment Management System Home Screen");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * @param actionEvent ActionEvent activates on Button update.
     *Closes current window and opens scene UpdateAppointment.fxml if a table view has an
     *                    appropriately selected item.
     * */
    public void updateApp(ActionEvent actionEvent) {
        try {
            if (appointTable.getSelectionModel().getSelectedItem() != null) {
                UpdateAppointment.appointmentToUpdate(appointTable.getSelectionModel().getSelectedItem());
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                Parent scene = FXMLLoader.load(getClass().getResource("/View/UpdateAppointment.fxml"));
                stage.setScene(new Scene(scene));
                stage.setTitle("Update Appointment");
                stage.show();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Select an appointment");
                alert.setContentText("Please select an appointment to update.");
                alert.showAndWait();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * @param actionEvent ActionEvent activates on delete button press
     * If appointment is selected from tableview, deletes the appointment.
     * Provides ERROR alerts and CONFIRMATION alerts for selected appointment.
     * If no appointment is selected provides ERROR alert.
     * */
    public void deleteApp(ActionEvent actionEvent) {
        Model.Appointments selectedAppointment = appointTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Appointment Selected");
            alert.setContentText("Please select an appointment to delete.");
            alert.showAndWait();
        } else if (appointTable.getSelectionModel().getSelectedItem() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Appointment");
            alert.setContentText("Are you sure you would like to delete the appointment?");
            Optional<ButtonType> selection = alert.showAndWait();

            if (selection.get() == ButtonType.OK) {
                try {
                    if (AppointmentsQuery.deleteApp(appointTable.getSelectionModel().getSelectedItem().getAppointmentId())) {
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Successful Delete");
                        alert.setContentText("Successfully deleted Appointment ID: " + selectedAppointment.getAppointmentId() + " Type: " + selectedAppointment.getType());
                        alert.showAndWait();

                        appointments = AppointmentsQuery.getAppointments();
                        appointTable.setItems(appointments);
                        appointTable.refresh();
                    } else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Dialog");
                        alert.setContentText("Could not delete appointment.");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
