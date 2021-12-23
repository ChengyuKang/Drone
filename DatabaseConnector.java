package uk.ac.ed.inf.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author KangCY
 * @Description  This is the database connector, it takes a machine name and a database port
 *               to get connection to the database
 * @create 2021-11-30 21:24
 */
public class DatabaseConnector {
    private String machine;
    private String port;

    // constructor
    public DatabaseConnector(String machine, String port) {
        this.machine = machine;
        this.port = port;
    }

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

    /**
     * @name getConn()
     * @description Create a Connection object that we can use for running various
     *              SQL statement commands against the database
     * @return return connection with the database
     * @throws SQLException If the jdbc string is not available or can't connect to the database
     */
    public Connection getConn(){
        String jdbcString = "jdbc:derby://"+machine+":"+port+"/derbyDB";
        Connection conn = null;
        {
            try {
                conn = DriverManager.getConnection(jdbcString);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return conn;
    }


    /**
     * @name getStatement()
     * @description Create a statement object that we can use for running various
     *              SQL statement commands against the database
     * @return return statement with the database
     * @throws SQLException If the jdbc string is not available or can't connect to the database
     */
    public Statement getStatement(){
        // Create a statement object that we can use for running various
        // SQL statement commands against the database.
        String jdbcString = "jdbc:derby://"+machine+":"+port+"/derbyDB";
        Statement statement = null;
        {
            try {
                Connection conn = this.getConn();
                statement = conn.createStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return statement;
    }
    
}
