/* Author: Sinclert Perez (UC3M) */

import java.io.*;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

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

        Station s0 = new Station(2.70, 2.35);
        Station s1 = new Station(1.60, 0.12);
        Station s2 = new Station(0.34, 2.6);
        Station s3 = new Station(2.61, 3.43);
        Station s4 = new Station(0.09, 0.20);
        Station s5 = new Station(1.10, 3.78);
        Station s6 = new Station(0.87, 1.16);

        Station[] stations = new Station[7];
        stations[0] = s0;
        stations[1] = s1;
        stations[2] = s2;
        stations[3] = s3;
        stations[4] = s4;
        stations[5] = s5;
        stations[6] = s6;

        System.out.println("How many stations do you want to visit?");
        int numberOfStations = in.nextInt();

        System.out.println("Which stations do you want to visit?");
        ArrayList<Integer> stationsToVisit = new ArrayList<>();

        for (int i = 0; i < numberOfStations; i++) {
            stationsToVisit.add(in.nextInt());
        }

        //System.out.println(calculateDistance(s1.getX(), s1.getY(), s2.getX(), s2.getY()));
        double[][] matrix = new double[numberOfStations][numberOfStations];


        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);


        for (int i = 0; i < numberOfStations; i++) {
            for (int j = 0; j < numberOfStations; j++) {
                if (i == j) {
                    matrix[i][j] = 0.0;
                } else {
                    // matrix[i][j] = calculateDistance(stations[i].getX(), stations[i].getY(), stations[j].getX(), stations[j].getY());
                    // matrix[i][j] = Double.parseDouble(df.format(calculateDistance(stations[i].getX(), stations[i].getY(), stations[j].getX(), stations[j].getY())));
                    matrix[i][j] = Double.parseDouble(df.format(calculateDistance(stations[stationsToVisit.get(i)].getX(), stations[stationsToVisit.get(i)].getY(), stations[stationsToVisit.get(j)].getX(), stations[stationsToVisit.get(j)].getY())));
                    // matrix[stationsToVisit.get(i)][stationsToVisit.get(j)] = Double.parseDouble(df.format(calculateDistance(stations[stationsToVisit.get(i)].getX(), stations[stationsToVisit.get(i)].getY(), stations[stationsToVisit.get(j)].getX(), stations[stationsToVisit.get(j)].getY())));
                    // !!! Fix this: if we want to create a matrix of size 3, there is problem when we add station 4 because is out of the bound of the created matrix
                }
            }
        }

       /* Iterator i = stationsToVisit.iterator();
        while (stationsToVisit.hasNext())

        */

        PrintWriter writer = new PrintWriter("DistancesMatrix.txt", StandardCharsets.UTF_8);

        for (int i = 0; i < numberOfStations; i++) {
            for (int j = 0; j < numberOfStations; j++) {
                writer.print(matrix[i][j] + " ");
            }
            writer.println("  ");
        }
        writer.close();


       /*
        System.out.println("Give starting point: ");
        int from = in.nextInt();
        System.out.println("Give end point: ");
        int to = in.nextInt();

        takeAngle(stations, from, to);
        System.out.println();

        */


        // The path to the files with the distances is asked
        // Scanner input = new Scanner(System.in);
        // System.out.println("Please, introduce the path where the text file is stored");
        // String file = input.nextLine();

        // The size of the distance matrix is asked
        //System.out.println("Please, introduce the size of the matrix");
        int size = numberOfStations;

        // Distances array is initiated considering the size of the matrix
        distances = new double[size][size];

        // The file in that location is opened
        //FileReader f = new FileReader(file);
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

        /* ------------------------- ALGORITHM INITIALIZATION ----------------------- */

        // Initial variables to start the algorithm
        String path = "";
        int[] vertices = new int[size - 1];

        // Filling the initial vertices array with the proper values
        for (int i = 1; i < size; i++) {
            vertices[i - 1] = i;
        }

        // FIRST CALL TO THE RECURSIVE FUNCTION
        procedure(0, vertices, path, 0.0);

        System.out.println("Path referring to matrix: " + optimalPath + ". Distance = " + optimalDistance);

        // Creating array of string length
        int[] PathReferToMatrix = new int[optimalPath.length()];

        // Copy character by character into array
        for (int i = 0; i < optimalPath.length(); i++) {
            char d = optimalPath.charAt(i);
            PathReferToMatrix[i] = Integer.parseInt(String.valueOf(d));
        }

     /*   for (int c : PathReferToMatrix) {
            System.out.print(c);
        }

      */

        // System.out.println(stationsToVisit.get(PathReferToMatrix[1]));

        String FinalPAth = "";
        for (int i = 0; i < optimalPath.length(); i++) {
            FinalPAth = FinalPAth + stationsToVisit.get(PathReferToMatrix[i]);
        }

        System.out.println("Final path referring to actual stations: " + FinalPAth);

        int[] pathArr = new int[numberOfStations + 1];
        //System.out.println("Write acquired path to an array");
       /* for (int i = 0; i < pathArr.length; i++) {
            pathArr[i] = in.nextInt();
        }
        */

        for (int i = 0; i < pathArr.length; i++) {
            pathArr[i] = stationsToVisit.get(PathReferToMatrix[i]);
        }

       /* for (int i = 0; i < pathArr.length; i++) {
            System.out.print(pathArr[i]);
        }

        */

        System.out.println();

        PrintWriter commandsWriter = new PrintWriter("DroneCommands.txt", StandardCharsets.UTF_8);


        String[] anglesArray = new String[pathArr.length];

        for (int i = 0; i < pathArr.length; i++) {
            if (i == pathArr.length - 1) {
                //System.out.println("Check");
            } else if (i == pathArr.length - 2) {
                anglesArray[i] = takeAngle(stations, pathArr[i], pathArr[0]);
                //System.out.println(anglesArray[i]);
            } else {
                anglesArray[i] = takeAngle(stations, pathArr[i], pathArr[i + 1]);
                //System.out.println(anglesArray[i]);
            }
        }

        //  String j= getDifferenceInAngles(anglesArray);


        for (int i = 0; i < pathArr.length; i++) {
            if (i == pathArr.length - 1) {
                System.out.println("We did it!");
            } else if (i == pathArr.length - 2) {
                System.out.print("Travel from station " + pathArr[i] + " to " + "station 0 -> ");
                System.out.print("We are heading back home: ");
                // double di = Double.parseDouble(df.format(calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[0]].getX(), stations[pathArr[0]].getY())));
                // double dis = calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[0]].getX(), stations[pathArr[0]].getY())*100;
                // String di=df.format(dis);
                //int di = (int) calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[0]].getX(), stations[pathArr[0]].getY());
                double dis = calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[0]].getX(), stations[pathArr[0]].getY()) * 100;
                int di = (int) dis;

                //System.out.print("Turn " + takeAngle(stations, pathArr[i], pathArr[0]));
                // takeAngle(stations, pathArr[i], pathArr[0]);
                //commandsWriter.println(takeAngle(stations, pathArr[i], pathArr[0]));
                System.out.print("Turn " + getDifferenceInAngles(anglesArray, i));
                commandsWriter.println(getDifferenceInAngles(anglesArray, i));
                commandsWriter.println("forward " + di);
                System.out.println(" and fly " + di + " cm");
                //System.out.println(getDifferenceInAngles(anglesArray)[i]);
            } else {
                System.out.print("Travel from station " + pathArr[i] + " to " + "station " + pathArr[i + 1] + " -> ");
                //double di = Double.parseDouble(df.format(calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[i + 1]].getX(), stations[pathArr[i + 1]].getY())));
                //double dis = calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[0]].getX(), stations[pathArr[0]].getY())*100;
                //String di=df.format(dis);

                double dis = Double.parseDouble(df.format(calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[i + 1]].getX(), stations[pathArr[i + 1]].getY()))) * 100;
                int di = (int) dis;

                //double di = calculateDistance(stations[pathArr[i]].getX(), stations[pathArr[i]].getY(), stations[pathArr[i+1]].getX(), stations[pathArr[i+1]].getY())*100;
                //int dis=(int) di;

                if (i == 0) {
                    System.out.print("Turn " + takeAngle(stations, pathArr[i], pathArr[i + 1]));
                    //takeAngle(stations, pathArr[i], pathArr[i + 1]);
                    commandsWriter.println(takeAngle(stations, pathArr[i], pathArr[i + 1]));
                } else {
                    System.out.print("Turn " + getDifferenceInAngles(anglesArray, i));
                    commandsWriter.println(getDifferenceInAngles(anglesArray, i));
                }
                commandsWriter.println("forward " + di);
                System.out.print(" and fly " + di + " cm");
                // System.out.println(getDifferenceInAngles(anglesArray));
            }
            System.out.println();
        }
        commandsWriter.close();


        TelloDrone drone = new TelloDrone();
        drone.connect();

        //drone.setLogToConsole(true);
        // drone.connect();

        //FileReader readCommands = new FileReader("DroneCommands.txt");
        // BufferedReader bufCommands = new BufferedReader(readCommands);

        //int missionPad=1;
        //int CommandLength=(pathArr.length)*2 ;
        int counter=1;

        try (BufferedReader bufCommands = new BufferedReader(new FileReader("DroneCommands.txt"))) {
            String line;
            drone.sendCommand("takeoff");
            while ((line = bufCommands.readLine()) != null) {

                while(!drone.sendCommand(line)){
                    drone.sendCommand(line);
                }

                if (counter % 2 == 0){
                    drone.sendCommand("mon");
                    drone.sendCommand("go 0 0 100 20 m-2");
                    drone.sendCommand("moff");
                }
                counter++;

              /*  drone.sendCommand("mon");
                drone.sendCommand("go 0 0 200 m-2");
                drone.sendCommand("moff");

               */

                //while(!drone.sendCommand("cw 360")){
                   // drone.sendCommand("cw 360");
                //}

               // drone.sendCommand("cw 360");
                //System.out.println(line);


                //!!!! After every 'forward' command --> search for mission-pads and orientate accordingly !!!!
            }
            drone.sendCommand("land");
        }

        // drone.startCommandQueue();

    }

    public static double calculateDistance(double x1, double y1, double x2, double y2) { //Check the Earth's radius for outdoors (For the Discussion)
        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;
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

   /* public static void takeAngle(Station[] stations, int from, int to) {

        double angle = 0.0;

        double dXX = stations[to].getX() - stations[from].getX();
        double dYY = stations[to].getY() - stations[from].getY();

        DecimalFormat decimals = new DecimalFormat("#.##");
        decimals.setRoundingMode(RoundingMode.CEILING);

        if (dXX == 0) {
            if (stations[to].getY() < stations[from].getY()) {
                angle = 180.0;
            } else if (stations[to].getY() > stations[from].getY()) {
                angle = 0;
            }
            System.out.print(angle);
            return;
        }

        if (dYY == 0) {
            if (stations[to].getX() < stations[from].getX()) {
                angle = 90.0;
                System.out.print("ccw ");
            } else if (stations[to].getX() > stations[from].getX()) {
                angle = 90.0;
                System.out.print("cw ");

            }
            System.out.print(angle);
            return;
        }

        if (dXX > 0 && dYY > 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dXX, dYY));
            // angle = 90 - angle;
            System.out.print("cw ");
        }

        if (dXX > 0 && dYY < 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dYY, dXX));
            angle = 90 + angle;
            System.out.print("cw ");
        }

        if (dXX < 0 && dYY > 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dXX, dYY));
            System.out.print("ccw ");
        }

        if (dXX < 0 && dYY < 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dXX, dYY));
            angle = 180 - angle;
            System.out.print("ccw ");
        }
        angle = Double.parseDouble(decimals.format(angle));
        System.out.print(angle);
    }
    */

    public static String takeAngle(Station[] stations, int from, int to) {

        double angle = 0.0;
        //int angle=0;
        String dir = "";

        double dXX = stations[to].getX() - stations[from].getX();
        double dYY = stations[to].getY() - stations[from].getY();

        DecimalFormat decimals = new DecimalFormat("#.##");
        decimals.setRoundingMode(RoundingMode.CEILING);

        if (dXX == 0) {
            if (stations[to].getY() < stations[from].getY()) {
                angle = 180.0;
                // angle=180;
            } else if (stations[to].getY() > stations[from].getY()) {
                angle = 0.0;
                //angle = 0;
            }
            dir = "cw ";
        }

        if (dYY == 0) {
            if (stations[to].getX() < stations[from].getX()) {
                angle = 90.0;
                //angle = 90;
                dir = "ccw ";
                // System.out.print("ccw ");
            } else if (stations[to].getX() > stations[from].getX()) {
                angle = 90.0;
                // angle = 90;
                dir = "cw ";
            }
        }

        if (dXX > 0 && dYY > 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dXX, dYY));
            //angle = (int) Math.toDegrees(Math.atan2(dXX, dYY));
            dir = "cw ";
        }

        if (dXX > 0 && dYY < 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dYY, dXX));
            //angle = (int) Math.toDegrees(Math.atan2(dYY, dXX));
            angle = 90.0 + angle;
            dir = "cw ";
        }

        if (dXX < 0 && dYY > 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dXX, dYY));
            //angle = (int) Math.toDegrees(Math.atan2(dXX, dYY));
            dir = "ccw ";
        }

        if (dXX < 0 && dYY < 0) {
            dXX = abs(stations[to].getX() - stations[from].getX());
            dYY = abs(stations[to].getY() - stations[from].getY());
            angle = Math.toDegrees(Math.atan2(dXX, dYY));
            //angle = (int) Math.toDegrees(Math.atan2(dXX, dYY));
            angle = 180.0 - angle;
            dir = "ccw ";

        }
        angle = Double.parseDouble(decimals.format(angle));
        return dir + angle;
    }

    public static String getDifferenceInAngles(String[] anglesArray, int iter) {

        // String[] newAnglesArray=new String[anglesArray.length];
        String newAngle = "";

        String from = anglesArray[iter - 1];
        String[] fromSpli = from.split("\\s+");
        String to = anglesArray[iter];
        String[] toSpli = to.split("\\s+");

        double fromDouble = Double.parseDouble(fromSpli[1]);
        double toDouble = Double.parseDouble(toSpli[1]);

        /*int fromDouble = Integer.parseInt(fromSpli[1]);
        int toDouble = Integer.parseInt(toSpli[1]);
         */

        DecimalFormat decimalCut = new DecimalFormat("#.##");
        decimalCut.setRoundingMode(RoundingMode.CEILING);

        if (fromSpli[0].equals("cw") && toSpli[0].equals("ccw")) {
            newAngle = "ccw " + decimalCut.format((fromDouble + toDouble));
        } else if (fromSpli[0].equals("ccw") && toSpli[0].equals("cw")) {
            newAngle = "cw " + decimalCut.format((fromDouble + toDouble));
        } else if (fromSpli[0].equals("cw") && toSpli[0].equals("cw")) {
            if (fromDouble < toDouble) {
                newAngle = "cw " + decimalCut.format(abs(fromDouble - toDouble));
            } else if (fromDouble > toDouble) {
                newAngle = "ccw " + decimalCut.format(abs(fromDouble - toDouble));
            }
        } else if (fromSpli[0].equals("ccw") && toSpli[0].equals("ccw")) {
            if (fromDouble < toDouble) {
                newAngle = "ccw " + decimalCut.format(abs(fromDouble - toDouble));
            } else if (fromDouble > toDouble) {
                newAngle = "cw " + decimalCut.format(abs(fromDouble - toDouble));
            }
        }



       /* if (fromSpli[0].equals("cw") && toSpli[0].equals("ccw")) {
            newAngle = "ccw " + (fromDouble + toDouble);
        } else if (fromSpli[0].equals("ccw") && toSpli[0].equals("cw")) {
            newAngle = "cw " + (fromDouble + toDouble);
        } else if (fromSpli[0].equals("cw") && toSpli[0].equals("cw")) {
            if (fromDouble < toDouble) {
                newAngle = "cw " + abs(fromDouble - toDouble);
            } else if (fromDouble > toDouble) {
                newAngle = "ccw " + abs(fromDouble - toDouble);
            }
        } else if (fromSpli[0].equals("ccw") && toSpli[0].equals("ccw")) {
            if (fromDouble < toDouble) {
                newAngle = "ccw " + abs(fromDouble - toDouble);
            } else if (fromDouble > toDouble) {
                newAngle = "cw " + abs(fromDouble - toDouble);
            }
        }
        */
        return newAngle;
    }
}

