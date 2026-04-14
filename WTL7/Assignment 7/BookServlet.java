import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL_PROPERTY = "ebookshop.db.url";
    private static final String DB_USER_PROPERTY = "ebookshop.db.user";
    private static final String DB_PASSWORD_PROPERTY = "ebookshop.db.password";
    private static final String DB_URL_ENV = "EBOOKSHOP_DB_URL";
    private static final String DB_USER_ENV = "EBOOKSHOP_DB_USER";
    private static final String DB_PASSWORD_ENV = "EBOOKSHOP_DB_PASSWORD";
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/ebookshop";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "";

    private static final String BOOK_QUERY =
            "SELECT book_id, book_title, book_author, book_price, quantity FROM books ORDER BY book_id";
    private static final String INSERT_BOOK_QUERY =
            "INSERT INTO books (book_title, book_author, book_price, quantity) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_BOOK_QUERY =
            "UPDATE books SET book_title = ?, book_author = ?, book_price = ?, quantity = ? WHERE book_id = ?";
    private static final String DELETE_BOOK_QUERY =
            "DELETE FROM books WHERE book_id = ?";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String pagePath = request.getContextPath() + request.getServletPath();
        String feedbackMessage = request.getParameter("message");
        String feedbackType = normalizeFeedbackType(request.getParameter("type"));
        Integer editingBookId = parseOptionalInteger(request.getParameter("editId"));

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>E-Bookshop - Manage Books</title>");
            writeStyles(out);
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='page-shell'>");
            out.println("<div class='container'>");
            out.println("<div class='hero'>");
            out.println("<p class='eyebrow'>Assignment 7</p>");
            out.println("<h1>E-Bookshop Manager</h1>");
            out.println("<p class='hero-copy'>Add, update, and delete book records directly from this page.</p>");
            out.println("</div>");

            renderFeedback(out, feedbackMessage, feedbackType);
            renderAddForm(out, pagePath);

            try {
                List<BookRecord> books = fetchBooks();
                BookRecord editingBook = findBookById(books, editingBookId);

                if (editingBookId != null && editingBook == null) {
                    renderFeedback(out, "The selected book was not found. It may have been deleted already.", "error");
                }

                if (editingBook != null) {
                    renderUpdateForm(out, pagePath, editingBook);
                }

                renderBookTable(out, books, pagePath);
            } catch (ClassNotFoundException e) {
                getServletContext().log("Unable to load MySQL JDBC driver.", e);
                renderFeedback(out, "MySQL JDBC driver not found.", "error");
            } catch (SQLException e) {
                getServletContext().log("Database query failed while rendering the book page.", e);
                renderFeedback(out, "Database Error: " + e.getMessage(), "error");
            }

            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = trimToEmpty(request.getParameter("action"));
        String redirectUrl;

        try (Connection conn = openConnection()) {
            String message = processAction(action, request, conn);
            redirectUrl = buildRedirectUrl(request, "success", message);
        } catch (IllegalArgumentException e) {
            redirectUrl = buildRedirectUrl(request, "error", e.getMessage());
        } catch (ClassNotFoundException e) {
            getServletContext().log("Unable to load MySQL JDBC driver.", e);
            redirectUrl = buildRedirectUrl(request, "error", "MySQL JDBC driver not found.");
        } catch (SQLException e) {
            getServletContext().log("Database update failed for action " + action + ".", e);
            redirectUrl = buildRedirectUrl(request, "error", "Database operation failed: " + e.getMessage());
        }

        response.sendRedirect(redirectUrl);
    }

    private void writeStyles(PrintWriter out) {
        out.println("<style>");
        out.println(":root {");
        out.println("  --bg: #f4efe7;");
        out.println("  --surface: #ffffff;");
        out.println("  --surface-soft: #faf5ed;");
        out.println("  --ink: #22201c;");
        out.println("  --muted: #6c6358;");
        out.println("  --border: #ddcfbf;");
        out.println("  --accent: #1f6f5f;");
        out.println("  --accent-dark: #184f44;");
        out.println("  --warning: #b6462f;");
        out.println("  --warning-dark: #8b2d1b;");
        out.println("  --success-bg: #e7f6ef;");
        out.println("  --success-text: #1d6a4f;");
        out.println("  --error-bg: #fdeaea;");
        out.println("  --error-text: #9b2c2c;");
        out.println("  --shadow: 0 18px 40px rgba(55, 40, 22, 0.12);");
        out.println("}");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(180deg, #efe4d2 0%, #f9f5ef 38%, #f4efe7 100%); color: var(--ink); }");
        out.println(".page-shell { min-height: 100vh; padding: 32px 16px 48px; }");
        out.println(".container { max-width: 1180px; margin: 0 auto; }");
        out.println(".hero { text-align: center; margin-bottom: 28px; }");
        out.println(".eyebrow { text-transform: uppercase; letter-spacing: 0.22em; font-size: 0.8rem; color: var(--muted); margin: 0 0 10px; }");
        out.println("h1 { margin: 0; font-size: clamp(2rem, 4vw, 3.3rem); }");
        out.println(".hero-copy { max-width: 640px; margin: 14px auto 0; color: var(--muted); font-size: 1.05rem; }");
        out.println(".panel { background: var(--surface); border: 1px solid var(--border); border-radius: 22px; box-shadow: var(--shadow); padding: 24px; margin-bottom: 24px; }");
        out.println(".panel h2 { margin-top: 0; margin-bottom: 8px; }");
        out.println(".panel p { margin-top: 0; color: var(--muted); }");
        out.println(".form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 16px; }");
        out.println(".field { display: flex; flex-direction: column; gap: 8px; }");
        out.println(".field label { font-weight: 600; }");
        out.println(".field input { width: 100%; padding: 12px 14px; border-radius: 12px; border: 1px solid var(--border); background: #fffdf9; font-size: 0.98rem; }");
        out.println(".field input:focus { outline: none; border-color: var(--accent); box-shadow: 0 0 0 3px rgba(31, 111, 95, 0.15); }");
        out.println(".button-row { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 18px; }");
        out.println(".btn { display: inline-flex; align-items: center; justify-content: center; border: none; border-radius: 999px; padding: 11px 18px; cursor: pointer; text-decoration: none; font-size: 0.95rem; font-weight: 700; transition: transform 0.18s ease, box-shadow 0.18s ease, background-color 0.18s ease; }");
        out.println(".btn:hover { transform: translateY(-1px); }");
        out.println(".btn-primary { background: var(--accent); color: #fff; box-shadow: 0 10px 20px rgba(31, 111, 95, 0.2); }");
        out.println(".btn-primary:hover { background: var(--accent-dark); }");
        out.println(".btn-secondary { background: #f0e2cf; color: #5d4330; }");
        out.println(".btn-secondary:hover { background: #e6d3bc; }");
        out.println(".btn-danger { background: var(--warning); color: #fff; }");
        out.println(".btn-danger:hover { background: var(--warning-dark); }");
        out.println(".btn-link { background: transparent; border: 1px solid var(--border); color: var(--ink); }");
        out.println(".btn-link:hover { background: var(--surface-soft); }");
        out.println(".inline-form { display: inline; }");
        out.println(".action-cell { white-space: nowrap; display: flex; gap: 10px; align-items: center; }");
        out.println(".status { border-radius: 16px; padding: 14px 16px; margin-bottom: 20px; font-weight: 600; }");
        out.println(".status.success { background: var(--success-bg); color: var(--success-text); border: 1px solid #b9e3cf; }");
        out.println(".status.error { background: var(--error-bg); color: var(--error-text); border: 1px solid #f1c1c1; }");
        out.println(".table-wrap { overflow-x: auto; }");
        out.println("table { width: 100%; border-collapse: collapse; min-width: 760px; }");
        out.println("th, td { padding: 16px 14px; border-bottom: 1px solid #eee2d4; text-align: left; vertical-align: middle; }");
        out.println("th { background: #f8efe3; color: #4f4338; font-size: 0.95rem; }");
        out.println("tbody tr:hover { background: #fffcf7; }");
        out.println(".empty-state { padding: 18px; border: 1px dashed var(--border); border-radius: 16px; color: var(--muted); background: var(--surface-soft); }");
        out.println("@media (max-width: 768px) {");
        out.println("  .panel { padding: 18px; border-radius: 18px; }");
        out.println("  .button-row { flex-direction: column; align-items: stretch; }");
        out.println("  .action-cell { flex-direction: column; align-items: stretch; }");
        out.println("  .btn { width: 100%; }");
        out.println("}");
        out.println("</style>");
    }

    private void renderFeedback(PrintWriter out, String message, String type) {
        if (message == null || message.isBlank()) {
            return;
        }

        out.println("<div class='status " + escapeHtml(type) + "'>" + escapeHtml(message) + "</div>");
    }

    private void renderAddForm(PrintWriter out, String pagePath) {
        out.println("<section class='panel'>");
        out.println("<h2>Add New Book</h2>");
        out.println("<p>Use this form to insert a new record into the <strong>books</strong> table.</p>");
        out.println("<form method='post' action='" + escapeHtml(pagePath) + "'>");
        out.println("<input type='hidden' name='action' value='add'>");
        out.println("<div class='form-grid'>");
        renderTextField(out, "book_title", "Book Title", "", "100");
        renderTextField(out, "book_author", "Author", "", "100");
        renderNumberField(out, "book_price", "Price (Rs.)", "", "0.01", "0");
        renderNumberField(out, "quantity", "Quantity", "", "1", "0");
        out.println("</div>");
        out.println("<div class='button-row'>");
        out.println("<button type='submit' class='btn btn-primary'>Add Record</button>");
        out.println("</div>");
        out.println("</form>");
        out.println("</section>");
    }

    private void renderUpdateForm(PrintWriter out, String pagePath, BookRecord book) {
        out.println("<section class='panel'>");
        out.println("<h2>Update Record</h2>");
        out.println("<p>Editing book ID <strong>" + book.id + "</strong>. Change the fields and save your update.</p>");
        out.println("<form method='post' action='" + escapeHtml(pagePath) + "'>");
        out.println("<input type='hidden' name='action' value='update'>");
        out.println("<input type='hidden' name='book_id' value='" + book.id + "'>");
        out.println("<div class='form-grid'>");
        renderTextField(out, "book_title", "Book Title", book.title, "100");
        renderTextField(out, "book_author", "Author", book.author, "100");
        renderNumberField(out, "book_price", "Price (Rs.)", formatDecimal(book.price), "0.01", "0");
        renderNumberField(out, "quantity", "Quantity", String.valueOf(book.quantity), "1", "0");
        out.println("</div>");
        out.println("<div class='button-row'>");
        out.println("<button type='submit' class='btn btn-primary'>Update Record</button>");
        out.println("<a href='" + escapeHtml(pagePath) + "' class='btn btn-link'>Cancel</a>");
        out.println("</div>");
        out.println("</form>");
        out.println("</section>");
    }

    private void renderTextField(PrintWriter out, String name, String label, String value, String maxLength) {
        out.println("<div class='field'>");
        out.println("<label for='" + escapeHtml(name) + "'>" + escapeHtml(label) + "</label>");
        out.println("<input id='" + escapeHtml(name) + "' type='text' name='" + escapeHtml(name)
                + "' value='" + escapeHtml(value) + "' maxlength='" + escapeHtml(maxLength) + "' required>");
        out.println("</div>");
    }

    private void renderNumberField(PrintWriter out, String name, String label, String value, String step, String min) {
        out.println("<div class='field'>");
        out.println("<label for='" + escapeHtml(name) + "'>" + escapeHtml(label) + "</label>");
        out.println("<input id='" + escapeHtml(name) + "' type='number' name='" + escapeHtml(name)
                + "' value='" + escapeHtml(value) + "' step='" + escapeHtml(step) + "' min='" + escapeHtml(min)
                + "' required>");
        out.println("</div>");
    }

    private void renderBookTable(PrintWriter out, List<BookRecord> books, String pagePath) {
        out.println("<section class='panel'>");
        out.println("<h2>Available Books</h2>");
        out.println("<p>Each row has active buttons for update and delete operations.</p>");

        if (books.isEmpty()) {
            out.println("<div class='empty-state'>No books found in the database yet. Add one using the form above.</div>");
            out.println("</section>");
            return;
        }

        out.println("<div class='table-wrap'>");
        out.println("<table>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Book ID</th>");
        out.println("<th>Title</th>");
        out.println("<th>Author</th>");
        out.println("<th>Price (Rs.)</th>");
        out.println("<th>Quantity</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        for (BookRecord book : books) {
            out.println("<tr>");
            out.println("<td>" + book.id + "</td>");
            out.println("<td>" + escapeHtml(book.title) + "</td>");
            out.println("<td>" + escapeHtml(book.author) + "</td>");
            out.println("<td>Rs. " + formatDecimal(book.price) + "</td>");
            out.println("<td>" + book.quantity + "</td>");
            out.println("<td class='action-cell'>");
            out.println("<a class='btn btn-secondary' href='" + escapeHtml(pagePath) + "?editId=" + book.id + "'>Update</a>");
            out.println("<form method='post' action='" + escapeHtml(pagePath)
                    + "' class='inline-form' onsubmit=\"return confirm('Delete this record?');\">");
            out.println("<input type='hidden' name='action' value='delete'>");
            out.println("<input type='hidden' name='book_id' value='" + book.id + "'>");
            out.println("<button type='submit' class='btn btn-danger'>Delete</button>");
            out.println("</form>");
            out.println("</td>");
            out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");
        out.println("</section>");
    }

    private List<BookRecord> fetchBooks() throws SQLException, ClassNotFoundException {
        loadDriver();

        List<BookRecord> books = new ArrayList<>();
        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(BOOK_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(new BookRecord(
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getString("book_author"),
                        rs.getDouble("book_price"),
                        rs.getInt("quantity")));
            }
        }

        return books;
    }

    private String processAction(String action, HttpServletRequest request, Connection conn) throws SQLException {
        switch (action) {
            case "add":
                return addBook(request, conn);
            case "update":
                return updateBook(request, conn);
            case "delete":
                return deleteBook(request, conn);
            default:
                throw new IllegalArgumentException("Unknown action requested.");
        }
    }

    private String addBook(HttpServletRequest request, Connection conn) throws SQLException {
        String title = requireText(request, "book_title", "Book title");
        String author = requireText(request, "book_author", "Author");
        double price = requireNonNegativeDouble(request, "book_price", "Price");
        int quantity = requireNonNegativeInteger(request, "quantity", "Quantity");

        try (PreparedStatement stmt = conn.prepareStatement(INSERT_BOOK_QUERY)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No row was inserted.");
            }
        }

        return "Book added successfully.";
    }

    private String updateBook(HttpServletRequest request, Connection conn) throws SQLException {
        int bookId = requirePositiveInteger(request, "book_id", "Book ID");
        String title = requireText(request, "book_title", "Book title");
        String author = requireText(request, "book_author", "Author");
        double price = requireNonNegativeDouble(request, "book_price", "Price");
        int quantity = requireNonNegativeInteger(request, "quantity", "Quantity");

        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOK_QUERY)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setInt(5, bookId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("The selected book record was not found.");
            }
        }

        return "Book updated successfully.";
    }

    private String deleteBook(HttpServletRequest request, Connection conn) throws SQLException {
        int bookId = requirePositiveInteger(request, "book_id", "Book ID");

        try (PreparedStatement stmt = conn.prepareStatement(DELETE_BOOK_QUERY)) {
            stmt.setInt(1, bookId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("The selected book record was not found.");
            }
        }

        return "Book deleted successfully.";
    }

    private Connection openConnection() throws SQLException, ClassNotFoundException {
        loadDriver();
        return DriverManager.getConnection(getDatabaseUrl(), getDatabaseUser(), getDatabasePassword());
    }

    private void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    private BookRecord findBookById(List<BookRecord> books, Integer bookId) {
        if (bookId == null) {
            return null;
        }

        for (BookRecord book : books) {
            if (book.id == bookId) {
                return book;
            }
        }

        return null;
    }

    private int requirePositiveInteger(HttpServletRequest request, String parameterName, String label) {
        int value = parseInteger(request.getParameter(parameterName), label);
        if (value <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero.");
        }
        return value;
    }

    private int requireNonNegativeInteger(HttpServletRequest request, String parameterName, String label) {
        int value = parseInteger(request.getParameter(parameterName), label);
        if (value < 0) {
            throw new IllegalArgumentException(label + " cannot be negative.");
        }
        return value;
    }

    private double requireNonNegativeDouble(HttpServletRequest request, String parameterName, String label) {
        double value = parseDouble(request.getParameter(parameterName), label);
        if (value < 0) {
            throw new IllegalArgumentException(label + " cannot be negative.");
        }
        return value;
    }

    private String requireText(HttpServletRequest request, String parameterName, String label) {
        String value = trimToEmpty(request.getParameter(parameterName));
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value;
    }

    private int parseInteger(String rawValue, String label) {
        String value = trimToEmpty(rawValue);
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
    }

    private double parseDouble(String rawValue, String label) {
        String value = trimToEmpty(rawValue);
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a valid number.");
        }
    }

    private Integer parseOptionalInteger(String rawValue) {
        String value = trimToEmpty(rawValue);
        if (value.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeFeedbackType(String type) {
        if ("error".equalsIgnoreCase(type)) {
            return "error";
        }
        return "success";
    }

    private String buildRedirectUrl(HttpServletRequest request, String type, String message) {
        return request.getContextPath() + request.getServletPath()
                + "?type=" + urlEncode(type)
                + "&message=" + urlEncode(message);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String formatDecimal(double value) {
        if (Math.rint(value) == value) {
            return String.format("%.0f", value);
        }
        return String.format("%.2f", value);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String getDatabaseUrl() {
        return getConfigValue(DB_URL_PROPERTY, DB_URL_ENV, DEFAULT_DB_URL);
    }

    private String getDatabaseUser() {
        return getConfigValue(DB_USER_PROPERTY, DB_USER_ENV, DEFAULT_DB_USER);
    }

    private String getDatabasePassword() {
        return getConfigValue(DB_PASSWORD_PROPERTY, DB_PASSWORD_ENV, DEFAULT_DB_PASSWORD);
    }

    private String getConfigValue(String propertyName, String environmentName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return defaultValue;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static final class BookRecord {
        private final int id;
        private final String title;
        private final String author;
        private final double price;
        private final int quantity;

        private BookRecord(int id, String title, String author, double price, int quantity) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
