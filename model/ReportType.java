package model;

public class ReportType {

    public String appointmentType;

    public int appointmentTotal;


    /**
     *
     * @param appointmentTotal
     * @param appointmentType
     */
    public ReportType(String appointmentType, int appointmentTotal) {
        this.appointmentType = appointmentType;
        this.appointmentTotal = appointmentTotal;
    }


    /**
     *
     * Getters for AppointmentType and Appointment Total
     */
    public String getAppointmentType() {

        return appointmentType;
    }

    public int getAppointmentTotal() {

        return appointmentTotal;
    }



}
