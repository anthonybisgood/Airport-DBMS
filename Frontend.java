import java.util.Scanner;
import java.sql.*;                 // For access to the SQL interaction methods

// TODO: Choose 3rd benefit
// TODO: Choose 3rd Category for Query 4


public class Frontend {
    public static void main(String[] args) {
        // TODO: WHEN RAN IN LECTURA UNCOMMENT COMMENTED CODE BELOW AND DELETE LINE 26
        // final String oracleURL =   // Magic lectura -> aloe access spell
        // "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        // String username = null;
        // String password = null;

        // if (args.length == 2) {    // get username/password from cmd line args
        //     username = args[0];
        //     password = args[1];
        // } else {
        //     System.out.println("\nUsage:  java JDBC <username> <password>\n"
        //                 + "    where <username> is your Oracle DBMS"
        //                 + " username,\n    and <password> is your Oracle"
        //                 + " password (not your system password).\n");
        //     System.exit(-1);
        // }
        // Connection dbConn = getDbconn(oracleURL, username, password);
        Connection dbConn = null;
        try {   
            takeInput(dbConn);
            dbConn.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Creates a Db Connection with the oracleURL and the users username and password, returns the connection 
     * used to make queries. WRITTEN BY Proffessor Mccann, taken from JDBC.java
     * @param oracleURL "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle"
     * @param username the users oracle username
     * @param password the users oracle password
     * @return returns a db connection used to make queries
     */
    private static Connection getDbconn(String oracleURL, String username, String password) {
        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException:  "
                + "Error loading Oracle JDBC driver.  \n"
                + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);
        } 
            // make and return a database connection to the user's
            // Oracle database
        Connection dbconn = null;
        try {
                dbconn = DriverManager.getConnection
                               (oracleURL,username,password);
                return dbconn;
        } catch (SQLException e) {
                System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
        }
        return null;
    }

    /**
     * Acts as user interface, where user inputs commands for quaries they want
     * @param dbConn the Connection to the database and how we send quaries
     * @throws SQLException
     */
    private static void takeInput(Connection dbConn) throws SQLException{
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Type your query: '1', '2' or '3', or 'exit' to exit the program.");
            System.out.println("Type 'help' to view queries.");
            System.out.print(">: ");
            String value = input.nextLine().toLowerCase();
            if (value.equals("exit")){
                System.exit(0);
            } else if (value.equals("help")) {
                helpScreen();
            } else if (value.equals("1")) {
                
            } else if (value.equals("2")) {
                
            } else if (value.equals("3")) {
                
            } else if (value.equals("4")) {

            } else {
                System.out.println("Please input a valid query number or type 'help' for help");
            }
        }
    }

    /**
     * Provides the user with a help screen displaying the contents of each query
     */
    private static void helpScreen(){
        String query2 = "For any airline entered by the user, print the list of passengers, with the number of checked-in bags,\n" + 
                        "         sorted in ascending order by the number of checked-in bags and displays the output grouped by flights\n" + 
                        "         for a date in March that you choose.\n";
        String query3 = "Displays the schedule for flights on a date in June that you choose.\n" + 
                        "         The schedule contains the flight number, gate number, name of the airline of that flight,\n" + 
                        "         boarding time, departing time, duration of flight, and the origin and destination fo the flight.\n" +
                        "         Sorted in ascending order of boarding time.\n";
        System.out.println();
        System.out.println("HELP SCREEN\n");
        System.out.println("QUERY 1: Display the list of distinct passenger names, who took flights from all four airlines in the year 2021.\n");
        System.out.printf("QUERY 2: %s\n", query2);
        System.out.printf("QUERY 3: %s\n", query3);
        // TODO: Chose the 3rd Category of our choice and airline to query
        System.out.println("QUERY 4: Displays the a list of Students, Frequent Flyers, and ____ for ____ who: ");
        System.out.println("         1.) Traveled only once in the month of March.");
        System.out.println("         2.) Traveled with exactly one checked in bag anytime in the months of June aand July.");
        System.out.println("         3.) Ordered snacks/beverages on at least one flight.\n");
    }
}