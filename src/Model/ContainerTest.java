package Model;
import Shapes.*;
import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;


public class ContainerTest {


    public static void main(String[] args) {
        ArrayList<ParcelShape> givenParcels = new ArrayList<>();

        givenParcels.add(new ParcelA());
        givenParcels.add(new ParcelB());
        givenParcels.add(new ParcelC());
       
        ContainerModel container = new ContainerModel();

        givenParcels = container.orderParcelListByValue(givenParcels);
        //givenParcels = container.orderParcelListByRatio(givenParcels);

        container.setParcelList(givenParcels);
        container.setAmountOfParcels(20,40,20);
        ContainerModel maxValueContainer = new ContainerModel();
        maxValueContainer.setParcelList(givenParcels);
        int delay = 10000; // in milliseconds
        container.setDelay(delay);
        container.solveBacktracking(maxValueContainer, false,true);
        //container.solveFirstPackedCargoRandomOrder();
    }
}
