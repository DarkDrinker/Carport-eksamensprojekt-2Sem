package app.models;

import java.sql.Date;

public class Orders {

        private int id;
        private Date date;
        private int user_id;
        private boolean paid;

    public Orders(int id, Date date, int user_id, boolean paid) {
        this.id = id;
        this.date = date;
        this.user_id = user_id;
        this.paid = paid;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getUser_id() {
        return user_id;
    }

    public boolean isPaid() {
        return paid;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", date=" + date +
                ", user_id=" + user_id +
                ", paid=" + paid +
                '}';
    }
}
