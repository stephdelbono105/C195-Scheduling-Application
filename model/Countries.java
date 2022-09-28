package model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;

public class Countries extends DBConnection {

    private int countryID;
    private String countryName;

    /**
     *
     * @param countryName
     * @param countryID
     */
    public Countries(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     *
     * @return countryID
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     *
     * @return countryName
     */
    public String getCountryName() {
        return countryName;
    }



}
