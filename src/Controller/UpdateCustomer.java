package Controller;

import Model.Countries;
import Model.Customers;
import Model.Divisions;
import helper.CountryQuery;
import helper.CustomersQuery;
import helper.DivisionQuery;
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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for UpdateCustomer.fxml
 * */
public class UpdateCustomer implements Initializable {

    private static Customers selectedCustomer;
    static ObservableList<Customers> customers;
    @FXML
    private TextField id;
    @FXML
    private TextField name;
    @FXML
    private TextField address;
    @FXML
    private TextField zipcode;
    @FXML
    private TextField phone;
    @FXML
    private ComboBox<String> country;
    @FXML
    private ComboBox<String> division;
    @FXML
    private TableView<Customers> customerTable;
    @FXML
    private Button cancel;

    /**
     * Returns to previous screen and closes current scene, opens scene Customers.fxml
     * @param actionEvent ActionEvent activates on Button Cancel
     * */
    public void previousScreen(ActionEvent actionEvent){
        try{
            Stage primaryStage = (Stage) cancel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/View/Customers.fxml"));
            primaryStage.setTitle("Appointment Management System Customers");
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Saves updated customer information to the database. Calls validateNotEmpty() function to check all required information
     * is filled out. CONFIRMATION alert box is provided to make sure the user wants to update or cancel the update.
     * @param actionEvent ActionEvent activates on Button update
     * */
    public void updateCustomer(ActionEvent actionEvent) {
        if(validateNotEmpty(name.getText(), address.getText(), zipcode.getText(), phone.getText())){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Update");
            alert.setContentText("Are you sure you would like to update the customer?");
            Optional<ButtonType> selection = alert.showAndWait();
            if(selection.get() == ButtonType.OK){
                try {
                    CustomersQuery.update(Integer.parseInt(id.getText()), name.getText(), address.getText(), zipcode.getText(), phone.getText(), country.getValue(), division.getValue());
                }catch(SQLException e){
                    e.printStackTrace();
                }
                try{
                    Stage primaryStage = (Stage) cancel.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("/View/Customers.fxml"));
                    primaryStage.setTitle("Appointment Management System Customers");
                    primaryStage.setScene(new Scene(root, 900, 600));
                    primaryStage.show();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /** Helper function to validate Customer Fields are selected and not empty
     * Throws alert if fields are not selected or are empty
     * @param name String value of Customer Name
     * @param address String value of Customer Address
     * @param postalCode String value of Customer Postal Code
     * @param phone String value of Customer Phone Number
     * @return Boolean Returns true if valid and false if not valid
     */
    private boolean validateNotEmpty(String name, String address, String postalCode, String phone){
        if (name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Name is required.");
            alert.showAndWait();
            return false;
        }

        if (address.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Address is required.");
            alert.showAndWait();
            return false;
        }

        if (postalCode.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Postal Code is required.");
            alert.showAndWait();
            return false;
        }

        if (phone.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Phone Number is required.");
            alert.showAndWait();
            return false;
        }

        if (division.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Division is required.");
            alert.showAndWait();
            return false;
        }

        if (country.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Country is required.");
            alert.showAndWait();
            return false;
        }

        return true;
    }
    /**
     * Populates the division combo box with database divisions
     * */
    private void setDivisionCombo(){
        ObservableList<String> divisionList = FXCollections.observableArrayList();

        try {
            ObservableList<Divisions> divisions = DivisionQuery.getDivisionsByCountry(country.getValue());
            if (divisions != null) {
                for (Divisions division: divisions) {
                    divisionList.add(division.getDivision());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        division.setItems(divisionList);
    }

    /** Populates Country Combo Box with database countries
     */
    private void setCountryCombo(){
        ObservableList<String> countryList = FXCollections.observableArrayList();

        try {
            ObservableList<Countries> countries = CountryQuery.getCountries();;
            if (countries != null) {
                for (Countries country: countries) {
                    countryList.add(country.getCountry());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        country.setItems(countryList);
    }
    /**
     * assigns selected customer to private variable selectedCustomer
     * */
    static void selectedCustomer(Customers customer) {
        selectedCustomer = customer;
    }

    /** This method sets the fields with the selected customer fields and initializes the combo boxes in the Update Customer view.
     *  Catches Exception, throws alert, and prints stacktrace.
     *  @param location Location to resolve relative paths
     *  @param resources Resources to localize root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate dropdown for country
        setCountryCombo();

        // Populate text fields with existing data
        id.setText(Integer.toString(selectedCustomer.getCustomerId()));
        name.setText(selectedCustomer.getCustomerName());
        zipcode.setText(selectedCustomer.getPostalCode());
        address.setText(selectedCustomer.getAddress());
        phone.setText(selectedCustomer.getPhoneNumber());
        country.getSelectionModel().select(selectedCustomer.getCountry());
        // Populate dropdown for division after country has been selected.
        setDivisionCombo();
        division.getSelectionModel().select(selectedCustomer.getDivision());
    }
    public void countrySelected(ActionEvent actionEvent) {
        setDivisionCombo();
    }
}
