
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================

package User;

import Theater.*;


public class OrderList {
    Order head;
    Order tail;
    private int totalOrders;
    
    public OrderList(){
        head = null;
        tail = null;
        totalOrders = 0;
    }
    
    public Order getFirst(){
        return head;
    }
    
    public Order getLast(){
        return tail;
    }
    
    public int getTotalOrders(){
        return totalOrders;
    }
    
    
    public void addOrder(Auditorium room, SeatingList reserved){
        Order newNode = new Order(room, reserved);
        addOrder(newNode);
    }
    
    public void addOrder(Order newNode){
        if(newNode != null){
            if(head == null){
            head = newNode;
            tail = head;
            }
            else{   
                Order current = head;

                while(current.next != null)
                    current = current.next;


                current.next = newNode;
                newNode.prev = current;
                tail = newNode;
            }
            totalOrders++;
        }
    }
    
    public void deleteOrder(int orderNumber){
        Order cur = head;
        
        if(cur != null){
            for(int i = 1; i < orderNumber; i++){
                cur = cur.next;
            }
            if(cur != null){
                if(cur==head)
                    head = cur.next;
                if(cur==tail)
                    tail = cur.prev;
                if(cur.next != null)
                    cur.next.prev = cur.prev;
                if(cur.prev != null)
                    cur.prev.next = cur.next;
                
                totalOrders--; 
            }
            else
                System.out.println("Order not found.");
        }
        else
            System.out.println("Order not found.");
    }
    
    public void deleteOrder(Order order){
        Order cur = head;
        
        if(order != null && cur != null){
            while(cur != null && cur != order){
                cur = cur.next;
            }
            if(cur == order){
                if(cur==head)
                    head = cur.next;
                if(cur==tail)
                    tail = cur.prev;
                if(cur.next != null)
                    cur.next.prev = cur.prev;
                if(cur.prev != null)
                    cur.prev.next = cur.next;
                
                totalOrders--; 
            }
            else
                System.out.println("Order not found");
        }
        else
            System.out.println("Order not found.");
    }
    
    public Order removeOrder(int orderNumber){
        Order cur = head;
        
        if(cur != null){
            for(int i = 1; i < orderNumber; i++){
                cur = cur.next;
            }
            if(cur != null){
                if(cur==head)
                    head = cur.next;
                if(cur==tail)
                    tail = cur.prev;
                if(cur.next != null)
                    cur.next.prev = cur.prev;
                if(cur.prev != null)
                    cur.prev.next = cur.next;
                
                totalOrders--;
                cur.next = null;
                cur.prev = null;
                return cur;
            }
            else{
                System.out.println("Order not found.");
                return null; 
            }
                
        }
        System.out.println("Order not found.");
        return null;
    }
    
    public Order findSeat(int orderNumber){
        Order cur = head;
        
        if(cur != null){
            for(int i = 1; i < orderNumber; i++){
                cur = cur.next;
            }

            return cur;
        }
        System.out.println("No orders found for this user.");
        return null;
    }  
    
}
