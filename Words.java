package uk.ac.ed.inf.parsers;

import com.google.gson.Gson;
import uk.ac.ed.inf.beans.LongLat;
import uk.ac.ed.inf.utils.ServerConnector;

import java.net.http.HttpResponse;

/**
 * @author KangCY
 * @Description Words uses server to get coordinate by parsing a what3word string
 * @create 2021-12-01 12:50
 */
public class Words {
    private ServerConnector serverConnector;

    public Words(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }

    /**
     * @description getCoordinates gets a string of what three words, and parse it to a
     *              coordinate and then return
     * @param string what three words string, format of which is "xxxx.xxxx.xxxx"
     * @return coordinate the string represents
     */
    public LongLat getCoordinates(String string){
        // get the url for the response
        String url = "/words/"+string.replace(".","/")+"/details.json";
        HttpResponse<String> response = serverConnector.getResponse(url);
        LongLat coordinate = null;
        if (response.statusCode()==200){     //check the HTTP status code in avoid of wrong URL
            // body is the json file we get from the server
            String body = response.body();
            // get the type by using JAVA reflection API
            // deserialising a json record to a WhatThreeWords
            WhatThreeWords whatThreeWords = new Gson().fromJson(body, WhatThreeWords.class);
            coordinate = new LongLat(whatThreeWords.getCoordinates().lng,whatThreeWords.getCoordinates().lat);
        }else{        // report the error if the web server is not running
            System.out.println("Fatal error: Unable to connect to "+serverConnector.getMachine()
                    +" at port "+serverConnector.getPort()+".");
            //exit the application
            System.exit(1);
        }

        return coordinate;
    }

}
