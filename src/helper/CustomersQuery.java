package helper;
import Model.Customers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * helper class that queries the database for customers.
 * */
public abstract class CustomersQuery {

    /**
     * Inserts a new customer into the database
     * @param address String
     * @param country String
     * @param customerName String
     * @param divisionID int
     * @param phone String
     * @param zipcode String
     * */
    public static int insert(String customerName, String address, String zipcode, String phone, String country, int divisionID) throws SQLException {
        String sql = "INSERT INTO CUSTOMERS (Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES(?,?,?,?,?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3,zipcode);
        ps.setString(4, phone);
        ps.setInt(5, divisionID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;

    }
    /**
     * Updates a customer in the database
     * @param address String
     * @param country String
     * @param customerName String
     * @param division String
     * @param phone String
     * @param zipcode String
     * @param customerID int
     * */
    public static int update(int customerID, String customerName, String address, String zipcode, String phone, String country, String division)throws SQLException{
        String sql = "UPDATE CUSTOMERS SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?,  Division_ID = ? WHERE Customer_ID =?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,customerName);
        ps.setString(2, address);
        ps.setString(3,zipcode);
        ps.setString(4, phone);
        ps.setInt(5, DivisionQuery.getDivisionID(division).getDivisionID());
        ps.setInt(6,customerID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }
    /**
     * deletes a customer from the database
     * @param customerID int
     * */
    public static int delete(int customerID)throws SQLException{
        String sql = "DELETE FROM CUSTOMERS WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }
    /**
     * Queries all customers in the database and prints their ID and Name to console
     * */
    public static void select() throws SQLException {
        String sql = "SELECT * FROM CUSTOMERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            System.out.print(customerID + " | " + customerName + "\n");
        }
    }
    /**
     * Queries a specific customer and prints all their information to the console.
     * @param customerID int
     * */
    public static void select(int customerID) throws SQLException {
        String sql = "SELECT * FROM CUSTOMERS WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int custID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String zipcode = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            String createDate = rs.getString("Create_Date");
            String lastUpdate = rs.getString("Last_Update");
            String divisionID = rs.getString("Division_ID");

            System.out.print(custID + " | ");
            System.out.print(customerName + " | ");
            System.out.print(address + " | ");
            System.out.print(zipcode + " | ");
            System.out.print(phone + " | ");
            System.out.print(createDate + " | ");
            System.out.print(lastUpdate + " | ");
            System.out.print(divisionID + "\n");
        }
    }

    /** This method gets all Customers and First-Level-Division Objects joined by the Division ID
     * @return ObservableList Returns list of Customers
     * @throws SQLException Catches SQLException, prints stacktrace, and error message.
     */
    public static ObservableList<Customers> getCustomers() throws SQLException {
        ObservableList<Model.Customers> customers = FXCollections.observableArrayList();

        String searchStatement = "SELECT * FROM customers AS c INNER JOIN first_level_divisions AS d ON c.Division_ID = d.Division_ID INNER JOIN countries AS co ON co.Country_ID=d.COUNTRY_ID;";

        PreparedStatement ps = JDBC.connection.prepareStatement(searchStatement);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {

                Model.Customers newCustomer = new Model.Customers(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Division"),
                        resultSet.getString("Country"),
                        resultSet.getInt("Division_ID")
                );

                customers.add(newCustomer);
            }
            return customers;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Creates an observable list of Model.Customers with a specific customer ID
     * @param contactId int customers ID
     * @return ObservableList of customers
     * @throws SQLException Catches SQLException, prints stacktrace, and error message.
     * */
    public static ObservableList<Customers> getCustomerNameByID(int contactId) throws SQLException {
        ObservableList<Model.Customers> customers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM CUSTOMERS WHERE Customer_Id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {

                Model.Customers newCustomer = new Model.Customers(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Division"),
                        resultSet.getString("Country"),
                        resultSet.getInt("Division_ID")
                );

                customers.add(newCustomer);
            }
            return customers;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Creates an ObservableList of Model.Customers with a specific appointment type.
     * @param selectedType String type of appointment
     * @throws SQLException Catches SQLException, prints stacktrace, and error message.
     * */
    public static ObservableList<Customers> getCustomersOfType(String selectedType) throws SQLException {
        ObservableList<Model.Customers> customers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customers AS c INNER JOIN first_level_divisions AS d ON c.Division_ID = d.Division_ID " +
                "INNER JOIN countries AS co ON co.Country_ID=d.COUNTRY_ID " + "INNER JOIN appointments as ap ON c.Customer_ID = ap.Customer_ID WHERE Type = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        try {
            ps.setString(1, selectedType);
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {

                Model.Customers newCustomer = new Model.Customers(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Create_Date"),
                        resultSet.getString("Created_By"),
                        resultSet.getString("Last_Update"),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getString("Country")
                );

                customers.add(newCustomer);
            }
            return customers;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Queries an ObservableList of customers in a specific month.
     * @param selectedMonth int number of the month.
     * @return ObservableList of Model.Customers
     * @throws SQLException Catches SQLException, prints stacktrace, and error message.
     * */
    public static ObservableList<Customers> getCustomersOfMonth(int selectedMonth) throws SQLException {
        ObservableList<Model.Customers> customers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customers AS c INNER JOIN first_level_divisions AS d ON c.Division_ID = d.Division_ID " +
                "INNER JOIN countries AS co ON co.Country_ID=d.COUNTRY_ID " + "INNER JOIN appointments as ap ON c.Customer_ID = ap.Customer_ID WHERE MONTH(Start) = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        try {
            ps.setInt(1, selectedMonth);
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {

                Model.Customers newCustomer = new Model.Customers(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Create_Date"),
                        resultSet.getString("Created_By"),
                        resultSet.getString("Last_Update"),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getString("Country")
                );

                customers.add(newCustomer);
            }
            return customers;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Queries an ObservableList of customers in a specific country.
     * @param selectedCountry String number of the month.
     * @return ObservableList of Model.Customers
     * @throws SQLException Catches SQLException, prints stacktrace, and error message.
     * */
    public static ObservableList<Customers> getCustomersOfCountry(String selectedCountry) throws SQLException {
        ObservableList<Model.Customers> customers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customers AS c INNER JOIN first_level_divisions AS d ON c.Division_ID = d.Division_ID " +
                "INNER JOIN countries AS co ON co.Country_ID=d.COUNTRY_ID " + "INNER JOIN appointments as ap ON c.Customer_ID = ap.Customer_ID WHERE co.Country = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        try {
            ps.setString(1, selectedCountry);
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {

                Model.Customers newCustomer = new Model.Customers(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Create_Date"),
                        resultSet.getString("Created_By"),
                        resultSet.getString("Last_Update"),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getString("Country")
                );

                customers.add(newCustomer);
            }
            return customers;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
