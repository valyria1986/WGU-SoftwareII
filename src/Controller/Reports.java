package Controller;

import Model.Appointments;
import Model.Contacts;
import Model.Countries;
import helper.AppointmentsQuery;
import helper.ContactQuery;
import helper.CountryQuery;
import helper.CustomersQuery;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for Reports.fxml
 * */
public class Reports implements Initializable {
    @FXML
    private Label customerLabel;
    @FXML
    private Label countLabel;
    @FXML
    private TableColumn<?,?> lastUpdatedBy;
    @FXML
    private Label labelText;
    @FXML
    private TableColumn<?,?> lastUpdated;
    @FXML
    private TableColumn<?,?> stateProvince;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TableColumn<?,?> appID;
    @FXML
    private TableColumn<?,?> title;
    @FXML
    private TableColumn<?,?> description;
    @FXML
    private TableColumn<?,?> type;
    @FXML
    private TableColumn<?,?> startDateTime;
    @FXML
    private TableColumn<?,?> endDateTime;
    @FXML
    private TableColumn<?,?> custID;
    @FXML
    private TableView appointTable;
    static ObservableList<Appointments> appointments;
    static ObservableList<Model.Customers> customers;
    @FXML
    private RadioButton contactSchedule;
    @FXML
    private RadioButton customerType;
    @FXML
    private RadioButton customerMonth;
    @FXML
    private RadioButton customerCountry;
    @FXML
    private Button Exit;
    private final ObservableList<String> customerCountryCombo = FXCollections.observableArrayList();
    private final ObservableList<String> monthList = FXCollections.observableArrayList();
    private final ObservableList<String> typeList = FXCollections.observableArrayList();
    private final ObservableList<String> contactComboList = FXCollections.observableArrayList();

    /**
     * Closes current window and returns to the scene HomeScreen.fxml
     * @param actionEvent ActionEvent activates on Button Exit
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
     * initializes and fills the Observable lists to fill the Combo box.
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthList.addAll("January", "February", "March", "April", "May", "June","July", "August", "September", "October", "November", "December");
        typeList.addAll("Planning Session", "De-Briefing", "Follow-up", "Pre-Briefing", "Open Session");
        setCountryAndContactList();
    }
    /**
     * Populates the table view with and changes column titles for appointment list based on contact ID
     * */
    public void contactSchedule(ActionEvent actionEvent){
        comboBox.setItems(contactComboList);
        appID.setPrefWidth(56.0);
        appID.setText("Appt ID");
        title.setPrefWidth(81.0);
        title.setText("Title");
        description.setPrefWidth(88.0);
        description.setText("Description");
        type.setPrefWidth(72.0);
        type.setText("Type");
        startDateTime.setPrefWidth(146.0);
        startDateTime.setText("Start Date/Time");
        endDateTime.setPrefWidth(166.0);
        endDateTime.setText("End Date/Time");
        custID.setPrefWidth(94.0);
        custID.setText("Customer ID");
        appointTable.setPrefWidth(706.0);
        labelText.setText("Contact");
        customerType.setSelected(false);
        customerMonth.setSelected(false);
        customerCountry.setSelected(false);
        appointTable.getItems().clear();
        customerLabel.setText("");
        countLabel.setText("");

    }
    /**
     * populates the country and contacts observable lists for the combo box.
     * */
    private void setCountryAndContactList(){
        try {
            ObservableList<Countries> countries = CountryQuery.getCountries();
            if (countries != null){
                for (Countries country: countries) {
                    if (!customerCountryCombo.contains(country.getCountry())) {
                        customerCountryCombo.add(String.valueOf(country.getCountry()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ObservableList<Contacts> contacts = ContactQuery.getContacts();
            if (contacts != null){
                for (Contacts contact: contacts) {
                    if (!contactComboList.contains(contact.getContactName()) & !contactComboList.contains(String.valueOf(contact.getContactId()))) {
                        contactComboList.add(String.valueOf(contact.getContactId()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Populates the table view with the selected contact in the combo box
     * @param contactID int of selected contact id from combo box.
     * */
    private void setContactTable(int contactID){
        try {
            appointments = AppointmentsQuery.getContactAppoint(contactID);
            appointTable.setItems(appointments);
            appID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            title.setCellValueFactory(new PropertyValueFactory<>("title"));
            description.setCellValueFactory(new PropertyValueFactory<>("description"));
            type.setCellValueFactory(new PropertyValueFactory<>("type"));
            startDateTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            endDateTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
            custID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            lastUpdated.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
            lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
            stateProvince.setCellValueFactory(new PropertyValueFactory<>("country"));
            appointTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Checks the combo box selection and calls the appropriate function to populate the table view.
     * @param actionEvent ActionEvent activates on selection from combo box.
     * */
    public void selectedContact(ActionEvent actionEvent) {
        if(monthList.contains(comboBox.getSelectionModel().getSelectedItem())){
            setMonthTable(comboBox.getSelectionModel().getSelectedItem());
        }
        if(typeList.contains(comboBox.getSelectionModel().getSelectedItem())){
            setTypeTable(comboBox.getSelectionModel().getSelectedItem());
        }
        if(contactComboList.contains(comboBox.getSelectionModel().getSelectedItem())){
            setContactTable(Integer.parseInt(comboBox.getSelectionModel().getSelectedItem()));
        }
        if(customerCountryCombo.contains(comboBox.getSelectionModel().getSelectedItem())){
            setCountryTable(comboBox.getSelectionModel().getSelectedItem());
        }

    }
    /**
     * Populates the table view with appointment report based on selected country.
     * @param selectedCountry String of selected country from combo box.
     * */
    private void setCountryTable(String selectedCountry) {
        try {
            customers = CustomersQuery.getCustomersOfCountry(selectedCountry);
            appointTable.setItems(customers);
            appID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            title.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            description.setCellValueFactory(new PropertyValueFactory<>("address"));
            type.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            startDateTime.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            endDateTime.setCellValueFactory(new PropertyValueFactory<>("createDate"));
            custID.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
            lastUpdated.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
            lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
            stateProvince.setCellValueFactory(new PropertyValueFactory<>("country"));
            appointTable.refresh();
            countLabel.setText(Integer.toString(customers.toArray().length));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Populates the table view with the appointment report based on appointment type.
     * @param selectedType String of appointment type
     * */
    private void setTypeTable(String selectedType) {
        try {
            customers = CustomersQuery.getCustomersOfType(selectedType);
            appointTable.setItems(customers);
            appID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            title.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            description.setCellValueFactory(new PropertyValueFactory<>("address"));
            type.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            startDateTime.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            endDateTime.setCellValueFactory(new PropertyValueFactory<>("createDate"));
            custID.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
            lastUpdated.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
            lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
            stateProvince.setCellValueFactory(new PropertyValueFactory<>("country"));
            appointTable.refresh();
            countLabel.setText(Integer.toString(customers.toArray().length));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Populates the table with all appointments in the selected month.
     * @param selectedMonth String of month name selected from combo box.
     * */
    private void setMonthTable(String selectedMonth) {
        try {
            String subString = selectedMonth.substring(0,3);
            int monthNumber = getNumberFromMonthName(subString, Locale.ENGLISH);
            System.out.println(monthNumber);
            customers = CustomersQuery.getCustomersOfMonth(monthNumber);
            appointTable.setItems(customers);
            appID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            title.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            description.setCellValueFactory(new PropertyValueFactory<>("address"));
            type.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            startDateTime.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            endDateTime.setCellValueFactory(new PropertyValueFactory<>("createDate"));
            custID.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
            lastUpdated.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
            lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
            stateProvince.setCellValueFactory(new PropertyValueFactory<>("country"));
            appointTable.refresh();
            countLabel.setText(Integer.toString(customers.toArray().length));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Takes the first 3 letters of month name and converts it to the corresponding month number
     * @param selectedMonth String of First three letters of month only
     * @param locale Locale
     * @return int Number of the month
     * */
    private int getNumberFromMonthName(String selectedMonth, Locale locale) {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("MMM")
                .withLocale(locale);
        TemporalAccessor temporalAccessor = dtFormatter.parse(selectedMonth);
        return temporalAccessor.get(ChronoField.MONTH_OF_YEAR);
    }

    /**
     * Populates the combo box with appointment types and calls changeToCustomerTable to change the table view.
     * @param actionEvent ActionEvent activates on selection of Type radio button.
     * */
    public void customerTypeSched(ActionEvent actionEvent) {
        comboBox.setItems(typeList);
        changeToCustomerTable();
        labelText.setText("Type");
        contactSchedule.setSelected(false);
        customerMonth.setSelected(false);
        customerCountry.setSelected(false);
    }
    /**
     * Populates the combo box with list of months and calls changeToCustomerTable to change the table view.
     * @param actionEvent ActionEvent activates on selection of the Month radio button.
     * */
    public void customerMonthSched(ActionEvent actionEvent) {
        comboBox.setItems(monthList);
        changeToCustomerTable();
        labelText.setText("Month");
        contactSchedule.setSelected(false);
        customerType.setSelected(false);
        customerCountry.setSelected(false);
    }
    /**
     * Populates the combo box with list of countries in database and calls changeToCustomerTable to change the table view.
     * @param actionEvent ActionEvent activates on selection of Country radio button.
     * * */
    public void customerCountrySched(ActionEvent actionEvent) {
        comboBox.setItems(customerCountryCombo);
        changeToCustomerTable();
        labelText.setText("Country");
        contactSchedule.setSelected(false);
        customerMonth.setSelected(false);
        customerType.setSelected(false);
    }
    /**
     * Reformats the TableView to provide appropriate column tiles and columns.
     * */
    private void changeToCustomerTable(){
        appID.setPrefWidth(40.0);
        appID.setText("ID");
        title.setPrefWidth(97.0);
        title.setText("Name");
        description.setPrefWidth(88.0);
        description.setText("Address");
        type.setPrefWidth(72.0);
        type.setText("Zipcode");
        startDateTime.setPrefWidth(118.0);
        startDateTime.setText("Phone Number");
        endDateTime.setPrefWidth(109.0);
        endDateTime.setText("Created Date");
        custID.setPrefWidth(109.0);
        custID.setText("Created By");
        appointTable.setPrefWidth(930.0);
        appointTable.getItems().clear();
        customerLabel.setText("Total Customers:");
        countLabel.setText("0");
    }
}
