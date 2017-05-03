
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================

package User;

import Theater.*;
import java.util.*;


public class User {
    private String name;
    private String password;
    public OrderList orders;
    
    public User(String name, String password){
        this.name = name;
        this.password = password;
        orders = new OrderList();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public Order createOrder(Auditorium room){
        Order newOrder = new Order(room);
        orders.addOrder(newOrder);
        return newOrder;
    }
    public void deleteOrder(Order order){
        orders.deleteOrder(order);
    }
    public void deleteOrder(int orderNum){
        orders.deleteOrder(orderNum);
    }

}
