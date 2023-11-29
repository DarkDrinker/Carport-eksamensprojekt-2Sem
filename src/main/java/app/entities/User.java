package app.entities;

public class User {
    private int id;
    private String name;
    private String password;
    private String adresse;
    private String email;


    public User(int id, String password, String email) {
        this.id = id;
        this.password = password;
        this.email = email;
    }
    public User(int id, String name, String password, String adresse, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.adresse = adresse;
        this.email = email;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}