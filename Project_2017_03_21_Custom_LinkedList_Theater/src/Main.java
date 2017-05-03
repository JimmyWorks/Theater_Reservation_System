
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 2)
//
// Author: Jimmy Nguyen
//==============================================================================
//
// Contact me: Jimmy@JimmyWorks.net
//
// Project Details:
// This project is an update to a prior ticket master system developed using
// multi-dimensional arrays to hold each auditorium for the theater and each
// auditorium's 2-D array of seats, designated as either reserved or available.
// This project updates that project by using customized linked-lists and nodes,
// where each theater is a linked-list holding auditorium doubly-linked nodes.
// Each auditorium also contains two linked lists with doubly-linked nodes for 
// the seats, where one list holds the available seats and the other the reserved
// seats for that auditorium, organized by row and column.  This project uploads
// all of the seating charts from a series of source files and creates all of the
// auditoriums and seating lists necessary.  Then, a user interface is presented
// where users can make purchases from any auditorium.  Successful purchases are
// logged, objects are all updated accordingly, and, upon exiting, the final states
// of all objects are exported back to their respective source files.  A final
// executive summary on the number of sales, available and reserved seats is also
// printed to the console.  

// As a bonus, in the event of an unsuccessful purchase, the program will search
// through the entire auditorium and generate a list of alternative solutions.
// The program will then determine from this list the optimal solution closest
// to the center of the auditorium and present this solution to the user.  If the
// user agrees, this purchase will be made.
//==============================================================================

import java.io.*;
import java.util.*;
import LinkList.*;

//  Note: The LinkList package above contains all the custom linked list classes
//  necessary to run this program.  Files include:
//      - Theater.java          -- Double Linked List housing all the auditoriums
//      - Auditorium.java       -- Double Linked node in Theater object  
//                                 Contains linked lists: available and reserved seats
//      - BaseSeat.java         -- Abstract seating node template for seats
//      - DoubleLinkSeat.java   -- Concrete derived double linked seating node
//      - SeatingList.java      -- Double Linked List holding seat nodes
//                                 Used for listing available, reserved, and solution seats

public class Main{
    
//==============================================================================
//    I.                      Global Constants   
//==============================================================================    
//    The strings that hold the filenames that house the reservation maps for each
//    of the auditoriums, the number of filenames in FILENAMES array is very critical
//    to the program, since this is used in numerous locations to expand or contract
//    the number of auditoriums in our theater.  Simply add filenames to expand and
//    update the number of items in enum MainMenu.  The same applies to items in
//    the REPORT_LABELS, although added lines require update to report methods.
//==============================================================================
    static final String[] FILENAMES = {     "A1.txt",
                                            "A2.txt",
                                            "A3.txt"};
    static final String[] REPORT_LABELS = { "Reserved Seats",
                                            "Available Seats",
                                            "Total Sales ($)"};
    static final int PRICE = 7;
    static final String SHOWING = "Guardians of the Galaxy 2";   
    
    public enum MainMenu{
        ERROR ("Error"), //For debugging purposes and to offset by 1
        AUDITORIUM_1("Auditorium 1"),
        AUDITORIUM_2("Auditorium 2"),
        AUDITORIUM_3("Auditorium 3");
        
        private final String displayName;

        MainMenu(String displayName){
        this.displayName = displayName;
        }
    }
    
//==============================================================================    
//    II.                    Program Main
//==============================================================================    
     public static void main(String[] args) throws Exception{

        
//  Declare array of Files using filenames from FILENAMES array
        File[] fileArray = new File[FILENAMES.length];
        for(int i = 0; i < FILENAMES.length; i++){
            fileArray[i] = new File(FILENAMES[i]);
        }
//  Create Theater (only one in this driver) and Scanner for user input        
        Theater myTheater = new Theater();
        Scanner input = new Scanner(System.in);

//  Check every file in file array to make sure it exists and is not a directory
//  If failed, ask user to input the proper filename to correct
        for(int i = 0; i < fileArray.length; i++){
            while(!fileArray[i].exists() || fileArray[i].isDirectory()){ //while loop to locate file
                    System.out.printf("Your file name \"%s\" was not found.", fileArray[i].getName());
                    System.out.println("Please specify the name of your input file:");
                    String filename = input.nextLine();
                    fileArray[i] = new File(filename);
            }
        }
        
//  Create Scanner array holding all the scanners for each file        
        Scanner[] fileScanner = new Scanner[fileArray.length];        
        for(int i = 0; i < fileArray.length; i++){
            fileScanner[i] = new Scanner(fileArray[i]);
        }

//  Using the array of Scanner objects, create all Auditoriums and load all
//  Auditoriums using loadAuditorium method defined in Auditorium class
//  Once done, add the Auditorium to the Theater
        for(int i = 0; i < fileScanner.length; i++){
            Auditorium newAuditorium = new Auditorium(i+1);
            newAuditorium.loadAuditorium(fileScanner[i]);
            myTheater.addAuditorium(newAuditorium);
        }
       
//  Finally, create the array to hold the report items and populate the array
//  with the updateReport method.  This report is constantly updated throughout
//  the life of the program.
        int [][]report = new int[myTheater.getNumberOfAuditoriums()][REPORT_LABELS.length];
        updateReport(myTheater, report);  


//==============================================================================    
//  II-A.                          User Interface
//==============================================================================
                                
        System.out.println("\n\n=========== Welcome to Jimmy’s Movie Theater ===========");
        System.out.println("We look forward to giving you the best movie experience!");

        int selection;          //reuseable variable for storing user input
    
//==============================================================================          
//  This while-loop is where the program will be for the remainder of the program
//  The current movie title showing will display, prices, and the options for
//  where the user can purchase tickets or exit the program.  User input is
//  wrapped in a try block in cases of bad input for exception handling.  For
//  proper type input, logical if, if-else, and switch statements filter for 
//  valid values.
//==============================================================================  

        while(true) //user stays in this loop for entirety of program unless
        {           //unless proper input for exiting or executing sub-routines
            System.out.print("\n\n====================== Main Menu =======================\n\n");
            System.out.printf("Currently showing: %s\n\n", SHOWING);
            System.out.printf("Tickets:            Adult: $%d\n", PRICE);
            System.out.printf("Tickets:            Child: $%d\n", PRICE);
            System.out.printf("Tickets:           Senior: $%d\n\n", PRICE);
            System.out.println("Please select an option:\n");
            System.out.println("1 - Reserve Seats");
            System.out.println("2 - View Auditorium");
            System.out.printf("3 - Exit\n\n\n>> ");   
            
            try //any exception thrown will back the program out to the Main Menu
            {
            selection = input.nextInt();
    
            switch(selection){
                case 1:
                case 2:
                    //  Each auditorium also displays its number of open seating
                    System.out.println("Please select an auditorium:\n");
                    System.out.printf("1 - Auditorium 1 -- Open Seating: %d\n", report[0][1]);
                    System.out.printf("2 - Auditorium 2 -- Open Seating: %d\n", report[1][1]);
                    System.out.printf("3 - Auditorium 3 -- Open Seating: %d\n", report[2][1]);
                    
                    int selectedRoom = input.nextInt();
                    MainMenu options = MainMenu.values()[selectedRoom];
                    
                    //  If the room has no more available seats, do not allow the
                    //  user to purchase tickets or view seating and skip back
                    //  to the main menu.
                    if(report[selectedRoom-1][1] <= 0){
                                System.out.println("\n\nThis auditorium is fully booked out.");
                                System.out.println("Please try a different auditorium.");
                                break;
                    }
                    //  If the selected Auditorium is a valid entry, display the
                    //  name and Auditorium map with the Auditorium's printMap method
                    if(selectedRoom > 0 && selectedRoom <= FILENAMES.length)
                    {
                        System.out.println("\n===================== " + options.displayName + " =====================\n");
                        Auditorium selectedAuditorium = myTheater.getAuditorium(selectedRoom);
                        selectedAuditorium.printMap();
                    //  If the purchase menu was selected, if that Auditorium has
                    //  seats available, then execute sub-routine purchaseTickets
                        if(selection == 1){
                            if(report[selectedRoom-1][1] > 0)
                                purchaseTickets(options, selectedAuditorium, report, input);
                        }
                    }
                    //  If an invalid value was entered for Auditorium selected,
                    //  notify the user before going back to main menu
                    else 
                        System.out.println("\nYour input is invalid, please try again\n\n");
                    break;
                case 3: 
                    //  If user opted to exit the program, close all scanners,
                    //  print the final report summary and call the exportMaps
                    //  method to write all current Auditorium maps to their
                    //  respective output files.  Close all PrintWriters before
                    //  ending the program
                    System.out.println("\nExiting...");
                
                    input.close();
                    for(Scanner i: fileScanner)
                        i.close();
                    //  Print report
                    printReport(report);
                    //  Create array of PrintWriters for exporting maps
                    PrintWriter[] filePrinter = new PrintWriter[FILENAMES.length];
                    for(int i = 0; i < filePrinter.length; i++){
                        filePrinter[i] = new PrintWriter(FILENAMES[i]);
                    }
                    //  Write all maps to respective files
                    exportMaps(myTheater, filePrinter);
                    //  Close all printwriters
                    for(PrintWriter i: filePrinter)
                        i.close();
                    //  End the program    
                    endProgram();
                    break;
                default:    //catch for invalid entries that are not exceptions
                    System.out.println("\nYour input is invalid, please try again\n\n");
            }
    //Exception handling for any exceptions that may be thrown within the main program
    //loop.  Exceptions in sub-routines return user to main menu.
            }catch(ClassCastException cce){
                System.out.println("\nYour input is invalid, please try again\n\n");
            }catch(InputMismatchException ime){
                System.out.println("\nYour input is invalid, please try again\n\n");
            }catch(ArrayIndexOutOfBoundsException aioobe){
                System.out.println("\nYour input is invalid, please try again\n\n");
            }
    //For any errors or exceptions, the program simply asks for a new input and checks
    //the next line of user input to try again.  The program cannot terminate without
    //going through the proper procedures, i.e. the exit selection in Main Menu
            input.nextLine();
        
        }  
     }
//==============================================================================    
//  III.                          METHODS
//==============================================================================

//==============================================================================    
//  III-A.                     Update Report
//==============================================================================     
//  Illustration of the final report summary printed at the end of the program:
//
//                  Reserved    Available   Total Sales($)
//  Auditorium 1    
//  Auditorium 2
//  Auditorium 3
//
//  This method uses the Auditorium's two linked lists, reserved and available, 
//  to populate the report along with the PRICE constant given at the beginning.
//  Both lists use the SeatingList method, getTotalSeats, as defined in the
//  SeatingList class.
//==============================================================================
     
     static void updateReport(Theater theater, int[][] report){
         for(int i = 0; i < theater.getNumberOfAuditoriums(); i++){
             
            report[i][0] = theater.getAuditorium(i+1).reserved.getTotalSeats();
            report[i][1] = theater.getAuditorium(i+1).available.getTotalSeats();
            report[i][2] = report[i][0] * PRICE;
         }
     }
     
//==============================================================================    
//  III-B.                     Update Report Line
//==============================================================================  
//  Taking the Final Report, the index for the auditorium where the transaction
//  was completed and the number of reserved seats for the transaction, update
//  the report to reflect the new number of reserved seats, open seats and sales
//  for that auditorium
//==============================================================================
     
    public static void updateReportLine(int[][]report, int index, int reserve)
    {
        report[index][0] += reserve;    //add reserved seats to total
        report[index][1] -= reserve;    //subtract reserved seats from available
        report[index][2] += reserve*PRICE; //add the sale to the total sales       
    }
//==============================================================================    
//  III-C.                     Print Report
//==============================================================================  

    //Print the Final Report with formating to allow executives quick reference
    //to the total number of sold seats, unsold seats, and total in sales
    public static void printReport(int[][] report)
    {
        String total = "Total Sales"; //string to print the total sales
        
        //Header displaying the Sales report and movie's title
        System.out.println("Sales Report");
        System.out.println("Showing: " + SHOWING);
        System.out.printf("\n\n %20s", "");
        
        //Loop to print the report labels across the top for each column
        for(String i : REPORT_LABELS)
            System.out.printf("%20s", i);
        System.out.println();
        
        //Loop to print each auditorium's numbers
        for(int i = 0; i < FILENAMES.length; i++) //for each auditorium, i
        {       //first, print the name of the auditorium
                System.out.printf("%20s ", MainMenu.values()[i+1].displayName);
            for(int j = 0; j < REPORT_LABELS.length; j++) //for each label, j
                //print all of its values
                System.out.printf("%20d", report[i][j]);
            //newline
            System.out.printf("\n");
        }
        
        //Now that everything in the report is printed, calculate and print
        //the totals for the entire theater and append to the bottom
        int sum; //reusable sum variable
        System.out.printf("%20s ", total);
        for(int i = 0; i < REPORT_LABELS.length; i++) //for each report label
        {   //find its sum in the loop below:
            sum = 0;
            for(int j = 0; j < FILENAMES.length; j++) //for each auditorium, j
                sum += report[j][i]; //loop through and sum the values
            
            System.out.printf("%20d", sum); //print the value across the bottom
        }
        System.out.println();
    }
    
//==============================================================================    
//  III-D.             Export Maps and Delete All Seats
//==============================================================================
//  This method should only be used at the end of the program to export all maps
//  to their respective file.  A recursive call is made within exportMaps' block
//  of code found in the Auditorium's exportAndDeleteMap method.  This recursive
//  call not only exports all the nodes, but also deletes them as it goes through.
//==============================================================================
    
    public static void exportMaps(Theater theater, PrintWriter[] printer){
        for(int i = 0; i < printer.length; i++){
            theater.getAuditorium(i+1).exportAndDeleteMap(printer[i]);
        }
    }
    
//==============================================================================    
//  III-E.                     Purchase Tickets
//==============================================================================  
//  The largest sub-routine in this program.  This method executes after 
//  printing the map of the auditorium.  Data validation is performed on each
//  each step to make sure value is valid within the 
//  bounds.  The function then checks if the seat and adjacent seats are available.
//
//  If all available, the purchase is made.  If not available, the function will
//  check for all possible solutions in the entire auditorium and store all
//  solutions in a new solutions list.  Then, check the list for the best seats
//  to the center of the room, this solution is presented to the user for confirmation
//  if they would like to make the purchase.  If a purchase is made, the program
//  confirms the balance of the transaction, the row and seat numbers purchased,
//  and removes the seats from the available list of seats adding them to the
//  reserved list.  The report summary is updated after each successful purchase.
//==============================================================================
 
    public static void purchaseTickets(MainMenu theater, Auditorium auditorium, int[][] report, Scanner input)
    {
//  Declare all immediate variables needed
        int row,            //selected row
            seat,           //selected seat
            quantity,       //number of tickets/seats
            seatCounter;    //counter for counting seats in various scenarios
        int theater_index = theater.ordinal()-1; //auditorium's array index for easy reading
        row = seat = quantity = 0;              //initializing values to 0
        
//  Prompt the user for a row number.  The following while loop checks for invalid
//  numbers.  Only with a valid number will the function exit the loop.  Row number
//  must be at least 1 and at most the number of rows.
        System.out.print("\n\nPlease enter the row number:\n>> ");
        boolean valid = false;
        while(!valid) //row validation
        {
            row = input.nextInt();
            //row cannot be less than 1 or greater than number of rows
            if(row < 1 || row > auditorium.getTotalRows()) 
            {
                System.out.print("That row does not exist.  Please try again:\n>> ");
                input.nextLine();
            }
            else
                valid = true;
        }
//  Prompt the user for a seat number.  The following while loop checks for invalid
//  numbers.  Only with a valid number will the function exit the loop.  Seat number
//  must be at least 1 and at most the number of seats in that row.
        System.out.println("\n\nPlease enter the seat number (or starting seat number ");
        System.out.print("furthest to the left for multiple adjacent seats):\n>> ");        
        valid = false;
        while(!valid) //seat validation
        {
            seat = input.nextInt();
            //seat cannot be less than 1 or greater than the number of seats in that row
            if(seat < 1 || seat > auditorium.getTotalColumns())
            {
                System.out.print("That seat does not exist.  Please try again:\n>> ");
                input.nextLine();
            }
            else
                valid = true;
        }
       
//  Prompt the user for a number of tickets.  The following while loop checks for invalid
//  numbers.  Only with a valid number will the function exit the loop.  Number
//  must be at least 1 and at most the number of seats in the row.
        System.out.println("\n\nHow many tickets would you like to purchase? ");
        System.out.println("This includes the first seat and all adjacent seats ");
        System.out.println("to the right of the one you have selected ");
        System.out.print("(Enter 1 if only purchasing for yourself)\n>> ");
        
        valid = false;
        while(!valid) //ticket value validation
        {
            quantity = input.nextInt();
            if(quantity == 0)
            {   //if zero, return user to Main Menu     
                System.out.println("\nYou have selected to buy no tickets.");
                System.out.print("Returning to Main Menu...\n\n\n");
                return;
            }
            else if(quantity < 0)
            {   //cannot buy negative tickets
                System.out.println("\nYou must enter a positive number of tickets.");
                System.out.print("Please try again:\n>> ");
                input.nextLine();
            }
            else if(quantity > auditorium.getTotalColumns())
            {   //max number to tickets are the number of seats in a row, if all vacant
                System.out.println("\nThere are not enough seats in the row.");
                System.out.println("Please split purchase into multiple transactions ");
                System.out.print("for parties larger than " + auditorium.getTotalColumns());
                System.out.println("for this auditorium.");  
                System.out.print("For this row, please try again:\n>> ");
                input.nextLine();
            }
            else
                valid = true;
        }
        
//  Now, check each seat in the series starting with the first seat going right
//  If any seat is reserved, break fromm the for-loop and flag the validity of
//  the purchase as false
        //  Initialize the check
        int checkCounter = 0; 
        valid = false;
        DoubleLinkSeat currentSeat = auditorium.available.findSeat(row, seat);
        //  Check until the end the list, the row, or quantity is met
        if(currentSeat != null){
            int nextSeatNum = seat;
            do{
                checkCounter++;
                System.out.printf("Check %d seat\n", checkCounter);
                nextSeatNum++;
                currentSeat = currentSeat.next;
            }while(currentSeat != null && currentSeat.getRow() == row && currentSeat.getCol() == nextSeatNum && checkCounter < quantity);
        }   //  while the current seat is not null, is in the same row and equals the next seat column 
            //  while not equal to the number of reserved seats yet, repeat the check to the next seat
        
//  After the last check, see if quantity is met        
        if(checkCounter == quantity){
            valid = true;
        }

//  Now, if the purchase could not be made, then this next segment of code will
//  attempt to find all of the possible solutions in the entire Auditorium and
//  store it to a list.  The list will then be searched for a best fit which
//  will be presented to the user to decide if they want to make the purchase.
        if(!valid)
        {
            System.out.println("\n\nOne or more of the seats you wanted to book are ");
            System.out.println("not available.\n");
            //  Execute Auditorium's generateSolutions method and save the new
            //  list of possible solutions in SeatingList, solutions
            SeatingList solutions = auditorium.generateSolutions(quantity);
    
            //  If the head is null, no solution was found and execute else statement.
            //  Otherwise, find the center of the Auditorium and find the solution
            //  closest to the center of the Auditorium
            if(solutions.getFirst() != null){
                //  Calculate center of Auditorium
                int centerRow = auditorium.getTotalRows()/2 + auditorium.getTotalRows()%2;
                int centerCol = auditorium.getTotalColumns()/2 + auditorium.getTotalColumns()%2;
                //  Execute SeatingList method, closestMatch, by sending the location
                //  desired, the coordinates for the center of the Auditorium, and 
                //  it will return the seat closest to that location 
                DoubleLinkSeat bestMatch = solutions.closestMatch(centerRow, centerCol);
                //  Assign row and seat to the new best match
                row = bestMatch.getRow();
                seat = bestMatch.getCol();
                //  Ask user if they want to purchase the new best match
                System.out.println("However, we found a solution that meets your criteria:\n");
                if(quantity==1)
                    System.out.println("Row: " + row + ", Seat " + seat);
                else
                    System.out.println("Row: " + row + ", Seats " + seat + " - " + (seat+quantity-1));
                System.out.print("Would you like to accept? Y/N\n>> ");

                
                String acceptBestFit;   //variable storing check for user's answer
                boolean validAnswer = false; //for input validation

                acceptBestFit = input.next(); //get user input
                //Validate the input if it is a y, Y, n, or N
                while(!validAnswer){   //if they accept
                    if(acceptBestFit.equals("y") || acceptBestFit.equals("Y")){
                        valid = true; //change flag from invalid purchase to new valid purchase
                        validAnswer = true; //change validation flag to exit the loop
                    }
                    //if declined
                    else if(acceptBestFit.equals("n") || acceptBestFit.equals("N")){
                        valid = false; //purchase is still in invalid range
                        validAnswer = true; //however, valid user input                        
                    }
                    else{ //invalid value, loop and try again
                        System.out.println("Please accept or decline.  You must enter 'Y' or 'N'");
                        System.out.print("Do you want the suggested seats? Y/N\n>> ");
                        input.nextLine();
                        acceptBestFit = input.next();
                    }
                }

            }
            else
                System.out.println("No solutions found...");  
        }

        if(valid)
        {   //Confirm purchase as successful
            System.out.println("\n\nPurchase completed!!");
            System.out.println("Your seat(s) are reserved for tonight's showing of ");
            System.out.println(SHOWING + " for " + theater.displayName + " for ");
            if(quantity==1) //different message for single seat
                System.out.println("row " + row + ", seat " + seat);
            else //different message for multiple seats
                System.out.println("row " + row + ", seats " + seat + " through " + (seat+quantity-1));
            System.out.println("Purchase Total: $" + PRICE*quantity); //purchase balance
            

            for(int i = seat; i <= seat + quantity - 1; i++){
                auditorium.available.deleteSeat(row, i);
                auditorium.reserved.addSeat(row, i);
            }
            //Send values for the theater and number of seats purchased into the
            //"updateReportLine" function to update the Final Report
            updateReportLine(report, theater_index, quantity);
            

        }
        else{
            System.out.println("\n\nPlease try a different auditorium or purchase seats separately.");
            System.out.println("Returning to Main Menu...");
        }
            
    }
   
    
    
    
//==============================================================================    
//  III-F.                     End Program
//==============================================================================  
//  Prints final messages to the console and closes the program successfully
//==============================================================================
    public static void endProgram(){        
        System.out.println("\n\n====================================================\n\n");
        System.out.println("Thank you for visiting!\n\n");
        System.out.println("For all questions, please contact me at:");
        System.out.println("Jimmy@JimmyWorks.net\n\n");
        System.out.println("====================================================");
        
        System.exit(0);  //closes program... all io streams closed prior to 
                         //executing this function
    } 
}
