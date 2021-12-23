package uk.ac.ed.inf.beans.dbTestBeans;

/**
 * @author KangCY
 * @Description
 * @create 2021-12-08 21:30
 */
public class Deliveries {
    private String orderNo;
    private String deliverTo;
    private int pence;

    @Override
    public String toString() {
        return "Deliveries{" +
                "orderNo='" + orderNo + '\'' +
                ", deliverTo='" + deliverTo + '\'' +
                ", pence=" + pence +
                '}';
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDeliverTo() {
        return deliverTo;
    }

    public void setDeliverTo(String deliverTo) {
        this.deliverTo = deliverTo;
    }

    public int getPence() {
        return pence;
    }

    public void setPence(int pence) {
        this.pence = pence;
    }

    public Deliveries(String orderNo, String deliverTo, int pence) {
        this.orderNo = orderNo;
        this.deliverTo = deliverTo;
        this.pence = pence;
    }

    public Deliveries() {
    }
}
