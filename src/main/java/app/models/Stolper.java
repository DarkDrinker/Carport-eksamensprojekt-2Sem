package app.models;

public class Stolper {

    private int stolper_id;
    private String stolpe;
    private int size;
    private double price;

    public Stolper(int stolper_id, String stolpe, int size, double price) {
        this.stolper_id = stolper_id;
        this.stolpe = stolpe;
        this.size = size;
        this.price = price;
    }

    public int getStolper_id() {
        return stolper_id;
    }

    public String getStolpe() {
        return stolpe;
    }

    public int getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Stolper{" +
                "stolper_id=" + stolper_id +
                ", stolpe='" + stolpe + '\'' +
                ", size=" + size +
                ", price=" + price +
                '}';
    }
}
