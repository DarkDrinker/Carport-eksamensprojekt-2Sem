package app.persistence;

import app.exceptions.DatabaseException;
import app.models.Orders;
import java.util.List;

public class Calculator {


    public static double calculatePost(int id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> squareList = OrdersMapper.getSize(id, connectionPool);
        int numberOfPosts = 0;

        for (Orders order : squareList) {
            // Beregn de antal stolper man skal bruge
            double carportPost = (order.getCarport_length() * order.getCarport_width()) / 5.5;
            double shedPost = ((order.getShed_length() * order.getShed_width()) / 5.5) - 2;

            // Hvor man runder dem op til hele tal
            carportPost = Math.ceil(carportPost);
            shedPost = Math.ceil(shedPost);

            // Og lægger dem sammen
            numberOfPosts += carportPost + shedPost;
        }

        return numberOfPosts;
    }

    public static double calculateRafter(int id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> squareList = OrdersMapper.getSize(id, connectionPool);
        int numberOfRafters = 0;

        for (Orders order : squareList) {
            // Beregn de antal spær man skal bruge
            double carportRafter = (order.getCarport_length() * order.getCarport_width()) / 3;
            double shedRafter = ((order.getShed_length() * order.getShed_width()) / 3) - 3;

            // Hvor man runder dem op til hele tal
            carportRafter = Math.ceil(carportRafter);
            shedRafter = Math.ceil(shedRafter);

            // Og lægger dem sammen
            numberOfRafters += carportRafter + shedRafter;
        }

        return numberOfRafters;
    }

   /* public static double calculateStraps(int id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> squareList = OrdersMapper.getSize(id, connectionPool);
        int numberOfStraps = 0;

        for (Orders order : squareList) {
            // Beregn de antal remme man skal bruge
            double carportStrap = (order.getCarport_length() * order.getCarport_width()) / ;
            double shedStrap = ((order.getShed_length() * order.getShed_width()) / ) - ;

            // Hvor man runder dem op til hele tal
            carportStrap = Math.ceil(carportStrap);
            shedStrap = Math.ceil(shedStrap);

            // Og lægger dem sammen
            numberOfStraps += carportStrap + shedStrap;
        }

        return numberOfStraps;
    }*/
}
