package uk.ac.ed.inf.parsers;

import com.mapbox.geojson.FeatureCollection;
import uk.ac.ed.inf.utils.ServerConnector;

import java.net.http.HttpResponse;

/**
 * @author KangCY
 * @Description this class is used to get the no fly zone from the webserver
 * @create 2021-12-02 14:53
 */
public class NoFlyZone {
    private ServerConnector serverConnector;

    public NoFlyZone(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }

    /**
     * @name getNoFlyZone()
     * @description this function gets the no fly zone from the webserver
     * @return no fly zones as a feature collection
     */
    public FeatureCollection getNoFlyZone(){
        // initialize the tail of the url
        String url = "/buildings/no-fly-zones.geojson";
        //initialize the HttpRequest
        HttpResponse<String> response = serverConnector.getResponse(url);
        FeatureCollection featureCollection = null;
        if (response.statusCode()==200){     //check the HTTP status code in avoid of wrong URL
            // body is the json file we get from the web server
            String body = response.body();
            // deserialising a json record to a featureCollection
            featureCollection = FeatureCollection.fromJson(body);
        }else{        // report the error if the web server is not running
            System.out.println("Fatal error: Unable to connect to "+serverConnector.getMachine()
                    +" at port "+serverConnector.getPort()+".");
            //exit the application
            System.exit(1);
        }

        return featureCollection;
    }
}
