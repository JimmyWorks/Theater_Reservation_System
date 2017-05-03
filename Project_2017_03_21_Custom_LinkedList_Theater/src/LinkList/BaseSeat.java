/*
 * Project: GotG2 Ticket Reservation System (Maintenance Project 2)
 * Author: Jimmy Nguyen 
 * Contact me: Jimmy@JimmyWorks.net
 */
package LinkList;

// Seat nodes for the linked-list object: SeatingList
abstract public class BaseSeat {
    protected int row;
    protected int col;
    
    BaseSeat(int row, int col){
        setRow(row);
        setCol(col);
    }
    
    public int getRow (){
        return row;
    }
    
    public int getCol (){
        return col;
    }
    
    public void setRow (int row){
        this.row = row;
    }
    
    public void setCol (int col){
        this.col = col;
    }
    
}
