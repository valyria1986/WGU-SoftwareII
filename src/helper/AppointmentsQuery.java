package helper;

import Model.Appointments;
import Model.Contacts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * helper class to Query Appointments in the database.
 * */
public class AppointmentsQuery {

    /**
     * Queries an appointment from the database based on contact ID
     * @param contactId int contacts ID number
     * @return ObservableList of appointments
     * */
    public static ObservableList<Appointments> getContactAppoint(int contactId) throws SQLException {
        ObservableList<Appointments> appointments = FXCollections.observableArrayList();

        String sql = "SELECT * FROM appointments AS a INNER JOIN contacts AS c ON a.Contact_ID=c.Contact_ID WHERE a.Contact_ID=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, contactId);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {
                Appointments newAppointment = new Appointments(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getDate("Start").toLocalDate(),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getDate("End").toLocalDate(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")
                );

                appointments.add(newAppointment);
            }
            return appointments;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Queries database to find customers appointments
     * @param customerId int customers ID number
     * @return appointments ObservableList of appointments
     * */
    public static ObservableList<Appointments> getCustomerAppoint(int customerId) throws SQLException {
        ObservableList<Appointments> appointments = FXCollections.observableArrayList();

        String sql = "SELECT * FROM appointments AS a INNER JOIN contacts AS c ON a.Contact_ID=c.Contact_ID WHERE Customer_ID=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {
                Appointments newAppointment = new Appointments(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getDate("Start").toLocalDate(),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getDate("End").toLocalDate(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")
                );

                appointments.add(newAppointment);
            }
            return appointments;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Finds a specific appointment in database based on appointments ID number
     * @param AppointmentID int appointments ID number
     * @return Model.Appointments appointment if found/null if not found
     * */
    public static Appointments getAppointmentWithID(int AppointmentID) throws SQLException {

        String sql = "SELECT * FROM appointments AS a INNER JOIN contacts AS c ON a.Contact_ID=c.Contact_ID WHERE Appointment_ID=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, AppointmentID);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {
                Appointments newAppointment = new Appointments(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getDate("Start").toLocalDate(),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getDate("End").toLocalDate(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")
                );

                return newAppointment;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
    /**
     * Queries the database to create an ObservableList of all appointments.
     * @return ObservableList of appointments/null if no appointments found
     * */
    public static ObservableList<Appointments> getAppointments() throws SQLException {
        ObservableList<Appointments> appointments = FXCollections.observableArrayList();

        String sql = "SELECT * FROM appointments AS a INNER JOIN contacts AS c ON a.Contact_ID=c.Contact_ID;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            while (resultSet.next()) {
                Appointments newAppointment = new Appointments(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getDate("Start").toLocalDate(),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getDate("End").toLocalDate(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")
                );

                appointments.add(newAppointment);
            }
            return appointments;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Queries the database to create an ObservableList for monthly appointments
     * @return ObservableList of appointments/null if no appointments
     * */
    public static ObservableList<Appointments> monthlyAppointments() throws SQLException {

        ObservableList<Appointments> appointments = FXCollections.observableArrayList();
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;

        String sql = "SELECT * FROM appointments AS a INNER JOIN contacts AS c ON a.Contact_ID=c.Contact_ID WHERE MONTH(Start) = ?;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, currentMonth);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            while (resultSet.next()) {
                Appointments newAppointment = new Appointments(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getDate("Start").toLocalDate(),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getDate("End").toLocalDate(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")
                );

                appointments.add(newAppointment);
            }
            return appointments;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

    }
    /**
     * Queries the database to create an ObservableList for weekly appointments
     * @return ObservableList of appointments/null if no appointments
     * */
    public static ObservableList<Appointments> weeklyAppointments() throws SQLException {

        ObservableList<Appointments> appointments = FXCollections.observableArrayList();
        LocalDateTime currentDate = LocalDateTime.now();
        Calendar c = GregorianCalendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String startDate = df.format(c.getTime());
        c.add(Calendar.DATE, 6);
        String endDate = df.format(c.getTime());

        String sql = "SELECT * FROM appointments AS a INNER JOIN contacts AS c ON a.Contact_ID=c.Contact_ID WHERE Start <= ? AND Start >= ?;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, endDate);
        ps.setString(2, startDate);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            while (resultSet.next()) {
                Appointments newAppointment = new Appointments(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getDate("Start").toLocalDate(),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getDate("End").toLocalDate(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")
                );

                appointments.add(newAppointment);
            }
            return appointments;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    /**
     * Deletes the appointment from the database.
     * @param appID int appointment ID number to delete
     * */
    public static boolean deleteApp(int appID) throws SQLException {

        String sql = "DELETE from appointments WHERE Appointment_Id=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appID);
        ps.execute();
        return ps.getUpdateCount() > 0;
    }
    /**
     * Updates an existing appointment in the database.
     * @param customerId int
     * @param location String
     * @param description String
     * @param title String
     * @param appointmentID int
     * @param contactName String
     * @param end LocalDateTime
     * @param start LocalDateTime
     * @param type String
     * @param userID int
     * */
    public static boolean updateAppointment(String contactName, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, Integer customerId, Integer userID, Integer appointmentID) throws SQLException {

        Contacts contact = ContactQuery.getContactId(contactName);
        String sql = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Customer_ID=?, Contact_ID=?, User_ID=? WHERE Appointment_ID = ?;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, Timestamp.valueOf(start));
        ps.setTimestamp(6, Timestamp.valueOf(end));
        ps.setInt(7, customerId);
        ps.setInt(8, contact.getContactId());
        ps.setInt(9, userID);
        ps.setInt(10, appointmentID);

        try {
            ps.execute();
            if (ps.getUpdateCount() > 0) {
                System.out.println("Rows affected: " + ps.getUpdateCount());
            } else {
                System.out.println("No change");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    /**
     * Creates a new appointment.
     * @param customerId int
     * @param location String
     * @param description String
     * @param title String
     * @param contactName String
     * @param end LocalDateTime
     * @param start LocalDateTime
     * @param type String
     * @param userID int
     * */
    public static boolean createAppointment(String contactName, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, Integer customerId, Integer userID) throws SQLException {

        Contacts contact = ContactQuery.getContactId(contactName);

        String sql = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID, User_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, Timestamp.valueOf(start));
        ps.setTimestamp(6, Timestamp.valueOf(end));
        ps.setInt(7, customerId);
        ps.setInt(8, contact.getContactId());
        ps.setInt(9, userID);

        try {
            ps.execute();
            if (ps.getUpdateCount() > 0) {
                System.out.println("Rows affected: " + ps.getUpdateCount());
            } else {
                System.out.println("No change");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
