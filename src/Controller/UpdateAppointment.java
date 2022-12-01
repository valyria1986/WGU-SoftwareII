package Controller;

import Model.Appointments;
import Model.Contacts;
import Model.Customers;
import Model.Users;
import helper.AppointmentsQuery;
import helper.ContactQuery;
import helper.CustomersQuery;
import helper.UsersQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Controller class for UpdateAppointment.fxml
 * */
public class UpdateAppointment implements Initializable {
    /**
     * lambda expression for ERROR alert box
     * */
    Alerts alertBox = (title, message) -> {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();

        return alert;
    };
    private static Appointments appointmentToUpdate;
    @FXML
    private ComboBox<String> contact;
    @FXML
    private TextField title;
    @FXML
    private TextField description;
    @FXML
    private TextField appLocation;
    @FXML
    private ComboBox<String> type;
    @FXML
    private ComboBox<Integer> userID;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private ComboBox<String> startTime;
    @FXML
    private ComboBox<String> endTime;
    @FXML
    private ComboBox<Integer> customerID;
    @FXML
    private TextField appointID;
    @FXML
    private Button Exit;
    private ZonedDateTime startDateTimeConvert;
    private ZonedDateTime endDateTimeConvert;

    /**
     * assigns a selected appointment to private variable appointmentToUpdate
     * @param selectedAppointment Appointments model
     * */
    public static void appointmentToUpdate(Appointments selectedAppointment) {
        appointmentToUpdate = selectedAppointment;
    }
    /**
     * Returns to previous scene Appointments.fxml
     * @param actionEvent ActionEvent on Button Cancel
     * */
    public void previousScreen(ActionEvent actionEvent){
        try{
            Stage primaryStage = (Stage) Exit.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));
            primaryStage.setTitle("Appointment Management System Home Screen");
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * populates combo box Type
     * */
    private void populateTypeComboBox() {
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.addAll("Planning Session", "De-Briefing", "Follow-up", "Pre-Briefing", "Open Session");
        type.setItems(typeList);
    }
    /**
     * populates the time combo box in 15 minute increments.
     * */
    private void populateTimeComboBoxes() {
        ObservableList<String> time = FXCollections.observableArrayList();
        LocalTime startTimes = LocalTime.of(7, 0);
        LocalTime endTimes = LocalTime.of(23, 0);

        time.add(startTimes.toString());
        while (startTimes.isBefore(endTimes)) {
            startTimes = startTimes.plusMinutes(15);
            time.add(startTimes.toString());
        }

        startTime.setItems(time);
        endTime.setItems(time);
    }
    /**
     * populates the contact combo box with database list of contacts
     * */
    private void populateContactComboBox() {
        ObservableList<String> contactComboList = FXCollections.observableArrayList();

        try {
            ObservableList<Contacts> contacts = ContactQuery.getContacts();
            if (contacts != null){
                for (Contacts contact: contacts) {
                    if (!contactComboList.contains(contact.getContactName())) {
                        contactComboList.add(contact.getContactName());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        contact.setItems(contactComboList);
    }
    /**
     * populates the combo box with a list of customer ID's from database.
     * */
    private void populateCustomerIDComboBox() {
        ObservableList<Integer> customerIDComboList = FXCollections.observableArrayList();

        try {
            ObservableList<Customers> customers = CustomersQuery.getCustomers();
            if (customers != null) {
                for (Customers customer: customers) {
                    customerIDComboList.add(customer.getCustomerId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        customerID.setItems(customerIDComboList);
    }
    /**
     * populates the combo box with user ID's in database.
     * */
    private void populateUserIDComboBox() {
        ObservableList<Integer> userIDComboList = FXCollections.observableArrayList();

        try {
            ObservableList<Model.Users> users = UsersQuery.getUserList();
            if (users != null) {
                for (Users user: users) {
                    userIDComboList.add(user.getUserId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userID.setItems(userIDComboList);
    }
    /** Converts time to EST.
     * @param time LocalDateTime time to convert
     * @return ZonedDateTime Returns time converted to EST
     */
    private ZonedDateTime convertToEST(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.of("America/New_York"));
    }
    /**
     * Converts time to users time zone.
     * @param time LocalDateTime time to convert
     * @param zoneId String of time zone id
     * */
    private ZonedDateTime convertToTimeZone(LocalDateTime time, String zoneId) {
        return ZonedDateTime.of(time, ZoneId.of(zoneId));
    }
    /**
     * Initializes combo box populating functions and fills text fields with selected Appointment information.
     * @param location URL to resolve relative paths
     * @param resources ResourceBundle to localize root object
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTimeComboBoxes();
        populateContactComboBox();
        populateCustomerIDComboBox();
        populateUserIDComboBox();
        populateTypeComboBox();

        try {
            Appointments appointment = AppointmentsQuery.getAppointmentWithID(appointmentToUpdate.getAppointmentId());

            ZonedDateTime zonedStartTime = convertToTimeZone(appointment.getStartDate().atTime(appointment.getStartTime().toLocalTime()), String.valueOf(ZoneId.of(TimeZone.getDefault().getID())));
            ZonedDateTime zonedEndTime = convertToTimeZone(appointment.getEndDate().atTime(appointment.getEndTime().toLocalTime()), String.valueOf(ZoneId.of(TimeZone.getDefault().getID())));

            if (appointment != null) {
                contact.getSelectionModel().select(appointment.getContactName());
                title.setText(appointment.getTitle());
                description.setText(appointment.getDescription());
                appLocation.setText(appointment.getLocation());
                type.getSelectionModel().select(appointment.getType());
                userID.getSelectionModel().select(Integer.valueOf(appointment.getUserId()));
                appointID.setText(String.valueOf(appointment.getAppointmentId()));
                startDate.setValue(appointment.getStartDate());
                startTime.getSelectionModel().select(String.valueOf(zonedStartTime.toLocalTime()));
                endDate.setValue(appointment.getEndDate());
                endTime.getSelectionModel().select(String.valueOf(zonedEndTime.toLocalTime()));
                customerID.getSelectionModel().select(Integer.valueOf(appointment.getCustomerId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Saves changes to appointment with a CONFIRMATION alert
     * @param actionEvent ActionEvent activates on Button Save
     * */
    public void save(ActionEvent actionEvent) {


        if (validateAppointment(title.getText(), description.getText(), appLocation.getText(), appointID.getText()
        )) {
            try {

                boolean success = AppointmentsQuery.updateAppointment(
                        contact.getSelectionModel().getSelectedItem(),
                        title.getText(),
                        description.getText(),
                        appLocation.getText(),
                        type.getSelectionModel().getSelectedItem(),
                        LocalDateTime.of(startDate.getValue(), LocalTime.parse(startTime.getSelectionModel().getSelectedItem())),
                        LocalDateTime.of(endDate.getValue(), LocalTime.parse(endTime.getSelectionModel().getSelectedItem())),
                        customerID.getSelectionModel().getSelectedItem(),
                        userID.getSelectionModel().getSelectedItem(),
                        Integer.parseInt(appointID.getText()));

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Update");
                    alert.setContentText("Are you sure you would like to update the appointment?");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && (result.get() ==  ButtonType.OK)) {
                        try {
                            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                            Parent scene = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));
                            stage.setScene(new Scene(scene));
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error Dialog");
                            alert.setContentText("Load Screen Error.");
                            alert.showAndWait();
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update appointment");
                    Optional<ButtonType> result = alert.showAndWait();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Validates required information is filled out.
     * @param title String of title text box
     * @param description String of description text box
     * @param location String of location text box
     * @param appointmentId String of appointmentID text box
     * @return true if all required fields are filled out.
     *         false if a field is missing and produces an ERROR alert box with message.
     * */
    private boolean validateAppointment(String title, String description, String location, String appointmentId){
        if (contact.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Contact is required.");
            alert.showAndWait();
            return false;
        }

        if (title.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Title is required.");
            alert.showAndWait();
            return false;
        }

        if (description.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Description is required.");
            alert.showAndWait();
            return false;
        }

        if (location.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Location is required.");
            alert.showAndWait();
            return false;
        }

        if (type.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Type is required.");
            alert.showAndWait();
            return false;
        }

        if (appointmentId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Appointment ID is required.");
            alert.showAndWait();
            return false;
        }

        if (startDate.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Start Date is required.");
            alert.showAndWait();
            return false;
        }

        if (startTime.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Start Time is required.");
            alert.showAndWait();
            return false;
        }

        if (endDate.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("End Date is required.");
            alert.showAndWait();
            return false;
        }

        if (endDate.getValue().isBefore(startDate.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("End Date must be after Start Date.");
            alert.showAndWait();
            return false;
        }

        if (endTime.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("End Time is required.");
            alert.showAndWait();
            return false;
        }

        if (customerID.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Customer ID is required.");
            alert.showAndWait();
            return false;
        }

        if (userID.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("User ID is required.");
            alert.showAndWait();
            return false;
        }

        // additional date validation

        LocalTime startTimeGet = LocalTime.parse(startTime.getSelectionModel().getSelectedItem());
        LocalTime endTimeGet = LocalTime.parse(endTime.getSelectionModel().getSelectedItem());

        if (endTimeGet.isBefore(startTimeGet)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Appointment start time must be before end time.");
            alert.showAndWait();
            return false;
        };

        LocalDate startDateGet = startDate.getValue();
        LocalDate endDateGet = endDate.getValue();

        if (!startDateGet.equals(endDateGet)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Appointments must start and end on the same date.");
            alert.showAndWait();
            return false;
        };

        // Check for overlapping appointments


        LocalDateTime selectedStart = startDateGet.atTime(startTimeGet);
        LocalDateTime selectedEnd = endDateGet.atTime(endTimeGet);


        try {
            ObservableList<Model.Appointments> appointments = AppointmentsQuery.getAppointments();
            for (Appointments appointment: appointments) {
                if(appointment.getStartDate().compareTo(startDateGet) == 0) {
                    if (selectedStart.isAfter(appointment.getStartTime()) && selectedStart.isBefore(appointment.getEndTime())
                            || selectedStart.isEqual(appointment.getStartTime()) || selectedStart.isEqual(appointment.getEndTime())) {
                        alertBox.alertBox("Error Dialog", "Appointments must not overlap with existing customer appointments.");
                        return false;
                    } else if (selectedEnd.isAfter(appointment.getStartTime()) && selectedEnd.isBefore(appointment.getEndTime())
                            || selectedEnd.isEqual(appointment.getStartTime()) || selectedEnd.isEqual(appointment.getEndTime())) {
                        alertBox.alertBox("Error Dialog", "Appointments must not overlap with existing customer appointments.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // check if between business hours
        startDateTimeConvert = convertToEST(LocalDateTime.of(startDate.getValue(), LocalTime.parse(startTime.getSelectionModel().getSelectedItem())));
        endDateTimeConvert = convertToEST(LocalDateTime.of(endDate.getValue(), LocalTime.parse(endTime.getSelectionModel().getSelectedItem())));

        if (startDateTimeConvert.toLocalTime().isAfter(LocalTime.of(22, 0))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Appointments must be within business hours 8AM - 10PM EST.");
            alert.showAndWait();
            return false;
        }

        if (endDateTimeConvert.toLocalTime().isAfter(LocalTime.of(22, 0))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Appointments must be within business hours 8AM - 10PM EST.");
            alert.showAndWait();
            return false;
        }

        if (startDateTimeConvert.toLocalTime().isBefore(LocalTime.of(8, 0))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Appointments must be within business hours 8AM - 10PM EST.");
            alert.showAndWait();
            return false;
        }

        if (endDateTimeConvert.toLocalTime().isBefore(LocalTime.of(8, 0))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Appointments must be within business hours 8AM - 10PM EST.");
            alert.showAndWait();
            return false;
        }

        return true;
    }
}
