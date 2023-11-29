package app.models;

public class Orderline {


    private int id;
    private int orders_id;
    Stolper stolper;
    Remme remme;
    Spaer spaer;
    private double width;
    private double length;
    private double total_price;
    private boolean paid;

    public Orderline(int id, int orders_id, Stolper stolper, Remme remme, Spaer spaer, double width, double length, double total_price, boolean paid) {
        this.id = id;
        this.orders_id = orders_id;
        this.stolper = stolper;
        this.remme = remme;
        this.spaer = spaer;
        this.width = width;
        this.length = length;
        this.total_price = total_price;
        this.paid = paid;
    }

    public int getId() {
        return id;
    }

    public int getOrders_id() {
        return orders_id;
    }

    public Stolper getStolper() {
        return stolper;
    }

    public Remme getRemme() {
        return remme;
    }

    public Spaer getSpaer() {
        return spaer;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public double getTotal_price() {
        return total_price;
    }

    public boolean isPaid() {
        return paid;
    }

    @Override
    public String toString() {
        return "Orderline{" +
                "id=" + id +
                ", orders_id=" + orders_id +
                ", stolper=" + stolper +
                ", remme=" + remme +
                ", spaer=" + spaer +
                ", width=" + width +
                ", length=" + length +
                ", total_price=" + total_price +
                ", paid=" + paid +
                '}';
    }
}
