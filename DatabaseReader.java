package uk.ac.ed.inf.utils;

import uk.ac.ed.inf.beans.dbTestBeans.Deliveries;
import uk.ac.ed.inf.beans.dbTestBeans.FlightPath;
import uk.ac.ed.inf.beans.OrderRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author KangCY
 * @Description  This is a class that has many functions to read data from the database
 *               and store data to objects
 * @create 2021-12-01 11:05
 */
public class DatabaseReader {
    private DatabaseConnector dbconnector;

    public DatabaseReader(DatabaseConnector dbconnector) {
        this.dbconnector = dbconnector;
    }

    public DatabaseConnector getDbconnector() {
        return dbconnector;
    }

    public void setDbconnector(DatabaseConnector dbconnector) {
        this.dbconnector = dbconnector;
    }

    /**
     *  @name readOrder(String date)
     * @description this function gets a string of date as parameter, and search table 'orders' in the
     *              database, and get the data that have the same 'deliveryDate' with our param
     * @param date the date of the orders that are wanted
     * @return result set of the orders in that day
     * @throws SQLException If the query command is illegal
     */
    public ResultSet readOrder(String date){
        // get connection from dbconnector
        Connection conn = dbconnector.getConn();
        // orderquery is going to be used to search in database
        final String orderQuery = "select * from orders where deliveryDate = (?) ";
        PreparedStatement psOrdersQuery = null;
        try {
            //get the ps through the connector
            psOrdersQuery = conn.prepareStatement(orderQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            // inject the date to the sql command
            psOrdersQuery.setString(1,date);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        ResultSet rs = null;
        try {
            // get the result set
            rs = psOrdersQuery.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * @name getOrderRecordList(ResultSet rs)
     * @description this function gets a resultset of orders, and transfer the data to an arraylist
     *              of orderRecords
     * @param rs the 'orders' result set we got from the database
     * @return an arraylist of OrderRecords
     * @throws SQLException If the get string command is illegal
     */
    public ArrayList<OrderRecord> getOrderRecordList(ResultSet rs) {
        // initialize a list to store the data
        ArrayList<OrderRecord> orderRecordList = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String orderNo = null;
            String deliveryDate = null;
            String deliverTo = null;
            try {
                // get string values from the result set and store them
                orderNo = rs.getString("orderNo");
                deliveryDate = rs.getString("deliveryDate");
                deliverTo = rs.getString("deliverTo");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            // new an orderRecord that stores the data got from result set
            orderRecordList.add(new OrderRecord(orderNo,deliveryDate,deliverTo));
        }
        return orderRecordList;
    }

    /**
     * @name readOrderDetails
     * @description this function search table 'orderDetails' in the
     *              database, and return all the data inside as a resultSet
     * @return result set of all orderDetails
     * @throws SQLException If the query command is illegal
     */
    public ResultSet readOrderDetails(){
        // get connection from the dbconnector
        Connection conn = dbconnector.getConn();
        // define the query string for the orderdetails
        final String orderQuery = "select * from orderDetails ";
        PreparedStatement psOrdersQuery = null;
        try {
            //get the ps through the connector
            psOrdersQuery = conn.prepareStatement(orderQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ResultSet rs = null;
        try {
            // get the result set
            rs = psOrdersQuery.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * @name getOrderDetails(ResultSet rs)
     * @description this function gets a resultset of orders, and transfer the data
     *              to a hash map that stores orderNo as key and item as value
     * @param rs the 'orderDetails' result set we got from the database
     * @return a hashmap of orderNo and item
     * @throws SQLException If the get string command is illegal
     */
    public HashMap<String,ArrayList<String>> getOrderDetails(ResultSet rs) {
        HashMap<String,ArrayList<String>> orderDetails = new HashMap<>();
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String orderNo = null;
            String item = null;
            try {
                // get string values from the resultset
                orderNo = rs.getString("orderNo");
                item = rs.getString("item");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (orderDetails.containsKey(orderNo)){        // if the order already exists
                //put the orderNo and item in the hashmap
                orderDetails.get(orderNo).add(item);
            }else{                                         // if the order didn't exist
                ArrayList<String> items = new ArrayList<>();
                items.add(item);
                orderDetails.put(orderNo,items);
            }
        }
        return orderDetails;
    }


    /**
     * @description this is a test function to test the data written in database
     * @return result set of 'deliveries'
     */
    public ResultSet readDeliveries(){
        // get connection from the dbconnector
        Connection conn = dbconnector.getConn();
        // define the query string for the orderdetails
        final String orderQuery = "select * from deliveries ";
        PreparedStatement psOrdersQuery = null;
        try {
            //get the ps through the connector
            psOrdersQuery = conn.prepareStatement(orderQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ResultSet rs = null;
        try {
            // get the result set
            rs = psOrdersQuery.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * @description this is a test function to test the data written in database
     * @param rs result set of the data of deliveries
     * @return an array list of deliveries
     */
    public ArrayList<Deliveries> getDeliveriesList(ResultSet rs) {
        // initialize a list to store the data
        ArrayList<Deliveries> orderRecordList = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String orderNo = null;
            String deliverTo = null;
            int pence =0;
            try {
                // get string values from the result set and store them
                orderNo = rs.getString("orderNo");
                deliverTo = rs.getString("deliveredTo");
                pence = rs.getInt("costInPence");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            // new an orderRecord that stores the data got from result set
            orderRecordList.add(new Deliveries(orderNo,deliverTo,pence));
        }
        return orderRecordList;
    }


    //test flightpath
    /**
     * @description this is a test function to test the data written in database
     * @return result set of 'flightpaths'
     */
    public ResultSet readFlightPaths(){
        // get connection from the dbconnector
        Connection conn = dbconnector.getConn();
        // define the query string for the orderdetails
        final String orderQuery = "select * from flightpath ";
        PreparedStatement psOrdersQuery = null;
        try {
            //get the ps through the connector
            psOrdersQuery = conn.prepareStatement(orderQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ResultSet rs = null;
        try {
            // get the result set
            rs = psOrdersQuery.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * @description this is a test function to test the data written in database
     * @param rs result set of the data of flightpaths
     * @return an array list of flightpaths
     */
    public ArrayList<FlightPath> getFlightPathList(ResultSet rs) {
        // initialize a list to store the data
        ArrayList<FlightPath> orderRecordList = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String orderNo = null;
            Double fromLongitude = 0.0;
            Double fromLatitude = 0.0;
            int angle = 0;
            Double toLongitude = 0.0;
            Double toLatitude = 0.0;
            try {
                // get string values from the result set and store them
                orderNo = rs.getString("orderNo");
                fromLongitude = rs.getDouble("fromLongitude");
                fromLatitude = rs.getDouble("fromLatitude");
                toLongitude = rs.getDouble("toLongitude");
                toLatitude = rs.getDouble("toLatitude");
                angle = rs.getInt("angle");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            // new an orderRecord that stores the data got from result set
            orderRecordList.add(new FlightPath(orderNo,fromLongitude,fromLatitude,angle,toLongitude,toLatitude));
        }
        return orderRecordList;
    }
}
