package app.models;

public class Remme {

    private int remme_id;
    private String rem;
    private int size;
    private double price;

    public Remme(int remme_id, String rem, int size, double price) {
        this.remme_id = remme_id;
        this.rem = rem;
        this.size = size;
        this.price = price;
    }

    public int getRemme_id() {
        return remme_id;
    }

    public String getRem() {
        return rem;
    }

    public int getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Remme{" +
                "remme_id=" + remme_id +
                ", rem='" + rem + '\'' +
                ", size=" + size +
                ", price=" + price +
                '}';
    }
}
