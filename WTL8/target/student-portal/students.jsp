<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List,java.util.Collections,java.sql.SQLIntegrityConstraintViolationException,com.college.portal.dao.StudentDAO,com.college.portal.model.Student" %>
<%!
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

    private String trimValue(String value) {
        return value == null ? "" : value.trim();
    }
%>
<%
    request.setCharacterEncoding("UTF-8");

    StudentDAO studentDao = new StudentDAO();
    List<Student> students = Collections.emptyList();
    String errorMessage = null;
    String successMessage = null;
    boolean editMode = false;

    String formStudId = "";
    String formStudName = "";
    String formClass = "";
    String formDivision = "";
    String formCity = "";

    String status = trimValue(request.getParameter("status"));
    if ("added".equals(status)) {
        successMessage = "Student record added successfully.";
    } else if ("updated".equals(status)) {
        successMessage = "Student record updated successfully.";
    } else if ("deleted".equals(status)) {
        successMessage = "Student record deleted successfully.";
    } else if ("missing".equals(status)) {
        errorMessage = "The selected student record could not be found.";
    }

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        String action = trimValue(request.getParameter("action"));

        formStudId = trimValue(request.getParameter("studId"));
        formStudName = trimValue(request.getParameter("studName"));
        formClass = trimValue(request.getParameter("studentClass"));
        formDivision = trimValue(request.getParameter("division"));
        formCity = trimValue(request.getParameter("city"));

        try {
            if ("add".equals(action) || "update".equals(action)) {
                if (formStudId.isEmpty() || formStudName.isEmpty() || formClass.isEmpty()
                        || formDivision.isEmpty() || formCity.isEmpty()) {
                    throw new IllegalArgumentException("Please fill in all fields before submitting the form.");
                }

                int studId = Integer.parseInt(formStudId);
                if (studId <= 0) {
                    throw new IllegalArgumentException("Student ID must be greater than zero.");
                }

                Student submittedStudent = new Student(studId, formStudName, formClass, formDivision, formCity);

                if ("add".equals(action)) {
                    studentDao.addStudent(submittedStudent);
                    response.sendRedirect("students.jsp?status=added");
                    return;
                }

                editMode = true;
                if (studentDao.updateStudent(submittedStudent)) {
                    response.sendRedirect("students.jsp?status=updated");
                    return;
                }

                errorMessage = "Unable to update the selected student record.";
            } else if ("delete".equals(action)) {
                String deleteIdValue = trimValue(request.getParameter("studId"));
                if (deleteIdValue.isEmpty()) {
                    throw new IllegalArgumentException("Student ID is required to delete a record.");
                }

                int deleteId = Integer.parseInt(deleteIdValue);
                if (studentDao.deleteStudent(deleteId)) {
                    response.sendRedirect("students.jsp?status=deleted");
                    return;
                }

                response.sendRedirect("students.jsp?status=missing");
                return;
            }
        } catch (NumberFormatException exception) {
            errorMessage = "Student ID must be a valid whole number.";
            editMode = "update".equals(action);
        } catch (SQLIntegrityConstraintViolationException exception) {
            errorMessage = "Student ID already exists. Please use a unique Student ID.";
        } catch (IllegalArgumentException exception) {
            errorMessage = exception.getMessage();
            editMode = "update".equals(action);
        } catch (Exception exception) {
            errorMessage = exception.getMessage();
            editMode = "update".equals(action);
        }
    }

    String editIdValue = trimValue(request.getParameter("editId"));
    if (!editIdValue.isEmpty() && !editMode) {
        try {
            int editId = Integer.parseInt(editIdValue);
            Student selectedStudent = studentDao.getStudentById(editId);

            if (selectedStudent != null) {
                editMode = true;
                formStudId = String.valueOf(selectedStudent.getStudId());
                formStudName = selectedStudent.getStudName();
                formClass = selectedStudent.getStudentClass();
                formDivision = selectedStudent.getDivision();
                formCity = selectedStudent.getCity();
            } else if (errorMessage == null) {
                errorMessage = "The selected student record could not be loaded for editing.";
            }
        } catch (NumberFormatException exception) {
            if (errorMessage == null) {
                errorMessage = "Invalid student selected for editing.";
            }
        } catch (Exception exception) {
            if (errorMessage == null) {
                errorMessage = exception.getMessage();
            }
        }
    }

    try {
        students = studentDao.getAllStudents();
    } catch (Exception exception) {
        if (errorMessage == null) {
            errorMessage = exception.getMessage();
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Information Portal</title>
    <style>
        :root {
            --bg-start: #041421;
            --bg-end: #0a2c3d;
            --panel: rgba(9, 25, 39, 0.82);
            --panel-strong: #0c2234;
            --surface: #0f3146;
            --surface-soft: #153d57;
            --line: rgba(154, 196, 215, 0.20);
            --ink: #edf8ff;
            --muted: #9fbed0;
            --accent: #39d0c3;
            --accent-strong: #19a698;
            --accent-soft: rgba(57, 208, 195, 0.16);
            --highlight: #ffd166;
            --danger: #ff7f73;
            --danger-soft: rgba(255, 127, 115, 0.18);
            --success-bg: rgba(57, 208, 195, 0.18);
            --success-text: #9af7ef;
            --error-bg: rgba(255, 127, 115, 0.18);
            --error-text: #ffc1ba;
            --input-bg: rgba(6, 20, 33, 0.72);
            --shadow: 0 24px 70px rgba(0, 0, 0, 0.28);
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Trebuchet MS", "Segoe UI", sans-serif;
            background:
                radial-gradient(circle at top left, rgba(57, 208, 195, 0.16), transparent 30%),
                radial-gradient(circle at top right, rgba(255, 209, 102, 0.14), transparent 20%),
                linear-gradient(135deg, var(--bg-start) 0%, var(--bg-end) 100%);
            color: var(--ink);
        }

        a {
            color: inherit;
            text-decoration: none;
        }

        code {
            padding: 2px 8px;
            border-radius: 999px;
            background: rgba(255, 255, 255, 0.08);
            color: #d9fbf8;
        }

        .page {
            max-width: 1180px;
            margin: 42px auto 56px;
            padding: 0 20px;
        }

        .hero {
            position: relative;
            overflow: hidden;
            padding: 30px;
            border: 1px solid var(--line);
            border-radius: 28px;
            background: linear-gradient(160deg, rgba(12, 34, 52, 0.96), rgba(10, 46, 63, 0.88));
            box-shadow: var(--shadow);
        }

        .hero::after {
            content: "";
            position: absolute;
            inset: auto -60px -80px auto;
            width: 220px;
            height: 220px;
            border-radius: 50%;
            background: radial-gradient(circle, rgba(57, 208, 195, 0.24), transparent 65%);
        }

        .eyebrow {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            padding: 8px 14px;
            border-radius: 999px;
            background: var(--accent-soft);
            border: 1px solid rgba(57, 208, 195, 0.24);
            color: var(--accent);
            font-size: 0.82rem;
            font-weight: 700;
            letter-spacing: 0.12em;
            text-transform: uppercase;
        }

        h1 {
            margin: 18px 0 10px;
            font-size: clamp(2.2rem, 4vw, 3.5rem);
            line-height: 1.05;
        }

        .subtitle {
            max-width: 760px;
            margin: 0;
            color: var(--muted);
            line-height: 1.7;
            font-size: 1rem;
        }

        .hero-stats {
            display: flex;
            flex-wrap: wrap;
            gap: 14px;
            margin-top: 24px;
        }

        .stat {
            min-width: 160px;
            padding: 16px 18px;
            border-radius: 18px;
            border: 1px solid var(--line);
            background: rgba(255, 255, 255, 0.04);
        }

        .stat-label {
            display: block;
            color: var(--muted);
            font-size: 0.84rem;
            text-transform: uppercase;
            letter-spacing: 0.08em;
        }

        .stat-value {
            display: block;
            margin-top: 8px;
            font-size: 1.55rem;
            font-weight: 700;
            color: var(--ink);
        }

        .layout {
            display: grid;
            grid-template-columns: 360px minmax(0, 1fr);
            gap: 24px;
            margin-top: 26px;
            align-items: start;
        }

        .panel {
            border: 1px solid var(--line);
            border-radius: 26px;
            background: var(--panel);
            backdrop-filter: blur(12px);
            box-shadow: var(--shadow);
        }

        .panel-head {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 16px;
            padding: 24px 24px 0;
        }

        .panel-head h2 {
            margin: 0;
            font-size: 1.35rem;
        }

        .panel-copy {
            margin: 8px 0 0;
            color: var(--muted);
            line-height: 1.6;
        }

        .mode-pill {
            padding: 8px 12px;
            border-radius: 999px;
            background: rgba(255, 209, 102, 0.12);
            border: 1px solid rgba(255, 209, 102, 0.24);
            color: var(--highlight);
            font-size: 0.82rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            white-space: nowrap;
        }

        .message {
            margin-top: 22px;
            padding: 16px 18px;
            border-radius: 18px;
            font-weight: 600;
        }

        .message.success {
            border: 1px solid rgba(57, 208, 195, 0.24);
            background: var(--success-bg);
            color: var(--success-text);
        }

        .message.error {
            border: 1px solid rgba(255, 127, 115, 0.24);
            background: var(--error-bg);
            color: var(--error-text);
        }

        .form-body {
            padding: 22px 24px 24px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr;
            gap: 16px;
        }

        .field {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .field label {
            font-size: 0.92rem;
            font-weight: 700;
            color: #d8edf7;
        }

        .field input {
            width: 100%;
            padding: 13px 14px;
            border: 1px solid rgba(154, 196, 215, 0.18);
            border-radius: 16px;
            background: var(--input-bg);
            color: var(--ink);
            outline: none;
            transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
        }

        .field input:focus {
            border-color: rgba(57, 208, 195, 0.55);
            box-shadow: 0 0 0 4px rgba(57, 208, 195, 0.14);
            transform: translateY(-1px);
        }

        .field-note {
            margin: -2px 0 0;
            color: var(--muted);
            font-size: 0.84rem;
            line-height: 1.5;
        }

        .button-row,
        .table-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }

        .button-row {
            margin-top: 22px;
        }

        .button {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-height: 44px;
            padding: 0 18px;
            border: 0;
            border-radius: 14px;
            cursor: pointer;
            font-size: 0.95rem;
            font-weight: 700;
            transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
        }

        .button:hover {
            transform: translateY(-1px);
        }

        .button-primary {
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            color: #04202b;
            box-shadow: 0 14px 26px rgba(25, 166, 152, 0.24);
        }

        .button-secondary {
            background: rgba(255, 255, 255, 0.08);
            color: var(--ink);
            border: 1px solid var(--line);
        }

        .button-danger {
            background: var(--danger-soft);
            color: #ffd9d5;
            border: 1px solid rgba(255, 127, 115, 0.28);
        }

        .button-ghost {
            background: transparent;
            color: var(--muted);
            border: 1px dashed rgba(154, 196, 215, 0.22);
        }

        .button-small {
            min-height: 38px;
            padding: 0 14px;
            font-size: 0.88rem;
        }

        .table-panel {
            overflow: hidden;
        }

        .table-wrap {
            overflow-x: auto;
            padding: 0 10px 12px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 760px;
        }

        thead {
            background: rgba(57, 208, 195, 0.10);
        }

        th,
        td {
            padding: 16px 14px;
            text-align: left;
            border-bottom: 1px solid rgba(154, 196, 215, 0.12);
            vertical-align: middle;
        }

        th {
            color: #9ef5eb;
            font-size: 0.82rem;
            text-transform: uppercase;
            letter-spacing: 0.08em;
        }

        td {
            color: #e6f7ff;
        }

        tbody tr:hover {
            background: rgba(255, 255, 255, 0.04);
        }

        .table-id {
            font-weight: 700;
            color: var(--highlight);
        }

        .inline-form {
            margin: 0;
        }

        .empty {
            padding: 20px 24px 28px;
            color: var(--muted);
            line-height: 1.7;
        }

        .footer-note {
            margin-top: 18px;
            color: var(--muted);
            font-size: 0.92rem;
        }

        @media (max-width: 980px) {
            .layout {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 640px) {
            .page {
                margin: 24px auto 40px;
            }

            .hero,
            .panel {
                border-radius: 22px;
            }

            .hero,
            .form-body,
            .panel-head {
                padding-left: 18px;
                padding-right: 18px;
            }

            .panel-head {
                flex-direction: column;
            }

            .button,
            .button-small {
                width: 100%;
            }

            .table-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<main class="page">
    <section class="hero">
        <span class="eyebrow">JSP CRUD Demo</span>
        <h1>Student Information Portal</h1>
        <p class="subtitle">
            Manage the <code>students_info</code> table directly from JSP using JDBC.
            This page now supports create, read, update, and delete operations with action buttons.
        </p>
        <div class="hero-stats">
            <div class="stat">
                <span class="stat-label">Total Records</span>
                <span class="stat-value"><%= students.size() %></span>
            </div>
            <div class="stat">
                <span class="stat-label">Current Mode</span>
                <span class="stat-value"><%= editMode ? "Edit" : "Create" %></span>
            </div>
            <div class="stat">
                <span class="stat-label">SQL Source</span>
                <span class="stat-value">MySQL / MariaDB</span>
            </div>
        </div>
    </section>

    <% if (successMessage != null) { %>
        <div class="message success"><%= escapeHtml(successMessage) %></div>
    <% } %>

    <% if (errorMessage != null) { %>
        <div class="message error">
            <%= escapeHtml(errorMessage) %>
        </div>
    <% } %>

    <section class="layout">
        <div class="panel">
            <div class="panel-head">
                <div>
                    <h2><%= editMode ? "Update Student" : "Add Student" %></h2>
                    <p class="panel-copy">
                        Fill in the form below and use the action button to save the student record.
                    </p>
                </div>
                <span class="mode-pill"><%= editMode ? "Edit Mode" : "Create Mode" %></span>
            </div>

            <div class="form-body">
                <form method="post" action="students.jsp">
                    <input type="hidden" name="action" value="<%= editMode ? "update" : "add" %>">

                    <div class="form-grid">
                        <div class="field">
                            <label for="studId">Student ID</label>
                            <input id="studId" type="number" name="studId" min="1" required
                                   value="<%= escapeHtml(formStudId) %>" <%= editMode ? "readonly" : "" %>>
                            <p class="field-note">
                                <%= editMode ? "Student ID is locked while updating the existing record." : "Enter a unique numeric Student ID for a new record." %>
                            </p>
                        </div>

                        <div class="field">
                            <label for="studName">Student Name</label>
                            <input id="studName" type="text" name="studName" required
                                   value="<%= escapeHtml(formStudName) %>">
                        </div>

                        <div class="field">
                            <label for="studentClass">Class</label>
                            <input id="studentClass" type="text" name="studentClass" required
                                   value="<%= escapeHtml(formClass) %>">
                        </div>

                        <div class="field">
                            <label for="division">Division</label>
                            <input id="division" type="text" name="division" required
                                   value="<%= escapeHtml(formDivision) %>">
                        </div>

                        <div class="field">
                            <label for="city">City</label>
                            <input id="city" type="text" name="city" required
                                   value="<%= escapeHtml(formCity) %>">
                        </div>
                    </div>

                    <div class="button-row">
                        <button type="submit" class="button button-primary">
                            <%= editMode ? "Update Student" : "Add Student" %>
                        </button>
                        <a href="students.jsp" class="button button-secondary">
                            <%= editMode ? "Cancel Edit" : "Refresh Form" %>
                        </a>
                        <button type="reset" class="button button-ghost">Reset Fields</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="panel table-panel">
            <div class="panel-head">
                <div>
                    <h2>Student Records</h2>
                    <p class="panel-copy">
                        Use the <strong>Edit</strong> and <strong>Delete</strong> buttons in each row to manage records instantly.
                    </p>
                </div>
                <span class="mode-pill">Live Table</span>
            </div>

            <% if (students.isEmpty()) { %>
                <div class="empty">
                    No student records are available right now. Add a new student using the form to start the table.
                </div>
            <% } else { %>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Student Name</th>
                            <th>Class</th>
                            <th>Division</th>
                            <th>City</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (Student student : students) { %>
                            <tr>
                                <td class="table-id"><%= student.getStudId() %></td>
                                <td><%= escapeHtml(student.getStudName()) %></td>
                                <td><%= escapeHtml(student.getStudentClass()) %></td>
                                <td><%= escapeHtml(student.getDivision()) %></td>
                                <td><%= escapeHtml(student.getCity()) %></td>
                                <td>
                                    <div class="table-actions">
                                        <form method="get" action="students.jsp" class="inline-form">
                                            <input type="hidden" name="editId" value="<%= student.getStudId() %>">
                                            <button type="submit" class="button button-secondary button-small">Edit</button>
                                        </form>

                                        <form method="post" action="students.jsp" class="inline-form"
                                              onsubmit="return confirm('Delete student ID <%= student.getStudId() %>?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="studId" value="<%= student.getStudId() %>">
                                            <button type="submit" class="button button-danger button-small">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>
    </section>

    <p class="footer-note">
        Core query for display: <code>SELECT stud_id, stud_name, class, division, city FROM students_info ORDER BY stud_id</code>
    </p>
</main>
</body>
</html>
