package Controller;

import Model.Countries;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
/**
* CreateCustomer Controller
* Uses a lambda Interface Alerts for simplified ERROR alert messages.
* */
public class CreateCustomer implements Initializable {
    Alerts alertBox = (title, message) -> {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();

        return alert;
    };
    @FXML
    private ComboBox<String> division;
    @FXML
    private ComboBox<String> country;
    @FXML
    private TextField phone;
    @FXML
    private TextField zipcode;
    @FXML
    private TextField address;
    @FXML
    private TextField name;
    @FXML
    private Button cancel;

    /**
     * previousScreen
     * @param actionEvent activates on Button cancel and returns to previous screen.
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
 * createCustomer
 * @param actionEvent activates on Button Save and inserts the new customer into the SQL database.
 * calls validateNotEmpty function to check if all required data is entered.
 * */
    public void createCustomer(ActionEvent actionEvent){
        if(validateNotEmpty(name.getText(), address.getText(), zipcode.getText(), phone.getText())){
            try {
                CustomersQuery.insert(name.getText(), address.getText(), zipcode.getText(), phone.getText(), country.getValue(), DivisionQuery.getDivisionID(division.getValue()).getDivisionID());
            }catch(SQLException e){
                e.printStackTrace();
            }
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
    }
/**
 * setDivisionCombo
 * No Param, calls DivisionQuery and adds division options into a comboBox.
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

    /** Populates Country Combo Box
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
            alertBox.alertBox("Error Dialog","Name is required!");
            return false;
        }

        if (address.isEmpty()){
            alertBox.alertBox("Error Dialog","Address is required!");
            return false;
        }

        if (postalCode.isEmpty()){
            alertBox.alertBox("Error Dialog","Zipcode is required!");
            return false;
        }

        if (phone.isEmpty()){
            alertBox.alertBox("Error Dialog","Phone number is required!");
            return false;
        }

        if (division.getSelectionModel().isEmpty()) {
            alertBox.alertBox("Error Dialog","Division is required!");
            return false;
        }

        if (country.getSelectionModel().isEmpty()) {
            alertBox.alertBox("Error Dialog","Country is required!");
            return false;
        }

        return true;
    }

    /** This method initializes the combo boxes in the Create Customer view.
     *  @param location Location to resolve relative paths
     *  @param resources Resources to localize root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCountryCombo();
    }

    public void countrySelected(ActionEvent actionEvent) {
        setDivisionCombo();
    }
}
