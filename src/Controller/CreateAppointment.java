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
/**
 * Controller class for CreateAppointment.fxml
 * implements Alerts interface lamba expression for simplified alerts.
 * */
public class CreateAppointment implements Initializable {
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
    private ZonedDateTime startDateTimeConvert;
    private ZonedDateTime endDateTimeConvert;
    /**
     * @param actionEvent ActionEvent activates on Button cancel
     * Provides an CONFIRMATION alert upon exiting.
     * Closes current window on Alert OK button press and opens scene Appointments.fxml else window remains open.
     * */
    public void previousScreen(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your Appointment will not be saved! CONTINUE?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() ==  ButtonType.OK)) {
            try {
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                Parent scene = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                alertBox.alertBox("Error Dialog","Load Screen Error!");
            }
        }
    }
    /**
     * @param time LocalDateTime
     * converts current local time to Eastern Standard Time
     * */
    private ZonedDateTime convertToEST(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.of("America/New_York"));
    }

    /** Creates Appointment
     * Calls validation function
     * Catches Exception, throws alert, and prints stacktrace.
     * @param actionEvent creates Appointment if valid when Save button is clicked
     */
    @FXML
    public void save(ActionEvent actionEvent) {


        if (validateAppointment(title.getText(), description.getText(), appLocation.getText())) {
            try {

                boolean success = AppointmentsQuery.createAppointment(
                        contact.getSelectionModel().getSelectedItem(),
                        title.getText(),
                        description.getText(),
                        appLocation.getText(),
                        type.getSelectionModel().getSelectedItem(),
                        LocalDateTime.of(startDate.getValue(), LocalTime.parse(startTime.getSelectionModel().getSelectedItem())),
                        LocalDateTime.of(endDate.getValue(), LocalTime.parse(endTime.getSelectionModel().getSelectedItem())),customerID.getSelectionModel().getSelectedItem(),
                        userID.getSelectionModel().getSelectedItem());

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment Created!");
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
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment not CREATED!");
                    Optional<ButtonType> result = alert.showAndWait();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * @param location String from text box Location label.
     * @param description String from text box Description label.
     * @param title String from text box Title label.
     * Checks to make sure all required appointment parameters are added. Provides Alerts if all parameters aren't met.
     * */
    private boolean validateAppointment(String title, String description, String location){
        if (contact.getSelectionModel().isEmpty()){
            alertBox.alertBox("Error Dialog","Contact is required!");
            return false;
        }

        if (title.isEmpty()){
            alertBox.alertBox("Error Dialog","Title is required!");
            return false;
        }

        if (description.isEmpty()){
            alertBox.alertBox("Error Dialog","Description is required!");
            return false;
        }

        if (location.isEmpty()){
            alertBox.alertBox("Error Dialog","Location is required!");
            return false;
        }

        if (type.getSelectionModel().isEmpty()) {
            alertBox.alertBox("Error Dialog","Type is required!");
            return false;
        }

        if (startDate.getValue() == null) {
            alertBox.alertBox("Error Dialog","Start Date is required!");
            return false;
        }

        if (startTime.getSelectionModel().isEmpty()){
            alertBox.alertBox("Error Dialog","Start Time is required!");
            return false;
        }

        if (endDate.getValue() == null){
            alertBox.alertBox("Error Dialog","End Date is required!");
            return false;
        }

        if (startDate.getValue().isBefore(LocalDate.now())){
            alertBox.alertBox("Invalid date","Start date cannot be before today's date " + LocalDate.now() + "!");
            return false;
        }

        if (endDate.getValue().isBefore(startDate.getValue())) {
            alertBox.alertBox("Error Dialog","End Date cannot be before start date!");
            return false;
        }

        if (endTime.getSelectionModel().isEmpty()){
            alertBox.alertBox("Error Dialog","End Time is required!");
            return false;
        }

        if (customerID.getSelectionModel().isEmpty()) {
            alertBox.alertBox("Error Dialog","Customer ID is required!");
            return false;
        }

        if (userID.getSelectionModel().isEmpty()) {
            alertBox.alertBox("Error Dialog","User ID is required!");
            return false;
        }

        // additional date validation

        LocalTime startTimeGet = LocalTime.parse(startTime.getSelectionModel().getSelectedItem());
        LocalTime endTimeGet = LocalTime.parse(endTime.getSelectionModel().getSelectedItem());

        if (endTimeGet.isBefore(startTimeGet)) {
            alertBox.alertBox("Error Dialog","Appointment start time must be before end time!");
            return false;
        };

        LocalDate startDateGet = startDate.getValue();
        LocalDate endDateGet = endDate.getValue();

        if (!startDateGet.equals(endDateGet)){
            alertBox.alertBox("Error Dialog","Appointments must start and end on the same date!");
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
            alertBox.alertBox("Error Dialog","Appointments must be within business hours 8AM - 10PM EST.");
            return false;
        }

        if (endDateTimeConvert.toLocalTime().isAfter(LocalTime.of(22, 0))) {
            alertBox.alertBox("Error Dialog","Appointments must be within business hours 8AM - 10PM EST.");
            return false;
        }

        if (startDateTimeConvert.toLocalTime().isBefore(LocalTime.of(8, 0))) {
            alertBox.alertBox("Error Dialog","Appointments must be within business hours 8AM - 10PM EST.");
            return false;
        }

        if (endDateTimeConvert.toLocalTime().isBefore(LocalTime.of(8, 0))) {
            alertBox.alertBox("Error Dialog","Appointments must be within business hours 8AM - 10PM EST.");
            return false;
        }

        return true;
    }

    /** Populates Start Time and End Time Combo Boxes in 15 minute increments
     */
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

    /** Populates Contact Combo Box with Contacts List
     */
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

    /** Populates Customer ID Combo Box with Customer ID List
     */
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

    /** Populates User ID Combo Box with User ID List
     */
    private void populateUserIDComboBox() {
        ObservableList<Integer> userIDComboList = FXCollections.observableArrayList();

        try {
            ObservableList<Users> users = UsersQuery.getUserList();
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

    /** Populates Type Combo Box with hardcoded List
     */
    private void populateTypeComboBox() {
        ObservableList<String> typeList = FXCollections.observableArrayList();

        typeList.addAll("Planning Session", "De-Briefing", "Follow-up", "Pre-Briefing", "Open Session");

        type.setItems(typeList);
    }

    /** This method initializes the combo boxes in the Create Appointment view.
     *  @param location Location to resolve relative paths
     *  @param resources Resources to localize root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTimeComboBoxes();
        populateContactComboBox();
        populateCustomerIDComboBox();
        populateUserIDComboBox();
        populateTypeComboBox();


    }
}
