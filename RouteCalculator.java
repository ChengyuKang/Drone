package uk.ac.ed.inf.controllers;

import com.mapbox.geojson.FeatureCollection;
import uk.ac.ed.inf.beans.LongLat;
import uk.ac.ed.inf.beans.Pointer;
import uk.ac.ed.inf.beans.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author KangCY
 * @Description routeCalculator provides a calculator to count the route from one coordinate to another one
 * @create 2021-12-01 17:47
 */
public class RouteCalculator {
    /**
     * @description this function gets an array list of points, and sort them by totalDistance
     *              from short to long, finally return the one with shortest total distance
     * @param waitingPointers an array list of unsorted pointers
     * @return the pointer has shortest distance from the start to the end
     */
    public Pointer shortestPointer(ArrayList<Pointer> waitingPointers){
        //overide the compare function, sort by total distance of each pointer
        waitingPointers.sort(new Comparator<Pointer>() {
            @Override
            public int compare(Pointer o1, Pointer o2) {
                return  o1.getTotalDis().compareTo(o2.getTotalDis());
            }
        });
        return waitingPointers.get(0);
    }


    /**
     * @description
     * @param startL start coordinate of the route
     * @param endL end location of the route
     * @param featureCollection the no fly zone
     * @return the nearest route from start to end, without entering the no fly zone
     */
    public Route getRouteMove(LongLat startL, LongLat endL, FeatureCollection featureCollection){
        //initialize the coordinates by Pointers
        Pointer start = new Pointer();
        Pointer end = new Pointer();
        start.setCoordinate(startL);
        start.setStartMoves((double) 0);
        end.setCoordinate(endL);
        start.setTotalDis(start.getCoordinate().distanceTo(end.getCoordinate()));
        //three array lists storing the pointers
        ArrayList<Pointer> totalPointers = new ArrayList<>();        // it stores all the pointers that appear in the counting procedure
        ArrayList<Pointer> discoveredPointers = new ArrayList<>();   // it stores the pointers that has been scattered, they're fully discovered
        ArrayList<Pointer> waitingPointers = new ArrayList<>();      // it stores the pointers that haven't been diffused

        LongLat endLonglat = end.getCoordinate();           // the end location's coordinate
        LongLat currentLongLat;                             // current coordinate
        Pointer current;                                    // current pointer
        // initialize the lists by start pointer
        totalPointers.add(start);
        waitingPointers.add(start);
        // the while loop use a* algorithm to count the nearest route from
        // start to end point without entering no fly zones
        while (true){
            current = shortestPointer(waitingPointers);     // get the shortest pointer to the destination
            waitingPointers.remove(current);                // remove this pointer from the waiting list
            discoveredPointers.add(current);                // join this pointer to discovered pointer list

            currentLongLat = current.getCoordinate();       // refresh the current coordinate
            //if has arrived at the destination, break
            if (currentLongLat.closeTo(end.getCoordinate())){
                break;
            }

            //scatter from the current coordinate 12 times, 30 degrees each time
            for (int j = 0; j < 12; j++) {
                // new pointer is the new arrived coordinate
                Pointer newPointer = new Pointer();
                newPointer.setCoordinate(currentLongLat.nextPosition(j*30));
                newPointer.setAngle(j*30);
                LongLat newCoordinate = newPointer.getCoordinate();
                newPointer.setPreviousPointer(current);                                                     //set its previous point
                newPointer.setEndDis(newPointer.getCoordinate().distanceTo(endLonglat));                    // set the distance to the destination
                newPointer.setStartMoves(newPointer.getPreviousPointer().getStartMoves()+1);                 // set the moves from the start
                newPointer.setTotalDis(newPointer.getStartMoves()*0.00015+ newPointer.getEndDis());          // set the total distance
                // the drone shouldn't passed any no fly zone or outside areas
                if(!current.getCoordinate().isInNoFlyZone(newPointer.getCoordinate(), featureCollection) && newPointer.getCoordinate().isConfined()){
                    //control whether stop adding this pointer to our waiting list
                    boolean isBreak = false;
                    // compare this pointer with all other pointers
                    for (Pointer totalPointer : totalPointers) {
                        // if this point has enter the nearby areas of our previous found pointers
                        if (newCoordinate.isNeighbour(totalPointer.getCoordinate()) ){
                            // don't add this pointer if there was a better pointer nearby
                            if (!newPointer.thisIsBetterRoute(totalPointer)){
                                isBreak =true;
                                break;
                            }
                        }
                    }
                    // there was a better pointer, stop adding this one
                    if (isBreak){
                        continue;
                    }
                    // add the new pointer to the arraylist
                    totalPointers.add(newPointer);
                    waitingPointers.add(newPointer);
                }
            }
        }
        // resPointer represents the hovering move
        Pointer resPointer = new Pointer();
        resPointer.setCoordinate(currentLongLat.nextPosition(-999));                   // hover
        resPointer.setAngle(-999);                                                     // set hover angle
        resPointer.setPreviousPointer(current);                                        // set its previous point
        resPointer.setStartMoves(resPointer.getPreviousPointer().getStartMoves()+1);   // set the moves from the start
        // return the last pointer of the path as a route
        return new Route(resPointer.getStartMoves().intValue(),resPointer);
    }

    /**
     * @description this function transfer a route to an arraylist of pointers of the
     *                 route by tracing back from the last pointer
     * @param route the current route
     * @return an arraylist of pointers of this route
     */
    public ArrayList<Pointer> getPointers(Route route){
        // get the last pointer from the route
        Pointer pointer = route.getLastPointer();
        String orderNo = route.getOrder().getOrderNo();

        // pointers stores the coordinates along the drone's flypath
        ArrayList<Pointer> pointers = new ArrayList<>();
        while (pointer.getPreviousPointer()!=null){    // if the pointer's previous one is null, it'll be the start of the route, stop
            // set the order number of the pointer
            pointer.setOrderNo(orderNo);
            // get the coordinate of this pointer
            pointers.add(pointer);
            // get the previous pointer(parent node) that it is pointing to, and assign to the variable to trace back
            pointer = pointer.getPreviousPointer();
        }
        // reverse the arraylist because it was back traced
        Collections.reverse(pointers);
        return pointers;
    }

}
