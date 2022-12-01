package helper;

import Model.Countries;
import Model.Divisions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This class contains SQL operations made on the Divisions Collection.*/
public class DivisionQuery {

    /** This method gets a list of Divisions
     * @return ObservableList List containing Division Objects
     * @throws SQLException Catches SQLException and prints stacktrace.
     */
    public static ObservableList<Divisions> getDivisions() throws SQLException {
        ObservableList<Divisions> divisions = FXCollections.observableArrayList();

        String sql = "SELECT * FROM first_level_divisions;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            while (resultSet.next()) {

                Divisions newDivision = new Divisions(
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Division"),
                        resultSet.getInt("COUNTRY_ID")
                );

                divisions.add(newDivision);
            }
            return divisions;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    /** This method gets a Division by the Division Name
     * @param division String value of Division Name
     * @return Division Division Object
     * @throws SQLException Catches SQLException and prints stacktrace.
     */
    public static Divisions getDivisionID(String division) throws SQLException {
        String sql = "SELECT * FROM first_level_divisions WHERE Division=?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, division);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            while (resultSet.next()) {
                Divisions newDivision = new Divisions(
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Division"),
                        resultSet.getInt("COUNTRY_ID")
                );
                return newDivision;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /** This method gets a List of Divisions by Country
     * @param country String value of Country Name
     * @return ObservableList List containing Division Objects
     * @throws SQLException Catches SQLException and prints stacktrace.
     */
    public static ObservableList<Divisions> getDivisionsByCountry(String country) throws SQLException {
        Countries newCountry = CountryQuery.getCountryId(country);

        ObservableList<Divisions> divisions = FXCollections.observableArrayList();

        String sql = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID=?;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, newCountry.getCountryId());

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {

                Divisions newDivision = new Divisions(
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Division"),
                        resultSet.getInt("COUNTRY_ID")
                );

                divisions.add(newDivision);
            }
            return divisions;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}