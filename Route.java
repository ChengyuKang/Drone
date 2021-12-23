package uk.ac.ed.inf.beans;

/**
 * @author KangCY
 * @Description Route is an object that stores how many moves it needs, the order it relates to and
 *              the last pointer of the route which can be traced back to find the whole route.
 * @create 2021-12-01 16:56
 */
public class Route {
    private int moves;                          // how many moves it take to go to the destination
    private Pointer lastPointer;                // the last pointer of the whole journey of the drone
    private Order order;                        // the relating order of this route

    @Override
    public String toString() {
        return "Route{" +
                "moves=" + moves +
                ", lastPointer=" + lastPointer +
                ", order=" + order +
                '}';
    }

    public Route() {
    }

    public Route(int moves, Pointer lastPointer) {
        this.moves = moves;
        this.lastPointer = lastPointer;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Pointer getLastPointer() {
        return lastPointer;
    }

    public void setLastPointer(Pointer lastPointer) {
        this.lastPointer = lastPointer;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

}
