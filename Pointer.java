package uk.ac.ed.inf.beans;

/**
 * @author KangCY
 * @Description Pointer as its name, it has its own coordinate, and it also has parent and son pointer as its nodes
 *              Pointers will form a one direction linked list , to store the data during the flight of one route,
 *              including its moves, coordinate and total distance.
 * @create 2021-12-01 17:41
 */
public class Pointer {
    private String orderNo;            // the orderNo of this route
    private Double startMoves;         // moves spent from the start point
    private Double endDis;             // distance to the destination
    private Double totalDis;           // distance altogether
    private Pointer previousPointer;   // this pointer's previous pointer
    private LongLat coordinate;        // this pointer's coordinate
    private int angle;                 // this is angle from the previous pointer

    @Override
    public String toString() {
        return "Pointer{" +
                "orderNo='" + orderNo + '\'' +
                ", startMoves=" + startMoves +
                ", endDis=" + endDis +
                ", totalDis=" + totalDis +
                ", previousPointer=" + previousPointer +
                ", coordinate=" + coordinate +
                ", angle=" + angle +
                '}';
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Pointer getPreviousPointer() {
        return previousPointer;
    }

    public void setPreviousPointer(Pointer previousPointer) {
        this.previousPointer = previousPointer;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public Double getStartMoves() {
        return startMoves;
    }

    public void setStartMoves(Double startMoves) {
        this.startMoves = startMoves;
    }


    public Double getEndDis() {
        return endDis;
    }

    public void setEndDis(Double endDis) {
        this.endDis = endDis;
    }

    public Double getTotalDis() {
        return totalDis;
    }

    public void setTotalDis(Double totalDis) {
        this.totalDis = totalDis;
    }

    public LongLat getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LongLat coordinate) {
        this.coordinate = coordinate;
    }

    public Pointer() {
    }

    /**
     * @description it compares two pointer which is a better route, it compares how much
     *              distance the pointer has already moved and the straight line to the
     *              destination regardless of no fly zones
     * @param previousPointer the pointer which has already been counted by us
     * @return true or false whether this pointer is a better route
     */
    public boolean thisIsBetterRoute(Pointer previousPointer){
        if (this.getTotalDis() < previousPointer.getTotalDis()){
            return true;
        }else {
            return false;
        }
    }
}
