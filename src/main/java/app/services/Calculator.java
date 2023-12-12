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
        // Assuming a rafter is needed for every 55 units of length
        int rafterLength = 55;

        // Calculate the number of rafters, considering there is a strap at the beginning and end
        return Math.max(0, (int) Math.ceil((double) dimension / rafterLength));
    }


    private static int calculateRafterWidth(int width) {
        // List of available rafter lengths
        int[] rafterLengths = {600, 540, 480, 420, 360, 300};

        // Sort the array in descending order to start with the longest rafters
        Arrays.sort(rafterLengths);

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

            // Add the current count to the total count
            numberOfStraps += carportLengthStraps;
        }

        // Ensure a minimum of 4 straps
        numberOfStraps = Math.max(numberOfStraps, 4);

        return numberOfStraps;
    }

    public static int calculateStrapsLength(int side) {
        // List of available strap lengths
        int[] strapLengths = {600, 540, 480, 420, 360, 300};

        int remainingLength = side;

        // Initialize with a large value to ensure it gets updated
        int minStrapsCount = Integer.MAX_VALUE;

        // Iterate through all possible combinations of straps, this here goes through each
        // straplength and sees what can be used where there are least amount of strap surplus
        for (int i = 0; i < (1 << strapLengths.length); i++) {
            int currentLength = 0;
            int currentStrapsCount = 0;

            // Check which straps to include in the current combination, goes through and sees if
            // there is that you can straps use if there is length surplus and adds one more strap to the total
            for (int j = 0; j < strapLengths.length; j++) {
                if ((i & (1 << j)) != 0) {
                    currentLength += strapLengths[j];
                    currentStrapsCount++;
                }
            }

            // Check if the current combination covers the side length without surplus, it is to see
            // if there is length left and if there are then there are room for another strap
            if (currentLength >= side && currentLength - side < remainingLength) {
                remainingLength = currentLength - side;
                minStrapsCount = currentStrapsCount;
            }
        }

        // Multiply the count by 4 to account for both sides of the length
        return minStrapsCount * 4;
    }

}
