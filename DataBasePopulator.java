
import java.sql.*;
import java.util.*;

public class DataBasePopulator {
    public static void main(String[] args) {
        modifier x = new modifier();
        x.dropAll();
        new setUpTables();
        x.createSimple();
        x.createPassengers();
        x.createStaff();
        x.createPassengerBenefits();
        // new populator();
        x.createFlight();
        x.createTrips();
    }

    public static final ArrayList<String> tables = new ArrayList<String>(
            Arrays.asList("passenger", "passenger_benefit", "benefit", "passenger_history", "passenger_trip",
                    "role", "staff_trip", "staff_member", "flight", "airline"));

    public static class setUpTables {
        setUpTables() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;

            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);

                // creates passenger table
                String pass_table_fields = "passenger_id integer , email varchar2(20), phone_number varchar2(20), address varchar2(20), first_name varchar2(20), last_name varchar2(20)";
                String pass_table_key = "passenger_id";
                createTables(dbconn, "passenger", pass_table_fields, pass_table_key);

                // creater passenger benefits
                String pass_benefits_key = "passenger_id integer , id integer , benefit_id  integer";
                String pass_ben_key = "id";
                createTables(dbconn, "passenger_benefit", pass_benefits_key, pass_ben_key);

                // creates benefits table
                String benefits = "benefit_id integer , category varchar2(20)";
                String bpk = "benefit_id";
                createTables(dbconn, "benefit", benefits, bpk);

                // creates passenger history
                String ph = "id integer , passenger_id varchar2(20), flight_id integer";
                String phpk = "id";
                createTables(dbconn, "passenger_history", ph, phpk);

                // creates passenger trips
                String pt = "id integer , passenger_id integer, flight_id integer, num_bags integer, num_items_purchased integer";
                String ptpk = "id";
                createTables(dbconn, "passenger_trip", pt, ptpk);

                // // creates roles
                String r = "occupation_id integer , occupation_name varchar2(20)";
                String rpk = "occupation_id";
                createTables(dbconn, "role", r, rpk);

                // creates staff trips
                String st = "id integer , staff_member_id integer, flight_id integer";
                String stpk = "id";
                createTables(dbconn, "staff_trip", st, stpk);

                // creates staff member
                String sm = "staff_member_id integer , role_id integer, first_name varchar2(20), last_name varchar2(20), address varchar2(20), phone_number  varchar2(20), email varchar2(20), salary varchar2(20)";
                String smpk = "staff_member_id";
                createTables(dbconn, "staff_member", sm, smpk);

                // creates flight
                String f = "flight_id integer , airline_id integer, boarding_gate varchar2(20), departing_time TIMESTAMP, boarding_time TIMESTAMP, duration varchar2(20), origin varchar2(20), destination varchar2(20)";
                String fpk = "flight_id";
                createTables(dbconn, "flight", f, fpk);

                // creates staff trips
                String a = "airline_id integer , name varchar2(20)";
                String apk = "airline_id";
                createTables(dbconn, "airline", a, apk);
                dbconn.close();
            } catch (SQLException e) {
                System.out.println("error");
                System.out.println(e);
            }
        }

        private void createTables(Connection conn, String tableName, String query, String pk) {
            try {
                // System.out.println(tableName);
                Statement stmt = conn.createStatement();
                String table_constructor = "Create  table " + tableName + "  ( ";
                String end = ", constraint t_pk" + tableName
                        + " primary key(" + pk + "))";
                table_constructor = table_constructor + query + end;
                // System.out.println(table_constructor);
                table_constructor = table_constructor.replaceAll("\n", "");
                try {
                    stmt.execute(table_constructor);
                } catch (Exception e) {
                    System.out.println("Couldn't create table");
                    System.out.println(e);
                }
                String seq_name = tableName + "_seq";
                String create_seq = "CREATE SEQUENCE " + seq_name + " START WITH 1";
                try {
                    stmt.execute(create_seq);
                } catch (Exception e) {
                    System.out.println(e);
                }
                String trigger = "CREATE OR REPLACE TRIGGER  " + tableName + "trig BEFORE INSERT ON " + tableName
                        + " FOR EACH ROW BEGIN";
                trigger += "   :new." + pk + " :=  " + seq_name + ".NEXTVAL; END;";
                try {
                    stmt.execute(trigger);
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println(trigger);
                    System.out.println(tableName);
                }
                conn.commit();
                stmt.close();
            } catch (Exception e) {
                System.out.println(e);
            }

        }

    }

    public static class populator {
        populator() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;

            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static class modifier {
        modifier() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;
            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public String randString() {
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            String generatedString = buffer.toString();

            return generatedString;
        }

        public void createPassengers() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;
            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                for (int i = 0; i < 30; i++) {
                    PreparedStatement sql = dbconn.prepareStatement(
                            "insert into passenger  (email, phone_number, address, first_name, last_name) values (?,?,?,?,?)");
                    for (int j = 1; j < 6; j++) {
                        sql.setString(j, randString());
                    }
                    sql.execute();
                }
                dbconn.commit();
                dbconn.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void createPassengerBenefits() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;

            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                Statement stmt = dbconn.createStatement(); // statement for the dbconnection
                ResultSet answer = stmt.executeQuery("select passenger_id from passenger");
                ArrayList<String> parray = new ArrayList<String>(); // holds the states abreviations
                while (answer.next()) {
                    parray.add(answer.getString("passenger_id"));
                }
                ResultSet answer2 = stmt.executeQuery("select benefit_id from benefit");
                ArrayList<String> barray = new ArrayList<String>(); // holds the states abreviations
                while (answer2.next()) {
                    barray.add(answer2.getString("benefit_id"));
                }
                Random rand = new Random();

                for (int i = 0; i < 30; i++) {
                    PreparedStatement sql = dbconn.prepareStatement(
                            "insert into passenger_benefit  (passenger_id, benefit_id) values (?,?)");
                    sql.setString(1, parray.get(rand.nextInt(30)));
                    sql.setString(2, barray.get(rand.nextInt(3)));

                    sql.execute();
                }
                dbconn.commit();
                dbconn.close();
            } catch (Exception e) {
                System.out.println(e);
            }

        }

        public void createTrips() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;

            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                Statement stmt = dbconn.createStatement(); // statement for the dbconnection
                ResultSet answer = stmt.executeQuery("select passenger_id from passenger");
                ArrayList<Integer> parray = new ArrayList<Integer>(); // holds the states abreviations
                while (answer.next()) {
                    parray.add(answer.getInt("passenger_id"));
                }
                ResultSet answer2 = stmt.executeQuery("select staff_member_id from staff_member");
                ArrayList<Integer> starray = new ArrayList<Integer>(); // holds the states abreviations
                while (answer2.next()) {
                    starray.add(answer2.getInt("staff_member_id"));
                }

                ResultSet answer3 = stmt.executeQuery("select flight_id from flight");
                ArrayList<Integer> flightarray = new ArrayList<Integer>(); // holds the states abreviations
                while (answer3.next()) {
                    flightarray.add(answer3.getInt("flight_id"));
                }

                Random rand = new Random();

                for (int i = 0; i < 100; i++) {
                    dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                    dbconn.setAutoCommit(false);
                    int flight = flightarray.get(rand.nextInt(100));

                    for (int j = 0; j < 5; j++) {
                        PreparedStatement sql = dbconn.prepareStatement(
                                "insert into staff_trip  (staff_member_id, flight_id) values (?,?)");
                        sql.setInt(1, starray.get(rand.nextInt(30)));
                        sql.setInt(2, flight);
                        sql.execute();
                    }
                    for (int j = 0; j < 5; j++) {
                        PreparedStatement sql = dbconn.prepareStatement(
                                "insert into passenger_trip  (passenger_id, flight_id, num_bags, num_items_purchased) values (?,?,?, ?)");
                        sql.setInt(1, parray.get(rand.nextInt(30)));
                        sql.setInt(2, flight);
                        sql.setInt(3, rand.nextInt(4));
                        sql.setInt(4, rand.nextInt(4));
                        sql.execute();
                    }
                    dbconn.commit();
                    dbconn.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }

        public void createStaff() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;
            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                Statement stmt = dbconn.createStatement(); // statement for the dbconnection
                ResultSet answer = stmt.executeQuery("select occupation_id from role");
                ArrayList<String> occ = new ArrayList<String>(); // holds the states abreviations
                while (answer.next()) {
                    occ.add(answer.getString("occupation_id"));
                }
                Random rand = new Random();
                for (int i = 0; i < 30; i++) {
                    PreparedStatement sql = dbconn.prepareStatement(
                            "insert into staff_member  (role_id, first_name, last_name, address, email, phone_number, salary) values (?,?,?,?,?, ? , ?)");
                    for (int j = 2; j < 8; j++) {
                        sql.setString(j, randString());
                    }
                    sql.setString(1, occ.get(rand.nextInt(5)));
                    sql.execute();
                }
                dbconn.commit();
                dbconn.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void createFlight() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;
            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                Random rand = new Random();

                Statement stmt = dbconn.createStatement(); // statement for the dbconnection
                ResultSet answer = stmt.executeQuery("select airline_id from airline");
                ArrayList<String> airlines = new ArrayList<String>(); // holds the states abreviations
                while (answer.next()) {
                    airlines.add(answer.getString("airline_id"));
                }
                for (int i = 0; i < 100; i++) {
                    PreparedStatement sql = dbconn.prepareStatement(
                            "insert into flight  ( boarding_time, departing_time, boarding_gate,  duration, origin, destination, airline_id) values (?,?,?,?,?, ? , ?)");
                    for (int j = 3; j < 7; j++) {
                        if (j ==4) {continue;}
                        sql.setString(j, randString());
                    }
                    String randDuration = String.valueOf(rand.nextInt(20));
                    sql.setString(4, randDuration);
                    long rangeend = Timestamp.valueOf("2022-12-31 00:00:00").getTime();
                    long rangebegin = Timestamp.valueOf("2013-02-08 00:58:00").getTime();
                    long diff = rangeend - rangebegin + 1;
                    Timestamp randDate = new Timestamp(rangebegin + (long) (Math.random() * diff));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(randDate);
                    cal.add(Calendar.HOUR, rand.nextInt(23) + 1);
                    Timestamp endDate = new Timestamp(cal.getTime().getTime());
                    sql.setDate(1, new java.sql.Date(randDate.getTime()));
                    sql.setDate(2, new java.sql.Date(endDate.getTime()));
                    sql.setInt(7, rand.nextInt(4)+1);

                    sql.execute();
                }
                dbconn.commit();
                dbconn.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public String getRandomDate() {
            Random rand = new Random();
            return String.valueOf(rand.nextInt(22) + 2000) + "-" + String.valueOf(rand.nextInt(12) + 1)
                    + "-" + String.valueOf(rand.nextInt(31) + 1) + " " + String.valueOf(rand.nextInt(24)) + ":"
                    + String.valueOf(rand.nextInt(60)) + ":" + String.valueOf(rand.nextInt(60)) + ":0";
        }

        public void createSimple() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;
            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                Statement stmt = dbconn.createStatement();

                // populating benefits
                ArrayList<String> roles = new ArrayList<String>(
                        Arrays.asList(" 'Pilot'", "'Ground Crew'", "'Corporate' ", "'Other'", "'Cabin Crew'"));
                for (int i = 0; i < roles.size(); i++) {

                    stmt = dbconn.createStatement();
                    stmt.execute("insert into role  (occupation_name) values  (" + roles.get(i) + " )");
                }
                // populating benefits
                ArrayList<String> benefits = new ArrayList<String>(
                        Arrays.asList("'frequentflyer'", "'student'", "'priorityboarding'"));
                for (int i = 0; i < benefits.size(); i++) {
                    stmt = dbconn.createStatement();
                    stmt.execute("insert into benefit  (category) values  ( " + benefits.get(i) + " ) ");

                }

                ArrayList<String> airlines = new ArrayList<String>(
                        Arrays.asList("'Delta'", "'SouthWest'", "'United'", "'Alaska'"));
                for (int i = 0; i < airlines.size(); i++) {
                    stmt = dbconn.createStatement();
                    stmt.execute("insert into airline  (name) values  ( " + airlines.get(i) + " ) ");

                }

                dbconn.commit();
                dbconn.close();
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("didn't execute insert");
            }
        }

        public void dropAll() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;
            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                Statement stmt = dbconn.createStatement();
                ResultSet answer = stmt.executeQuery("select sequence_name from user_sequences");
                // while (answer.next()) {
                // System.out.println(answer.getString("sequence_name"));
                // }
                for (int i = 0; i < tables.size(); i++) {
                    try {
                        try {
                            stmt.executeUpdate("drop sequence " + tables.get(i) + "_seq");
                        } catch (Exception e) {
                            System.out.println("failed to drop sequence     " + tables.get(i) + "_seq");
                            System.out.println(e);
                        }
                        try {
                            stmt.executeUpdate("drop trigger " + tables.get(i) + "trig");
                        } catch (Exception e) {
                            System.out.println("failed to drop trigger");
                            System.out.println(e);
                        }
                        try {
                            stmt.executeUpdate("drop table " + tables.get(i));
                        } catch (Exception e) {
                            System.out.println("failed to drop table");
                            System.out.println(e);
                        }
                        try {
                            stmt.executeUpdate("purge table " + tables.get(i));
                        } catch (Exception e) {
                            System.out.println("failed to purge table");
                            System.out.println(e);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void dropCons() {
            final String oracleURL = // Magic lectura -> aloe access spell
                    "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
            Connection dbconn = null;
            try {
                dbconn = DriverManager.getConnection(oracleURL, "anthonybisgood", "a8156");
                dbconn.setAutoCommit(false);
                Statement stmt = dbconn.createStatement();
                for (int i = 0; i < tables.size(); i++) {
                    try {
                        System.out.println("drop table " + tables.get(i) + " purge");
                        stmt.executeUpdate("purge table " + tables.get(i));
                    } catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
