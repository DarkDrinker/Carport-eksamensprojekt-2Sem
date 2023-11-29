package app.models;

public class Spaer {

    private int spaer_id;
    private String spaertrae;
    private int size;
    private double price;


    public Spaer(int spaer_id, String spaertrae, int size, double price) {
        this.spaer_id = spaer_id;
        this.spaertrae = spaertrae;
        this.size = size;
        this.price = price;
    }

    public int getSpaer_id() {
        return spaer_id;
    }

    public String getSpaertrae() {
        return spaertrae;
    }

    public int getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Spaer{" +
                "spaer_id=" + spaer_id +
                ", spaer='" + spaertrae + '\'' +
                ", size=" + size +
                ", price=" + price +
                '}';
    }
}
