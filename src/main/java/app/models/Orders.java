package app.models;

import java.sql.Date;

public class Orders {

        private int id;
        private Date date;
        private int user_id;
        private double carport_length;
        private double carport_width;
        private double shed_length;
        private double shed_width;
        private String status;

    public Orders(int id, Date date, int user_id, double carport_length, double carport_width, double shed_length, double shed_width, String status) {
        this.id = id;
        this.date = date;
        this.user_id = user_id;
        this.carport_length = carport_length;
        this.carport_width = carport_width;
        this.shed_length = shed_length;
        this.shed_width = shed_width;
        this.status = status;
    }

    public Orders(double carport_length, double carport_width, double shed_length, double shed_width) {
        this.carport_length = carport_length;
        this.carport_width = carport_width;
        this.shed_length = shed_length;
        this.shed_width = shed_width;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setCarport_length(double carport_length) {
        this.carport_length = carport_length;
    }

    public void setCarport_width(double carport_width) {
        this.carport_width = carport_width;
    }

    public void setShed_length(double shed_length) {
        this.shed_length = shed_length;
    }

    public void setShed_width(double shed_width) {
        this.shed_width = shed_width;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public java.sql.Date getDate() {
        return (java.sql.Date) date;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getCarport_length() {
        return (int) carport_length;
    }

    public int getCarport_width() {
        return (int) carport_width;
    }

    public int getShed_length() {
        return (int) shed_length;
    }

    public int getShed_width() {
        return (int) shed_width;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", date=" + date +
                ", user_id=" + user_id +
                ", carport_length=" + carport_length +
                ", carport_width=" + carport_width +
                ", shed_length=" + shed_length +
                ", shed_width=" + shed_width +
                ", status='" + status + '\'' +
                '}';
    }
}
