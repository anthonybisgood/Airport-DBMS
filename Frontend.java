
/**
 * export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${PWD}
 */

import java.util.Scanner;
import java.sql.*; // For access to the SQL interaction methods
import java.util.HashMap;
import java.util.HashSet;
// TODO: Choose 3rd benefit
// TODO: Choose 3rd Category for Query 4
// TODO: Create Query 5 "You are required to implement the four provided queries as well as at least one query of your own design."
// TODO: do user record changing for flights
// TODO: READMe
// TODO: ability to add, remove, and update staff members

public class Frontend {
    public static void main(String[] args) {

        final String oracleURL = // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        String username = null;
        String password = null;

        if (args.length == 2) { // get username/password from cmd line args
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage: java JDBC <username> <password>\n"
                    + " where <username> is your Oracle DBMS"
                    + " username,\n and <password> is your Oracle"
                    + " password (not your system password).\n");
            System.exit(-1);
        }
        Connection dbConn = getDbconn(oracleURL, username, password);
        // Connection dbConn = null;
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
     * Creates a Db Connection with the oracleURL and the users username and
     * password, returns the connection
     * used to make queries. WRITTEN BY Proffessor Mccann, taken from JDBC.java
     *
     * @param oracleURL "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle"
     * @param username  the users oracle username
     * @param password  the users oracle password
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
            dbconn = DriverManager.getConnection(oracleURL, username, password);
            dbconn.setAutoCommit(false);
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
     *
     * @param dbConn the Connection to the database and how we send quaries
     * @throws SQLException
     */
    private static void takeInput(Connection dbConn) throws SQLException {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Type your query: '1', '2', '3', '4', '5', or 'exit' to exit the program.");
            System.out.println("Type 'help' to view queries.");
            System.out.println("Type 'rc' to edit records.");
            System.out.print(">: ");
            String value = input.nextLine().trim().toLowerCase();
            if (value.equals("exit")) {
                System.exit(0);
            } else if (value.equals("help")) {
                helpScreen();
            } else if (value.equals("1")) {
                query1Handler(dbConn);
            } else if (value.equals("2")) {
                query2Handler(input, dbConn);
            } else if (value.equals("3")) {
                query3Handler(input, dbConn);
            } else if (value.equals("4")) {
                query4Handler(dbConn);
            } else if (value.equals("5")) {
                query5Handler(input, dbConn);
            } else if (value.equals("rc")) {
                editDatabase(input, dbConn);
            } else {
                System.out.println("Please input a valid query number or type 'help' for help");
            }
        }
    }

    private static void query1Handler(Connection dbConn) {
        String query = "SELECT DISTINCT first_name, last_name " +
                "FROM Passenger INNER JOIN Passenger_trip ON Passenger.passenger_id = Passenger_trip.passenger_id " +
                "WHERE 4 IN (SELECT COUNT(DISTINCT airline_id) FROM Flight WHERE Flight.flight_id = Passenger_trip.flight_id)";
        executeQuery(query, dbConn, 1);
    }

    private static void query2Handler(Scanner input, Connection dbConn) {
        String date = validateDate(input, "march");
        if (date == null) {
            return;
        }
        int airline = -1;
        while (true) {
            System.out.println("Input an airline (1-4).");
            System.out.print(">: ");
            String airId = input.nextLine().trim();
            if (airId.equals("exit")) {
                return;
            }
            try {
                airline = Integer.parseInt(airId);
                if (airline >= 1 && airline <= 4) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input an integer.");
            }
        }
        String query = "select Passenger_ID, Num_Bags " +
                "from Passenger_Trip join Flight using (flight_ID) " +
                "where airline_id = ? and extract(DAY from Flight.Boarding_time) = ? and extract(MONTH from Flight.Boarding_time) = 3 "
                +
                "order by num_bags";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, airline);
        string_parameters.put(2, date);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, 2);
    }

    private static void query3Handler(Scanner input, Connection dbConn) {
        String query = "SELECT DISTINCT flight_id boarding_gate name boarding_time departing_time duration origin destination "
                +
                "FROM Flight WHERE EXTRACT(MONTH FROM Flight.boarding_time) = 'June' AND " +
                "EXTRACT(DAY FROM Flight.boarding_time) = ?";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        String month = validateDate(input, "june");
        if (month == null) {
            return;
        }
        string_parameters.put(1, month);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, 3);
    }

    /*
     * Purpose: Displays a series of queries for passengers on United Airlines,
     * separated into the following passenger types: Student, Frequent Flyer, and
     * Priority Boarding.
     * For each of those passenger types flying on United Airlines, the results of
     * these queries
     * will be displayed:
     *
     * (Passenger Type) That traveled only once in the month of March
     * (Passenger Type) That traveled with exactly one checked bag anytime in the
     * months of June and July
     * (Passenger Type) That ordered snacks/beverages on at least one flight
     *
     * Displays all information for those passengers
     *
     *
     * Parameters:
     * dbconn: The current connection to the database
     *
     * Returns: Nothing, prints to console.
     *
     *
     */
    private static void query4Handler(Connection dbConn) {
        // Set the roles that we have for passengers
        String[] types = { "'frequentflyer'", "'student'", "'priorityboarding'" };
        // Set the airline for which we are making this query
        String airline = "'United'";
        // Start date of the month of march
        String marchStart = "to_date('2021-03-01', 'YYYY-MM-DD')";
        // End date of the month of march
        String marchEnd = "to_date('2021-03-31', 'YYYY-MM-DD')";
        // Start date of month of june
        String juneStart = "to_date('2021-06-01', 'YYYY-MM-DD')";
        // End date of month of July
        String julEnd = "to_date('2021-07-31', 'YYYY-MM-DD')";

        String passengerType;
        String query;
        for (int i = 0; i < types.length; i++) {
            passengerType = types[i];

            // Query that returns all passengers of this passenger type
            String passengerQuery = "SELECT passenger.passenger_id, passenger.first_name, passenger.last_name FROM passenger "
                    + "JOIN passenger_benefit ON passenger.passenger_id=passenger_benefit.passenger_id "
                    + "JOIN benefit ON passenger_benefit.benefit_id=benefit.benefit_id "
                    + "WHERE benefit.category=" + passengerType;

            // March Query
            query = "SELECT pngrvalid.passenger_id, pngrvalid.first_name, pngrvalid.last_name FROM "
                    + "(" + passengerQuery + ") pngrvalid "
                    // Join with the passenger trips
                    + "JOIN passenger_trip ON pngrvalid.passenger_id=passenger_trip.passenger_id "
                    // Join with the flights
                    + "JOIN flight ON passenger_trip.flight_id=flight.flight_id "
                    // Join with the airlines
                    + "JOIN airline ON flight.airline_id=airline.airline_id "
                    // Filter only United Airlines
                    + "WHERE airline.name=" + airline + " "
                    // Filter such that we only get flights from March
                    + "AND flight.departing_time > " + marchStart + " "
                    + "AND flight.departing_time < " + marchEnd + " "
                    // Ensure that we only get results that have one of these records
                    + "GROUP BY pngrvalid.passenger_id, pngrvalid.first_name, pngrvalid.last_name "
                    + "HAVING COUNT(pngrvalid.passenger_id)=1";

            System.out.println("\nDisplaying results for " + passengerType + " passengers who flew " +
                    "only once in the month of March");
            System.out.println("----------------------------------");

            executeQuery(query, dbConn, 4);

            // One Checked Bag Query
            // Passenger must have traveled with exactly one checked bag at least
            // one time within the months of june or july
            query = "SELECT DISTINCT pngrvalid.passenger_id, pngrvalid.first_name, pngrvalid.last_name FROM "
                    + "(" + passengerQuery + ") pngrvalid "
                    + "JOIN passenger_trip ON pngrvalid.passenger_id=passenger_trip.passenger_id "
                    + "JOIN flight ON passenger_trip.flight_id=flight.flight_id "
                    + "JOIN airline ON flight.airline_id=airline.airline_id "
                    + "WHERE airline.name=" + airline + " "
                    // Filter for trips with only one checked bag
                    + "AND passenger_trip.num_bags=1 "
                    // Filter for flights between June and July
                    + "AND flight.departing_time > " + juneStart + " "
                    + "AND flight.departing_time < " + julEnd;

            System.out.println("\nDisplaying results for " + passengerType + " passengers" +
                    " who traveled with exactly one checked bag in the months of June or July");
            System.out.println("----------------------------------");
            executeQuery(query, dbConn, 4);

            // Ordered snacks/beverages query at least once
            query = "SELECT DISTINCT pngrvalid.passenger_id, pngrvalid.first_name, pngrvalid.last_name FROM "
                    + "(" + passengerQuery + ") pngrvalid "
                    + "JOIN passenger_trip ON pngrvalid.passenger_id=passenger_trip.passenger_id "
                    + "JOIN flight ON passenger_trip.flight_id=flight.flight_id "
                    + "JOIN airline ON flight.airline_id=airline.airline_id "
                    + "WHERE airline.name=" + airline
                    + "AND passenger_trip.num_items_purchased > 0";

            System.out.println("\nDisplaying results for " + passengerType + " passengers" +
                    " who ordered beverages at least once on a flight");
            System.out.println("----------------------------------");
            executeQuery(query, dbConn, 4);
        }

    }

    private static void query5Handler(Scanner input, Connection dbConn) {

    }

    /**
     * Method for sending the user to either change the airline side of passenger
     * side of the Database
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void editDatabase(Scanner input, Connection dbConn) {
        String ans = "";
        while (true) {
            System.out.println(
                    "Type '1' to edit passenger info, '2' for flight info, '3' for staff info, or 'exit' to quit");
            System.out.print(">: ");
            ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            if (ans.equals("1") || ans.equals("2") || ans.equals("3")) {
                break;
            } else {
                System.out.println("Please enter a valid query.");
            }
        }
        if (ans.equals("1")) {
            editPassengerInfo(input, dbConn);
        } else if (ans.equals("2")) {
            editFlightInfo(input, dbConn);
        } else if (ans.equals("3")) {
            editStaffInfo(input, dbConn);
        }
    }

    /**
     * Method for taking user input on if to add, update, or delete passenger info
     * from the database.
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void editPassengerInfo(Scanner input, Connection dbConn) {
        String ans = "";
        while (true) {
            System.out.println("Type '1' to add passenger, '2' to update passenger info, or '3' to delete a passenger");
            System.out.print(">: ");
            ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            if (ans.equals("1") || ans.equals("2") || ans.equals("3")) {
                break;
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
        if (ans.equals("1")) {
            addPassenger(input, dbConn);
        } else if (ans.equals("2")) {
            updatePassengerInfo(input, dbConn);
        } else if (ans.equals("3")) {
            deletePassengerInfo(input, dbConn);
        }
    }

    /**
     * Method for asking the user if they would like to add, update, or delete a
     * flight
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void editFlightInfo(Scanner input, Connection dbConn) {
        String ans = "";
        while (true) {
            System.out.println("Type '1' to add Flight, '2' to update Flight info, or '3' to delete a Flight");
            System.out.print(">: ");
            ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            if (ans.equals("1") || ans.equals("2") || ans.equals("3")) {
                break;
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
        if (ans.equals("1")) {
            addFlight(input, dbConn);
        } else if (ans.equals("2")) {
            updateFlightInfo(input, dbConn);
        } else if (ans.equals("3")) {
            deleteFlight(input, dbConn);
        }
    }

    /**
     * 
     * @param input A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void editStaffInfo(Scanner input, Connection dbConn) {
        String ans = "";
        while (true) {
            System.out.println("Type '1' to add staff, '2' to update staff info, or '3' to delete a staff member");
            System.out.print(">: ");
            ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            if (ans.equals("1") || ans.equals("2") || ans.equals("3")) {
                break;
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
        if (ans.equals("1")) {
            addStaffMember(input, dbConn);
        } else {
            int staffID = -1;
            while (true) {
                System.out.println("Enter a staff ID number.");
                System.out.print(">: ");
                String val = input.nextLine().trim();
                if (val.equals("exit")) {
                    return;
                }
                try {
                    staffID = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    System.out.println("Input an Integer");
                    continue;
                }
                if (validateId(staffID, "staff_member", dbConn, "staff_member_id")) {
                    break;
                } else {
                    System.out.println("Inputted Staff ID number is not in database");
                }
            }
            if (ans.equals("2")) {
                updateStaffMember(input, dbConn, staffID);
            } else if (ans.equals("3")) {
                deleteStaffMember(input, dbConn, staffID);
            }
        }
    }

    /**
     * Method to handle user adding staff members to the database
     * 
     * @param input A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void addStaffMember(Scanner input, Connection dbConn) {
        // role ID is an int so restrict it
        String[] fields = { "role_id", "first_name", "last_name", "address", "phone_number", "email", "salary" };
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            if (field.equals("role_id")) {
                while (true) {
                    System.out.println("Enter a role id. (1-5)");
                    System.out.print(">: ");
                    String val = input.nextLine().trim();
                    if (val.equals("exit")) {
                        return;
                    } else if (val.equals("1") || val.equals("2") || val.equals("3") || val.equals("4")
                            || val.equals("5")) {
                        int_parameters.put(i + 1, Integer.parseInt(val));
                        break;
                    }
                    System.out.println("Enter an Integer from 1-5");
                }
            } else {
                System.out.printf("Enter a new %s.\n", fields[i]);
                System.out.print(">: ");
                String val = input.nextLine().trim();
                if (val.equals("exit")) {
                    return;
                }
                string_parameters.put(i + 1, val);
            }
        }
        String query = "insert into staff_member (role_id, first_name, last_name, address, phone_number, email, salary) values (?,?,?,?,?,?,?)";
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    }

    /**
     * 
     * @param input A Scanner Object used to take user input
     * @param dbConn  the database connection used to send and recieve queries.
     * @param staffID Staff Id we are trying to change
     */
    private static void updateStaffMember(Scanner input, Connection dbConn, int staffID) {
        String[] fields = { "role_id", "first_name", "last_name", "address", "phone_number", "email", "salary" };
        int toChange = -1;
        while (true) {
            System.out.println("What field would you like to update?");
            System.out.println("    Type '1' to change roleID");
            System.out.println("    Type '2' to change First Name");
            System.out.println("    Type '3' to change Last Name");
            System.out.println("    Type '4' to change Address");
            System.out.println("    Type '5' to change Phone Number");
            System.out.println("    Type '6' to change email");
            System.out.println("    Type '7' to change salary");
            System.out.print(">: ");
            String ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            // validate user input is an integer and within bounds
            try {
                toChange = Integer.parseInt(ans);
                if (toChange > 0 && toChange < 8) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an Integer.");
            }
            System.out.println("Please enter a valid integer (1-7).");
        }
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        String val = "";
        while (true) {
            System.out.printf("Enter the new value for %s that you would like to change.\n", fields[toChange - 1]);
            System.out.print(">: ");
            val = input.nextLine().trim();
            if (val.equals("exit")) {
                return;
                // if they want to change role id we have to sanitize
            } else if (toChange == 1) {
                if (val.equals("1") || val.equals("2") || val.equals("3") || val.equals("4") || val.equals("5")) {
                    int_parameters.put(1, Integer.parseInt(val));
                    break;
                } else {
                    System.out.println("Enter an Integer from 1-5");
                }
            } else {
                string_parameters.put(1, val);
                break;
            }
        }
        int_parameters.put(2, staffID);
        String query = String.format("UPDATE staff_member SET %s = ? where staff_member_id = ?", fields[toChange - 1]);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    }

    /**
     * 
     * @param input A Scanner Object used to take user input
     * @param dbConn  the database connection used to send and recieve queries.
     * @param staffID Staff Id we are trying to change
     */
    private static void deleteStaffMember(Scanner input, Connection dbConn, int staffID) {
        String[] tables = { "staff_member", "staff_trip" };
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, staffID);
        for (int i = 0; i < tables.length; i++) {
            String query = String.format("delete from %s where staff_member_id = ?", tables[i]);
            executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
        }
    }

    /**
     * Adds a flight to the flight table using user input representing the different
     * fields in the flight table.
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn The database connection used to send and recieve queries.
     */
    private static void addFlight(Scanner input, Connection dbConn) {
        String[] fields = { "boarding_time", "departing_time", "boarding_gate", "duration", "origin", "destination",
                "airline_id" };
        long boarding_time = -1;
        long departing_time = -1;
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        for (int i = 0; i < fields.length; i++) {
            System.out.printf("\nEnter a new %s value.\n", fields[i]);
            // validate times
            if (i == 0) {
                boarding_time = takeUserDate(input);
                if (boarding_time == -1) {
                    System.out.println(boarding_time);
                    return;

                }
            } else if (i == 1) {
                departing_time = takeUserDate(input);
                if (departing_time == -1) {
                    return;
                }
            } else {
                System.out.print(">: ");
                String val = input.nextLine().trim();
                // validate duration > 0 and <= 20
                if (i == 3) {
                    try {
                        int dur = Integer.parseInt(val);
                        if (dur < 0 || dur > 20) {
                            System.out.println("Input a duration > 0 and < 20");
                            i -= 1;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Input an Integer");
                        i -= 1;
                    }
                }
                if (i == 6) {
                    if (!(val.equals("1") || val.equals("2") || val.equals("3") || val.equals("4"))) {
                        System.out.println("Enter a valid airline id (1-4)");
                        i -= 1;
                        continue;
                    }
                    int_parameters.put(i + 1, Integer.parseInt(val));
                }
                string_parameters.put(i + 1, val);
            }
        }
        try {
            PreparedStatement sql = dbConn.prepareStatement(
                    "insert into flight  (boarding_time, departing_time, boarding_gate, duration, origin, destination, airline_id) values (?,?,?,?,?,?,?)");
            sql.setDate(1, new java.sql.Date(boarding_time));
            sql.setDate(2, new java.sql.Date(departing_time));
            executeProtectedQuery(sql, dbConn, string_parameters, int_parameters, -1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for taking and validating user input pertaining to time slots. Takes
     * year, month, day, hour, and
     * minute and checks if inputs are valid. returns a long representation of the
     * java sql timestamp value of a string value of a date.
     * 
     * @param input A Scanner Object used to take user input s
     * @return returns a long representing a dateTime.
     */
    private static long takeUserDate(Scanner input) {
        int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        String[] times = { "year", "month", "day", "hour", "minute" };
        String toRes = "";
        int i = 0;
        int month = -1;
        System.out.println("\nEnter years, months, and days in Integer format. (hours in 24 hour format)");
        while (i < times.length) {
            String timeState = times[i];
            System.out.printf("Enter a %s\n", timeState);
            System.out.print(">: ");
            String val = input.nextLine().trim();
            if (val.equals("exit")) {
                return -1;
            }
            int valInt = 0;
            try {
                valInt = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                System.out.println("Enter an integer");
                continue;
            }
            switch (timeState) {
                case "year":
                    if (valInt < 1000 || valInt > 9999) {
                        System.out.println("Enter a valid year. (1000-9999)");
                        continue;
                    }
                    toRes += val + "-";
                    i += 1;
                    break;
                case "month":
                    if (valInt < 1 || valInt > 12) {
                        System.out.println("Enter a valid month. (1-12)");
                        continue;
                    }
                    month = valInt;
                    toRes += val + "-";
                    i += 1;
                    break;
                case "day":
                    if (valInt < 1 || valInt > monthDays[month - 1]) {
                        System.out.printf("Enter a valid day. (1-%d)\n", monthDays[month - 1]);
                        continue;
                    }
                    toRes += val + " ";
                    i += 1;
                    break;
                case "hour":
                    if (valInt < 0 || valInt > 23) {
                        System.out.println("Enter a valid hour. (0-23)");
                        continue;
                    }
                    String hour = String.format("%2d", valInt).replace(" ", "0");
                    toRes += hour + ":";
                    i += 1;
                    break;
                case "minute":
                    if (valInt < 0 || valInt > 59) {
                        System.out.println("Enter a valid hour. (0-23)");
                        continue;
                    }
                    String minute = String.format("%2d", valInt).replace(" ", "0");
                    toRes += minute + ":00";
                    i += 1;
                    break;
            }
        }
        long res = -1;
        try {
            res = Timestamp.valueOf(toRes).getTime();
        } catch (Exception e) {
            System.out.println("Count not parse value: " + toRes);
        }
        return res;
    }

    /**
     * Allows the user to change certain values in flight and staff_trip tables via
     * questions.
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void updateFlightInfo(Scanner input, Connection dbConn) {
        int flight_ID = -1;
        while (true) {
            System.out.println("Enter a flight_id.");
            System.out.print(">: ");
            try {
                String in = input.nextLine().trim();
                if (in.equals("exit")) {
                    return;
                }
                flight_ID = Integer.parseInt(in);
                // check if the flight is real
                if (validateId(flight_ID, "flight", dbConn, "flight_id")) {
                    break;
                }
                System.out.println("Flight id does not exist");
            } catch (NumberFormatException e) {
                System.out.println("Enter an integer");
            }
        }
        int toChange = -1;
        while (true) {
            System.out.println("What field would you like to update?");
            System.out.println("    Type '1' to change Boarding Gate");
            System.out.println("    Type '2' to change Boarding Time");
            System.out.println("    Type '3' to change Departing Time");
            System.out.println("    Type '4' to change Duration");
            System.out.println("    Type '5' to change Origin");
            System.out.println("    Type '6' to change Destination");
            System.out.println("    Type '7' to add staff to flight");
            System.out.println("    Type '8' to remove staff from flight");
            System.out.print(">: ");
            String ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            // validate user input is an integer and within bounds
            try {
                toChange = Integer.parseInt(ans);
                if (toChange > 0 && toChange < 9) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an Integer.");
            }
            System.out.println("Please enter a valid integer (1-8).");
        }
        if (toChange <= 6) {
            changeFlightTable(input, dbConn, toChange, flight_ID);
        }
        if (toChange == 7 || toChange == 8) {
            Boolean remove = (toChange == 8);
            changeStaffFromFlight(input, dbConn, flight_ID, remove);
        }
    }

    /**
     * Allows the user to change the staff on a flight via staff_trip
     * 
     * @param input     A Scanner Object used to take user input
     * @param dbConn    the database connection used to send and recieve queries.
     * @param flight_id The flight id of the flight we are trying to change
     * @param remove    Boolean value indicating whether we are supposed to remove
     *                  or add staff
     */
    private static void changeStaffFromFlight(Scanner input, Connection dbConn, int flight_id, Boolean remove) {
        int staffId = -1;
        while (true) {
            System.out.println("Enter the Staff ID that you would like to change from this flight");
            System.out.print(">: ");
            String val = input.nextLine().trim();
            if (val.equals("exit")) {
                return;
            }
            try {
                staffId = Integer.parseInt(val);
                // check if staff member is in DB
                if (validateId(staffId, "staff_member", dbConn, "staff_member_id")) {
                    // checks if staff member is on the fligth
                    Boolean alreadyOn = validateId(staffId, "staff_trip", dbConn,
                            "flight_id = " + flight_id + " and staff_member_Id");
                    // if the staff member we're trying to add is on the flight
                    if (alreadyOn && !remove) {
                        System.out.printf("Staff member is already on that flight, flight Id: %d\n", flight_id);
                        // if we are trying to remove a staff member but they're not on the flight
                    } else if (!alreadyOn && remove) {
                        System.out.printf("Staff member is not on that flight, flight Id: %d\n", flight_id);
                    } else {
                        break;
                    }
                } else {
                    System.out.println("Staff member is not in the Database.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter an Integer");
            }
        }
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, flight_id);
        int_parameters.put(2, staffId);
        String query = "";
        if (remove) {
            query = String.format("delete from staff_trip where flight_id = ? and staff_member_id = ?");
        } else {
            query = String.format("insert into staff_trip (flight_id, staff_member_id) values (?, ?)");
        }
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    }

    /**
     * Allows the user to change all fields in flight table exculding airlineID and
     * FlightId
     * 
     * @param input     A Scanner Object used to take user input
     * @param dbConn    the database connection used to send and recieve queries.
     * @param toChange  The field we are trying to change
     * @param flight_id The flight id of the flight we are trying to change
     */
    private static void changeFlightTable(Scanner input, Connection dbConn, int toChange, int flight_id) {
        String[] fields = { "boarding_Gate", "boarding_time", "departing_time", "duration", "origin", "destination" };
        String val = "";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        if (toChange == 2 || toChange == 3) {
            long newTime = takeUserDate(input);
            // check if any passenger_trip join flight has the same date that doesnt include
            // current flight id
            String query = "select distinct trunc(boarding_time), passenger_id " +
                    "from passenger_trip join flight using (flight_id) " +
                    "where flight_id != ? and passenger_id IN (select distinct passenger_id " +
                    "from flight join passenger_trip using(flight_id) " +
                    "where flight_id = ?) " +
                    "and trunc(boarding_time) = (select distinct trunc(boarding_time) " +
                    "from flight	" +
                    "where flight_id = ?)";
            boolean conflicting = false;
            try {
                PreparedStatement statement = dbConn.prepareStatement(query);
                statement.setInt(1, flight_id);
                statement.setInt(2, flight_id);
                statement.setInt(3, flight_id);
                ResultSet answer = statement.executeQuery();
                while (answer.next()) {
                    ResultSetMetaData answermetadata = answer.getMetaData();
                    if (answermetadata.getColumnCount() > 0) {
                        conflicting = true;
                    }
                }
                statement.close();
            } catch (SQLException e) {
                System.out.println("ERROR GETTING CONFLICITING FLIGHT TIMES");
                e.printStackTrace();
                System.exit(1);
            }
            if (!conflicting) {
                // add flight to user
                query = String.format("UPDATE flight SET %s= ? where flight_id = ?", fields[toChange - 1]);
                try {
                    PreparedStatement sql = dbConn.prepareStatement(query);
                    sql.setDate(1, new java.sql.Date(newTime));
                    int_parameters = new HashMap<Integer, Integer>();
                    int_parameters.put(2, flight_id);
                    executeProtectedQuery(sql, dbConn, string_parameters, int_parameters, -1);
                } catch (SQLException e) {
                    System.err.println("*** SQLException:  "
                            + "Could not fetch query results.");
                    System.err.println("\tMessage:   " + e.getMessage());
                    System.err.println("\tSQLState:  " + e.getSQLState());
                    System.err.println("\tErrorCode: " + e.getErrorCode());
                    System.exit(-1);
                }

            } else {
                System.out.println("Cannot change flight time, conflicting passenger times.");
            }
        } else {
            if (toChange == 4) {
                while (true) {
                    System.out.println("Input a new value.");
                    System.out.print(">: ");
                    val = input.nextLine().trim();
                    try {
                        int dur = Integer.parseInt(val);
                        if (dur < 0 || dur > 20) {
                            System.out.println("Input a duration > 0 and < 20");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Input an Integer");
                    }
                }
            } else {
                System.out.println("Input a new value.");
                System.out.print(">: ");
                val = input.nextLine().trim();
            }
            String query = String.format("UPDATE flight SET %s = ? where flight_id = ?", fields[toChange - 1]);
            string_parameters.put(1, val);
            int_parameters.put(2, flight_id);
            executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
        }
    }

    /**
     * Allows the user to delete a flight from flight table
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void deleteFlight(Scanner input, Connection dbConn) {
        int flight_id = -1;
        while (true) {
            System.out.println("Enter the ID of the flight you want to delete.");
            String val = input.nextLine().trim();
            try {
                flight_id = Integer.parseInt(val);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Input a valid integer.");
            }
        }
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, flight_id);
        String[] tables = { "flight", "passenger_trip", "staff_trip" };
        for (int i = 0; i < tables.length; i++) {
            String query = String.format("delete from %s where flight_id = ?", tables[i]);
            executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
        }
    }

    /**
     * takes user input about a new passenger and adds it to the database.
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void addPassenger(Scanner input, Connection dbConn) {
        String[] fields = { "email", "phone_number", "address", "first_name", "last_name" };
        String res;
        try {
            PreparedStatement sql = dbConn.prepareStatement(
                    "insert into passenger (email, phone_number, address, first_name, last_name) values (?,?,?,?,?)");
            for (int i = 0; i < fields.length; i++) {
                System.out.printf("Input a new %s\n", fields[i]);
                System.out.print(">: ");
                res = input.nextLine().trim();
                if (res.equals("exit")) {
                    return;
                }
                sql.setString(i + 1, res);
            }
            sql.execute();
            dbConn.commit();
        } catch (SQLException e) {
            System.out.println("Error inserting passenger info into database");
            e.printStackTrace();
        }
    }

    /**
     * Method for taking in user input and allowing them to update fields in the
     * passenger side of the DB including fields from the passenger Table,
     * Passenger benefits, number of bags, the flights they take, and the food/drink
     * that they have
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void updatePassengerInfo(Scanner input, Connection dbConn) {
        int toChange = -1;
        String query = "";
        int id = -1;
        while (true) {
            System.out.println("What is the passenger id of the passenger you want to update?");
            System.out.print(">: ");
            String strId = input.nextLine().trim();
            try {
                id = Integer.parseInt(strId);
            } catch (NumberFormatException e) {
                System.out.println("Input a valid integer.");
                continue;
            }
            if (!validateId(id, "passenger", dbConn, "passenger_id")) {
                System.out.println("id is not in table, choose another");
            } else {
                break;
            }
        }
        while (true) {
            System.out.println("What field would you like to update?");
            System.out.println("    Type '1' to change Email Address");
            System.out.println("    Type '2' to change Phone Number");
            System.out.println("    Type '3' to change Address");
            System.out.println("    Type '4' to change First Name");
            System.out.println("    Type '5' to change Last Name");
            System.out.println("    Type '6' to add Passenger benefit");
            System.out.println("    Type '7' to remove Passenger benefit");
            System.out.println("    Type '8' to change number of bags");
            System.out.println("    Type '9' to change passenger flights");
            System.out.println("    Type '10' to add Food/drink");
            System.out.print(">: ");
            String ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            // validate user input is an integer and within bounds
            try {
                toChange = Integer.parseInt(ans);
                if (toChange > 0 && toChange < 11) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an Integer.");
            }
            System.out.println("Please enter a valid integer (1-10).");
        }
        String[] fields = { "email", "phone_number", "address", "first_name", "last_name" };
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        if (toChange >= 1 && toChange <= 5) {
            System.out.println("Please enter the updated value.");
            String newVal = input.nextLine().trim();
            System.out.print(">: ");
            query = String.format("UPDATE passenger SET %s = ? where passenger_id = ?", fields[toChange - 1]);
            string_parameters.put(1, newVal);
            int_parameters.put(2, id);
            executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
        } else if (toChange == 6 || toChange == 7) {
            Boolean remove = (toChange == 7);
            changeBenefits(input, remove, id, dbConn);
        } else if (toChange == 8) {
            changeNumBags(input, id, dbConn);
        } else if (toChange == 9) {
            changePassengerFlights(input, id, dbConn);
        } else if (toChange == 10) {
            changePassengerFood(input, id, dbConn);
        }
    }

    /**
     * Allows the user to change the amount of food a passenger has bought on one of
     * their trips.
     * 
     * @param input  A Scanner Object used to take user input
     * @param id     The id of the passenger
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void changePassengerFood(Scanner input, int id, Connection dbConn) {
        int flight_id = validatePassengerOnFlight(input, id, dbConn);
        int amtBought = -1;
        while (true) {
            System.out.println("How many snacks/beverages has the passenger bought?");
            System.out.print(">: ");
            String user_input = input.nextLine().trim();
            if (user_input.equals("exit")) {
                return;
            }
            try {
                amtBought = Integer.parseInt(user_input);
                if (amtBought > 0 && amtBought < 20) {
                    break;
                } else {
                    System.out.println("Cannot buy more than 20 items");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter an Integer.");
            }
        }
        String query = "UPDATE passenger_trip set num_items_purchased = ? where passenger_id = ? and flight_id = ?";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, amtBought);
        int_parameters.put(2, id);
        int_parameters.put(3, flight_id);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    }

    /**
     * Allows the user to update passenger info in realation to adding/removing
     * flights from the todatabase. When adding flights, checks if the passenger
     * has the same boarding date before adding.
     * 
     * @param input  A Scanner Object used to take user input
     * @param id
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void changePassengerFlights(Scanner input, int id, Connection dbConn) {
        String ret = "";
        while (true) {
            System.out.println("Type '1' to add flight, '2' to remove passenger from flight.");
            System.out.print(">: ");
            ret = input.nextLine().trim();
            if (ret.equals("exit")) {
                return;
            }
            if (ret.equals("1") || ret.equals("2")) {
                break;
            }
            System.out.println("Please type either 1 or 2");
        }
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        if (ret.equals("1")) {
            // get flight number
            int flight_id = -1;
            while (true) {
                System.out.println("Enter a flight_id.");
                System.out.print(">: ");
                try {
                    String in = input.nextLine().trim();
                    if (in.equals("exit")) {
                        return;
                    }
                    flight_id = Integer.parseInt(in);
                    // checks if the flight is real
                    if (validateId(flight_id, "flight", dbConn, "flight_id")) {
                        break;
                    } else {
                        System.out.println("Flight ID not in DB records.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input an integer");
                }
            }
            // check if user has conflicting flight schedule
            // this query takes the passenger id and flight to add, finds the boarding time
            // of the flight and compares it to
            // any of the other flights that passenger has
            String query = String.format("select *" +
                    "from flight join passenger_trip using (flight_id)" +
                    "where passenger_id = %d and trunc(boarding_time) in (select distinct trunc(boarding_time)" +
                    "from flight join passenger_trip using (flight_id)" +
                    "where flight_id != %d)", id, flight_id);
            boolean conflicting = false;
            try {
                PreparedStatement statement = dbConn.prepareStatement(query);
                ResultSet answer = statement.executeQuery();
                while (answer.next()) {
                    ResultSetMetaData answermetadata = answer.getMetaData();
                    if (answermetadata.getColumnCount() > 0) {
                        conflicting = true;
                    }
                }
                statement.close();
            } catch (SQLException e) {
                System.out.println("ERROR GETTING CONFLICITING FLIGHT TIMES");
                e.printStackTrace();
                System.exit(1);
            }
            if (!conflicting) {
                // add flight to user
                query = "insert into passenger_trip (passenger_id, flight_id, num_bags, num_items_purchased) values (?,?,?,?)";
                int_parameters.put(1, id);
                int_parameters.put(2, flight_id);
                int_parameters.put(3, 0);
                int_parameters.put(4, 0);
                executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
            } else {
                System.out.println("Cannot add passenger to that flight, conflicting boarding times.");
            }
            // delete flight from passenger
        } else if (ret.equals("2")) {
            int flight_ID = validatePassengerOnFlight(input, id, dbConn);
            if (flight_ID == -1) {
                return;
            }
            String query = "delete from passenger_trip where passenger_id = ? and flight_id = ?";
            int_parameters.put(1, id);
            int_parameters.put(2, flight_ID);
            executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
        }
    }

    /**
     * Takes user input of a flight number and returns that flight number if the
     * passenger is on that flight and if that flight_id is valid.
     * 
     * @param input  A Scanner Object used to take user input
     * @param id     Passenger_id
     * @param dbConn the database connection used to send and recieve queries.
     * @return
     */
    private static int validatePassengerOnFlight(Scanner input, int id, Connection dbConn) {
        int flight_ID = -1;
        while (true) {
            System.out.println("Enter a flight_id.");
            System.out.print(">: ");
            try {
                String in = input.nextLine().trim();
                if (in.equals("exit")) {
                    return -1;
                }
                flight_ID = Integer.parseInt(in);
                // check if the flight is real
                if (validateId(flight_ID, "Passenger_trip", dbConn, "flight_id")) {
                    // check if passenger is on that flight
                    if (validateId(flight_ID, "Passenger_trip", dbConn, "passenger_id = " + id + " and flight_id")) {
                        break;
                    } else {
                        System.out.println("Passenger is not on that flight.");
                    }
                } else {
                    System.out.println("Flight ID not in DB records.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input an integer");
            }
        }
        return flight_ID;
    }

    /**
     * This method allows the user to change the number of bags a passenger has on a
     * flight. Takes into account if the flight is real, if the passenger is on that
     * flight, and if the passenger is a student(allowed 1 more bag).
     * Changes num_bags field in passenger_trip.
     * 
     * @param input  A Scanner Object used to take user input
     * @param id     Passenger id
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void changeNumBags(Scanner input, int id, Connection dbConn) {
        int flight_ID = validatePassengerOnFlight(input, id, dbConn);
        if (flight_ID == -1) {
            return;
        }
        int num_bags = 0;
        // checks to see if the passenger is a student
        boolean isStudent = validateId(id, "passenger_benefit", dbConn, "benefit_id = 2 and passenger_id");
        while (true) {
            System.out.println("How many bags? (Max 2 unless student).");
            System.out.print(">: ");
            try {
                String in = input.nextLine().trim();
                if (in.equals("exit")) {
                    return;
                }
                num_bags = Integer.parseInt(in);
                if (num_bags == 3 && isStudent) {
                    break;
                } else if (num_bags < 0 || num_bags > 2) {
                    System.out.println("Max number of bags is 2 (excluding students), cannot have negative bags");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input an integer");
            }
        }
        String query = "UPDATE passenger_trip SET num_bags = ? where flight_id = ? and passenger_id = ?";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, num_bags);
        int_parameters.put(2, flight_ID);
        int_parameters.put(3, id);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    }

    /**
     * Uses user input to add/remove benefits from Passenger_benefits based on the
     * passenger_id and benefit_id. Sanity checks benefit_id to make sure it is in
     * bounds.
     * 
     * @param input  A Scanner Object used to take user input Scanner for input
     * @param remove A boolean where true if the user is removing a benefit from the
     *               passenger and
     *               false if the user is adding a benefit to the passenger.
     * @param id     passenger ID
     *               Uses user input to add/remove benefits from Passenger_benefits
     *               based on the passenger_id
     *               and benefit_id. Sanity checks benefit_id to make sure it is in
     *               bounds.
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void changeBenefits(Scanner input, Boolean remove, int id, Connection dbConn) {
        int benefit_id = -1;
        String query = "";
        while (true) {
            System.out.println("What is the benefit id that you would like to add/remove?");
            System.out.print(">: ");
            try {
                benefit_id = Integer.parseInt(input.nextLine().trim());
                if (benefit_id < 1 || benefit_id > 3) {
                    System.out.println("Please enter a valid Integer (1-3)");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an Integer");
            }
        }
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, id);
        int_parameters.put(2, benefit_id);
        if (remove) {
            query = String.format("delete from passenger_benefit where passenger_id = ? and benefit_id = ?");
        } else {
            query = String.format("insert into passenger_benefit (passenger_id, benefit_id) values (?, ?)");
        }
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    }

    /**
     * Method for validating if an ID is in a table, also can be used for checking
     * the join
     * of 2 conditions (if sneaky enough, check changeNumBags()). Executes a query
     * andchecks if the query resulted in a Result set that is empty.
     * 
     * @param id     Any Id that you want to check
     * @param table  the table which you want to check
     * @param dbConn Connection to the DB
     * @param field  The field attr that the ID is based on (if id is a flight_id
     *               (Ex. 31), field is 'flight_id')
     * @return Returns a boolean if the id is in the table or not.
     */
    private static boolean validateId(Integer id, String table, Connection dbConn, String field) {
        String query = String.format("select * from %s where %s = ?", table, field);
        try {
            PreparedStatement statement = dbConn.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet answer = statement.executeQuery();
            while (answer.next()) {
                ResultSetMetaData answermetadata = answer.getMetaData();
                if (answermetadata.getColumnCount() > 0) {
                    statement.close();
                    return true;
                } else {
                    statement.close();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    /**
     * Executes the query with a prepared statement to avoid sql injection entirely
     * 
     * @param query             The query to be executed
     * @param dbConn            the database connection used to send and recieve
     *                          queries.
     * @param string_parameters A hashmap of the string parameters to be inserted
     *                          into the query
     * @param int_parameters    A hashmap of the int parameters to be inserted into
     *                          the query
     * @param queryNumber       The number of the query
     */
    private static void executeProtectedQuery(String query, Connection dbConn,
            HashMap<Integer, String> string_parameters, HashMap<Integer, Integer> int_parameters, int queryNumber) {
        try {
            PreparedStatement statement = dbConn.prepareStatement(query);
            for (Integer key : string_parameters.keySet()) {
                statement.setString(key, string_parameters.get(key));
            }
            for (Integer key : int_parameters.keySet()) {
                statement.setInt(key, int_parameters.get(key));
            }
            if (queryNumber < 0) {
                statement.executeUpdate();
                statement.close();
                dbConn.commit();
                return;
            }
            ResultSet answer = statement.executeQuery();
            if (answer != null) {
                while (answer.next()) {
                    ResultSetMetaData answermetadata = answer.getMetaData();
                    for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                        System.out.print(answermetadata.getColumnName(i) + "\t");
                    }
                    System.out.println();
                    for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                        System.out.print(answer.getString(i) + "\t");
                    }
                    System.out.println();
                }
            }
            statement.close();
            dbConn.commit();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Polymorphic method but takes in a prepared statement, used for queries that
     * include special SQL date fields.
     * 
     * @param statement         a Prepared statenemnt that is used to execute
     *                          queries via dbConn
     * @param dbConn            the database connection used to send and recieve
     *                          queries.
     * @param string_parameters A HashMap<Integer, String> of string parameters in
     *                          statement
     * @param int_parameters    A HashMap<Integer, Integer> of integer parameters in
     *                          statement
     * @param queryNumber       The query number used to signal what query we
     *                          execute
     */
    private static void executeProtectedQuery(PreparedStatement statement, Connection dbConn,
            HashMap<Integer, String> string_parameters, HashMap<Integer, Integer> int_parameters, int queryNumber) {
        try {
            for (Integer key : string_parameters.keySet()) {
                statement.setString(key, string_parameters.get(key));
            }
            for (Integer key : int_parameters.keySet()) {
                statement.setInt(key, int_parameters.get(key));
            }
            if (queryNumber < 0) {
                statement.executeUpdate();
                statement.close();
                dbConn.commit();
                return;
            }
            ResultSet answer = statement.executeQuery();
            if (answer != null) {
                while (answer.next()) {
                    ResultSetMetaData answermetadata = answer.getMetaData();
                    for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                        System.out.print(answermetadata.getColumnName(i) + "\t");
                    }
                    System.out.println();
                    for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                        System.out.print(answer.getString(i) + "\t");
                    }
                    System.out.println();
                }
            }
            statement.close();
            dbConn.commit();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Allows the user to delete a passenger by their passenger_id number, deletes
     * all instances of that
     * passenger across all tables
     * 
     * @param input  A Scanner Object used to take user input
     * @param dbConn the database connection used to send and recieve queries.
     */
    private static void deletePassengerInfo(Scanner input, Connection dbConn) {
        int idNum = -1;
        while (true) {
            System.out.println("Enter the passenger_id that you would like to delete");
            System.out.print(">: ");
            String id = input.nextLine().trim();
            if (id.equals("exit")) {
                return;
            }
            try {
                idNum = Integer.parseInt(id);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
        String[] tables = { "passenger", "passenger_trip", "passenger_benefit", "passenger_history" };
        for (int i = 0; i < tables.length; i++) {
            String query = String.format("delete from %s where passenger_id = %d", tables[i], idNum);
            executeQuery(query, dbConn, -1);
        }
    }

    /**
     * Executes quaries that are passed in by paramenters through the dbConnection.
     * (Influenced by Proffessor McCanns JDBC.java)
     *
     * @param query    The query string we wish to execute
     * @param dbConn   the Connection to the database and how we send quaries
     * @param queryNum The number query that we wish to execute (1-3)
     */
    private static void executeQuery(String query, Connection dbConn, int queryNumber) {
        Statement stmt = null;
        ResultSet answer = null;
        try {
            stmt = dbConn.createStatement();
            answer = stmt.executeQuery(query);
            // The lengths of each of the column names for formatting purposes
            int[] colLengths;
            if (answer != null) {
                // Get the data about the query result to learn
                // the attribute names and use them as column headers
                ResultSetMetaData answermetadata = answer.getMetaData();
                colLengths = new int[answermetadata.getColumnCount()];
                for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                    System.out.print(answermetadata.getColumnName(i) + "\t");
                    colLengths[i - 1] = answermetadata.getColumnName(i).length();
                }
                System.out.println();
                if (queryNumber == 2) {
                    while (answer.next()) {
                        System.out.println(answer.getString("Passenger_ID") + "\t"
                                + answer.getInt("num_bags"));
                    }
                } else if (queryNumber == 4) {
                    while (answer.next()) {
                        System.out.println(answer.getInt("passenger_id")
                                + " ".repeat(colLengths[0] - String.valueOf(answer.getInt("passenger_id")).length())
                                + "\t"
                                + answer.getString("first_name")
                                + " ".repeat(colLengths[1] - answer.getString("first_name").length()) + "\t"
                                + answer.getString("last_name"));
                    }
                }
            }
            dbConn.commit();
            System.out.println();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Takes the scanner input and the months of EITHER JUNE OR MARCH ONLY and asks
     * the user for a date in each respective month until a valid date is inputed.
     *
     * 
     * @param input A Scanner Object used to take user input the Scanner object used
     *              to read user input
     * @param month the months of either march or june to validate
     * @return Returns a string of the valid user date in the respective month, or
     *         null if user exits
     */
    private static String validateDate(Scanner input, String month) {
        int days = 31;
        month = month.toLowerCase().trim();
        if (month.equals("june")) {
            days = 30;
        }
        String date = "";
        while (true) {
            System.out.printf("Enter a date from %s that you would like to query (1-%d).\n", month, days);
            System.out.print(">: ");
            date = input.nextLine().trim();
            // go back to main loop if user types exit
            if (date.equals("exit")) {
                return null;
            }
            try {
                int dateNum = Integer.parseInt(date);
                // if the user enters a valid number break the while loop, else keep going
                if (dateNum > 0) {
                    if (month.equals("june") && dateNum < 31) {
                        break;
                    }
                    if (month.equals("march") && dateNum < 32) {
                        break;
                    }
                }
                // if user does not enter a number
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1 to 31 (inclusive)");
            }
        }
        return date;
    }

    /**
     * Provides the user with a help screen displaying the contents of each query
     */
    private static void helpScreen() {
        String query2 = "For any airline entered by the user, print the list of passengers, with the number of checked-in bags,\n"
                +
                "         sorted in ascending order by the number of checked-in bags and displays the output grouped by flights\n"
                +
                "         for a date in March that you choose.\n";
        String query3 = "Displays the schedule for flights on a date in June that you choose.\n" +
                "         The schedule contains the flight number, gate number, name of the airline of that flight,\n" +
                "         boarding time, departing time, duration of flight, and the origin and destination fo the flight.\n"
                +
                "         Sorted in ascending order of boarding time.\n";
        System.out.println();
        System.out.println("HELP SCREEN\n");
        System.out.println(
                "QUERY 1: Display the list of distinct passenger names, who took flights from all four airlines in the year 2021.\n");
        System.out.printf("QUERY 2: %s\n", query2);
        System.out.printf("QUERY 3: %s\n", query3);
        System.out.println(
                "QUERY 4: Displays the a list of Students, Frequent Flyers, and Priority Boarding for United Airlines who: ");
        System.out.println("         1.) Traveled only once in the month of March.");
        System.out.println(
                "         2.) Traveled with exactly one checked in bag anytime in the months of June aand July.");
        System.out.println("         3.) Ordered snacks/beverages on at least one flight.\n");
        System.out.println(
                "QUERY 5: For each airline, list the passenger that has had the most flights on that airline that is not a frequent flyer.");
    }
}
