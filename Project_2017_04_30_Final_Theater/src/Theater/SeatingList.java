
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================

package Theater;

import Enum.*;


public class SeatingList {
    DoubleLinkSeat head;
    DoubleLinkSeat tail;
    private int totalSeats;
    
    public SeatingList(){
        head = null;
        tail = null;
        totalSeats = 0;
    }
    
    public DoubleLinkSeat getFirst(){
        return head;
    }
    
    public DoubleLinkSeat getLast(){
        return tail;
    }
    
    public int getTotalSeats(){
        return totalSeats;
    }
    
    public void addSeat(int row, int col){
        DoubleLinkSeat newNode = new DoubleLinkSeat(row, col);
        addSeat(newNode);
    }
    
    public void addSeat(int row, int col, Price type){
        DoubleLinkSeat newNode = new DoubleLinkSeat(row, col, type);
        addSeat(newNode);
    }
    
    public void addSeat(DoubleLinkSeat newNode){
        if(head == null){
        head = newNode;
        tail = head;
        }
        else if(newNode.getRow() <= head.getRow() && newNode.getCol() < head.getCol()){

            newNode.next = head;
            newNode.next.prev = newNode;
            head = newNode;   
        }
        else{   
            DoubleLinkSeat current = head;

            while(current.next != null && current.next.getRow() < newNode.getRow())
                current = current.next;

            if(current.next == null){
                current.next = newNode;
                newNode.prev = current;
                tail = newNode;
            }
            else if(current.next.getRow() > newNode.getRow()){
                newNode.next = current.next;
                current.next.prev = newNode;
                newNode.prev = current;
                current.next = newNode;
            }
            else{ //they are equal row
                int x = current.next.getRow();  //save the row number

                while(current.next != null && current.next.getRow() == x && current.next.getCol() < newNode.getCol())
                    current = current.next;

                if(current.next == null){
                    current.next = newNode;
                    newNode.prev = current;
                    tail = newNode;
                }
                else{ //else, the next node is in the next row or higher column
                    newNode.next = current.next;
                    current.next.prev = newNode;
                    newNode.prev = current;
                    current.next = newNode;
                }
                  
            }
        }
        totalSeats++;
    }
    
    public void deleteSeat(int row, int col){
        DoubleLinkSeat deleteMe = findSeat(row, col);
        
        if(deleteMe == null)
            return;
        else if(deleteMe == head){
            head = deleteMe.next;
            if(head == null){
                tail = null;
            }
            else
                head.prev = null;
        }
        else{
            if(deleteMe.next == null)
                tail = deleteMe.prev;
            else
                deleteMe.next.prev = deleteMe.prev;
            deleteMe.prev.next = deleteMe.next;
        }
        totalSeats--;
    }
    
    public void deleteSeat(DoubleLinkSeat deleteMe){
        if(deleteMe != null)
            deleteSeat(deleteMe.getRow(), deleteMe.getCol());
            
    }
    
    public DoubleLinkSeat removeSeat(int row, int col){
        DoubleLinkSeat deleteMe = findSeat(row, col);
        
        if(deleteMe == null){
            return null;
        }
        else if(deleteMe == head){
            head = deleteMe.next;
            if(head == null){
                tail = null;
            }
            else
                head.prev = null;
            
            totalSeats--;
            deleteMe.next = null;
            deleteMe.prev = null;
            return deleteMe;
        }
        else{
            if(deleteMe.next == null)
                tail = deleteMe.prev;
            else
                deleteMe.next.prev = deleteMe.prev;
            
            deleteMe.prev.next = deleteMe.next;
            
            totalSeats--;
            deleteMe.next = null;
            deleteMe.prev = null;
            return deleteMe;
        }
    }
    
    public DoubleLinkSeat removeSeat(DoubleLinkSeat removeMe){
        return removeSeat(removeMe.getRow(), removeMe.getCol());
    }
    
    
    
    public void printList(){
        DoubleLinkSeat current = head;
        
        while(current != null){
            System.out.printf("Seat - row %d, seat %d\n", current.getRow(), current.getCol());
            current = current.next;
        }
        System.out.println("\n");
    }
    
    public DoubleLinkSeat findSeat(int row, int col){
        DoubleLinkSeat current = head;
        
        if(current != null){
            //For debug:
            //System.out.printf("The head node has row %d and col %d\n", current.getRow(), current.getCol());
            while(current.getRow() != row && current.next != null){
                current = current.next;
                //For debug:
                //System.out.printf("The current node has row %d and col %d\n", current.getRow(), current.getCol());
            }
            while(current.getRow() == row && current.getCol() != col && current.next != null){
                current = current.next;
                //For debug:
                //System.out.printf("The current node has row %d and col %d\n", current.getRow(), current.getCol());
            }
            if(current.getRow() == row && current.getCol() == col)
                return current;
        }
        return null;
    }
    
    public DoubleLinkSeat bestMatch(int row, int col){
        DoubleLinkSeat bestMatch, current;
        
        bestMatch = current = head;
        
        if(bestMatch != null && (bestMatch.getRow() != row || bestMatch.getCol() != col)){
                
                double bestMatchDist, currentDist;
                
                bestMatchDist = Math.sqrt(Math.pow((bestMatch.getRow()-row), 2) + Math.pow((bestMatch.getCol()-col), 2));
                //For debug:
                //System.out.printf("Solution exists!  Current Best Solution: (%d, %d), Best Dist: %f\n", bestMatch.row, bestMatch.col, bestMatchDist);
                
                while(current != null && (bestMatch.getRow() != row || bestMatch.getCol() != col)){
                    
                    currentDist = Math.sqrt(Math.pow((current.getRow()-row), 2) + Math.pow((current.getCol()-col), 2));
                    //For debug:
                    //System.out.printf("Next Comparison:  (%d, %d), Dist: %f\n", current.row, current.col, currentDist);
                    
                    if(currentDist < bestMatchDist){
                        bestMatch = current;
                        bestMatchDist = currentDist;
                        //For debug:
                        //System.out.printf("New Best! (%d, %d), Dist: %")
                    }
                    
                    current = current.next;
                }
        }
        
        return bestMatch;
    }
    
    
}
