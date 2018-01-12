package Model;

import Shapes.*;

import java.lang.reflect.Array;
import java.util.*;
import Util.Coordinates;

public class Grasp {

    private int[][][] containerMatrix = new int[ContainerModel.containerZ][ContainerModel.containerY][ContainerModel.containerX];

    private ArrayList<ParcelShape> typesLeft = new  ArrayList<ParcelShape>(); //parcel types (A,B,C) of which there are still atleast 1 parcel left (should maybe have type of chars or enums instead of ParcelShape?)
    private int A_ParcelsLeft;
    private int B_ParcelsLeft;
    private int C_ParcelsLeft;
    private ArrayList<ParcelShape> parcelsPacked = new ArrayList<ParcelShape>();
    private ArrayList<MaximalSpace> maximalSpaces = new ArrayList<MaximalSpace>();

    private static Coordinates[] containerVertices;

    /*
    private final static Coordinates v1 = new Coordinates(0,0,0);
    private final static Coordinates v2 = new Coordinates(ContainerModel.containerX,0,0);
    private final static Coordinates v3 = new Coordinates(0,ContainerModel.containerY,0);
    private final static Coordinates v4 = new Coordinates(0,0,ContainerModel.containerZ);
    private final static Coordinates v5 = new Coordinates(ContainerModel.containerX,ContainerModel.containerY,0);
    private final static Coordinates v6 = new Coordinates(ContainerModel.containerX,0,ContainerModel.containerZ);
    private final static Coordinates v7 = new Coordinates(0,ContainerModel.containerY,ContainerModel.containerZ);
    private final static Coordinates v8 = new Coordinates(ContainerModel.containerX,ContainerModel.containerY,ContainerModel.containerZ);
    */

    public Grasp(int nrOfA, int nrOfB, int nrOfC){
        A_ParcelsLeft = nrOfA;
        B_ParcelsLeft = nrOfB;
        C_ParcelsLeft = nrOfC;

        Coordinates containerMinCoords = new Coordinates(0,0,0);
        Coordinates containerMaxCoords = new Coordinates(ContainerModel.containerX,ContainerModel.containerY,ContainerModel.containerZ);
        containerVertices = findAllVertices(containerMinCoords,containerMaxCoords);

        Coordinates initialMinCoords = new Coordinates(0,0,0);
        Coordinates initialMaxCoords = new Coordinates(ContainerModel.containerX, ContainerModel.containerY, ContainerModel.containerZ);
        MaximalSpace initialMaximalSpace = new MaximalSpace(initialMinCoords,initialMaxCoords);
        maximalSpaces.add(initialMaximalSpace);
    }

    public Coordinates[] findAllVertices(Coordinates minCoords, Coordinates maxCoords){
        int minX = minCoords.getX();
        int minY = minCoords.getY();
        int minZ = minCoords.getZ();

        int maxX = maxCoords.getX();
        int maxY = maxCoords.getY();
        int maxZ = minCoords.getZ();

        Coordinates[] vertices =  new Coordinates[8];

        vertices[0] = new Coordinates(minX, minY, minZ);
        vertices[1] = new Coordinates(maxX, minY, minZ);
        vertices[2] = new Coordinates(minX, maxY, minZ);
        vertices[3] = new Coordinates(minX, minY, maxZ);
        vertices[4] = new Coordinates(maxX, maxY, minZ);
        vertices[5] = new Coordinates(maxX, minY, maxZ);
        vertices[6] = new Coordinates(minX, maxY, maxZ);
        vertices[7] = new Coordinates(maxX, maxY, maxZ);

        return vertices;
    }

    public double computeDistanceClosestCorner(MaximalSpace space){ //returns the coordinates of the corner closest to the input MaximalSpace space
        /*
        "For each
        new maximal-space, we compute the distance from every corner of the
        space to the corner of the container nearest to it and keep the minimum
        distance in the lexicographical order"
         */

        //so first we need to store the coordinates of the 8 corners of the container: containerVertices

        //then we loop through the containerVertices and compute for both the min and max coords the closest corner

        //we save a MaximalSpace's min and max coords, and using these we can find the coordinates of all 8 vertices

        Coordinates minCoords = space.getMinCoords();
        Coordinates maxCoords = space.getMaxCoords();

        Coordinates[] maximalSpaceVertices = findAllVertices(minCoords, maxCoords);

        System.out.println("maximalSpaceVerticesLength: " + maximalSpaceVertices.length);

        //Coordinates closestCorner = v1;
        double[][] distance = new double[8][8]; //distance of each MaximalSpace vertex to each container corner
        double[] minDistancePerVertex = new double[8];   //distance of each MaximalSpace vertex to its closest container corner
        double minDistance = 0;

        for (int i = 0; i < maximalSpaceVertices.length; i++) {
            for(int j = 0; j < containerVertices.length; j++){
                distance[i][j] =  Math.sqrt(
                                   Math.pow(/*containerVertices[j].getX()*/ - maximalSpaceVertices[i].getX(),2)
                                 + Math.pow(containerVertices[j].getY() - maximalSpaceVertices[i].getY(),2)
                                 + Math.pow(containerVertices[j].getZ() - maximalSpaceVertices[i].getZ(),2));
                if(j == 0) minDistancePerVertex[i] = distance[i][0];
                else if (distance[i][j] < minDistancePerVertex[i]) minDistancePerVertex[i] = distance[i][j];
            }
            if(i == 0) minDistance = minDistancePerVertex[i];
            else if (minDistancePerVertex[i] < minDistance){
                minDistance = minDistancePerVertex[i];
                //closestCorner = maximalSpaceVertices[i];
            }
        }
        return minDistance;
    }

    public MaximalSpace chooseMaximalSpace() {

        ArrayList<Double> closestCornerDistances = new ArrayList<>();
        //double[] closestCornerDistances = new double[8];

        for(int i = 0; i < maximalSpaces.size(); i++){
        //for(MaximalSpace space : maximalSpaces){
            //computeDistanceClosestCorner(space);

            //closestCornerDistances[i] = computeDistanceClosestCorner(maximalSpaces.get(i));
            closestCornerDistances.add(computeDistanceClosestCorner(maximalSpaces.get(i)));
        }

        double minimumClosestCornerDistance = 0;
        int minimumClosestCornerDistanceIndex = 0;
        int j = 0;

        System.out.println("maxSpaces size: " + maximalSpaces.size());
        System.out.println("closestCornerDistancesList size: " + closestCornerDistances.size());

        for(; j < closestCornerDistances.size(); j++){
            if(j == 1) //minimumClosestCornerDistance = closestCornerDistances.get(j);
                minimumClosestCornerDistanceIndex = j;
            else if(closestCornerDistances.get(j) < minimumClosestCornerDistance)
                //minimumClosestCornerDistance = closestCornerDistances.get(j);
                minimumClosestCornerDistanceIndex = closestCornerDistances.indexOf(closestCornerDistances.get(j));
        }
        //index of the maximalSpace we choose, the one closest to a corner of the container
        System.out.println("index of chosen space: " + minimumClosestCornerDistanceIndex);
        MaximalSpace chosenMaximalSpace = maximalSpaces.get(minimumClosestCornerDistanceIndex);

        return chosenMaximalSpace;
    }

    public void testPlaceParcel(int z, int y, int x, ParcelShape parcel){
        parcel.setCurrentCoordinates(new Coordinates(x,y,z));
        //sets a 1 in the containerMatrix for each coordinate with the vectors of the parcel shape
        for (int zCoord = z; zCoord < z + parcel.getShape()[2]; zCoord++) {
            for (int yCoord = y; yCoord < y + parcel.getShape()[1]; yCoord++) {
                for (int xCoord = x; xCoord < x + parcel.getShape()[0]; xCoord++) {
                    containerMatrix[zCoord][yCoord][xCoord] = 1;
                }
            }
        }
    }

    public void testPrintContainer(){
        for(int z=0;z<ContainerModel.containerZ;z++){
            System.out.println("Layer for z = "+z);
            for(int y =0;y<ContainerModel.containerY;y++){
                for (int x=0;x<ContainerModel.containerX;x++){
                    System.out.print(containerMatrix[z][ContainerModel.containerY-1-y][x]+" "); // supposing the origin is in lower left corner (instead of upper)
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void fillChosenMaximalSpace(){
        //we place parcels in the chosen space to try to either completely fill the container or get the highest value


    }

    public void generateMaximalSpaces(ParcelShape lastPlacedParcel){ //should later use blocks (groups of parcels put into a layer) instead of single parcels

        Coordinates coords = lastPlacedParcel.getCurrentCoordinates();
        Coordinates minCoordsParcel = coords;
        Coordinates maxCoordsParcel = new Coordinates
        (coords.getX() + lastPlacedParcel.getShape()[0],
        coords.getY() + lastPlacedParcel.getShape()[1],
        coords.getZ() + lastPlacedParcel.getShape()[2]);
        System.out.println("X-coord: " + coords.getX() + " Y-coord:  " + coords.getY() + " Z-coord: " + coords.getZ());

        //Coordinates[] blockVertices = new Coordinates[8];
        Coordinates[] blockVertices = findAllVertices(minCoordsParcel, maxCoordsParcel);

        //ArrayList<Coordinates> blockVertices = new ArrayList<>();
        ArrayList<MaximalSpace> generatedSpaces = new ArrayList<>();


        /*
        After we place a block (a collection of parcels to best fill the last chosen maximal space),
        we select a vertex of this block and check in all directions how much empty space is around it,
        and save the dimensions.

        for each vertex there are certain directions on which there can be empty space and certain directions
        in which the block is.

        When a new maximal space is created any old maximal space with which it overlaps should be removed from the list

         */

        //Coordinates[] currentMaxSpaceVertices = new Coordinates[8];

        MaximalSpace currentMaxSpace;

        for(int i = 0; i < blockVertices.length; i++) {
            int z = blockVertices[i].getZ();
            int y = blockVertices[i].getY();
            int x = blockVertices[i].getX();

            for (; z < ContainerModel.containerZ-1 && containerMatrix[z][y][x] == 1; z++) {
                int zDimension = z;
            }
            for (; y < ContainerModel.containerY-1 && containerMatrix[z][y][x] == 1; y++) {
                int yDimension = y;
            }
            for (; x < ContainerModel.containerX-1 && containerMatrix[z][y][x] == 1; x++) {
                int xDimension = x;
            }

            System.out.println(x + " " + y + " " + z);
            Coordinates minCoords = new Coordinates(blockVertices[i].getX(), y, blockVertices[i].getZ());
            Coordinates maxCoords = new Coordinates(x, blockVertices[i].getY(), z);
            currentMaxSpace = new MaximalSpace(minCoords, maxCoords);
            generatedSpaces.add(currentMaxSpace);
            System.out.println("generatedSpaces size: " + generatedSpaces.size());
            //System.out.println("maxSpaces size: " + maximalSpaces.size());
        }


/*
        for(int i = 0; i < blockVertices.length; i++){
            for(int z = blockVertices[i].getZ(); z < ContainerModel.containerZ || containerMatrix[z][y][x] == 1; z++){
                for(int y = blockVertices[i].getY(); y < ContainerModel.containerY || containerMatrix[z][y][x] == 1; y++){
                    for(int x = blockVertices[i].getX(); x < ContainerModel.containerX || containerMatrix[z][y][x] == 1; x++){

                        Coordinates minCoords = new Coordinates(blockVertices[i].getX(), y, blockVertices[i].getZ());
                        Coordinates maxCoords = new Coordinates(x, blockVertices[i].getY(), z);
                        currentMaxSpaceVertices = findAllVertices(minCoords, maxCoords);

                    }
                }

            }
        }

*/
        maximalSpaces.addAll(generatedSpaces);
       // return generatedSpaces;
      }

}
