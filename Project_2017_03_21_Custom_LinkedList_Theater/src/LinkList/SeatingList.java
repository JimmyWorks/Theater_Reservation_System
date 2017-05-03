/*
 * Project: GotG2 Ticket Reservation System (Maintenance Project 2)
 * Author: Jimmy Nguyen
 * Contact me: Jimmy@JimmyWorks.net
 */
package LinkList;


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
    
    public void addSeat(DoubleLinkSeat newNode){
        if(head == null){
        head = newNode;
        tail = head;
        }
        else if(newNode.row <= head.row && newNode.col < head.col){

            newNode.next = head;
            newNode.next.prev = newNode;
            head = newNode;   
        }
        else{   
            DoubleLinkSeat current = head;

            while(current.next != null && current.next.row < newNode.row)
                current = current.next;

            if(current.next == null){
                current.next = newNode;
                newNode.prev = current;
                tail = newNode;
            }
            else if(current.next.row > newNode.row){
                newNode.next = current.next;
                current.next.prev = newNode;
                newNode.prev = current;
                current.next = newNode;
            }
            else{ //they are equal row
                int x = current.next.row;  //save the row number

                while(current.next != null && current.next.row == x && current.next.col < newNode.col)
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
            while(current.row != row && current.next != null){
                current = current.next;
                //For debug:
                //System.out.printf("The current node has row %d and col %d\n", current.getRow(), current.getCol());
            }
            while(current.row == row && current.col != col && current.next != null){
                current = current.next;
                //For debug:
                //System.out.printf("The current node has row %d and col %d\n", current.getRow(), current.getCol());
            }
            if(current.row == row && current.col == col)
                return current;
        }
        return null;
    }
    
    public DoubleLinkSeat closestMatch(int row, int col){
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
