
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================

package Theater;

import Enum.Price;


public class DoubleLinkSeat{
    private int row;
    private int col;
    private Price ticket;   
    
    public DoubleLinkSeat next;
    public DoubleLinkSeat prev;
    
    
    DoubleLinkSeat(int row, int col){
        setRow(row);
        setCol(col);
        ticket = null;
        next = null;
        prev = null;
    }
    
    DoubleLinkSeat(int row, int col, Price type){
        setRow(row);
        setCol(col);
        ticket = type;
        next = null;
        prev = null;
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
    
    public void setTicket(Price type){
        ticket = type;
    }
    
    public Price getTicket(){
        return ticket;
    }
}   

