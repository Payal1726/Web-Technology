# E-Bookshop Servlet Application

This project is a Jakarta Servlet application for Tomcat 10. It connects to the
`ebookshop` MySQL database and shows the `books` table in a browser.

## Current environment

- Tomcat home:
  `C:\Users\payal\OneDrive\Desktop\Tomcat1.1\apache-tomcat-10.1.54-windows-x64\apache-tomcat-10.1.54`
- Database URL default:
  `jdbc:mysql://localhost:3306/ebookshop`
- Database user default:
  `root`
- Database password default:
  blank

## Project layout

```text
Assignment 7/
  BookServlet.java
  index.html
  WEB-INF/
    classes/
    lib/
      mysql-connector-j-9.6.0.jar
    web.xml
  scripts/
    deploy-tomcat.ps1
    start-tomcat.ps1
    stop-tomcat.ps1
```

## How to run

Open PowerShell in this project folder and run:

```powershell
.\scripts\deploy-tomcat.ps1
.\scripts\start-tomcat.ps1
```

Then open:

- http://localhost:8080/ebookshop/
- http://localhost:8080/ebookshop/books

To stop Tomcat:

```powershell
.\scripts\stop-tomcat.ps1
```

## Optional database overrides

The servlet uses the defaults above, but you can override them with either:

- Environment variables:
  `EBOOKSHOP_DB_URL`, `EBOOKSHOP_DB_USER`, `EBOOKSHOP_DB_PASSWORD`
- Java system properties:
  `ebookshop.db.url`, `ebookshop.db.user`, `ebookshop.db.password`

Example:

```powershell
.\scripts\start-tomcat.ps1 -DbUser "root" -DbPassword "your_password"
```

## Notes

- This project is written with `jakarta.servlet.*`, so it should run on
  Tomcat 10.1.x, not Tomcat 8/9.
- The MySQL JDBC driver is already included in `WEB-INF/lib`, so no extra jar
  copy is needed.
