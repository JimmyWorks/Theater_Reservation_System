/*
 * Project: GotG2 Ticket Reservation System (Maintenance Project 2)
 * Author: Jimmy Nguyen
 * Contact me: Jimmy@JimmyWorks.net
 */
package LinkList;


public class Theater {
    
    protected Auditorium head;
    protected Auditorium tail;
    
    private int number_of_auditoriums;
    
    public Theater(){

        head = null;
        tail = null;
        number_of_auditoriums = 0;
    }

    public void addAuditorium(Auditorium newNode){
        if(head == null){
            head = newNode;
            tail = head;
        }
        else if(newNode.ID < head.ID){

            newNode.next = head;
            newNode.next.prev = newNode;
            head = newNode;   
        }
        else{   
            Auditorium current = head;

            while(current.next != null && current.next.ID < newNode.ID)
                current = current.next;
            
            if(current.next != null)
                current.next.prev = newNode;
            else
                tail = newNode;
            newNode.next = current.next;
            newNode.prev = current;
            current.next = newNode;    
            }
        number_of_auditoriums++;

        }

    public void addAuditorium(int ID){
        if(head == null){
            head = new Auditorium(ID);
            tail = head;
        }
        else if(ID < head.ID){
            Auditorium newNode = new Auditorium(ID);
            newNode.next = head;
            newNode.next.prev = newNode;
            head = newNode;   
        }
        else{   
            Auditorium newNode = new Auditorium(ID);
            Auditorium current = head;

            while(current.next != null && current.next.ID < ID)
                current = current.next;
            
            if(current.next != null)
                current.next.prev = newNode;
            else
                tail = newNode;
            newNode.next = current.next;
            newNode.prev = current;
            current.next = newNode;    
            }
        number_of_auditoriums++;
    }
    
    public Auditorium getAuditorium(int ID){
        Auditorium current = head;
        
        if(current != null){
            while(current.ID != ID && current.next != null)
                current = current.next;

            if(current.ID == ID)
                return current;
        }
        return null;
    }
    
    public int getNumberOfAuditoriums(){
        return number_of_auditoriums;
    }
    public void printAllSeats(){
        Auditorium current = head;
        
        while(current != null){
            System.out.printf("Reserved Seats for Auditorium %d\n", current.ID);
            current.reserved.printList();
            System.out.printf("Available Seats for Auditorium %d\n", current.ID);
            current.available.printList();
            current = current.next;
        }
    }
} 
