/*
 * Project: GotG2 Ticket Reservation System (Maintenance Project 2)
 * Author: Jimmy Nguyen
 * Contact me: Jimmy@JimmyWorks.net
 */
package LinkList;

import java.util.*;
import java.io.*;

public class Auditorium {
    public int ID;
    protected Auditorium next;
    protected Auditorium prev;
    private int max_rows;
    private int max_cols;
    
    public SeatingList reserved;
    public SeatingList available;
    
    
    public Auditorium(int num){
        ID = num;
        next = null;
        prev = null;
        max_rows = 0;
        max_cols = 0;
        
        reserved = new SeatingList();
        available = new SeatingList();
        
    }
    
    public int getTotalRows(){
        return max_rows;
    }
    
    public int getTotalColumns(){
        return max_cols;
    }
    
    public void loadAuditorium(Scanner source){
        String buffer;
        
        for(int i = 0; source.hasNext(); i++){
            buffer = source.nextLine();
            max_rows = i+1;
            max_cols = buffer.length();
            for(int j = 0; j < buffer.length(); j++){
                char x = buffer.charAt(j);
                DoubleLinkSeat newSeat = new DoubleLinkSeat(i+1, j+1);
                
                if(x == '#'){
                    available.addSeat(newSeat);
                }
                else //for '.' or any invalid character, safer to assume seat is reserved
                    reserved.addSeat(newSeat);
            }
        }  
    }
    
    public void printMap(){
        
        DoubleLinkSeat currentRes = reserved.head;
        DoubleLinkSeat currentAvail = available.head;
        int currentRow;
        Boolean errorDetected = false;
        
        if(currentRes == null && currentAvail == null){
            System.out.println("No map loaded.\n");
            return;
        }
        else if(currentRes == null){
            currentRow = currentAvail.row;
        }
        else if(currentAvail == null){
            currentRow = currentRes.row;
        }
        else{
            if(currentRes.row < currentAvail.row)
                currentRow = currentRes.row;
            else
                currentRow = currentAvail.row;
        }
        
        System.out.printf("Auditorium map:\n  ");
       
        for(int i = 0; i < max_cols; i++) //for each row, i
        {   //print the modulus of the counting number for that seat, values 0-9
            System.out.printf("%d", (i+1)%10);
        }
        
        System.out.print("\n1 "); //newline
        while(currentRes != null || currentAvail != null){
            if(currentRes == null){
                if(currentAvail.row != currentRow){
                    currentRow = currentAvail.row;
                    System.out.printf("\n%d ", currentRow%10);
                }
                System.out.print('#');
                currentAvail = currentAvail.next;
            }
            else if(currentAvail == null){
                if(currentRes.row != currentRow){
                    currentRow = currentRes.row;
                    System.out.printf("\n%d ", currentRow%10);
                }
                System.out.print('.');
                currentRes = currentRes.next;
            }
            else if(currentRes.row < currentAvail.row){
                if(currentRes.row != currentRow){
                    currentRow = currentRes.row;
                    System.out.printf("\n%d ", currentRow%10);
                }                
                System.out.print('.');
                currentRes = currentRes.next;
            }
            else if(currentRes.row > currentAvail.row){
                if(currentAvail.row != currentRow){
                    currentRow = currentAvail.row;
                    System.out.printf("\n%d ", currentRow%10);
                }                
                System.out.print('#');
                currentAvail = currentAvail.next;
            }
            else{ //both are in the same row
                if(currentRes.col < currentAvail.col){
                    if(currentRes.row != currentRow){
                        currentRow = currentRes.row;
                        System.out.printf("\n%d ", currentRow%10);
                    }
                    System.out.print('.');
                    currentRes = currentRes.next;
                }
                else if(currentRes.col > currentAvail.col){
                    if(currentAvail.row != currentRow){
                        currentRow = currentAvail.row;
                        System.out.printf("\n%d ", currentRow%10);
                    }                
                    System.out.print('#');
                    currentAvail = currentAvail.next;
                }
                else{
                    errorDetected = true;
                    if(currentAvail.row != currentRow){
                        currentRow = currentAvail.row;
                        System.out.printf("\n%d ", currentRow%10);
                    }
                    System.out.print('.');
                    currentRes = currentRes.next;
                    currentAvail = currentAvail.next;
                }
            }
            
        }
        if(errorDetected)
            System.out.println("Error: Seat detected in both availability and reserved lists.");
        
        System.out.println("\n\nEmpty seats: '#'  Reserved seats: '.'");
    }
    
    public SeatingList generateSolutions(int quantity){
        SeatingList solutions = new SeatingList();
        DoubleLinkSeat current, prospect;
        int counter = 0;
        int currentRow, currentCol;
        
        current = prospect = available.head;
        
        if(current != null){
            currentRow = current.getRow();
            currentCol = current.getCol();
            
            while(current != null){
                if(current.getRow() == currentRow && current.getCol() == currentCol){
                    counter++;
                    currentCol++;
                    current = current.next;
                }
                else{
                    counter = 0;
                    currentRow = current.getRow();
                    currentCol = current.getCol();
                    prospect = current;
                }    
                
                if(counter == quantity){
                    solutions.addSeat(prospect.getRow(), prospect.getCol());
                    prospect = prospect.next;
                    counter--;
                }
                    
            }
        }
        
        return solutions;
    }
    
    public void exportAndDeleteMap(PrintWriter printer){
        
        DoubleLinkSeat currentRes = reserved.head;
        DoubleLinkSeat currentAvail = available.head;
        int currentRow;
        
        //=====  First find the starting current row (expect to find row 1) =====
        if(currentRes == null && currentAvail == null){
            System.out.println("No map loaded for export for Auditorium #" + ID);
            return;
        }
        else if(currentRes == null){
            currentRow = currentAvail.row;
        }
        else if(currentAvail == null){
            currentRow = currentRes.row;
        }
        else{
            if(currentRes.row < currentAvail.row)
                currentRow = currentRes.row;
            else
                currentRow = currentAvail.row;
        }
        
        exportAndDeleteHelper(currentRow, printer);     
    }
    
    public void exportAndDeleteHelper(int currentRow, PrintWriter printer){
        
        Boolean errorDetected = false;
        
        if(reserved.head != null || available.head != null){
            if(reserved.head == null){
                if(available.head.row != currentRow){
                    printer.println();
                    currentRow = available.head.row;
                }
                printer.print('#');
                available.deleteSeat(available.head);
            }
            else if(available.head == null){
                if(reserved.head.row != currentRow){
                    printer.println();
                    currentRow = reserved.head.row;
                }
                printer.print('.');
                reserved.deleteSeat(reserved.head);
            }
            else if(reserved.head.row < available.head.row){
                if(reserved.head.row != currentRow){
                    printer.println();
                    currentRow = reserved.head.row;
                }                
                printer.print('.');
                reserved.deleteSeat(reserved.head);
            }
            else if(reserved.head.row > available.head.row){
                if(available.head.row != currentRow){
                    printer.println();
                    currentRow = available.head.row;
                }                
                printer.print('#');
                available.deleteSeat(available.head);
            }
            else{ //both are in the same row
                if(reserved.head.col < available.head.col){
                    if(reserved.head.row != currentRow){
                        printer.println();
                        currentRow = reserved.head.row;
                    }
                    printer.print('.');
                    reserved.deleteSeat(reserved.head);
                }
                else if(reserved.head.col > available.head.col){
                    if(available.head.row != currentRow){
                        printer.println();
                        currentRow = available.head.row;
                    }                
                    printer.print('#');
                    available.deleteSeat(available.head);
                }
                else{
                    errorDetected = true;
                    if(available.head.row != currentRow){
                        printer.println();
                        currentRow = available.head.row;
                    }
                    printer.print('.');
                    reserved.deleteSeat(reserved.head);
                    available.deleteSeat(available.head);
                }
            }
            exportAndDeleteHelper(currentRow, printer);
        }
        if(errorDetected)
            System.out.println("Error: Seat detected in both availability and reserved lists for map export.");  
    }
    
}
