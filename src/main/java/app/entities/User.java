package app.entities;

public class User {
    private int id;
    private String name;
    private String password;
    private String adresse;
    private String email;
    private String role;
    private String city;
    private int zip;


    public User(int id, String name, String adresse, String email, String role, String city, int zip) {
        this.id = id;
        this.adresse = adresse;
        this.email = email;
        this.name = name;
        this.role = role;
        this.city = city;
        this.zip = zip;
    }

    public User(int id, String name, String password, String adresse, String email, String role, String city, int zip) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.adresse = adresse;
        this.email = email;
        this.role = role;
        this.city = city;
        this.zip = zip;
    }

    public User(int id, String name, String password, String email, String role, String city) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getCity() {
        return city;
    }

    public int getZip() {
        return zip;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", city='" + city + '\'' +
                ", zip=" + zip +
                '}';
    }
}