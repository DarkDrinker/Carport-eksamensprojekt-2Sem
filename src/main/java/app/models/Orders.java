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

    public double getCarport_length() {
        return carport_length;
    }

    public double getCarport_width() {
        return carport_width;
    }

    public double getShed_length() {
        return shed_length;
    }

    public double getShed_width() {
        return shed_width;
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
