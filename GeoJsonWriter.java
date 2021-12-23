package uk.ac.ed.inf.utils;

import com.mapbox.geojson.*;
import uk.ac.ed.inf.beans.LongLat;
import uk.ac.ed.inf.beans.Pointer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author KangCY
 * @Description GeoJsonWriter provides some functions that can transfer Pointer
 *              to a geojson map
 * @create 2021-12-06 17:54
 */
public class GeoJsonWriter {
    /**
     * @name readOrder(String date)
     * @description this function gets a pointer as a parameter, and trace back this
     *              pointer to find the whole path of the route
     * @param pointer the last pointer of the route, it's the final son node
     * @return an arraylist of Longlat coordinates of the whole fly path
     */
    public ArrayList<LongLat> getLonglats(Pointer pointer){
        // longlats stores the coordinates along the drone's flypath
        ArrayList<LongLat> longLats = new ArrayList<>();
        while (pointer.getPreviousPointer()!=null){    // if the pointer's previous pointer is null, it'll be the start of the route, stop
            // get the coordinate of this pointer
            longLats.add(pointer.getCoordinate());
            // get the previous pointer(parent node) that it is pointing to, and assign to the variable to trace back
            pointer = pointer.getPreviousPointer();
        }
        // reverse the arraylist because it was back traced
        Collections.reverse(longLats);
        return longLats;
    }

    /**
     * @name getGeoJson(ArrayList<Pointer> pointers)
     * @description this function gets a string of pointers as parameter, and return a string of geojson map
     * @param pointers an array of pointers of the whole fly route in order, all of them are final son nodes of each route
     * @return a string of geojson map
     */
    public String getGeoJson(ArrayList<Pointer> pointers){
        // longLats stores all the coordinates of the drone flypath
        ArrayList<LongLat> longLats = new ArrayList<>();
        for (Pointer pointer : pointers) {
            // add all the longlats of the route to the whole flypath route
            longLats.addAll(getLonglats(pointer));
        }
        // transfer longlat array to point array
        ArrayList<Point> points = new ArrayList<>();
        for (LongLat longLat : longLats) {
            points.add(Point.fromLngLat(longLat.getLongitude(),longLat.getLatitude()));
        }
        // transfer to LineString
        LineString lineString = LineString.fromLngLats(points);
        // transfer to feature
        Feature feature = Feature.fromGeometry((Geometry) lineString);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        // transfer to string and return
        return  featureCollection.toJson();
    }


    /**
     * @description this is a function for testing in my test class
     * @param flightpath - An array of strings of all the moves
     * @param yyyy       - The year of the flight
     * @param mm         - The month of the flight
     * @param dd         - The day of the flight
     * @throws IOException If file cannot be written
     */
    public void writeFiles(String flightpath, String yyyy,
                                   String mm, String dd) {
        // Write the flight path file
        try {
            FileWriter myWriter = new FileWriter(
                    "flightpath-" + dd + "-" + mm + "-" + yyyy + ".txt");
            myWriter.write(flightpath);
            myWriter.close();
            System.out.println("Flightpath text file successfully created!");
        } catch (IOException e) {
            System.out.println("Fatal error: Readings GeoJson wasn't created.");
            e.printStackTrace();
        }
    }
}
