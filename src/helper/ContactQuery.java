package helper;

import Model.Contacts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * helper class that Queries the database of contacts
 * */
public class ContactQuery {

    /**
     * Queries the database for all contacts in the database.
     * @return ObservableList of Contacts
     * @throws SQLException Catches SQLException, prints stacktrace, and error message.
     * */
    public static ObservableList<Contacts> getContacts() throws SQLException {

        ObservableList<Contacts> contacts = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contacts AS c INNER JOIN appointments AS a ON c.Contact_ID = a.Contact_ID;";
        PreparedStatement ps= JDBC.connection.prepareStatement(sql);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();;

            // Forward scroll resultSet
            while (resultSet.next()) {
                Contacts newContact = new Contacts(
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name"),
                        resultSet.getString("Email")
                );

                contacts.add(newContact);
            }
            return contacts;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    /** This method gets a Contact Object by the Contact Name
     * @param contactName String value of Contact Name
     * @return Contact Returns Contact
     * @throws SQLException Catches SQLException, prints stacktrace, and error message.
     */
    public static Contacts getContactId(String contactName) throws SQLException {

        String sql= "SELECT * FROM contacts WHERE Contact_Name=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

                ps.setString(1, contactName);


        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();;

            // Forward scroll resultSet
            while (resultSet.next()) {
                Contacts newContact = new Contacts(
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name"),
                        resultSet.getString("Email")
                );

                return newContact;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
