# WGU C195 - Software II Advanced Java Concepts Performance Assessment
- Author: Stephanie DelBono
- Email: sdelbon@wgu.edu
- Application Version: 1.0
- Submission Date: September 25, 2022


# Title: Scheduling Application
## Purpose: To provide a Scheduling Desktop Application for a global consulting organization.


## Functionalities and Features:
Upon program start, the user is presented with a login screen. The user will be required to enter a valid username
and password. The login form supports language translation based on the user's computer language setting. All user
login attempts are automatically recorded and can be found in the login_activity.txt file. Once validated, the user
will have access to the company's database.


## Program Features:
- Utilitzes JDBC to manipulate data from an external SQL Database
- Includes User Account and Login Support
- Localization implemented using .properties files
- Supports three Time Zones including UTC, EST, and Local Time
- Generated notifications for users with an appointment within 15 minutes of logging in
- Add, Update, View, and Delete Customer items from the mySQL database
- Add, Update, View, and Delete Appointment items from the mySQL database 
- Generate Reports based on countries, customers, contacts or appointments


## Development Environment:
- IDE: Intellij IDEA Community Edition Version 2022.2
- JDK: 17.0.1
- JavaFX SDK: 17.0.1
- JDBC: mysql-connector-java-8.0.30



### Directions:
- Use the sqldatabase.properties file and input the connection information for the database
- Open C195_PA_Project file with IntelliJ IDEA
- Update the paths for your JDK, JDBC, and JavaFX SDK
- Press Run



### Additional Report:
For the third custom report, I chose to generate a location report to display the total of
customers per country. I created a SQL query using inner joins to combine the three tables and
display two columns. The Location column represents the name of the country and the Total column
represents the total number of customers per country.