package uk.ac.ed.inf.beans;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.awt.geom.Line2D;
import java.util.List;

/**
 * @author KangCY
 * @Description Longlat is a coordinate which represents the longitude and latitude of the current
 *              position, it is used for marking the exact location of the drone during the flight.
 * @create 2021-10-09 19:49
 */
public class LongLat {

    private Double longitude;
    private Double latitude;


    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public LongLat(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * @name isConfined()
     * @description  This function tells if the location is in the valid region
     * @return       it returns true if the point is within the drone confinement area and false if it is not.
     */
    public Boolean isConfined(){
        if ((this.longitude<-3.184319 && this.longitude>-3.192473) && (this.latitude> 55.942617 && this.latitude<55.946233)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * @name distanceTo(LongLat location)
     * @description This function takes a LongLat object as a parameter
     *              and returns the Pythagorean distance between the two points as a value of type double.
     * @param       location the given location
     * @return      distance between this location and the given location
     */
    public Double distanceTo(LongLat location){
        //count the Pythagorean distance
        Double dis_pow = Math.pow(this.longitude-location.longitude,2)+Math.pow(this.latitude- location.latitude,2);
        Double distance = Math.sqrt(dis_pow);
        return distance;
    }


    /**
     * @name closeTo(LongLat location)
     * @description Being close to the location will be sufficient, where x1 is close to x2
     *              if the distance between x1 and x2 is strictly less than the
     *              distance tolerance of 0.00015 degrees.
     * @param       location is the second location
     * @return      true if the locations are close to each other, if not returns false
     */
    public Boolean closeTo(LongLat location){
        Double dis_pow = Math.pow(this.longitude-location.longitude,2)+Math.pow(this.latitude- location.latitude,2);
        Double distance = Math.sqrt(dis_pow);
        if (distance<0.00015){
            return true;
        }else {
            return false;
        }
    }

    /**
     * @name nextPosition(int angle)
     * @description it takes an int angle as a parameter and returns a LongLat object which represents
     *              the new position of drone if it makes a move in the direction of the angle,
     *              every move when flying is a straight line of length 0.00015 degrees
     * @param angle is the angle in which direction the drone moves, from 0 to 350, junk value -999 means hovering
     * @return the LongLat object where the drone will be after this move
     */
    public LongLat nextPosition(int angle){
        if(angle==-999){  //hovring
            return new LongLat(this.longitude,this.latitude);
        }else {          //moving
            //angle needs to be transferred to radians
            Double new_lon = Math.cos(Math.toRadians(angle))*0.00015+this.longitude;
            Double new_lat = Math.sin(Math.toRadians(angle))*0.00015+this.latitude;
            return new LongLat(new_lon,new_lat);
        }
    }

    @Override
    public String toString() {
        return "LongLat{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    /**
     * @name isInNoFlyZone(LongLat nextLonglat, FeatureCollection featureCollection)
     * @description Tell whether the move from current coordinate to the next one has passed
     *              the no fly zone, by detecting whether there is line intersection with the
     *              no fly zone.
     * @param nextLonglat the destination of the move from current coordinate
     * @param featureCollection the no fly zone
     * @return whether passed the no fly zone during this move
     */
    public boolean isInNoFlyZone(LongLat nextLonglat, FeatureCollection featureCollection){
        // the result boolean is set to false default
        boolean res = false;
        // flypath is a line that from current coordinate to next coordinate
        Line2D flyPath = new Line2D.Double(this.getLongitude(),this.getLatitude(),
                nextLonglat.getLongitude(),nextLonglat.getLatitude());
        //transfer no fly zone feature collections to Geometry list
        List<Feature> features = featureCollection.features();

        // detect whether the line intersects with the no fly zone
        for (int i = 0; i < features.size(); i++) {
            //get each feature and transfer them to a list of points
            Geometry geometry = features.get(i).geometry();
            Polygon polygon = (Polygon) geometry;
            List<Point> points = polygon.coordinates().get(0);
            //draw lines between each points
            for (int a = 0; a < points.size(); a++) {
                int b = (a+1) % points.size();
                // draw a line
                Line2D boundary = new Line2D.Double(points.get(a).longitude(),points.get(a).latitude(),
                        points.get(b).longitude(),points.get(b).latitude());
                //detect if the lines intersect with each other
                if (flyPath.intersectsLine(boundary)){
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * @name isNeighbour(LongLat coordinate)
     * @description This function tells whether the coordinate is a
     *              neighbour of a point that we have been to before,
     *              this is for filtering nearby points
     * @param coordinate this is the coordinate that we want to compare with
     * @return whether the distance between two coordinates are very nearby points
     */
    public boolean isNeighbour(LongLat coordinate){
        // check the distance from this coordinate to the param one
        if (this.distanceTo(coordinate)<0.000077){
            return true;
        }else {
            return false;
        }
    }

}
