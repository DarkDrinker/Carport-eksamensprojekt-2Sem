package app.models;

import java.sql.Date;

public class Stykliste {

    private int id;
    private int orderline_id;
    Stolper stolper;
    private int stolper_quantity;
    Remme remme;
    private int remme_quantity;
    Spaer spaer;
    private int spaer_quantity;
    private double width;
    private double length;
    private double height;
    private Date date;


    public Stykliste(int id, int orderline_id, Stolper stolper, int stolper_quantity, Remme remme, int remme_quantity, Spaer spaer, int spaer_quantity, double width, double length, double height, Date date) {
        this.id = id;
        this.orderline_id = orderline_id;
        this.stolper = stolper;
        this.stolper_quantity = stolper_quantity;
        this.remme = remme;
        this.remme_quantity = remme_quantity;
        this.spaer = spaer;
        this.spaer_quantity = spaer_quantity;
        this.width = width;
        this.length = length;
        this.height = height;
        this.date = date;
    }


    public int getId() {
        return id;
    }

    public int getOrderline_id() {
        return orderline_id;
    }

    public Stolper getStolper() {
        return stolper;
    }

    public int getStolper_quantity() {
        return stolper_quantity;
    }

    public Remme getRemme() {
        return remme;
    }

    public int getRemme_quantity() {
        return remme_quantity;
    }

    public Spaer getSpaer() {
        return spaer;
    }

    public int getSpaer_quantity() {
        return spaer_quantity;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Stykliste{" +
                "id=" + id +
                ", orderline_id=" + orderline_id +
                ", stolper=" + stolper +
                ", stolper_quantity=" + stolper_quantity +
                ", remme=" + remme +
                ", remme_quantity=" + remme_quantity +
                ", spaer=" + spaer +
                ", spaer_quantity=" + spaer_quantity +
                ", width=" + width +
                ", length=" + length +
                ", height=" + height +
                ", date=" + date +
                '}';
    }
}
