package uk.ac.ed.inf.utils;

import uk.ac.ed.inf.beans.Order;
import uk.ac.ed.inf.beans.Pointer;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author KangCY
 * @Description database writer provides a lot of functions that can be used to
 *              write data in several tables the database
 * @create 2021-12-06 15:55
 */
public class DatabaseWriter {
    private DatabaseConnector dbconnector;

    public DatabaseWriter(DatabaseConnector dbconnector) {
        this.dbconnector = dbconnector;
    }

    public DatabaseConnector getDbconnector() {
        return dbconnector;
    }

    public void setDbconnector(DatabaseConnector dbconnector) {
        this.dbconnector = dbconnector;
    }

    /**
     * @description this function create table deliveries and gets an array of Orders,
     *              then write the values into the table Deliveries
     * @param orderArrayList an array of delivered orders
     * @throws SQLException if the sql cammands are illegal
     */
    public void writeInDeliveries(ArrayList<Order> orderArrayList) throws SQLException {
        Connection conn = dbconnector.getConn();
        Statement statement = dbconnector.getStatement();
        DatabaseMetaData databaseMetadata = null;
        databaseMetadata = conn.getMetaData();
        // Note: must capitalise DELIVERIES in the call to getTables
        ResultSet resultSet = null;
        resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
        // If the resultSet is not empty then the table exists, so we can drop it
        if (resultSet.next()) {
            statement.execute("drop table deliveries");
        }
        //create table deliveries
        statement.execute("create table deliveries(orderNo char(8)," +
                "deliveredTo varchar(19)," +
                "costInPence int)");

        // set the preparedStatement for writing in deliveries
        PreparedStatement psDeliveries = conn.prepareStatement(
                "insert into deliveries values (?, ?, ?)");
        //write strings in deliveries
        for (Order order : orderArrayList) {
            psDeliveries.setString(1,order.getOrderNo());
            psDeliveries.setString(2,order.getDeliveredTo());
            psDeliveries.setInt(3,order.getDeliveryCost());
            psDeliveries.execute();
        }
    }

    /**
     * @description create table flightpath and write each coordinate of the flight path
     *              into the database and its previous points and angle
     * @param pointers each pointer is a pointer contains the previous one, angle and orderNo
     * @throws SQLException if the sql cammands are illegal
     */
    public void writeInFlightpath(ArrayList<Pointer> pointers) throws SQLException {
        Connection conn = dbconnector.getConn();
        Statement statement = dbconnector.getStatement();
        DatabaseMetaData databaseMetadata = conn.getMetaData();
        // Note: must capitalise FLIGHTPATH in the call to getTables
        ResultSet resultSet =
                databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
        // If the resultSet is not empty then the table exists, so we can drop it
        if (resultSet.next()) {
            statement.execute("drop table flightpath");
        }

        //create table Flightpath
        statement.execute("create table flightpath(orderNo char(8)," +
                "fromLongitude double," +
                "fromLatitude double," +
                "angle integer," +
                "toLongitude double," +
                "toLatitude double)");

        // set the preparedStatement for writing in deliveries
        PreparedStatement psFlightpath = conn.prepareStatement(
                "insert into flightpath values (?, ?, ?, ?, ?, ?)");
        //write strings in deliveries
        for (Pointer pointer:pointers) {
            psFlightpath.setString(1,pointer.getOrderNo());
            psFlightpath.setDouble(2,pointer.getCoordinate().getLongitude());
            psFlightpath.setDouble(3,pointer.getCoordinate().getLatitude());
            psFlightpath.setInt(4,pointer.getAngle());
            psFlightpath.setDouble(5,pointer.getCoordinate().getLongitude());
            psFlightpath.setDouble(6,pointer.getCoordinate().getLatitude());
            psFlightpath.execute();
        }
    }

}
