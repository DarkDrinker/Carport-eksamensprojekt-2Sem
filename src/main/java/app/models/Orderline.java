package app.models;

public class Orderline {


    private int id;
    private int orders_id;
    Material material;
    private int quantity;

    public Orderline(int id, int orders_id, Material material, int quantity) {
        this.id = id;
        this.orders_id = orders_id;
        this.material = material;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getOrders_id() {
        return orders_id;
    }

    public Material getMaterial() {
        return material;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Orderline{" +
                "id=" + id +
                ", orders_id=" + orders_id +
                ", material=" + material +
                ", quantity=" + quantity +
                '}';
    }
}
