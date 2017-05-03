/*

 CS 2336 PROJECT 1
GotG2 Ticket Reservation System (Maintenance Project 1)
Project Due: 2/4 by 11:59 PM

Author: Jimmy Nguyen (Thanh Hoang Nguyen)
UTD Net-ID: tbn160230

 */


import java.util.*;

public class Main{

//==============================================================================
//                          Global Constants   
//==============================================================================    
//    Here are all of the global contants for the program.  These are coded at the
//    top of the program to allow easy reference and updates for future versions.
//    The strings that hold the filenames that house the reservation maps for each
//    of the auditoriums, the number of auditoriums (which should equal the number
//    of files), the number of report lines and report labels, as well as the price
//    per ticket and current movie title showing are all listed below.
    
    static final int NUMBER_OF_AUDITORIUMS = 3;  //Must equal number of upload files
    static final String FILENAME_A1 = "A1.txt";
    static final String FILENAME_A2 = "A2.txt";
    static final String FILENAME_A3 = "A3.txt";

    static final int NUMBER_OF_REPORT_LINES = 3; //Must equal number of labels below
    static final String[] REPORT_LABELS = {"Reserved Seats",
                                           "Available Seats",
                                           "Total Sales ($)"};
    static final int PRICE = 7;
    static final String SHOWING = "Guardians of the Galaxy 2";
    
//==============================================================================
//                     Enumeration for Main Menu
//==============================================================================
//    Here is a simple enumerated options menu for use in the Main Menu.  The list
//    for this enumeration matches the numberic options for the user input found in
//    the program.  This enumeration also allows calls to the display name.
    
    
    public enum MainMenu
    {
        ERROR ("Error"), //For debugging purposes and to offset by 1
        AUDITORIUM_1("Auditorium 1"),
        AUDITORIUM_2("Auditorium 2"),
        AUDITORIUM_3("Auditorium 3"),
        EXIT("Exit"); //For this code, the Exit option is always the number of
                      //auditoriums + 1
        
        private final String displayName;

        MainMenu(String displayName) {
        this.displayName = displayName;
    }

    }

//==============================================================================
//                               Main
//==============================================================================
    
    
    public static void main(String[] args) throws Exception
    {
        
    //Declarations and Initializations =========================================
    //==========================================================================
        
    //  Declare file paths to handle files storing auditorium seating maps
        java.io.File fileA1 = new java.io.File(FILENAME_A1);
        java.io.File fileA2 = new java.io.File(FILENAME_A2);
        java.io.File fileA3 = new java.io.File(FILENAME_A3);
        
        
    //Check if these files can be read and written
    //If any files cannot be read or written, an error message will appear
    //and the program will end.
        if( badFileCheck(fileA1) || 
            badFileCheck(fileA2) ||
            badFileCheck(fileA3)){
            System.out.println("One or more files cannot be read/written.");
            System.out.println("Please check file location and permissions");
            System.out.println("and try again.");
            endProgram();
        }

        
    //Declare variables to read user input and from text files: A1, A2, and A3
    //These scanner objects are stored in an array for easy parallel processing
        Scanner input = new Scanner(System.in);         //scanner for reading user input
        Scanner readA1 = new Scanner(fileA1);           //scanner for reading A1.txt
        Scanner readA2 = new Scanner(fileA2);           //scanner for reading A2.txt
        Scanner readA3 = new Scanner(fileA3);           //scanner for reading A3.txt
        Scanner []readFile = {readA1, readA2, readA3};
        
    //Declare array to house dimensions and all reservation maps in the theater
    //These next two arrays have very specific functions.  The dimensions array
    //will hold each auditorium index for the theater along with two cells in the
    //second array dimension holding the actual number of rows and seats per
    //auditorium.  This will be used to construct the arrayofMaps array.
    //The arrayOfMaps array is the master reservation plan for the entire theater.
    //It is a 3-dimensional array holding each auditoriums' 2-dimensional reservation
    //mapping.
        //dimensions[theater_index][0] = row_index
        //dimensions[theater_index][1] = seat_index
        int [][] dimensions;
        dimensions = new int[readFile.length][2];
        //Master Reservation Mapping Array for Entire Theater
        char [][][] arrayOfMaps;       
        
        //Function that will find the dimensions of each auditorium and store it
        //to the dimensions array
        getSeatingDimensions(readFile, dimensions); 

        //Now that we know dimensions for all auditoriums, initialize the arrayOfMaps
        //By first initializing the number of auditoriums then 2nd and 3rd dimmension
        //for each auditorium's rows and seats
        arrayOfMaps = new char[readFile.length][][];
        for(int i=0; i < readFile.length; i++)
            arrayOfMaps[i] = new char[dimensions[i][0]][dimensions[i][1]];
        

        //Close all the read files to recreate the scanners for reading
        for(Scanner i: readFile)
        {
            i.close();
        }
        //Reinitialize the scanner files to read from the top of the file
        readFile[0] = new Scanner(fileA1);
        readFile[1] = new Scanner(fileA2);
        readFile[2] = new Scanner(fileA3);

    //Now that we have a blank template for the master reservation mapping for
    //all of the auditoriums, let's fill every map by re-reading the files and
    //loading them into our master reservation report using the "loadMaps" function
        loadMaps(readFile, arrayOfMaps);
    
    //Next, let's create the final report to log sales, reserved seats and
    //available seats, updated after each transaction. The report elements
    //are composed of Total Reserved Seats, Total Available Seats, and Total Sales
    //which can be updated in the global constants and the "updateReport" function
    //and "printReport" function, respectively.  The first dimension of most of these
    //arrays are the theater_indexes for accessing the respective auditorium
        int [][]report = new int[readFile.length][NUMBER_OF_REPORT_LINES];
        updateReport(arrayOfMaps, report);

        
    //Begin User Interface Welcome Screen=======================================
    //==========================================================================
    
        System.out.println("=========Welcome to Jimmy’s Movie Theater=========");
        System.out.println("We look forward to giving you the best movie experience!\n");

        int selection = 0;          //reuseable variable for storing user input
        boolean badInput = true;    //checking for bad input
    
        
    //This while-loop is where the program will be for the remainder of the runtime
    //The current movie title showing will display, prices, and the options for
    //where the user can purchase tickets or exit the program.  User input is
    //wrapped in a try block in cases of bad input for exception handling.  For
    //proper type input, logical if/if-else statements filter for invalid values.
    //Each auditorium also displays its number of open seating for ease of use.

        while(true)
        {
            System.out.printf("Currently showing: %s\n\n", SHOWING);
            System.out.printf("Tickets:            Adult: $%d\n", PRICE);
            System.out.printf("Tickets:            Child: $%d\n", PRICE);
            System.out.printf("Tickets:           Senior: $%d\n\n", PRICE);
            System.out.println("Please select an option:\n");
            System.out.printf("1 - Auditorium 1 -- Open Seating: %d\n", report[0][1]);
            System.out.printf("2 - Auditorium 2 -- Open Seating: %d\n", report[1][1]);
            System.out.printf("3 - Auditorium 3 -- Open Seating: %d\n", report[2][1]);
            System.out.printf("4 - Exit\n\n\n>> ");   
            
            try
            {
            selection = input.nextInt();
            
            MainMenu options = MainMenu.values()[selection];
    
    //For valid values when selecting the auditorium, the console will print the
    //auditorium selected via enum method and print the current reservation map
    //for that specific auditorium via "printMap" function.  The user will then be
    //sent to a "purchaseTickets" function to complete their transaction which is 
    //still managed by the try block for exception handling.
    
            if(selection > 0 && selection <= NUMBER_OF_AUDITORIUMS)
            {
                System.out.println("\n" + options.displayName);
                printMap(arrayOfMaps, selection);
                System.out.println("\nEmpty seats: '#'  Reserved seats: '.'");
                purchaseTickets(options, arrayOfMaps, report);
            }
    //For the Exit option, the program will acknowledge the selection and close
    //all data streams including the scanner objects for each file.  The final report
    //will then be printed via the "printReport" function.  The program will then
    //create output PrintWriter objects for each file (which is currently coded
    //to over-write the original text files) and store them in an array "printer" 
    //for easy processing.  The "exportMaps" function then takes the master
    //reservation report "arrayOfMaps" and the array of PrintWriter output files
    //to export the updated maps onto their respective text files.  The program then
    //closes the array of output streams and goes through final "endProgram" messages
    //to the screen.
            else if(selection == NUMBER_OF_AUDITORIUMS + 1)
            {
                System.out.println("\nExiting...");
                
                input.close();
                for(Scanner i: readFile)
                    i.close();
                printReport(report);
                
                java.io.PrintWriter outFileA1 = new java.io.PrintWriter(FILENAME_A1);
                java.io.PrintWriter outFileA2 = new java.io.PrintWriter(FILENAME_A2);
                java.io.PrintWriter outFileA3 = new java.io.PrintWriter(FILENAME_A3);
                java.io.PrintWriter[] printer = {outFileA1, outFileA2, outFileA3};
                
                
                exportMaps(arrayOfMaps, printer);
                
                for(java.io.PrintWriter i: printer)
                    i.close();
                
                endProgram();  
            }
            else
                System.out.println("\nYour input is invalid, please try again\n\n");
    //Exception handling for any exceptions that may be thrown within the main program
    //loop.  This takes care of sub-routines as well.
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
            System.out.print("\n\n========================================================\n\n\n");
        
        }
    }
//==============================================================================
//                             Functions
//==============================================================================

    //File Checking=============================================================
    //==========================================================================
    //This program takes the path for the filename given and checks if the file
    //can be read and written to and returns a boolean.  A false denotes no errors
    //and a true denotes errors found.  A console message also appears for positive
    //tests
    public static boolean badFileCheck(java.io.File checkFile)
    {
        boolean error = false;
        if(!checkFile.canRead()) //Checks if file can be read
        {
            System.out.println("Cannot read file: " + checkFile.getName());
            error = true;
        }
        if(!checkFile.canWrite()) //Checks if file can be written to
        {
            System.out.println("Cannot write file: " + checkFile.getName());
            error = true;
        }
            
        return error;
    }
    
    //Getting Auditorium Dimensions for Each Auditorium=========================
    //==========================================================================
    //This program reads each auditorium's reservation mapping text file and counts
    //counts the number of rows and seats in each auditorium, storing them to the
    //dimensions array for creating the master theater seating reservation report
    public static void getSeatingDimensions(Scanner[] scanFiles, int[][] dimensions)
    {
        String buffer = "empty";
        
        for(int i=0; i < scanFiles.length; i++) //loops through each auditorium, i
        {
            buffer = scanFiles[i].next();       //store the string into a buffer
            dimensions[i][0]++;                 //dimensions[i][0] stores the number of rows
            dimensions[i][1] = buffer.length(); //using the first row, get the number of seats

            while(scanFiles[i].hasNext())       //use while-loop to count the remaining rows
            {
                buffer = scanFiles[i].next();
                dimensions[i][0]++;
            }
        }
    }
    //Load the Text File Maps onto the Master Reservation Map===================
    //==========================================================================
    //This program takes the newly created Master Reservation Map which will house
    //all the auditoriums and their respective reservation map for free and reserved
    //seats.  Reading each line from each of the text files, it will clone the
    //character array to the respective row in the respective auditorium specified
    public static void loadMaps(Scanner[] mapFiles, char[][][] theaterMap)
    {
        for(int i=0; i < mapFiles.length; i++) //for each auditorium, i
        {
            for(int j = 0; mapFiles[i].hasNext(); j++) //for each row, j
            {   //clone the character array for the string for each row in the 
                //respective mapping file, for every map/auditorium
                theaterMap[i][j] = mapFiles[i].next().toCharArray().clone(); 
            }
        }
    }
    
    //Export All Updated Auditorium Maps to Their Respective Text Files=========
    //==========================================================================
    //With the Master Reservation Map, print each auditorium's map back to the
    //original files, over-writing the data there with the new updated transactions
    public static void exportMaps(char[][][] theaterMap, java.io.PrintWriter[] printer)
    {
        for(int i = 0; i < theaterMap.length; i++) //for each auditorium, i
        {   //for each row in that auditorium, j
            for(int j = 0; j < theaterMap[i].length; j++) 
            {   //print the char array into the parallel output stream to the file
                //for every row
                printer[i].println(theaterMap[i][j]);          
            }
        }
    }
    
    //Fully Load/Update Final Report After Populating the Master Reservation Map
    //==========================================================================
    //With the Master Reservation Map loaded and updated, this function will do
    //a full load/update to make the Final Report updated
    public static void updateReport(char[][][] theaterMap, int[][] myReport)
    {   //for each auditorium, i
        for(int i = 0; i < theaterMap.length && i < myReport.length; i++)
        {
            int reservedSeat = 0; //initialize reserved seat count to zero
            int openSeat = 0;     //initialize free seat count to zero
            
            //for each row, j
            for(int j = 0; j < theaterMap[i].length; j++)
            {   //for each seat, k
                for(int k = 0; k < theaterMap[i][j].length; k++)
                {   //if the seat is not free
                    if(theaterMap[i][j][k] != '#')
                        reservedSeat++;  //it is a reserved seat
                    else
                        openSeat++;      //otherwise, it is a free seat
                }
            }
            //After looping all the rows for auditorium, i, update report values
            //This would have to be editted every time a new version of the report is needed
            myReport[i][0] = reservedSeat;          //total reserved seats
            myReport[i][1] = openSeat;              //total open seats
            myReport[i][2] = reservedSeat * PRICE;  //total sales
        }
        
    }
    
    //Update Final Report After Each Transaction================================
    //==========================================================================
    //Taking the Final Report, the index for the auditorium where the transaction
    //was completed and the number of reserved seats for the transaction, update
    //the report to reflect the new number of reserved seats, open seats and sales
    //for that auditorium
    public static void updateReportLine(int[][]report, int index, int reserve)
    {
        report[index][0] += reserve;    //add reserved seats to total
        report[index][1] -= reserve;    //subtract reserved seats from available
        report[index][2] += reserve*PRICE; //add the sale to the total sales       
    }
    
    //Print the Final Report====================================================
    //==========================================================================
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
        for(int i = 0; i < NUMBER_OF_REPORT_LINES; i++)
            System.out.printf("%20s", REPORT_LABELS[i]);
        System.out.println();
        
        //Loop to print each auditorium's numbers
        for(int i = 0; i < NUMBER_OF_AUDITORIUMS; i++) //for each auditorium, i
        {       //first, print the name of the auditorium
                System.out.printf("%20s ", MainMenu.values()[i+1].displayName);
            for(int j = 0; j < NUMBER_OF_REPORT_LINES; j++) //for each label, j
                //print all of its values
                System.out.printf("%20d", report[i][j]);
            //newline
            System.out.printf("\n");
        }
        
        //Now that everything in the report is printed, calculate and print
        //the totals for the entire theater and append to the bottom
        int sum; //reusable sum variable
        System.out.printf("%20s ", total);
        for(int i = 0; i < NUMBER_OF_REPORT_LINES; i++) //for each report label
        {   //find its sum in the loop below:
            sum = 0;
            for(int j = 0; j < NUMBER_OF_AUDITORIUMS; j++) //for each auditorium, j
                sum += report[j][i]; //loop through and sum the values
            
            System.out.printf("%20d", sum); //print the value across the bottom
        }
            
    }
    //Print Map for Selected Auditorium=========================================
    //==========================================================================
    //This function prints the map for the respective auditorium from the index
    //and Master Auditorium Map
    public static void printMap(char[][][] myMap, int theaterNo)
    {
        System.out.printf("\n  "); //offset to align the numbers across the top
        
        //First, print the numbers to reference each seat
        for(int i = 0; i < myMap[theaterNo-1][0].length; i++) //for each row, i
        {   //print the modulus of the counting number for that seat, values 0-9
            System.out.printf("%d", (i+1)%10);
        }
        System.out.println(); //newline
        
        //Then, print the '#' or '.' for each seat
        for(int i = 0; i < myMap[theaterNo-1].length; i++) //for each row, i
        {
            System.out.printf("%d ", (i+1)%10); //first, print the mod row number
            System.out.println(myMap[theaterNo-1][i]); //then, print the row map
        }
    }
    
    //Purchase Tickets After Selecting an Auditorium============================
    //==========================================================================
    //The largest function call in this program.  This function executes after 
    //printing the map of the auditorium.  The function takes the "MainMenu" enum,
    //the Master Reservation Map and the Final Report as aruguments.  The function
    //will ask the user for a specified row, seat, and number of seats.  Data
    //validation is performed on each step to make sure value is valid within the 
    //bounds.  The function then checks if the seat and adjacent seats are available.
    //If all available, the purchase is made.  If not available, the function will
    //check for another adjacent set of seats in that row.  If multiple solutions
    //are found, the set closest to the center of the row is selected.  If a solution
    //is found, the best solution is presented to the customer for confirmation if
    //they would like to make the purchase.  If a purchase is made, the program
    //confirms the balance of the transaction, the row and seat numbers purchase, and
    //logs the seats as reserved in the Master Reservation Map.  The Final Report is
    //then updated to included the transaction in its totals.
 
    public static void purchaseTickets(MainMenu theater, char[][][] myMap, int[][] report)
    {
        //Declare all immediate variables needed
        int row,            //selected row
            seat,           //selected seat
            quantity,       //number of tickets/seats
            seatCounter;    //counter for counting seats in various scenarios
        int theater_index = theater.ordinal()-1; //auditorium's array index for easy reading
        Scanner input = new Scanner(System.in); //input stream for user input
        row = seat = quantity = 0;              //initializing values to 0
        
    //Prompt the user for a row number.  The following while loop checks for invalid
    //numbers.  Only with a valid number will the function exit the loop.  Row number
    //must be at least 1 and at most the number of rows.
        System.out.print("\n\nPlease enter the row number:\n>> ");
        boolean valid = false;
        while(!valid) //row validation
        {
            row = input.nextInt();
            //row cannot be less than 1 or greater than number of rows
            if(row < 1 || row > myMap[theater_index].length) 
            {
                System.out.print("That row does not exist.  Please try again:\n>> ");
                input.nextLine();
            }
            else
                valid = true;
        }
    //Prompt the user for a seat number.  The following while loop checks for invalid
    //numbers.  Only with a valid number will the function exit the loop.  Seat number
    //must be at least 1 and at most the number of seats in that row.
        System.out.println("\n\nPlease enter the seat number (or starting seat number ");
        System.out.print("furthest to the left for multiple adjacent seats):\n>> ");        
        valid = false;
        while(!valid) //seat validation
        {
            seat = input.nextInt();
            //seat cannot be less than 1 or greater than the number of seats in that row
            if(seat < 1 || seat > myMap[theater_index][row-1].length)
            {
                System.out.print("That seat does not exist.  Please try again:\n>> ");
                input.nextLine();
            }
            else
                valid = true;
        }
       
    //Prompt the user for a number of tickets.  The following while loop checks for invalid
    //numbers.  Only with a valid number will the function exit the loop.  Number
    //must be at least 1 and at most the number of seats in the row.
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
            else if(quantity > myMap[theater_index][row-1].length)
            {   //max number to tickets are the number of seats in a row, if all vacant
                System.out.println("\nThere are not enough seats in the row.");
                System.out.println("Please split purchase into multiple transactions ");
                System.out.print("for parties larger than " + myMap[theater_index][row].length);
                System.out.println("for this auditorium.");  
                System.out.print("For this row, please try again:\n>> ");
                input.nextLine();
            }
            else
                valid = true;
        }
        
    //Now, check each seat in the series starting with the first seat going right
    //If any seat is reserved, break fromm the for-loop and flag the validity of
    //the purchase as false
        int checkSeat; //seat instance to check
        //Check seats up to the quantity while not exceeding the row length
        for(checkSeat = 0; checkSeat < quantity && seat + checkSeat <= myMap[theater_index][row-1].length; checkSeat++){
        //check validity of all seats
            if(myMap[theater_index][row-1][seat-1+checkSeat]!='#') //if the char in the cell is not '#', its reserveed
            {
                valid = false; //not a valid purchase
                checkSeat++;   //counter checkSeat for this check
                break;         //leave the loop
            }                  
        }
        //Check the case where all the seats at the end of the row is available but the for-loop terminates
        //early due to no more seats to check, i.e. trying to book seats to the end of the row beyond the last
        //seat.  If so, the number of checked seats will be less than the requested amount for purchase.
        if (checkSeat != quantity)
            valid = false;
        
        //Now, first check the invalid case.  Display that the selection cannot
        //be completed, then check for solutions for the number of tickets the
        //customer wants to purchase
        if(!valid)
        {
            System.out.println("One or more of the seats you wanted to book are ");
            System.out.println("not available.\n");
            
            boolean foundBestFit = false; //flag if a Best Fit solution is found
            
            //Create an array of the size of the seats in the row to store markers
            //for each solution found.  The program will check this later for the
            //best solution
            int []seatSolution = new int[myMap[theater_index][row-1].length];
            seatCounter = 0; //initialize the seat counter to zero
            for(int i = 0; i < seatSolution.length; i++) //for each seat, i
            {   //check each seat in that row
                if(myMap[theater_index][row-1][i] == '#') //if free
                    seatCounter++; //increment counter
                else
                    seatCounter = 0; //else, false and reset counter to zero
                
                if(seatCounter >= quantity) //after checking seat, check the counter
                {   //if the counter is equal or greater than the ticket quantity
                    seatSolution[i+1-quantity] = 1; //mark the first seat in the set as a solution
                    foundBestFit = true; //flag that at least one solution has been found
                }
            }
            
        //Now that every instance for a solution has been checked, if a solution has
        //been found, look for the best one.  This involves calculating the center
        //of the row and the center of the set of seats
            if(foundBestFit)
            {
                //Declare variables for the center of the row and center seat for the
                //number of seats purchased.  The center in both cases is defined
                //immediately after as half of the value for even numbers and half
                //of the value plus 1 for odd numbers.  The center of the row is 
                //calculated directly from the length of the respective row index
                //in the array holding the auditorium mapping 
                int centerSeat, centerOfRow; //for storing half of each for the center
                centerSeat = quantity/2 + quantity%2; //half plus the modulus for odd numbers
                centerOfRow = myMap[theater_index][row-1].length/2 + myMap[theater_index][row-1].length%2;
                
                seat = -1; //sentinel for no solution -- guaranteed to change due to opening if statement
                for(int i = 0; i < seatSolution.length; i++) //for each solution cell, i, in the row
                {   //if it is indeed a solution denoted by 1
                    if(seatSolution[i]==1)
                    {   //if the variable, "seat", storing the best fit is zero, denoting
                        //a best fit has not been stored yet
                        if(seat == -1)
                            seat = i; //store that current solution
                        //Otherwise, check the stored solution with the new solution.
                        //If the new solution is closer, store the new solution
                        //Offsets for the center of the seating set and the row are used
                        else if(Math.abs((i+centerSeat)-centerOfRow) < Math.abs((seat+centerSeat)-centerOfRow))
                            seat = i;
                    }
                }
                //Convert seat from index to counting numbers
                seat += 1;
                
                //Prompt the user that a best fit soluton is found and present it
                //Ask if the user would like to accept the solution
                System.out.println("However, we found a set of " + quantity + " seats");
                System.out.println("in that row that best meets your criteria:");
                if(quantity==1)
                    System.out.println("Row: " + row + ", Seat " + seat);
                else
                    System.out.println("Row: " + row + ", Seats " + seat + " - " + (seat+quantity-1));
                System.out.print("Would you like to accept? Y/N\n>> ");
                
                String acceptBestFit;   //variable storing check for user's answer
                boolean validAnswer = false; //for input validation
                
                acceptBestFit = input.next(); //get user input
                //Validate the input if it is a y, Y, n, or N
                while(!validAnswer)
                {   //if they accept
                    if(acceptBestFit.equals("y") || acceptBestFit.equals("Y"))
                    {
                        valid = true; //change flag from invalid purchase to new valid purchase
                        validAnswer = true; //change validation flag to exit the loop
                    }
                    //if declined
                    else if(acceptBestFit.equals("n") || acceptBestFit.equals("N")){
                        valid = false; //purchase is still in invalid range
                        validAnswer = true; //however, valid user input
                        foundBestFit = false; //best fit was declined
                    }
                    else //invalid value, loop and try again
                    {
                        System.out.println("Please accept or decline.  You must enter 'Y' or 'N'");
                        System.out.print("Do you want the suggested seats? Y/N\n>> ");
                        input.nextLine();
                        acceptBestFit = input.next();
                    }
                }
            }
            //If they declined the offer, print messages and go back to main menu
            if(!foundBestFit){
            System.out.println("\n\nPlease try a different row or auditorium.");
            System.out.println("Returning to Main Menu...");
            }
        }
        
        //All valid purchases will go through this block of code whether they
        //originally picked a valid range of seats or accepted the best fit
        //solution.  Declined offers will skip the final confirmation block
        //and nothing is updated.
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
            
            //Loop through each seat and update the Master Reservation Map with
            //those seats are reserved
            for(int i = seat; i <= seat + quantity - 1; i++)
                myMap[theater_index][row-1][i-1] = '.';
            
            //Send values for the theater and number of seats purchased into the
            //"updateReportLine" function to update the Final Report
            updateReportLine(report, theater_index, quantity);
            

        }
            
    }
    //End Program===============================================================
    //==========================================================================
    //Prints final messages to the console and closes the program successfully
    public static void endProgram()
    {        
        System.out.println("\n\n====================================================\n\n");
        System.out.println("Thank you for visiting!\n\n");
        System.out.println("For all questions, please contact me at:");
        System.out.println("Jimmy@JimmyWorks.net\n\n");
        System.out.println("====================================================");
        
        System.exit(0);  //closes program... all io streams closed prior to 
                         //executing this function
    }
}