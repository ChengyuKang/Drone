package uk.ac.ed.inf.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author KangCY
 * @Description This is the server connector, it takes a machine name and a database port
 *  *               to get connection to the webserver
 * @create 2021-12-02 13:25
 */
public class ServerConnector {
    //just have one static HttpClient,shared between all HttpRequests
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private String machine;
    private String port;

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public ServerConnector(String machine, String port) {
        this.machine = machine;
        this.port = port;
    }

    /**
     * @name getResponse(String url)
     * @description Create a response string that we can use for running various
     *              web services
     * @return return the response of the web server
     * @throws InterruptedException If the send request is illegal
     */
    public HttpResponse<String> getResponse(String url){
        // create the url string with the machine and port
        String urlString = "http://"+machine+":"+ port +url;
        // get request from the http
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        HttpResponse<String> response = null;
        try {
            //call the method on the client which we created
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }
}
