package uk.ac.ed.inf.parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import uk.ac.ed.inf.utils.ServerConnector;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author KangCY
 * @Description menus use server connector, and have two functions which can get shop locations and
 *              get item cost from the menus.
 * @create 2021-10-09 21:52
 */
public class Menus {
    private ServerConnector serverConnector;

    public Menus(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }

    /**
     * @name getDeliveryCost(String... strings)
     * @description this function will count the total cost of an order, including the items and 50p delivery cost.
     *              it first collect all the items menus from the web server,and deserializes the json list to java object
     *              then search the prices of the items in the order ,and finally count the total cost by adding the basic delivery cost
     * @param strings are several item names ordered by the customer
     * @return total cost of one order, including the price of items and 50p delivery cost
     */
    public int getDeliveryCost(ArrayList<String> strings){
        //initialize the tail of the url
        String url = "/menus/menus.json";
        //initialize the HttpRequest
        HttpResponse<String> response = serverConnector.getResponse(url);
        //cost is the price of the items in the order with standard dilivery charge 50p
        int cost = 50;
        if (response.statusCode()==200){     //check the HTTP status code in avoid of wrong URL
            // body is the json file we get from the web server
            String body = response.body();
            // get the type by using JAVA reflection API
            // deserialising a json record to an arraylist of Shop
            Type type = new TypeToken<ArrayList<Shop>>() {}.getType();
            ArrayList<Shop> shopList = new Gson().fromJson(body, type);
            // create a hashmap to store all the Shop.item objects
            HashMap<String, Integer> itemHashMap = new HashMap<>();
            // iterate through the shop items and add them to the hash map
            for (Shop shop:shopList) {
                for (Shop.Item item: shop.getMenu()) {
                    itemHashMap.put(item.item,item.pence);
                }
            }
            // get the cost of the items in the order
            for (String s:strings) {
                cost+=itemHashMap.get(s);
            }
        }else{        // report the error if the web server is not running
            System.out.println("Fatal error: Unable to connect to "+serverConnector.getMachine()
                                +" at port "+serverConnector.getPort()+".");
            //exit the application
            System.exit(1);
        }
        return cost;
    }


    /**
     * @name getShops(String... strings)
     * @description This function get the item names from one order, and search the web server to get the what3words
     *              location of the shop, if the order have two different shops, add them into the arraylist in the same way,
     *              and return the array list
     * @param strings are several item names in one order
     * @return the what3words strings of the place of the shop(s) that the drone needs to fly to get food in one order
     */
    public ArrayList<String> getShops(ArrayList<String> strings){
        //initialize the tail of the url
        String url = "/menus/menus.json";
        // initialize the HttpRequest
        HttpResponse<String> response = serverConnector.getResponse(url);
        //
        ArrayList<String> shopLocation = new ArrayList<>();
        if (response.statusCode()==200){     //check the HTTP status code in avoid of wrong URL
            // body is the json file we get from the web server
            String body = response.body();
            // get the type by using JAVA reflection API
            // deserialising a json record to an arraylist of Shop
            Type type = new TypeToken<ArrayList<Shop>>() {}.getType();
            ArrayList<Shop> shopList = new Gson().fromJson(body, type);
            // create a hashmap to store all the Shop.item objects
            HashMap<String, String> itemShopHashMap = new HashMap<>();
            for (Shop shop:shopList) {
                for (Shop.Item item: shop.getMenu()) {
                    itemShopHashMap.put(item.item,shop.getLocation());
                }
            }
            // get the location of the shop of that food
            for (String s:strings) {
                String s1 = itemShopHashMap.get(s);
                // in avoid of repeat add the same shop
                if (!shopLocation.contains(s1)){
                    shopLocation.add(itemShopHashMap.get(s));
                }
            }

        }else{        // report the error if the web server is not running
            System.out.println("Fatal error: Unable to connect to "+serverConnector.getMachine()
                    +" at port "+serverConnector.getPort()+".");
            //exit the application
            System.exit(1);
        }
        return shopLocation;
    }

    /**
     * @author KangCY
     * @Description Shop is an object acting as a receiver of the data from
     *              json files through the web server
     * @create 2021-10-12 23:03
     */
    public static class Shop {
        private String name;
        private String location;
        private ArrayList<Item> menu;


        public static class Item{
            String item;
            int pence;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public ArrayList<Item> getMenu() {
            return menu;
        }

        public void setMenu(ArrayList<Item> menu) {
            this.menu = menu;
        }
    }
}
