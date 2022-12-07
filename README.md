# CSC460-Project-4
In this project, we built a database–driven information management system from ground up by implementing a a two–tier client–server architecture on Oracles DBMS. The user is able to query the database via the JDBC text based program Frontend.java.

The underlying database mimics an airport with the ability to store passenger, flight, and staff member information. Users are able to add, update and delete fields in this table through the text application. In addition, there are 5 pre-made queries.
<ol>
    <li>  Display the list of distinct passenger names, who took flights from all four airlines in the year 2021.</li>
    <li>  For any airline entered by the user, print the list of passengers, with the number of checked–in bags. Sort the list in ascending order of the number of             checked–in bags. Display the output grouped by flights for a particular date in March (input by the user).</li>
    <li>  Print the schedule of flights for a given date in June (input by the user). The schedule should contain the
          flight number, gate number, name of the airline of that flight, boarding time, departing time, duration
          of flight, route of the flight (i.e. origin for arriving flights and destination for the departing flights). Sort
          the schedule in ascending order of the boarding time.</li>
    <li>  Print the list for the three categories of passengers (Student, Frequent Flyer, Frequent Flyer)
          for each of the following queries for United Airlines who:
          <ol>
            <li>Traveled only once in the month of March</li>
            <li>Traveled with exactly one checked in bag anytime in the months of June and July</li>
            <li>Ordered snacks/beverages on at least on one flight</li>
          </ol> </li>
        
        
   <li> For each airline, list the passenger that has had the most flights on that airline that is not a frequent flyer.</li></ol>

To Run JDBC application:
<ol>
<li>export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${PWD} </li>
<li>javac Frontend.java </li>
<li>java Frontend</li> </ol>

To run DataBasePopulator application:
<ol>
<li>export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${PWD} </li>
<li>javac DataBasePopulator.java </li>
<li>java DataBasePopulator</li> </ol>

Anthony Bisgood:
<ol>
<li> Query 2 </li>
<li> Ability for user to add, update or delete a passenger and their details, including the passenger’s history. </li>
<li> Ability for user to insert, update or delete flights, staff and their details. </li> </ol>

Jesse Gomez:
<ol>
<li> Query 1 and 3 </li>
<li> Relational schema diagram </li>
<li> Functional Dependencies </li>

