package uk.ac.ed.inf.beans;

/**
 * @author KangCY
 * @Description the class Order is an object that is created to store the data from
 *              the OrderRecord, and also it have some variables that records the order's
 *              start and end location, it also stores the moves the drone needs to make in
 *              the is order. It's the key object in this whole project.
 * @create 2021-11-30 20:45
 */
public class Order {
    private int deliveryCost;           // the reward of this order
    private LongLat startLocation;      // the start of the order,  the shop's location
    private LongLat midLocation;        // the mid point of the route, if there are two shops in this order
    private LongLat endLocation;        // the end of the order, where the customer is
    private String orderNo;             // the order number
    private int backMoves;              // back route to Appleton Tower from the destination of the order
    private int deliveryMoves;          // route the drone need to complete this delivery
    private int totalMoves;             // the total moves of a order from start going to shop to deliver the food to the student
    private String deliveredTo;         // the what3words destination

    public LongLat getMidLocation() {
        return midLocation;
    }

    public void setMidLocation(LongLat midLocation) {
        this.midLocation = midLocation;
    }

    public String getDeliveredTo() {
        return deliveredTo;
    }

    public void setDeliveredTo(String deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }

    @Override
    public String toString() {
        return "Order{" +
                "deliveryCost=" + deliveryCost +
                ", startLocation=" + startLocation +
                ", midLocation=" + midLocation +
                ", endLocation=" + endLocation +
                ", orderNo='" + orderNo + '\'' +
                ", backMoves=" + backMoves +
                ", deliveryMoves=" + deliveryMoves +
                ", totalMoves=" + totalMoves +
                ", deliveredTo='" + deliveredTo + '\'' +
                '}';
    }

    public Order() {
    }

    public int getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(int deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public LongLat getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LongLat startLocation) {
        this.startLocation = startLocation;
    }

    public LongLat getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LongLat endLocation) {
        this.endLocation = endLocation;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getBackMoves() {
        return backMoves;
    }

    public void setBackMoves(int backMoves) {
        this.backMoves = backMoves;
    }

    public int getDeliveryMoves() {
        return deliveryMoves;
    }

    public void setDeliveryMoves(int deliveryMoves) {
        this.deliveryMoves = deliveryMoves;
    }
}
