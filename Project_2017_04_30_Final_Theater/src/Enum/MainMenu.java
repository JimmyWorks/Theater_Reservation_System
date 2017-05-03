
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================

package Enum;



public enum MainMenu{
    ERROR ("Error"), //For debugging purposes and to offset by 1
    AUDITORIUM_1("Auditorium 1"),
    AUDITORIUM_2("Auditorium 2"),
    AUDITORIUM_3("Auditorium 3"),
    TOTAL("Total");

    private final String displayName;

    MainMenu(String displayName){
        this.displayName = displayName;
    }
    
    public String getName(){
        return displayName;
    }
}

