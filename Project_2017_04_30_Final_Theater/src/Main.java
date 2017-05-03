
//============================================================================== 
// Project: GotG2 Ticket Reservation System (Maint Project 3
//
// Author: Jimmy Nguyen
// Contact me: Jimmy@JimmyWorks.net
//==============================================================================
// Project Details:
// This project is an update to a prior ticket master system developed using
// customized linked-lists for auditoriums and seating.  A login system has been
// added to track customer user accounts on a daily basis.  Users can log in, 
// create a new order by making a purchase of tickets, review orders, update orders
// either by removing or adding tickets or cancelling an order, view receipt, and
// log out.  An admin user can also log into the system to a different admin
// menu which allows viewing auditorium maps, printing total reserved and available
// seats and daily booked ticket types and ticket sales, and logging out to shut
// down the system.
//==============================================================================

import java.io.*;
import java.util.*;
import Theater.*;
import Enum.*;
import User.*;

//  Note: The custom packages above contains all the custom classes
//  necessary to run this program.  
//  Files include:
//    Theater Package:
//      - Theater.java          -- Double Linked List housing all the auditoriums
//      - Auditorium.java       -- Double Linked node in Theater object  
//                                 Contains linked lists: available and reserved seats
//      - SeatingList.java      -- Double Linked List holding seat nodes
//                                 Used for listing available, reserved, and solution seats
//      - DoubleLinkSeat.java   -- Double Linked seating node holding a row and column number
//
//    Enum Package:
//      - MainMenu.java         -- Contains Main Menu selections and labels for report
//      - Price.java            -- Contains prices for all ticket rates
//
//    User Package:
//      - User.java             -- User Account Object for each user (contains username and password)
//      - OrderList.java        -- User's order list which contains each order created
//      - Order.java            -- Order object which holds a seating list with all
//                                 the reserved seats for that order


public class Main{
    
//==============================================================================
//    I.                      Global Constants   
//==============================================================================    

    static final String[] FILENAMES = {     "A1.txt", //location of each auditorium map
                                            "A2.txt",
                                            "A3.txt"};
    static final String[] REPORT_LABELS = { "Open",   //labels for admin report summary
                                            "Reserved",
                                            "Adult",
                                            "Senior",
                                            "Child"};
    static final String SHOWING = "Guardians of the Galaxy 2";   // current movie showing
    static final String HASH_FILENAME = "userdb.dat";   // location of username-password database
    static final String ERRORLOG = "error.log";     //file output for error debugging
    
//==============================================================================    
//    II.                    Program Main
//==============================================================================    
    public static void main(String[] args) throws Exception{

        Scanner input = new Scanner(System.in);   // user input scanner
        Theater myTheater = new Theater();        // the primary theater for this program
        HashMap <String, User>myHash = new HashMap<String, User>(); // HashMap that will house all the usernames and user accounts
        
//  Declare array of Files using filenames from FILENAMES array
        File[] fileArray = new File[FILENAMES.length];
        for(int i = 0; i < FILENAMES.length; i++){
            fileArray[i] = new File(FILENAMES[i]);
        }
        File hashFile = new File(HASH_FILENAME);
        
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
//  Also check if HashMap filename for file which holds all usernames and passwords is valid
        while(!hashFile.exists() || hashFile.isDirectory()){ //while loop to locate file
                System.out.printf("Your file name \"%s\" was not found.", hashFile.getName());
                System.out.println("Please specify the name of your input file:");
                String filename = input.nextLine();
                hashFile = new File(filename);
        }
        
//  Create Scanner array holding all the scanners for each file and one for HashMap     
        Scanner[] fileScanner = new Scanner[fileArray.length];        
        for(int i = 0; i < fileArray.length; i++){
            fileScanner[i] = new Scanner(fileArray[i]);
        }
        Scanner hashScanner = new Scanner(hashFile);
        
//  Using the array of Scanner objects, create all Auditoriums and load all
//  Auditoriums using loadAuditorium method defined in Auditorium class
//  Once done, add the Auditorium to the Theater
        for(int i = 0; i < fileScanner.length; i++){
            Auditorium newAuditorium = new Auditorium(i+1);
            newAuditorium.loadAuditorium(fileScanner[i]);
            myTheater.addAuditorium(newAuditorium);
        }
//  Error logger for any invalid entries in the username-password database file
    PrintWriter errorLog = new PrintWriter(ERRORLOG);
        
//  Fill the HashMap with username keys and create User objects for each username
    while(hashScanner.hasNext()){
        String[] buffer = hashScanner.nextLine().split(" ", 2);
        try{
            User newUser = new User(buffer[0], buffer[1]);
            myHash.put(buffer[0], newUser);
        }catch(Exception err){
            errorLog.println(String.format("%s %s", buffer[0], buffer[1]));
            System.out.printf("Error found in HashMap file.  Error logged to \"%s\"\n", ERRORLOG);
        }  // log all lines with errors
    }
    
//  For People Testing the Program
    System.out.println("Welcome to my Movie Theater Kiosk Simulator!\n");
    System.out.println("For login usernames, you can use one from the list below");
    System.out.println("or create your own in the \"userdb.dat\" file\n");
    System.out.printf("%-10s     %15s\n", "Usernames", "Passwords");
    System.out.println("------------------------------------------");
    for(Object i: myHash.keySet()){
        System.out.printf("%-10s --- %15s\n", i, myHash.get(i).getPassword());
    }
    System.out.println("\n\nNote: The \"admin\" password is required to get access");
    System.out.println("to the admin console for viewing reports and shutting down");
//  Finally, create the array to hold the report items and populate the array
//  with the updateReport method.  This report is constantly updated throughout
//  the life of the program.
        int [][]report = new int[myTheater.getNumberOfAuditoriums()+1][REPORT_LABELS.length];
        updateReport(myTheater, report);  
        
    

//==============================================================================    
//  II-A.                       Login Screen
//==============================================================================
Boolean running = true;

    while(running){
        Boolean validLogin = false;
        String user = "default";
                     
        while(!validLogin){
        System.out.println("\n\n=========== Welcome to Jimmy’s Movie Theater ===========");
        System.out.println("We look forward to giving you the best movie experience!");
        
        System.out.print("\n\nUsername: ");
        user = input.next();
        int counter = 0;
        String password;
        Boolean validPassword = false;
        
        while(!validPassword && counter < 3){
            System.out.print("Password: ");
            password = input.next();
            
            if(password.equals(myHash.get(user).getPassword())){
                validPassword = true;
                validLogin = true;
            }
            else{
                System.out.println("Invalid Password.  Please try again.");
                counter++;
            }
        }
        
        }
int selection = 0;          //reuseable variable for storing user input  
//==============================================================================    
//  II-B.                          Admin Login
//==============================================================================
        if(user.equals("admin")){
            
            System.out.println("\n\nOpening Admin Menu...\n\n");
            
            while(selection != 3){

                    System.out.print("\n\n====================== Admin Menu =======================\n\n");
                    System.out.printf("Currently showing: %s\n\n", SHOWING);
                    System.out.println("Current Prices:");
                System.out.printf("%s: $%.2f\n", Price.ADULT.name(), Price.ADULT.getRate());
                System.out.printf("%s: $%.2f\n", Price.SENIOR.name(), Price.SENIOR.getRate());                
                System.out.printf("%s: $%.2f\n\n", Price.CHILD.name(), Price.CHILD.getRate());
                    System.out.println("Please select an option:\n");
                    System.out.println("1 - View Auditorium");  
                    System.out.println("2 - Print Report");
                    System.out.printf("3 - Exit and Shut Down System\n\n\n>> ");
                try{
                    selection = input.nextInt();            
                
                switch(selection){
                    case 1:  // admin chooses to see current auditorium open/reserved mapping
                        //  Each auditorium also displays its number of open seating
                        System.out.println("Please select an auditorium:\n");
                        System.out.printf("1 - Auditorium 1 -- Open Seating: %d\n", report[0][1]);
                        System.out.printf("2 - Auditorium 2 -- Open Seating: %d\n", report[1][1]);
                        System.out.printf("3 - Auditorium 3 -- Open Seating: %d\n\n>> ", report[2][1]);
                        
                        int selectedRoom = input.nextInt();
                        
                        if(selectedRoom > 0 && selectedRoom <= FILENAMES.length)
                        {
                            System.out.println("\n===================== " + MainMenu.values()[selectedRoom].getName() + " =====================\n");
                            Auditorium selectedAuditorium = myTheater.getAuditorium(selectedRoom);
                            selectedAuditorium.printMap();
                        }
                        break;
                    case 2:  // admin chooses to print report of open and reserved seats and daily sales report
                        printReport(myTheater, report);
                        break;
                    case 3:  // admin chooses to log out and power down system
                        System.out.println("Logging off...");
                        input.close();
                        
                        for(Scanner i: fileScanner)
                            i.close();
                        
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
                        
                        running = false; // when checking main program loop, will break the loop to shutdown
                        break;
                    default:
                        System.out.println("\nInvalid input.\n\n");
                        input.nextLine();
                }
                }catch(Exception err){  // catch all exceptions and prompt for new input
                    System.out.println("Invalid input.");
                    input.nextLine();
                }
            }
        }
        else{

            System.out.printf("\n\nWelcome back, %s!\n\n", user);
//==============================================================================    
//  II-C.                          Customer Login
//==============================================================================

            while(selection != 5) //user stays in this loop for entirety of program unless
            {           //unless proper input for exiting or executing sub-routines
                System.out.print("\n\n====================== Main Menu =======================\n\n");
                System.out.printf("Currently showing: %s\n\n", SHOWING);
                System.out.printf("Tickets:            %s: $%.2f\n", Price.ADULT.name(), Price.ADULT.getRate());
                System.out.printf("Tickets:           %s: $%.2f\n", Price.SENIOR.name(), Price.SENIOR.getRate());                
                System.out.printf("Tickets:            %s: $%.2f\n\n", Price.CHILD.name(), Price.CHILD.getRate());
                System.out.println("Please select an option:\n");
                System.out.println("1 - Reserve Seats");
                System.out.println("2 - View Orders");
                System.out.println("3 - Update Order");
                System.out.println("4 - Display Receipt");
                System.out.printf("5 - Exit\n\n\n>> ");

                try //any exception thrown will back the program out to the Main Menu
                {
                selection = input.nextInt();

                switch(selection){
                    case 1: //================ Create Order and Reserve Seats ====================
                        //  Each auditorium also displays its number of open seating
                        System.out.println("Please select an auditorium:\n");
                        System.out.printf("1 - Auditorium 1 -- Open Seating: %d\n", report[0][1]);
                        System.out.printf("2 - Auditorium 2 -- Open Seating: %d\n", report[1][1]);
                        System.out.printf("3 - Auditorium 3 -- Open Seating: %d\n", report[2][1]);
                        
                        while(true){
                            try{
                            int selectedRoom = input.nextInt();

                            //  If the room has no more available seats, do not allow the
                            //  user to purchase tickets or view seating and skip back
                            //  to the main menu.
                            if(myTheater.getAuditorium(selectedRoom).available.getTotalSeats()==0){
                                        System.out.println("\n\nThis auditorium is fully booked out.");
                                        System.out.println("Please try a different auditorium.");
                                        break;
                            }
                            //  If the selected Auditorium is a valid entry, display the
                            //  name and Auditorium map with the Auditorium's printMap method
                            if(selectedRoom > 0 && selectedRoom <= FILENAMES.length){
                                System.out.println("\n===================== " + MainMenu.values()[selectedRoom].getName() + " =====================\n");
                                myTheater.getAuditorium(selectedRoom).printMap();
                                System.out.println("\n");
                                
                                purchaseTickets(myTheater.getAuditorium(selectedRoom), report, input, myHash.get(user));
                                break;
                            }
                            //  If an invalid value was entered for Auditorium selected,
                            //  notify the user before going back to main menu
                            else 
                                throw new Exception();
                            }catch(Exception nval){
                                System.out.println("\nYour input is invalid, please try again\n\n");
                                input.nextLine();
                            }
                        }
                        break;
                        
                    case 2:  //================ Preview Orders ====================  
                        if(myHash.get(user).orders.getFirst()==null){
                            System.out.println("You have no orders for today's session.");
                            System.out.println("Returning to Main Menu...\n");
                        }
                        else{
                            System.out.printf("\n\n====================== Order History for %s=======================\n\n", myHash.get(user).getName());
                            viewOrders(myHash.get(user));
                        }
                        break;
                        
                    case 3:  //================ Edit/Update/Delete Orders ====================
                        int number_of_orders = myHash.get(user).orders.getTotalOrders();                       
                        
                        if(number_of_orders ==0){
                            System.out.println("You have no orders to update.");
                            System.out.println("Returning to Main Menu...\n");
                            break;
                        }
                        else{
                            System.out.printf("\n\n====================== Update Order for %s=======================\n\n", myHash.get(user).getName());                            
                            System.out.printf("You have %d orders...\n\n", number_of_orders);

                            viewOrders(myHash.get(user));

                            System.out.println("Please enter the ORDER NUMBER you wish to update.");
                            System.out.printf("Enter \'0\' to exit.\n\n>> ");
                            
                            while(true){
                            try{
                                int orderNum = input.nextInt();
                                
                                if(orderNum == 0)
                                    break;
                                else if(orderNum > 0 && orderNum <= number_of_orders){
                                    updateOrder(input, myHash.get(user), report, myTheater, orderNum);
                                    break;
                                }
                                else
                                    throw new Exception();
                            }catch(Exception inval2){
                                System.out.printf("You entered an invalid order.  Try again.\n\n>> ");
                                input.nextLine();
                            }
                            }
                        }
                        break;
                        
                    case 4:  //================ View Receipt ====================
                        printReceipt(myHash.get(user));
                        break;
                        
                    case 5:  //================ Customer Log out ====================
                        System.out.println("Thank you for visiting!\n");
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
                input.nextLine();
            }
        }
        
    }  // Only the admin can get to the line below by logging out (running = false)
    System.out.println("System shutting down...");
    endProgram();
    
}
//==============================================================================    
//  III.                          METHODS
//==============================================================================

//==============================================================================    
//  III-A.                     Update Report
//==============================================================================     
//  Illustration of the final report summary printed at the end of the program:
//
//                  Theater Totals              Session Tickets
//                  Open Seats  Booked Seats    Adult   Senior  Child   Total Sales($)
//  Auditorium 1    
//  Auditorium 2
//  Auditorium 3
//  Total
//
//==============================================================================
     
     static void updateReport(Theater theater, int[][] report){
        for(int i = 0; i < theater.getNumberOfAuditoriums(); i++){             
            report[i][0] = theater.getAuditorium(i+1).available.getTotalSeats();
            report[i][1] = theater.getAuditorium(i+1).reserved.getTotalSeats();
            report[i][2] = theater.getAuditorium(i+1).adultCt;
            report[i][3] = theater.getAuditorium(i+1).seniorCt;
            report[i][4] = theater.getAuditorium(i+1).childCt;
        }
        // Update Totals
        for(int i = 0; i < REPORT_LABELS.length; i++){
            int total = 0;
            for(int j = 0; j < theater.getNumberOfAuditoriums(); j++){
                total += report[j][i];
            }
            report[theater.getNumberOfAuditoriums()][i] = total;
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
     
    public static void updateReportLine( Auditorium auditorium, int[][]report)
    {
        report[auditorium.ID-1][0] = auditorium.available.getTotalSeats();
        report[auditorium.ID-1][1] = auditorium.reserved.getTotalSeats();
        report[auditorium.ID-1][2] = auditorium.adultCt;
        report[auditorium.ID-1][3] = auditorium.seniorCt;
        report[auditorium.ID-1][4] = auditorium.childCt;

    }
//==============================================================================    
//  III-C.                     Print Report
//==============================================================================  
//  Admin sales report printer.  This method formats the information for each
//  auditorium and the theater and prints all reserved/open seats and the ticket
//  sales for that day at each ticket rate with the total sales for the day.
//==============================================================================
    public static void printReport(Theater theater, int[][] report)
    {
        
        // Update Totals
        for(int i = 0; i < REPORT_LABELS.length; i++){
            int total = 0;
            for(int j = 0; j < theater.getNumberOfAuditoriums(); j++){
                total += report[j][i];
            }
            report[theater.getNumberOfAuditoriums()][i] = total;
        }
        
        String total = "Total Sales"; //string to print the total sales
        
        //Header displaying the Sales report and movie's title
        System.out.println("\n\n=============================================================\n");
        System.out.println("Sales Report");
        System.out.println("=============================================================\n");        
        System.out.println("Showing: " + SHOWING);
        
        System.out.printf("\n\n%35s","Total Theater Seating");
        System.out.printf("%-40s\n", "   Session Tickets Purchased");
        System.out.printf("%15s", "");
        //Loop to print the report labels across the top for each column
        for(String i : REPORT_LABELS)
            System.out.printf("%10s", i);
        System.out.printf("%16s", "Total Sales($)");
        System.out.println();
        
        //Loop to print each auditorium's numbers
        for(int i = 0; i < report.length; i++) //for each auditorium, i
        {       //first, print the name of the auditorium
                System.out.printf("%15s", MainMenu.values()[i+1].getName());
            for(int j = 0; j < REPORT_LABELS.length; j++) //for each label, j
                //print all of its values
                System.out.printf("%10d", report[i][j]);
            System.out.printf("%16.2f", report[i][2]*Price.ADULT.getRate()+report[i][3]*Price.SENIOR.getRate()+report[i][4]*Price.CHILD.getRate());
            System.out.printf("\n");
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
//  each step to make sure value is valid and exception handling is always preformed.
//
//  This method is overloaded for purchasing tickets which addresses if a new
//  order is being created or if an order is being updated.  New orders require  
//  the first method defined below to create a new order for that user and a 
//  boolean of false for "update."  This order is then deleted if the user decides
//  not to go through with the purchase.  An updated to an order calls this same
//  method but the order to be updated is sent along with a boolean of true for
//  "update."  This way, if the user cancels the update, the order is not deleted.
//==============================================================================

    public static void purchaseTickets(Auditorium auditorium, int[][] report, Scanner input, User user){
        Order newOrder = user.createOrder(auditorium);
        purchaseTickets(auditorium, report, input, user, newOrder, false);
    }
    
    public static void purchaseTickets(Auditorium auditorium, int[][] report, Scanner input, User user, Order order, Boolean update)
    {
        //  Declare all immediate variables needed
        int row, col;               //selected seat
        row = col = 0;              //initializing values to 0
        
        int[] tickets = new int[Price.values().length];  //array to hold number of each ticket type
        
        //Prompt the User for Number of Tickets for Each Ticket Rate
        for(int i = 0; i < tickets.length; i++){
            while(true){
                System.out.printf("Please enter the number of %s tickets\n>> ", Price.values()[i].name());
                try{
                tickets[i] = input.nextInt();
                if(tickets[i] >= 0 && tickets[i] <= (auditorium.getTotalRows()*auditorium.getTotalColumns()))
                    break;
                else
                    throw new Exception();
                }catch(Exception error1){
                    System.out.println("Invalid value.  Please try again.\n\n");
                    input.nextLine();
                }
            }            
        }
        
        //Check if the User Decided Not to Purchase Any Tickets
        int check = 0;
        for(int i = 0; i < tickets.length; i++){
            check += tickets[i];
        }       
        if(check < 1){ // If not, send the user back to Main Menu
            System.out.println("You have not selected to purchase any tickets.");
            System.out.println("Returning to Main Menu...");
            if(!update)
                user.orders.deleteOrder(order);
            return;
        }
        //If User Did Choose to Purchase Tickets, Confirm the Values They Entered
        System.out.printf("You have selected to purchase the following tickets:\n");
        for(int i = 0; i < tickets.length; i++){
            System.out.printf("%s tickets: %d\n", Price.values()[i].name(), tickets[i]);
        }
        
        //Seating Linked List Array for Holding All Seats the User Enters for Each Ticket Type
        SeatingList[] orderSeats = new SeatingList[tickets.length];
        
        //For Each Ticket Type
        for(int i = 0; i < orderSeats.length; i++){
            orderSeats[i] = new SeatingList();
            //Up to the Max Number of Tickets the User Entered
            for(int j = 0; j < tickets[i]; j++){
                //Prompt the User for the Row Number
                while(true){
                System.out.printf("\nPlease enter the row number for %s ticket #%d:\n>> ", Price.values()[i].name(), j+1);                    
                    try{
                    row = input.nextInt();
                    //row cannot be less than 1 or greater than number of rows
                    if(row < 1 || row > auditorium.getTotalRows()) 
                    {
                        System.out.print("That row does not exist.\n");
                        throw new Exception();
                    }
                    else
                        break;
                    }catch(Exception error2){
                    System.out.println("Invalid value.  Please try again.\n\n");
                    input.nextLine();
                    }
                }
                //Prompt the User for the Column Number   
                while(true){
                    System.out.printf("\nPlease enter the column number for %s ticket #%d:\n>> ", Price.values()[i].name(), j+1);  
                    try{
                    col = input.nextInt();
                    
                    if(col < 1 || col > auditorium.getTotalColumns()) 
                    {
                        System.out.print("That column does not exist.\n");
                        throw new Exception();
                    }
                    else
                        break;    
                    }catch(Exception error3){
                    System.out.println("Invalid value.  Please try again.\n\n");
                    input.nextLine();
                    }
                }
                
                orderSeats[i].addSeat(row, col);
            }
            
        }
        
        DoubleLinkSeat cur;

        
//        Checking if the lists are properly made
//        System.out.println("Selected seats:");
//        for(int i=0; i < orderSeats.length; i++){
//            System.out.printf("%s seats:\n", Price.values()[i]);
//            cur = orderSeats[i].getFirst();
//            while(cur != null){
//                System.out.printf("(%d, %d) ", cur.getRow(), cur.getCol());
//                cur = cur.next;
//                
//            }
//            System.out.println();
//        }
        
        //Define boolean to check for valid purchases and temporary seating list
        //which contains all the removed seats from that auditorium's availability
        //list and will be modified with ticket type later before inserted into
        //proper order and reserved list.
        Boolean valid = true;   
        SeatingList tempSeats = new SeatingList();
        
        //  Check If All Seats Are Available in the Seating Lists Created per Ticket Type     
        for(int i = 0; i < orderSeats.length; i++){
            cur = orderSeats[i].getFirst();  //get the first seat in that list
            while(cur != null){ //while not the end of the list
                DoubleLinkSeat checkedSeat = auditorium.available.removeSeat(cur); //check that seat
                
                if(checkedSeat == null){ //if that seat cannot be found, invalidate the purchase and break out of the loop
                    System.out.printf("Seat (%d, %d) not found for %s ticket.\n", cur.getRow(), cur.getCol(), Price.values()[i].name());
                    valid = false;
                    break;
                }
                else{ //otherwise, update the ticket type on the seat and add the seat to the temporary seating list
                    checkedSeat.setTicket(Price.values()[i]);
                    tempSeats.addSeat(checkedSeat);
                }
                cur = cur.next;  //check the next seat
            } 
            if(!valid)
                break;
        }
        //  If Every Seat is Not Available, Put the Seats Removed Back into Availability List      
        if(!valid){
            //System.out.printf("Number of Available: %d; Number of Temp Seats: %d\n", auditorium.available.getTotalSeats(), tempSeats.getTotalSeats());
            while(tempSeats.getFirst() != null){
                auditorium.available.addSeat(tempSeats.removeSeat(tempSeats.getFirst()));
            }
            //System.out.printf("Number of Available: %d; Number of Temp Seats: %d\n", auditorium.available.getTotalSeats(), tempSeats.getTotalSeats());
        }
//        else{  // Check what seats from the available seats of the auditorium are pending to be added
//            System.out.println("Temporary List to Add to Reserved List:");
//            cur = tempSeats.getFirst();
//            while(cur != null){
//                System.out.printf("Seat (%d, %d) for %s\n", cur.getRow(), cur.getCol(), cur.getTicket().name());
//                cur = cur.next;
//            }
//        }
        
        if(!valid){
            System.out.println("\n\nOne or more of the seats you wanted to book are ");
            System.out.println("not available.\n");
    // Generate list of possible solutions
            int quantity = 0;
            
            for(int i = 0; i < tickets.length; i++){
                quantity += tickets[i];
            }
            SeatingList solutions = auditorium.generateSolutions(quantity);
    
    //  Search for a Best Solution if the Solutions List is Not Empty
            if(solutions.getFirst() != null){
        //  Find Center of Auditorium
                int centerRow = auditorium.getTotalRows()/2 + auditorium.getTotalRows()%2;
                int centerCol = auditorium.getTotalColumns()/2 + auditorium.getTotalColumns()%2;
        //  Adjust Center Seat with Correction to Reference the "First Seat" in Collection
                centerCol -= (quantity/2-(quantity+1)%2);   //Shift left to compare "First Seat" to "First Seats"
                //System.out.printf("The reference center is (%d, %d)\n", centerRow, centerCol);
        //  Call Best Match Method to Find the Best Match
                DoubleLinkSeat bestMatch = solutions.bestMatch(centerRow, centerCol);
                //  Assign row and seat to the new best match
                row = bestMatch.getRow();
                col = bestMatch.getCol();
                
    //  Ask user if they want to purchase the new best match
                System.out.println("However, we found a solution that meets your criteria:\n");
                if(quantity==1)
                    System.out.println("Row: " + row + ", Seat " + col);
                else
                    System.out.println("Row: " + row + ", Seats " + col + " - " + (col+quantity-1));
                System.out.print("Would you like to accept? Y/N\n>> ");

                
                String acceptBestFit;   //variable storing check for user's answer
                boolean validAnswer = false; //for input validation

                acceptBestFit = input.next(); //get user input
                //Validate the input if it is a y, Y, n, or N
                while(!validAnswer){   
                    //If User Accepts the Recommended Seats...
                    if(acceptBestFit.equals("y") || acceptBestFit.equals("Y")){ 
                        valid = true; //change flag from invalid purchase to new valid purchase
                        validAnswer = true; //change validation flag to exit the loop
                        //For each seat, modify the ticket type with the correct number of times
                        for(int i = 0; i < tickets.length; i++){
                            for(int j = 0; j < tickets[i]; j++){
                                DoubleLinkSeat movedSeat = auditorium.available.removeSeat(row, col);
                                movedSeat.setTicket(Price.values()[i]);
                                tempSeats.addSeat(movedSeat);
                                col++;
                            }
                        }
                        
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
        //If the purchase was valid and user elected to proceed...
        if(valid)
        {   //Confirm purchase as successful
            System.out.println("\n\nPurchase completed!!");
            System.out.println("Your seat(s) are reserved for tonight's showing of ");
            System.out.println(SHOWING + " for " + MainMenu.values()[auditorium.ID].getName() + " for Seats:");
            //Print each seat, copy the seat to 
            while(tempSeats.getFirst() != null){
                System.out.printf("%s: Row: %d, Seat: %d\n", tempSeats.getFirst().getTicket().name(), tempSeats.getFirst().getRow(), tempSeats.getFirst().getCol());
                auditorium.reserved.addSeat(tempSeats.getFirst().getRow(), tempSeats.getFirst().getCol(), tempSeats.getFirst().getTicket());
                order.reserved.addSeat(tempSeats.removeSeat(tempSeats.getFirst()));
            }
            //Update the number of adult, senior and child tickets for this session
            auditorium.adultCt += tickets[Price.ADULT.ordinal()];
            auditorium.seniorCt += tickets[Price.SENIOR.ordinal()];
            auditorium.childCt += tickets[Price.CHILD.ordinal()];
            updateReportLine(auditorium, report); //Update the report
            

        }
        else{ //If user declined the purchase
            if(!update) //If this was not an update, delete the new order
                user.orders.deleteOrder(order);
            System.out.println("\n\nPlease try again or check other showings.");
            System.out.println("Returning to Main Menu...");
        }
            
    }
//==============================================================================    
//  III-F.                     View Orders
//==============================================================================  
 public static void viewOrders(User user){

    Order cur = user.orders.getFirst();
    for(int i = 1; cur != null; i++){
        System.out.printf("Order #%d Details:\n", i);
        System.out.println("======================");
        System.out.printf("Viewing in Auditorium %d\n", cur.getAuditorium().ID);
        System.out.println("Tickets:");
        DoubleLinkSeat curSeat = cur.reserved.getFirst();
        while(curSeat != null){
            System.out.printf("%s -- Row: %d, Seat: %d\n", curSeat.getTicket().name(), curSeat.getRow(), curSeat.getCol());
            curSeat = curSeat.next;
        }
        System.out.println();
                cur = cur.next;
    }
 }
//==============================================================================    
//  III-G.                     Update Orders
//==============================================================================
//  Second longest sub-routine in the program.  Update orders allows the user
//  to add seats to the current order, remove seats from the current order, or
//  delete the order, altogether.  To add tickets to the order, the purchase
//  tickets sub-routine is used sending all arguments required along with the
//  current order selected and the boolean "update" = true.  The remove seats
//  option allows removal of seats which must also update the report and
//  auditorium's available seating list.  If all seats are removed from an order,
//  the order is also cancelled, altogether, since keeping empty orders would 
//  be bad practice and a waste of memory usage.
//============================================================================== 
public static void updateOrder(Scanner input, User user, int[][] report, Theater theater, int orderNum){
    
    //First, iterate to the current order
    Order order = user.orders.getFirst();    //from the top of the list
    for(int i = 1; i < orderNum; i++){
        order = order.next;
    } 
    // now, order is the current order of interest
    
    //Get the auditorium for the current order
    Auditorium room = order.getAuditorium();
    //Print the order details for the selected order that requires update
    System.out.printf("\n\n====================== Order #%d =======================\n\n", orderNum);
    System.out.printf("Viewing in Auditorium %d\n", room.ID);
        System.out.println("Tickets:");
        DoubleLinkSeat cur = order.reserved.getFirst();
        while(cur != null){
            System.out.printf("%s -- Row: %d, Seat: %d\n", cur.getTicket().name(), cur.getRow(), cur.getCol());
            cur = cur.next;
        }
        System.out.println("\nPlease select an option:");
        System.out.println("1 - Add Tickets to Order");
        System.out.println("2 - Delete Tickets from Order");
        System.out.printf("3 - Cancel Order\n\n>> ");
        
        int selection;
        Boolean valid = false;
        //Validation loop for selected update option.  Only one option executes 
        //before returning to Main Menu
        while(!valid){
        try{
            selection = input.nextInt();
            
            switch(selection){
                case 1: // User selected to Add Tickets
                    System.out.println("\n===================== " + MainMenu.values()[room.ID].getName() + " =====================\n");
                    room.printMap();
                    System.out.println();
                    purchaseTickets(room, report, input, user, order, true);
                    valid = true;
                    break;
                case 2: // User selected to Delete Tickets from the order
                    Boolean deleting = true;
                    //Continue looping until the user decides to leave or has 
                    //deleted every last seat in the order
                    while(deleting){
                        try{
                        System.out.printf("Viewing in Auditorium %d\n\n", room.ID);
                        System.out.println("Select the ticket you would like to delete.");
                        cur = order.reserved.getFirst();
                        //Print all ticket details in the order
                        int total_tickets = 0;
                        for(int i = 1; cur != null; i++){
                            System.out.printf("%d - %s Ticket for Row %d, Seat %d\n", i, cur.getTicket().name(), cur.getRow(), cur.getCol());
                            total_tickets++;
                            cur = cur.next;
                        }
                        System.out.printf("\nPress \'0\' to exit.\n\n>> ");

                        int ticketNum = input.nextInt();
                        //If the user elects to not delete any tickets, return to Main Menu
                        if(ticketNum == 0){
                            deleting = false;
                        }// If the user elects to delete a valid ticket number, delete it
                        else if(ticketNum > 0 && ticketNum <= total_tickets){
                            cur = order.reserved.getFirst();
                            for(int i = 1; cur != null && i < ticketNum; i++)
                                cur = cur.next;
                            //Remove the ticket from the reserved list in the auditorium
                            System.out.printf("Removing %s: Row %d, Seat %d\n", cur.getTicket().name(), cur.getRow(), cur.getCol());
                            DoubleLinkSeat removed = room.reserved.removeSeat(cur);
                            //Determine the ticket type and subtract from the ticket rate counter in that auditorium
                            if(removed.getTicket().name().equals("ADULT"))
                                room.adultCt--;
                            if(removed.getTicket().name().equals("SENIOR"))
                                room.seniorCt--;
                            if(removed.getTicket().name().equals("CHILD"))
                                room.childCt--;
                            //Set the ticket's rate to null and add it back to the available list of seats
                            removed.setTicket(null);
                            room.available.addSeat(removed);
                            //Delete the seat from the order
                            order.reserved.deleteSeat(cur);
                            //Update the Admin Sales Report
                            updateReportLine(room, report);
                            //If it is the last seat in the order, delete the order and return to Main Menu
                            if(order.reserved.getTotalSeats()==0){
                                user.deleteOrder(order);
                                deleting = false;
                                System.out.println("You have deleted all tickets from this order.");
                                System.out.println("Deleting order...");
                                System.out.println("Returning to Main Menu...");
                            }
                                
                            
                        }
                        else
                            throw new Exception();
                        
                        }catch(Exception inval){
                            System.out.println("Invalid number.  Please try again.\n");
                            input.nextLine();
                        }

                    
                    }
                    
                    valid = true;
                    break;
                case 3://   User selected to Cancel the order
                    cur = order.reserved.getFirst();
                    int adult, senior, child; //rate count to keep track of all tickets
                    adult = senior = child = 0;
                    //While loop to delete every seat from order and update auditorium lists
                    while(cur != null){
                        System.out.printf("Removing %s: Row: %d, Seat: %d\n", order.reserved.getFirst().getTicket().name(), order.reserved.getFirst().getRow(), order.reserved.getFirst().getCol());
                        DoubleLinkSeat removed = room.reserved.removeSeat(cur); //remove seat from order
                        //Determine the ticket rate for that seat and update rate count
                        if(removed.getTicket().name().equals("ADULT"))
                            adult++;
                        if(removed.getTicket().name().equals("SENIOR"))
                            senior++;
                        if(removed.getTicket().name().equals("CHILD"))
                            child++;
                        //Set the ticket rate to null
                        removed.setTicket(null);
                        room.available.addSeat(removed); //add the seat to the available list
                        //go to next seat
                        cur = cur.next;
                    }
                    //Now that all seats are accounted for, delete the order
                    user.deleteOrder(order);
                    //Update the auditorium's ticket sales for each ticket rate
                    room.adultCt -= adult;
                    room.seniorCt -= senior;
                    room.childCt -= child;                    
                    updateReportLine(room, report);
                    
                    System.out.printf("Order #%d cancelled.\n", orderNum);
                    System.out.println("Returning to Main Menu...");                    
                    valid = true;
                    break;
                default:
                    throw new Exception();
                    
            }
        }catch(Exception error){
            System.out.printf("You made an invalid selection.  Please try again.\n\n>> ");
            input.nextLine();
        }
        }    
}
//==============================================================================    
//  III-H.                     Print Receipt
//==============================================================================
//  This method prints the user a receipt of all orders, the respective auditorium
//  and number of tickets for that auditorium with number of tickets per ticket
//  rate, the totals for those ticket rates, the subtotal for the order and the 
//  final total for all orders.
//==============================================================================
public static void printReceipt(User user){
    
    if(user.orders.getFirst()==null){
        System.out.println("You have no orders for today's session.");
    }
    else{
    System.out.printf("\n\n====================== Transaction Receipt for All Orders for %s =======================\n\n", user.getName());
    
        double total = 0.00;
    
        Order cur = user.orders.getFirst();
        for(int i = 1; cur != null; i++){
            System.out.printf("Order #%d\n", i);
            System.out.println("======================");
            System.out.printf("Auditorium: %d\n", cur.getAuditorium().ID);
            System.out.println("Seats:");
            
            DoubleLinkSeat curSeat = cur.reserved.getFirst();
            int adult, senior, child;
            adult = senior = child = 0;            
            while(curSeat != null){
                System.out.printf("Row: %d, Seat: %d\n", curSeat.getRow(), curSeat.getCol());
                
                if(curSeat.getTicket().name().equals("ADULT"))
                    adult++;
                if(curSeat.getTicket().name().equals("SENIOR"))
                    senior++;
                if(curSeat.getTicket().name().equals("CHILD"))
                    child++;
                
                curSeat = curSeat.next;
            }
            System.out.println();
            double subTotal = 0.00;
            if(adult != 0)
                System.out.printf("%2d Adult  Tickets       $%6.2f\n", adult, ((double)adult)*Price.ADULT.getRate());
            subTotal += ((double)adult)*Price.ADULT.getRate();
            if(senior != 0)
                System.out.printf("%2d Senior Tickets       $%6.2f\n", senior, ((double)senior)*Price.SENIOR.getRate());
            subTotal += ((double)senior)*Price.SENIOR.getRate();
            if(child != 0)
                System.out.printf("%2d Child  Tickets       $%6.2f\n", child, ((double)child)*Price.CHILD.getRate());
            subTotal += ((double)child)*Price.CHILD.getRate();
            System.out.printf("\nSubtotal                $%6.2f\n\n", subTotal);
            System.out.printf("___________________________________\n\n");
            
            total += subTotal;
            cur = cur.next;
        }
        System.out.printf("Total                   $%6.2f\n\n", total);
    }
}
    
//==============================================================================    
//  III-I.                     End Program
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
