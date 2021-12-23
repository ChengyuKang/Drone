package uk.ac.ed.inf.controllers;

import uk.ac.ed.inf.beans.LongLat;
import uk.ac.ed.inf.beans.Order;
import uk.ac.ed.inf.beans.OrderRecord;
import uk.ac.ed.inf.beans.Route;
import uk.ac.ed.inf.parsers.Menus;
import uk.ac.ed.inf.parsers.NoFlyZone;
import uk.ac.ed.inf.parsers.Words;

import java.util.ArrayList;

/**
 * @author KangCY
 * @Description the order operator have some functions that can operate orders ,
 *              like sorting, removing and also it can transfer OrderRecord to Order
 * @create 2021-12-03 19:46
 */
public class OrderOperator {

    private Menus menus;
    private Words words;
    private NoFlyZone noFlyZone;
    private RouteCalculator routeCalculator;

    public OrderOperator(Menus menus, Words words, NoFlyZone noFlyZone, RouteCalculator routeCalculator) {
        this.menus = menus;
        this.words = words;
        this.noFlyZone = noFlyZone;
        this.routeCalculator = routeCalculator;
    }

    /**
     * @name transferToOrder(OrderRecord or, LongLat apt)
     * @description this function transfer an OrderRecord to Order, if this order only needs to
     *              get items from one shop, one Order object will be created. Otherwise it needs
     *              to pick up food from two shops, then two Orders will be created and they share
     *              the same orderNo, but their start locations are different.
     * @param or the orderRecord that needs to be transferred to Order
     * @return no fly zones as a feature collection
     */
    public ArrayList<Order> transferToOrder(OrderRecord or, LongLat apt){
        ArrayList<Order> orderList = new ArrayList<>();
        // if this order contains all items from one shop
        if (menus.getShops(or.getItem()).size()==1){
            Order order = new Order();

            LongLat customerCoordinate = words.getCoordinates(or.getDestination());
            // get shop's what3word string and parse it to coordinates
            String shop = menus.getShops(or.getItem()).get(0);
            LongLat shopCoordinate = words.getCoordinates(shop);
            // add the values to the Order
            order.setEndLocation(customerCoordinate);
            order.setStartLocation(shopCoordinate);
            order.setOrderNo(or.getOrderNo());
            order.setDeliveryCost(menus.getDeliveryCost(or.getItem()));
            order.setDeliveredTo(or.getDestination());

            orderList.add(order);
        }else{      // if this order contains items from two different shops
            String shop1 = menus.getShops(or.getItem()).get(0);
            String shop2 = menus.getShops(or.getItem()).get(1);
            LongLat shopCoordinate1 = words.getCoordinates(shop1);
            LongLat shopCoordinate2 = words.getCoordinates(shop2);
            Order order1 = new Order();
            Order order2 = new Order();
            LongLat customerCoordinate = words.getCoordinates(or.getDestination());
            //add the values to the Order
            order1.setEndLocation(customerCoordinate);
            order1.setOrderNo(or.getOrderNo());
            order1.setDeliveryCost(menus.getDeliveryCost(or.getItem()));
            order1.setDeliveredTo(or.getDestination());
            order2.setEndLocation(customerCoordinate);
            order2.setOrderNo(or.getOrderNo());
            order2.setDeliveryCost(menus.getDeliveryCost(or.getItem()));
            order2.setDeliveredTo(or.getDestination());

            //set different start locations for 2-shop order
            order1.setStartLocation(shopCoordinate1);
            order2.setStartLocation(shopCoordinate2);

            // set mid points for 2-shop order
            order1.setMidLocation(shopCoordinate2);
            order2.setMidLocation(shopCoordinate1);

            orderList.add(order1);
            orderList.add(order2);
        }
        return orderList;
    }


    /**
     * @name getNextOrder(ArrayList<Order> orders,LongLat currLoc,int step)
     * @description this function sorts the orders and find the one with highest
     *              sampled average percentage monetary value.
     * @param orders the orders haven't been delivered and waiting to be sorted
     * @return the best Route of the order with highest sampled average percentage monetary value
     */
    public Route getNextOrder(ArrayList<Order> orders, LongLat currLoc, int step){
        // this is the location of the appleton tower
        LongLat apt = new LongLat(-3.186874, 55.944494);
        // recored the current location
        LongLat current = currLoc;
        Route resRoute = null;
        Double maxValue = 0.0;
        //iterate through all orders waiting to be selected
        for (Order order : orders) {
            // get the cost of the order
            int money = order.getDeliveryCost();
            // calculate the route from current location to next shop
            Route shopRoute = routeCalculator.getRouteMove(current, order.getStartLocation(), noFlyZone.getNoFlyZone());
            current = shopRoute.getLastPointer().getCoordinate();        // refresh the new current location
            Route customerRoute;
            int shop1toshop2 = 0;
            if (order.getMidLocation()==null){
                // calculate the route from shop to customer
                customerRoute = routeCalculator.getRouteMove(current,order.getEndLocation(),noFlyZone.getNoFlyZone());
                current = customerRoute.getLastPointer().getCoordinate();    // refresh the new current location
            }else {
                // calculate the route from shop1 to shop2 , from shop2 to customer
                Route toShop2Route = routeCalculator.getRouteMove(current,order.getMidLocation(),noFlyZone.getNoFlyZone());
                shop1toshop2 = toShop2Route.getMoves();
                current = toShop2Route.getLastPointer().getCoordinate();    // refresh the new current location
                customerRoute = routeCalculator.getRouteMove(current,order.getEndLocation(),noFlyZone.getNoFlyZone());
                current = customerRoute.getLastPointer().getCoordinate();    // refresh the new current location
            }
            // get the moves to shop and to the customer
            int toShopMoves = shopRoute.getMoves();
            int toCustomerMoves = customerRoute.getMoves()+shop1toshop2;
            // calculate the back moves
            Route backRoute = routeCalculator.getRouteMove(current,apt,noFlyZone.getNoFlyZone());
            // if it can't fly back, this order won't be considered(the flyback don't need hover,subtract one)
            if (step<(toCustomerMoves+toShopMoves+backRoute.getMoves()-1)){
                continue;
            }
            // value is the average money get per move of the drone, also called sampled average percentage monetary value
            Double value = money / (double) (toCustomerMoves + toShopMoves);
            // select the order with the highest sampled average percentage monetary value
            // reset the resRoute if a new better order appears
            if (value > maxValue){
                order.setTotalMoves(toCustomerMoves + toShopMoves);
                order.setBackMoves(backRoute.getMoves());
                order.setDeliveryMoves(toCustomerMoves);
                shopRoute.setOrder(order);
                resRoute = shopRoute;
                maxValue = value;
            }
        }
        return resRoute;
    }

    /**
     * @name removeOrders(ArrayList<Order> orderArrayList,Order order)
     * @description this function removes the target order from the arraylist with the
     *              same order number.
     * @param orderArrayList the orders haven't been delivered and waiting to be sorted
     * @param order the order that is being delivered
     * @return the waiting list of all unpicked orders, and the target order has been removed
     */
    public ArrayList<Order> removeOrders(ArrayList<Order> orderArrayList,Order order){
        //get the order number of the current order
        String currOrderNo = order.getOrderNo();
        // find the orders that share the same order number with the current one
        ArrayList<Order> removeOrders = new ArrayList<>();
        for (Order orderx : orderArrayList) {
            if (orderx.getOrderNo().equals(currOrderNo)){
                removeOrders.add(orderx);
            }
        }
        // remove those orders from the list
        for (Order removeOrder : removeOrders) {
            orderArrayList.remove(removeOrder);
        }
        return  orderArrayList;
    }
}
