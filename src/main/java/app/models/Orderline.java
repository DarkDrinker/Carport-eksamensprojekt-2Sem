package app.models;

public class Orderline {

    private int id;
    private int orders_id;
    private Material material;
    private int quantity;
    private double total_price;
    private int material_id;

    public Orderline(int id, int orders_id, Material material, int quantity) {
        this.id = id;
        this.orders_id = orders_id;
        this.material = material;
        this.quantity = quantity;
    }

    public Orderline(int id, int orders_id, Material material, int quantity, double total_price, int material_id) {
        this.id = id;
        this.orders_id = orders_id;
        this.material = material;
        this.quantity = quantity;
        this.total_price = total_price;
        this.material_id = material_id;
    }

    public Orderline(int id, int orders_id, Material material, int quantity, int material_id) {
        this.id = id;
        this.orders_id = orders_id;
        this.material = material;
        this.quantity = quantity;
        this.material_id = material_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getTotal_price() {
        return total_price;
    }

    public int getMaterial_id() {
        return material_id;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return "Orderline{" +
                "id=" + id +
                ", orders_id=" + orders_id +
                ", material=" + material +
                ", quantity=" + quantity +
                ", total_price=" + total_price +
                ", material_id=" + material_id +
                '}';
    }
}
