package app.models;

public class Material {

    private int material_id;
    private String material_description;
    private int size;
    private double price;

    public Material(int material_id, String material_description, int size, double price) {
        this.material_id = material_id;
        this.material_description = material_description;
        this.size = size;
        this.price = price;
    }

    public int getMaterial_id() {
        return material_id;
    }

    public String getMaterial_description() {
        return material_description;
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
                "material_id=" + material_id +
                ", material_description='" + material_description + '\'' +
                ", size=" + size +
                ", price=" + price +
                '}';
    }
}
