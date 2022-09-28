package model;

public class Customers {

    private int customerID;
    private String customerName;
    private String customerAddress;
    private String customerPostalCode;
    private String customerPhone;
    private String divisionName;
    private int divisionID;



    public  Customers(int customerID, String customerName, String customerAddress, String customerPostalCode,
                     String customerPhone, String divisionName, int divisionID) {

        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostalCode = customerPostalCode;
        this.customerPhone = customerPhone;
        this.divisionName = divisionName;
        this.divisionID = divisionID;

    }

    /**
     *
     * @return customerID
     */
    public int getCustomerID() {
        return customerID;
    }


    public void setCustomerID() {
        this.customerID = customerID;
    }

    /**
     *
     * @return customerName
     */
    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName() {
        this.customerName = customerName;
    }


    /**
     *
     * @return customerAddress
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress() {
        this.customerAddress = customerAddress;
    }


    /**
     *
     * @return customerPostalCode
     */
    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    public void setCustomerPostalCode() {
        this.customerPostalCode = customerPostalCode;
    }


    /**
     *
     * @return customerPhone
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone() {
        this.customerPhone = customerPhone;
    }


    /**
     *
     * @return divisionName
     */
    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName() {
        this.divisionName = divisionName;
    }


    /**
     *
     * @return divisionID
     */
    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID() {
        this.divisionID = divisionID;
    }



}