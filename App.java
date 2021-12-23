package uk.ac.ed.inf;


import uk.ac.ed.inf.beans.*;
import uk.ac.ed.inf.controllers.OrderOperator;
import uk.ac.ed.inf.controllers.RouteCalculator;
import uk.ac.ed.inf.parsers.Menus;
import uk.ac.ed.inf.parsers.NoFlyZone;
import uk.ac.ed.inf.parsers.Words;
import uk.ac.ed.inf.utils.*;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
    // Constant IP
    private static final String IP = "localhost";

    /**
     * Write files
     *
     * @param flightpath - An array of strings of all the moves
     * @param yyyy       - The year of the flight
     * @param mm         - The month of the flight
     * @param dd         - The day of the flight
     * @throws IOException If file cannot be written
     */
    private static void writeFiles(String flightpath, String yyyy,
                                   String mm, String dd) {
        // Write the flight path file
        try {
            FileWriter myWriter = new FileWriter(
                    "drone-" + dd + "-" + mm + "-" + yyyy + ".geojson");
            myWriter.write(flightpath);
            myWriter.close();
            System.out.println("drone geojson file successfully created!");
        } catch (IOException e) {
            System.out.println("Fatal error: Readings GeoJson wasn't created.");
            e.printStackTrace();
        }
    }

    /**
     * main
     *
     * @param args args[0]  - The day of the flight
     *             args[1]  - The month of the flight
     *             args[2]  - The year of the flight
     *             args[3]  - The web port of the web server
     *             args[4]  - The database port of the database server
     * @throws IOException If file cannot be written
     */
    public static void main( String[] args ) {
        // Take in arguments for the details of the flight
        String dd = args[0];
        String mm = args[1];
        String yyyy = args[2];
        String webport = args[3];
        String dbport = args[4];

        //create database and server connector
        DatabaseConnector databaseConnector = new DatabaseConnector(IP,dbport);
        ServerConnector serverConnector = new ServerConnector(IP,webport);
        //get menus,words,noflyzone from server
        Menus menus = new Menus(serverConnector);
        Words words = new Words(serverConnector);
        NoFlyZone noFlyZone = new NoFlyZone(serverConnector);
        //get routecalculator
        RouteCalculator routeCalculator = new RouteCalculator();

        //create database reader
        DatabaseReader reader = new DatabaseReader(databaseConnector);
        //get order record resultSet from database
        //and transfer resultSet to arraylist
        ResultSet rsOrder = reader.readOrder(yyyy+"-"+mm+"-"+dd);
        ArrayList<OrderRecord> orderRecordList = reader.getOrderRecordList(rsOrder);
        //get orderDetails resultSet from database
        //and transfer resultSet to HashMap
        ResultSet rsOrderDetails = reader.readOrderDetails();
        HashMap<String,ArrayList<String>> orderDetails = reader.getOrderDetails(rsOrderDetails);
        // relate items with orders which have the same order number
        for (OrderRecord orderRecord : orderRecordList) {
            ArrayList<String> item = orderDetails.get(orderRecord.getOrderNo());
            orderRecord.setItem(item);
        }

        // appleton tower is a static coordinate where the drone leave and land
        LongLat apt = new LongLat(-3.186874, 55.944494);
        // create an order operator to sort orders
        OrderOperator orderOperator = new OrderOperator(menus, words, noFlyZone, routeCalculator);
        // create an arraylist to store orders
        ArrayList<Order> orderArrayList = new ArrayList<>();
        // transfer each OrderRecord to an Order, and add them to the arraylist created before
        for (OrderRecord orderRecord : orderRecordList) {
            for (Order order : orderOperator.transferToOrder(orderRecord, apt)) {
                orderArrayList.add(order);
            }
        }

        //the initial moves left is 1500, and initial coordinate is appleton tower
        int step = 1500;
        LongLat currentLocat = apt;
        // currOrder and currRoute records the current delivery status of the drone
        Order currOrder;
        Route currRoute;
        // arrOrders stores the orders which are waiting to be record in the database
        // arrPointers stores the last Pointer of each route, this will generate the geogson map later
        // arrRoutes stores the routes as a whole this will be written in database
        ArrayList<Order> arrOrders = new ArrayList<>() ;
        ArrayList<Pointer> arrPointers = new ArrayList<>();
        ArrayList<Route> arrRoutes = new ArrayList<>();
        // blank order is when the drone is flying from one place to the first shop of the order
        Order blankOrder = new Order();
        blankOrder.setOrderNo("00000000");
        while (true){
            // get the current route
            currRoute = orderOperator.getNextOrder(orderArrayList,currentLocat,step);
            if (currRoute==null){
                //no other routes available, flyback now
                break;
            }
            int deliveryMove = 0;      // this is the moves from the first shop to the customer

            // this is the move from shop1 to shop2 if there are two shops in one order
            Route shop1toshop2Move = null;
            // get the selected current order
            currOrder = currRoute.getOrder();
            // 1.calculate the route going to next shop
            Route shopMove = routeCalculator.getRouteMove(currentLocat,currOrder.getStartLocation(),noFlyZone.getNoFlyZone());
            shopMove.setOrder(blankOrder);
            //refresh the currrent location
            currentLocat = shopMove.getLastPointer().getCoordinate();
            if (currOrder.getMidLocation()!=null){
                //2.calculate shop1 to shop2 moves
                shop1toshop2Move = routeCalculator.getRouteMove(currentLocat,currOrder.getMidLocation(),noFlyZone.getNoFlyZone());
                shop1toshop2Move.setOrder(currOrder);
                //refresh the currrent location
                currentLocat = shop1toshop2Move.getLastPointer().getCoordinate();
            }
            // 3.calculate the route to the customer
            Route customerMove = routeCalculator.getRouteMove(currentLocat, currOrder.getEndLocation(), noFlyZone.getNoFlyZone());
            customerMove.setOrder(currOrder);
            // refresh the current location again
            currentLocat = customerMove.getLastPointer().getCoordinate();

            // add the end pointers of each route
            arrPointers.add(shopMove.getLastPointer());       // end pointer to shop1
            if (shop1toshop2Move!=null){
                arrPointers.add(shop1toshop2Move.getLastPointer());   // end pointer to shop2
            }
            arrPointers.add(customerMove.getLastPointer());   // end pointer to customer

            // add the routes as a whole this will be written in database
            arrRoutes.add(shopMove);               // route to shop1
            if (shop1toshop2Move!=null){
                arrRoutes.add(shop1toshop2Move);   // route to shop2
            }
            arrRoutes.add(customerMove);          // route to customer

            // add orders as a whole this will be written in database
            arrOrders.add(currOrder);
            // remove all orders with the same orderNo
            orderArrayList = orderOperator.removeOrders(orderArrayList,currOrder);
            // subtract the moves spent and hover
            if (shop1toshop2Move!=null){
                deliveryMove = customerMove.getMoves() + shop1toshop2Move.getMoves();
            }else {
                deliveryMove = customerMove.getMoves();
            }
            // subtract the moves spent and hover
            step-= shopMove.getMoves() + deliveryMove;
            System.out.println(currOrder);
            System.out.println("step:"+step+" moves:"+currOrder.getTotalMoves()+
                    " value:"+(double)currOrder.getDeliveryCost()/currOrder.getTotalMoves());
        }
        //flyback
        // get the flyback route and add the pointer to array
        Route backRoute = routeCalculator.getRouteMove(currentLocat,apt,noFlyZone.getNoFlyZone());
        //the flyback don't need to hover, get the previous pointer as last pointer
        Pointer previousPointer = backRoute.getLastPointer().getPreviousPointer();
        backRoute.setLastPointer(previousPointer);
        // set blank orderNo
        backRoute.setOrder(blankOrder);
        arrPointers.add(backRoute.getLastPointer());
        arrRoutes.add(backRoute);
        step-= backRoute.getMoves();
        //print the steps left and order numbers
        System.out.println("moves remaining:"+step);
        System.out.println("orders delivered:"+arrOrders.size());

        //parse the cooordinates to a flypath
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        String flyPath = geoJsonWriter.getGeoJson(arrPointers);
        //write files to geojson file
        writeFiles(flyPath,yyyy,mm,dd);

        //write data in the database
        DatabaseWriter databaseWriter = new DatabaseWriter(databaseConnector);
        try {
            // write in deliveries table
            databaseWriter.writeInDeliveries(arrOrders);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("database deliveries added successfully ");


        ArrayList<Pointer> allPointers = new ArrayList<>();
        for (Route route : arrRoutes) {
            ArrayList<Pointer> pointers = routeCalculator.getPointers(route);
            allPointers.addAll(pointers);
        }
        try {
            // write in flightpath table
            databaseWriter.writeInFlightpath(allPointers);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("all pointers size:"+allPointers.size());
        System.out.println("database flightPath added successfully ");
    }

}
