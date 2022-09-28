package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;
import model.Appointments;
import model.Reports;
import model.Countries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class reportAccess extends Appointments {

    public reportAccess(int appointmentID, String appointmentTitle, String appointmentDescription, String appointmentLocation,
                        String appointmentType, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
        super(appointmentID, appointmentTitle, appointmentDescription, appointmentLocation, appointmentType, start, end, customerID, userID, contactID);
    }


    /**
     * SQL Query that gets the precise data needed for the custom report.
     * @throws SQLException
     * @return countriesObservableList
     */
    public static ObservableList<Reports> getCountries() throws SQLException {
        ObservableList<Reports> countriesObservableList = FXCollections.observableArrayList();

        String sql = "select countries.Country, count(*) as countryCount from customers inner join first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID inner join countries on countries.Country_ID = first_level_divisions.Country_ID where customers.Division_ID = first_level_divisions.Division_ID group by first_level_divisions.Country_ID order by count(*) desc";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String countryName = rs.getString("Country");
            int countryCount = rs.getInt("countryCount");

            Reports report = new Reports(countryName, countryCount);
            countriesObservableList.add(report);
        }
        return countriesObservableList;

    }
}










