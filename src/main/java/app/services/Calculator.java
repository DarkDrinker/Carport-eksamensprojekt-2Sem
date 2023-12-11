package app.services;

import app.exceptions.DatabaseException;
import app.models.Orders;
import app.persistence.ConnectionPool;
import app.persistence.OrdersMapper;

import java.util.Arrays;
import java.util.List;

public class Calculator {


    public static double calculatePost(int id, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> squareList = OrdersMapper.getSize(id, connectionPool);
        int numberOfPosts = 0;

        for (Orders order : squareList) {
            // Beregn de antal stolper man skal bruge ved hver 5.5m2 som er 55000cm2 og vi beregner i cm og cm2
            double carportPost = (order.getCarport_length() * order.getCarport_width()) / 55000;
            double shedPost = ((order.getShed_length() * order.getShed_width()) / 55000) - 2;

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


            totalRafters += carportRafters;
        }

        return totalRafters;
    }


    private static int calculateRafterCount(int dimension) {
        // Assuming a rafter is needed for every 55 units of length, except at the beginning and end
        int rafterLength = 55;

        // Calculate the number of rafters, considering there is a strap at the beginning and end
        return Math.max(0, (int) Math.ceil((double) dimension / rafterLength)-2);
    }


    private static int calculateRafterWidth(int width) {
        // List of available rafter lengths
        int[] rafterLengths = {300, 360, 420, 480, 540, 600};

        // Sort the array in descending order to start with the longest rafters
        Arrays.sort(rafterLengths);
        int[] descendingRafterLengths = new int[rafterLengths.length];
        for (int i = 0; i < rafterLengths.length; i++) {
            descendingRafterLengths[i] = rafterLengths[rafterLengths.length - 1 - i];
        }

        // Find the combination of rafters that best fits the width
        int remainingWidth = width;
        int raftersCount = 0;

        // Loop through rafter lengths and calculate the count
        for (int length : descendingRafterLengths) {
            if (remainingWidth >= length) {
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
            int carportWidthStraps = calculateStrapsWidth(order.getCarport_width());

            // Sum up the straps
            numberOfStraps += carportLengthStraps + carportWidthStraps;
        }

        // Ensure a minimum of 4 straps
        return Math.max(numberOfStraps, 4);
    }

    private static int calculateStrapsLength(int side) {
        // List of available strap lengths
        int[] strapLengths = {300, 360, 420, 480, 540, 600};

        // Sort the array in descending order to start with the longest straps
        Arrays.sort(strapLengths);
        int[] descendingStrapLengths = new int[strapLengths.length];
        for (int i = 0; i < strapLengths.length; i++) {
            descendingStrapLengths[i] = strapLengths[strapLengths.length - 1 - i];
        }

        // Find the combination of straps that best fits the side length
        int remainingLength = side;
        int strapsCount = 0;

        // Loop through strap lengths and calculate the count
        for (int length : descendingStrapLengths) {
            while (remainingLength >= length) {
                remainingLength -= length;
                strapsCount++;
            }
        }

        return (strapsCount*2);
    }
    public static int calculateStrapsWidth(int width) {
        // List of available strap lengths
        int[] strapLengths = {300, 360, 420, 480, 540, 600};

        // Sort the array in descending order to start with the longest straps
        Arrays.sort(strapLengths);
        int[] descendingStrapLengths = new int[strapLengths.length];
        for (int i = 0; i < strapLengths.length; i++) {
            descendingStrapLengths[i] = strapLengths[strapLengths.length - 1 - i];
        }

        // Find the combination of straps that best fits the width
        int remainingWidth = width;
        int strapsCount = 0;

        // Loop through strap lengths and calculate the count
        for (int length : descendingStrapLengths) {
            while (remainingWidth >= length) {
                remainingWidth -= length;
                strapsCount++;
            }
        }

        // Multiply the count by 2 to account for both sides of the width
        return (strapsCount * 2);
    }

}
