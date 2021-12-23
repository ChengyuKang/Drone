package uk.ac.ed.inf.beans;

import java.util.ArrayList;

/**
 * @author KangCY
 * @Description OrderRecord is a receiver object for order record data in the database
 * @create 2021-11-30 21:09
 */
public class OrderRecord {
    private String orderNo;
    private String deliveryDate;
    private String customer;
    private String destination;
    private ArrayList<String> item;

    //constructor
    public OrderRecord() {
    }
    public OrderRecord(String orderNo, String deliveryDate,  String destination) {
        this.orderNo = orderNo;
        this.deliveryDate = deliveryDate;
        this.destination = destination;
    }

    // getter and setter


    public ArrayList<String> getItem() {
        return item;
    }

    public void setItem(ArrayList<String> item) {
        this.item = item;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "OrderRecord{" +
                "orderNo='" + orderNo + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", customer='" + customer + '\'' +
                ", destination='" + destination + '\'' +
                ", item='" + item + '\'' +
                '}';
    }

}
