/* Author: Sinclert Perez (UC3M) */

import java.io.*;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.abs;

/**
 * The Held Karp algorithm:
 * <p>
 * There are 2 possible cases in each iteration:
 * <p>
 * A) A base case where we already know the answer. (Stopping condition)
 * B) Decreasing the number of considered vertices and calling our algorithm again. (Recursion)
 * <p>
 * Explanation of every case:
 * <p>
 * A) If the list of vertices is empty, return the distance between starting point and vertex.
 * B) If the list of vertices is not empty, lets decrease our problem space:
 * <p>
 * 1) Consider each vertex in vertices as a starting point ("initial")
 * 2) As "initial" is the starting point, we have to remove it from the list of vertices
 * 3) Calculate the cost of visiting "initial" (costCurrentNode) + cost of visiting the rest from it ("costChildren")
 * 4) Return the minimum result from step 3
 * <p>
 * https://github.com/Sinclert/Heuristics-TSP/blob/master/HK_Optimal.java
 */
public class Main {

    /* ----------------------------- GLOBAL VARIABLES ------------------------------ */
    private static double[][] distances;
    private static double optimalDistance = Integer.MAX_VALUE;
    private static String optimalPath = "";


    /* ------------------------------ MAIN FUNCTION -------------------------------- */

    public static void main(String args[]) throws IOException {

        /* ----------------------------- IO MANAGEMENT ----------------------------- */

        Scanner in = new Scanner(System.in);

        Station[] stations = createStations();

        System.out.println("How many stations do you want to visit?");
        int numberOfStations = in.nextInt();


        ArrayList<Integer> stationsToVisit = takeStationsToVisit(numberOfStations);

        //System.out.println(stationsToVisit.size());

        numberOfStations += 1;

        double[][] matrix = createMatrixWithDistances(stationsToVisit, stations, numberOfStations);
        createFileWithDistances(matrix, numberOfStations);

        fillMatrixDistances(numberOfStations);

        /* ------------------------- ALGORITHM INITIALIZATION ----------------------- */
        int size = numberOfStations;

        // Initial variables to start the algorithm
        String path = "";
        int[] vertices = new int[size - 1];

        // Filling the initial vertices array with the proper values
        for (int i = 1; i < size; i++) {
            vertices[i - 1] = i;
        }

        // FIRST CALL TO THE RECURSIVE FUNCTION
        procedure(0, vertices, path, 0.0);

        System.out.println("Path referring to the matrix: " + optimalPath);

        int[] pathReferToStations = takePathReferToStations(optimalPath, stationsToVisit, numberOfStations);

        System.out.println();

        String[] anglesArray = createArrayOfAngles(pathReferToStations, stations);

        printWriteTrip(pathReferToStations, stations, anglesArray);

        controlDrone(numberOfStations);

    }

    private static Station[] createStations() {


        Station s0 = new Station(2.70, 2.35);
        Station s1 = new Station(1.60, 0.12);
        Station s2 = new Station(0.34, 2.60);
        Station s3 = new Station(2.61, 3.43);
        Station s4 = new Station(0.09, 0.20);
        Station s5 = new Station(1.10, 3.78);
        Station s6 = new Station(0.87, 1.16);



/*
        Station s0 = new Station(0, 0);
        Station s1 = new Station(4, 3);
        Station s2 = new Station(4, 2);
        Station s3 = new Station(1, 3);
        Station s4 = new Station(1, 1);
        Station s5 = new Station(5, 4);
        Station s6 = new Station(5, 0);

 */



        Station[] stations = new Station[7];
        stations[0] = s0;
        stations[1] = s1;
        stations[2] = s2;
        stations[3] = s3;
        stations[4] = s4;
        stations[5] = s5;
        stations[6] = s6;

        return stations;
    }

    private static ArrayList<Integer> takeStationsToVisit(int numberOfStations){
        Scanner in= new Scanner(System.in);

        System.out.println("Which stations do you want to visit?");
        ArrayList<Integer> stationsToVisit = new ArrayList<>();

       stationsToVisit.add(0);

        for (int i = 0; i < numberOfStations; i++) {

            stationsToVisit.add(in.nextInt());

        }

        return stationsToVisit;
    }

    private static double calculateDistance(double x1, double y1, double x2, double y2) { //Check the Earth's radius for outdoors (For the Discussion)
        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;
    }

    private static double[][] createMatrixWithDistances(ArrayList<Integer> stationsToVisit, Station[] stations, int numberOfStations) {

        double[][] matrix = new double[numberOfStations][numberOfStations];


        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);


        for (int i = 0; i < numberOfStations; i++) {
            for (int j = 0; j < numberOfStations; j++) {
                if (i == j) {
                    matrix[i][j] = 0.0;
                } else {
                    matrix[i][j] = Double.parseDouble(df.format(calculateDistance(stations[stationsToVisit.get(i)].getX(), stations[stationsToVisit.get(i)].getY(), stations[stationsToVisit.get(j)].getX(), stations[stationsToVisit.get(j)].getY())));
                }
            }
        }

        return matrix;
    }


    private static void createFileWithDistances(double[][] matrix, int numberOfStations) throws IOException {

        PrintWriter writer = new PrintWriter("DistancesMatrix.txt", StandardCharsets.UTF_8);

        for (int i = 0; i < numberOfStations; i++) {
            for (int j = 0; j < numberOfStations; j++) {
                writer.print(matrix[i][j] + " ");
            }
            writer.println("  ");
        }
        writer.close();
    }

    private static void fillMatrixDistances(int numberOfStations) throws IOException {

        int size = numberOfStations;

        // Distances array is initiated considering the size of the matrix
        distances = new double[size][size];

        // The file in that location is opened
        FileReader f = new FileReader("DistancesMatrix.txt");
        BufferedReader b = new BufferedReader(f);


        // Our matrix is filled with the values of the file matrix
        for (int row = 0; row < size; row++) {

            // Every value of each row is read and stored
            String line = b.readLine();
            String[] values = line.trim().split("\\s+");

            for (int col = 0; col < size; col++) {
                distances[row][col] = Double.parseDouble(values[col]);
            }
        }
        // Closing file
        b.close();

    }

    private static int[] takePathReferToStations(String optimalPath, ArrayList<Integer> stationsToVisit, int numberOfStations) {
        // Creating array of string length
        int[] PathReferToMatrix = new int[optimalPath.length()];

        // Copy character by character into array
        for (int i = 0; i < optimalPath.length(); i++) {
            char d = optimalPath.charAt(i);
            PathReferToMatrix[i] = Integer.parseInt(String.valueOf(d));
        }

        String FinalPAth = "";
        for (int i = 0; i < optimalPath.length(); i++) {
            FinalPAth = FinalPAth + stationsToVisit.get(PathReferToMatrix[i]);
        }

        System.out.println("Final path referring to actual stations: " + FinalPAth);

        int[] pathArr = new int[numberOfStations + 1];

        for (int i = 0; i < pathArr.length; i++) {
            pathArr[i] = stationsToVisit.get(PathReferToMatrix[i]);
        }

        return pathArr;

    }

    private static String[] createArrayOfAngles(int[] pathArr, Station[] stations) {

        String[] anglesArray = new String[pathArr.length];

        for (int i = 0; i < pathArr.length; i++) {
            if (i == pathArr.length - 1) {
            } else if (i == pathArr.length - 2) {
                anglesArray[i] = takeAngle(stations, pathArr[i], pathArr[0]);
            } else {
                anglesArray[i] = takeAngle(stations, pathArr[i], pathArr[i + 1]);

            }
        }
        return anglesArray;
    }

    private static void printWriteTrip(int[] pathArr, Station[] stations, String[] anglesArray) throws IOException {

        PrintWriter commandsWriter = new PrintWriter("DroneCommands.txt", StandardCharsets.UTF_8);
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        for (int i = 0; i < pathArr.length; i++) {
            if (i == pathArr.length - 1) {
                System.out.println("We did it!");
            } else if (i == pathArr.length - 2) {
                System.out.print("Travel from station " + pathArr[i] + " to " + "station 0 -> ");
                System.out.print("We are heading back to the Control Station: ");
                double dis = calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[0]].getX(), stations[pathArr[0]].getY()) * 100;
                int di = (int) dis;
                System.out.print("Turn " + getDifferenceInAngles(anglesArray, i));
                commandsWriter.println(getDifferenceInAngles(anglesArray, i));
                commandsWriter.println("forward " + di);
                System.out.println(" and fly " + di + " cm");

            } else {
                System.out.print("Travel from station " + pathArr[i] + " to " + "station " + pathArr[i + 1] + " -> ");

                double dis = Double.parseDouble(df.format(calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[i + 1]].getX(), stations[pathArr[i + 1]].getY()))) * 100;
                int di = (int) dis;

                if (i == 0) {
                    System.out.print("Turn " + takeAngle(stations, pathArr[i], pathArr[i + 1]));
                    commandsWriter.println(takeAngle(stations, pathArr[i], pathArr[i + 1]));
                } else {
                    System.out.print("Turn " + getDifferenceInAngles(anglesArray, i));
                    commandsWriter.println(getDifferenceInAngles(anglesArray, i));
                }
                commandsWriter.println("forward " + di);
                System.out.print(" and fly " + di + " cm");
            }
            System.out.println();
        }
        commandsWriter.close();
    }

    private static void controlDrone(int numberOfStations) throws IOException {

        TelloDrone drone = new TelloDrone();
        drone.connect();

        int CommandLength = (numberOfStations) * 2;
        int counter = 1;

        try (BufferedReader bufCommands = new BufferedReader(new FileReader("DroneCommands.txt"))) {
            String line;
            drone.sendCommand("takeoff");

            while (!drone.sendCommand("up 110")) {
                drone.sendCommand("up 110");
            }

            while ((line = bufCommands.readLine()) != null) {

                while (!drone.sendCommand(line)) {
                    drone.sendCommand(line);
                }

                if (counter % 2 == 0) { // check if the drone rotates!!!
                    drone.sendCommand("mon");
                    while (!drone.sendCommand("go 0 0 80 20 m-2")) {
                        drone.sendCommand("go 0 0 80 20 m-2");
                    }
                    drone.sendCommand("moff");

                    if (counter != CommandLength) {
                        while (!drone.sendCommand("up 110")) {
                            drone.sendCommand("up 110");
                        }
                    }

                }
                counter++;
            }
            drone.sendCommand("land");
        }
    }

    private static String takeAngle(Station[] stations, int from, int to) {

        double angle = 0.0;
        String dir = "";

        double dX = stations[to].getX() - stations[from].getX();
        double dY = stations[to].getY() - stations[from].getY();

        DecimalFormat decimals = new DecimalFormat("#.##");
        decimals.setRoundingMode(RoundingMode.CEILING);

        if (dX == 0) {
            if (stations[to].getY() < stations[from].getY()) {
                angle = 180.0;
            } else if (stations[to].getY() > stations[from].getY()) {
                angle = 0.0;
            }
            dir = "cw ";
        }

        if (dY == 0) {
            if (stations[to].getX() < stations[from].getX()) {
                angle = 90.0;
                dir = "ccw ";
            } else if (stations[to].getX() > stations[from].getX()) {
                angle = 90.0;
                dir = "cw ";
            }
        }

        if (dX > 0 && dY > 0) {
            dX = abs(stations[to].getX() - stations[from].getX());
            dY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dX, dY));
            dir = "cw ";
        }

        if (dX > 0 && dY < 0) {
            dX = abs(stations[to].getX() - stations[from].getX());
            dY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dY, dX));
            angle = 90.0 + angle;
            dir = "cw ";
        }

        if (dX < 0 && dY > 0) {
            dX = abs(stations[to].getX() - stations[from].getX());
            dY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dX, dY));
            dir = "ccw ";
        }

        if (dX < 0 && dY < 0) {
            dX = abs(stations[to].getX() - stations[from].getX());
            dY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dX, dY));
            angle = 180.0 - angle;
            dir = "ccw ";

        }
        angle = Double.parseDouble(decimals.format(angle));
        return dir + angle;
    }

    private static String getDifferenceInAngles(String[] anglesArray, int iter) {

        String newAngle = "";

        String from = anglesArray[iter - 1];
        String[] fromSpli = from.split("\\s+");
        String to = anglesArray[iter];
        String[] toSpli = to.split("\\s+");

        double fromDouble = Double.parseDouble(fromSpli[1]);
        double toDouble = Double.parseDouble(toSpli[1]);

        DecimalFormat decimalCut = new DecimalFormat("#.##");
        decimalCut.setRoundingMode(RoundingMode.CEILING);

        if (fromSpli[0].equals("cw") && toSpli[0].equals("ccw")) {
            newAngle = "ccw " + decimalCut.format((fromDouble + toDouble));
        }

        else if (fromSpli[0].equals("ccw") && toSpli[0].equals("cw")) {
            newAngle = "cw " + decimalCut.format((fromDouble + toDouble));
        }

        else if (fromSpli[0].equals("cw") && toSpli[0].equals("cw")) {
            if (fromDouble < toDouble) {
                newAngle = "cw " + decimalCut.format(abs(fromDouble - toDouble));
            } else if (fromDouble > toDouble) {
                newAngle = "ccw " + decimalCut.format(abs(fromDouble - toDouble));
            } else if (fromDouble == toDouble) {
                newAngle = "cw 0.0";
            }
        }

        else if (fromSpli[0].equals("ccw") && toSpli[0].equals("ccw")) {
            if (fromDouble < toDouble) {
                newAngle = "ccw " + decimalCut.format(abs(fromDouble - toDouble));
            } else if (fromDouble > toDouble) {
                newAngle = "cw " + decimalCut.format(abs(fromDouble - toDouble));
            } else if (fromDouble == toDouble) {
                newAngle = "cw 0.0";
            }
        }
        return newAngle;
    }

    /* ------------------------------- RECURSIVE FUNCTION ---------------------------- */

    private static double procedure(int initial, int vertices[], String path, double costUntilHere) {

        // We concatenate the current path and the vertex taken as initial
        // path = path + Integer.toString(initial) + " - ";
        path += Integer.toString(initial);
        int length = vertices.length;
        double newCostUntilHere;

        // Exit case, if there are no more options to evaluate (last node)
        if (length == 0) {
            newCostUntilHere = costUntilHere + distances[initial][0];

            // If its cost is lower than the stored one
            if (newCostUntilHere < optimalDistance) {
                optimalDistance = newCostUntilHere;
                optimalPath = path + "0";
            }

            return (distances[initial][0]);
        }

        // If the current branch has higher cost than the stored one: stop traversing
        else if (costUntilHere > optimalDistance) {
            return 0;
        }
        // Common case, when there are several nodes in the list
        else {

            int[][] newVertices = new int[length][(length - 1)];
            double costCurrentNode, costChild;
            double bestCost = Double.MAX_VALUE;

            // For each of the nodes of the list
            for (int i = 0; i < length; i++) {
                // Each recursion new vertices list is constructed
                for (int j = 0, k = 0; j < length; j++, k++) {

                    // The current child is not stored in the new vertices array
                    if (j == i) {
                        k--;
                        continue;
                    }
                    newVertices[i][k] = vertices[j];
                }

                // Cost of arriving the current node from its parent
                costCurrentNode = distances[initial][vertices[i]];

                // Here the cost to be passed to the recursive function is computed
                newCostUntilHere = costCurrentNode + costUntilHere;

                // RECURSIVE CALLS TO THE FUNCTION IN ORDER TO COMPUTE THE COSTS
                costChild = procedure(vertices[i], newVertices[i], path, newCostUntilHere);

                // The cost of every child + the current node cost is computed
                double totalCost = costChild + costCurrentNode;

                // Finally we select from the minimum from all possible children costs
                if (totalCost < bestCost) {
                    bestCost = totalCost;
                }
            }
            return (bestCost);
        }
    }
}

