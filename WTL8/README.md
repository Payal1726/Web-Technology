# Student Portal JSP Demo

This project demonstrates a simple JSP-based web application that connects to a MySQL database, reads all rows from the `students_info` table, and displays them in an HTML table.

## Project structure

- `src/main/webapp/students.jsp` renders the student list.
- `src/main/java/com/college/portal/dao/StudentDAO.java` runs the SQL `SELECT` query.
- `src/main/java/com/college/portal/util/DBConnection.java` creates the JDBC connection.
- `database/students_info.sql` creates the database table and inserts sample data.

## Database setup

1. Start your MySQL server.
2. Open MySQL command line or MySQL Workbench.
3. Run the script in `database/students_info.sql`.
4. Update `src/main/resources/db.properties` if your MySQL username, password, host, or database name are different.

Default connection details:

- Database: `college_portal`
- Username: `root`
- Password: `root`

## Build and deploy

1. Install Apache Maven if it is not already available.
2. From the project root, run:

   ```powershell
   mvn clean package
   ```

3. Deploy the generated `target/student-portal.war` file to Apache Tomcat 10.1 or later.
4. Open:

   ```text
   http://localhost:8080/student-portal/
   ```

## Notes

- This project targets Jakarta Servlet/JSP APIs, so it is meant for Tomcat 10.1+.
- The MySQL JDBC driver is pulled automatically by Maven.
- If the page shows a database error, verify that MySQL is running and the values in `db.properties` are correct.
