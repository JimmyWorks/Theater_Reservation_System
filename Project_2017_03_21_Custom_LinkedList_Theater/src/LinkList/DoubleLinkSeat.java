/*
 * Project: GotG2 Ticket Reservation System (Maintenance Project 2)
 * Author: Jimmy Nguyen
 * Contact me: Jimmy@JimmyWorks.net
 */
package LinkList;

/**
 *
 * @author Jimmy
 */
public class DoubleLinkSeat extends BaseSeat {
    public DoubleLinkSeat next;
    public DoubleLinkSeat prev;
    
    
    DoubleLinkSeat(int row, int col){
        super(row, col);
        next = null;
        prev = null;
    }
    
    
}
