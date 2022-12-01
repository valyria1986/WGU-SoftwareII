package helper;

import Model.Countries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * helper class that queries the database for countries.
 * */
public class CountryQuery {

    /** This method gets a list of Country Objects
     * @return ObservableList List containing Country Objects
     * @throws SQLException Catches SQLException and prints stacktrace.
     */
    public static ObservableList<Countries> getCountries() throws SQLException {
        ObservableList<Countries> countries = FXCollections.observableArrayList();

        String sql = "SELECT * FROM countries;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            // Forward scroll resultSet
            while (resultSet.next()) {

                Model.Countries newCountry = new Model.Countries(
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country")
                );

                countries.add(newCountry);
            }
            return countries;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    /** This method gets a Country Object by Country Name
     * @param country String value of Country Name
     * @return Country Country Object
     * @throws SQLException Catches SQLException and prints stacktrace.
     */
    public static Countries getCountryId(String country) throws SQLException {

        String sql = "SELECT * FROM countries WHERE Country=?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, country);

        try {
            ps.execute();
            ResultSet resultSet = ps.getResultSet();

            while (resultSet.next()) {
                Countries newCountry = new Countries(
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country")
                );
                return newCountry;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
