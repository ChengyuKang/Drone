package uk.ac.ed.inf.beans.dbTestBeans;

/**
 * @author KangCY
 * @Description
 * @create 2021-12-08 21:44
 */
public class FlightPath {
    private String orderNo;
    private Double fromLongitude;
    private Double fromLatitude;
    private int angle;
    private Double toLongitude;
    private Double toLatitude;

    @Override
    public String toString() {
        return "FlightPath{" +
                "orderNo='" + orderNo + '\'' +
                ", fromLongitude=" + fromLongitude +
                ", fromLatitude=" + fromLatitude +
                ", angle=" + angle +
                ", toLongitude=" + toLongitude +
                ", toLatitude=" + toLatitude +
                '}';
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(Double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public Double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(Double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public Double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(Double toLongitude) {
        this.toLongitude = toLongitude;
    }

    public Double getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(Double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public FlightPath(String orderNo, Double fromLongitude, Double fromLatitude, int angle, Double toLongitude, Double toLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
    }

    public FlightPath() {
    }
}
