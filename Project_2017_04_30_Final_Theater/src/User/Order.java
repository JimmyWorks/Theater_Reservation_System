
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================

package User;

import Theater.*;


public class Order {
    private Auditorium room;
    public SeatingList reserved;
    
    public Order next;
    public Order prev;

    protected Order(Auditorium room, SeatingList reserved){
        this.room = room;
        this.reserved = reserved;
        next = null;
        prev = null;
    }
    protected Order(Auditorium room) {
        this.room = room;
        reserved = new SeatingList();
        next = null;
        prev = null;
    }

    public Auditorium getAuditorium() {
        return room;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.room = auditorium;
    }

    public SeatingList getReserved() {
        return reserved;
    }

    public void setReserved(SeatingList reserved) {
        this.reserved = reserved;
    }

}
