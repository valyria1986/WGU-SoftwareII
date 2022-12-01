package Controller;

import helper.AppointmentsQuery;
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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for Customers.fxml
 * */
public class Customers implements Initializable {

    static ObservableList<Model.Customers> customers;
    @FXML
    private TableView<Model.Customers> customerTable;
    @FXML
    private TableColumn<?,?> idColumn;
    @FXML
    private TableColumn<?,?> nameColumn;
    @FXML
    private TableColumn<?,?> addressColumn;
    @FXML
    private TableColumn<?,?> zipcodeColumn;
    @FXML
    private TableColumn<?,?> phoneColumn;
    @FXML
    private TableColumn<?,?> countryColumn;
    @FXML
    private TableColumn<?,?> divisionColumn;
    @FXML
    private Button Exit;
    @FXML
    private Button createCustomer;
    @FXML
    private Button updateCustomer;
    @FXML
    private TextField searchText;

    /**
     * @param actionEvent activates on button press of Exit. Closes current window and opens HomeScreen.fxml
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
     * @param actionEvent ActionEvent on button press of Update.
     * Opens scene of UpdateCustomer.fxml with selected customer.
     * If no customer is selected provides ERROR Alert box.
     * */
    public void updateCustomer(ActionEvent actionEvent){
        if(customerTable.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Please select a customer to update!");
            alert.showAndWait();
        }else{
            Model.Customers update = customerTable.getSelectionModel().getSelectedItem();
            UpdateCustomer.selectedCustomer(update);
            try{
                Stage primaryStage = (Stage) updateCustomer.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/View/UpdateCustomer.fxml"));
                primaryStage.setTitle("Appointment Management System Update Customer");
                primaryStage.setScene(new Scene(root, 600, 400));
                primaryStage.show();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    /**
     * @param actionEvent ActionEvent activates on Button create.
     * Closes current scene and opens scene CreateCustomer.fxml
     * */
    public void createCustomer(ActionEvent actionEvent){
        try{
            Stage primaryStage = (Stage) createCustomer.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/View/CreateCustomer.fxml"));
            primaryStage.setTitle("Appointment Management System Create Customer");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * @param actionEvent ActionEvent activates on Button delete.
     * Deletes selected customer from database.
     * If no customer is selected provides ERROR alert box.
     * If customer has existing appointments provides ERROR alert box
     * */
    public void deleteCustomer(ActionEvent actionEvent) {
        Model.Customers delete = customerTable.getSelectionModel().getSelectedItem();
        if(delete == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete Error");
            alert.setContentText("Please select a customer to delete.");
            alert.showAndWait();
        }else{
            try{
                if(checkAppointments(delete)){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Delete");
                    alert.setContentText("Are you sure you would like to delete the customer?");
                    Optional<ButtonType> selection = alert.showAndWait();
                    if(selection.get() == ButtonType.OK){
                        CustomersQuery.delete(delete.getCustomerId());
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Error");
                    alert.setContentText("Would you like to delete the customer and all existing appoints for customer?");
                    Optional<ButtonType> selection = alert.showAndWait();
                    if(selection.get() == ButtonType.OK){
                        ObservableList<Model.Appointments> appointments = AppointmentsQuery.getAppointments();
                        for(Model.Appointments appointment: appointments){
                            Model.Customers update = customerTable.getSelectionModel().getSelectedItem();
                            if(update.getCustomerId() == appointment.getCustomerId()){
                                AppointmentsQuery.deleteApp(appointment.getAppointmentId());
                            }
                        }
                        CustomersQuery.delete(delete.getCustomerId());
                    }
                }
                customers = CustomersQuery.getCustomers();
                customerTable.setItems(customers);
                customerTable.refresh();
            }catch(SQLException e){
                e.printStackTrace();
                }
            }

        }

    /**
     * @param selectedCustomer Model.Customers
     * Queries the database using helper class AppointmentsQuery to check if customer has existing appointments.
     * */
    private boolean checkAppointments(Model.Customers selectedCustomer) {
        try {
            ObservableList appointments = AppointmentsQuery.getCustomerAppoint(selectedCustomer.getCustomerId());
            if (appointments != null && appointments.size() < 1) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * @param actionEvent ActionEvent activates on Button search.
     * calls lookupCustomer function and repopulates the table view with the found results.
     * */
    public void search(ActionEvent actionEvent) {
        ObservableList<Model.Customers> updateTable = lookupCustomer(searchText.getText());
        customerTable.setItems(updateTable);
    }

    /** Helper function for Search Functionality
     * Gets Customer List based on Search input
     * @param input String value of search text
     * @return ObservableList List of Customers
     */
    private static ObservableList<Model.Customers> lookupCustomer(String input) {
        ObservableList<Model.Customers> customerList = FXCollections.observableArrayList();

        for (Model.Customers customer: customers) {
            if (customer.getCustomerName().contains(input)) {
                customerList.add(customer);
            } else if (Integer.toString(customer.getCustomerId()).contains(input)) {
                customerList.add(customer);
            }
        }
        return customerList;
    }
    /** This method initializes the table in the Customers View.
     *  Catches Exception, throws alert, and prints stacktrace.
     *  @param location Location to resolve relative paths
     *  @param resources Resources to localize root object
     */

    public void initialize(URL location, ResourceBundle resources) {
        try {
            customers = CustomersQuery.getCustomers();
            customerTable.setItems(customers);
            idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            zipcodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            divisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));
            countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
