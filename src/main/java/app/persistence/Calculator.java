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

        // Så vil der være et minimum af 4 stolper
        return Math.max(numberOfPosts, 4);
    }


    public static int calculateRafter(int id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> orderList = OrdersMapper.getSize(id, connectionPool);
        int totalRafters = 0;

        for (Orders order : orderList) {
            int carportWidth = (int) order.getCarport_width();
            int carportLength = (int) order.getCarport_length();

            // Beregner carport spær basered på længde og bredde, og ganger dem sammen
            int carportRafters = calculateRafterCount(carportLength) * calculateRafterWidth(carportWidth);

            int shedWidth = (int) order.getShed_width();
            int shedRafters = calculateRafterCount(shedWidth);

            totalRafters += carportRafters + shedRafters;
        }

        return totalRafters;
    }


    private static int calculateRafterCount(int dimension) {
        // Assuming a rafter is needed for every 110 units of length, except at the beginning and end
        int rafterLength = 55;

        // Calculate the number of rafters, but ensure there is a strap at the beginning and end
        return Math.max(0, (int) Math.ceil((double) (dimension - rafterLength * 2) / rafterLength));
    }


    private static int calculateRafterWidth(int width) {
        // List of available rafter lengths
        int[] rafterLengths = {300, 360, 420, 480, 540, 600};

        // Find the combination of rafters that best fits the width
        int remainingWidth = width;
        int raftersCount = 0;

        // Loop through rafter lengths and calculate the count
        for (int length : rafterLengths) {
            while (remainingWidth >= length) {
                remainingWidth -= length;
                raftersCount++;
            }
        }

        return raftersCount;
    }


    public static double calculateStraps(int id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> orderList = OrdersMapper.getSize(id, connectionPool);
        int numberOfStraps = 0;

        for (Orders order : orderList) {
            // Calculate straps for carport length and width
            int carportLengthStraps = calculateStrapsLength(order.getCarport_length());
            int carportWidthStraps = calculateStrapsLength(order.getCarport_width());

            // Calculate straps for shed length and width
            int shedLengthStraps = calculateStrapsLength(order.getShed_length());
            int shedWidthStraps = calculateStrapsLength(order.getShed_width());

            // Sum up the straps
            numberOfStraps += carportLengthStraps + carportWidthStraps + shedLengthStraps + shedWidthStraps;
        }

        // Ensure a minimum of 4 straps
        return Math.max(numberOfStraps, 4);
    }

    private static int calculateStrapsLength(int side) {
        // List of available strap lengths
        int[] strapLengths = {300, 360, 420, 480, 540, 600};

        // Find the combination of straps that best fits the side length
        int remainingLength = side;
        int strapsCount = 0;

        // Loop through strap lengths and calculate the count
        for (int length : strapLengths) {
            while (remainingLength >= length) {
                remainingLength -= length;
                strapsCount++;
            }
        }

        return strapsCount;
    }


}
