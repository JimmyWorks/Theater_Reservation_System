
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================

package Enum;


public enum Price{
    ADULT(10.0),
    SENIOR(7.5),
    CHILD(5.25);

    private final double rate;

    Price(double rate){
        this.rate = rate;
    }

    public double getRate(){
        return rate;
    }

}
