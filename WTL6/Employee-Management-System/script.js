// Employee Management System JavaScript

document.addEventListener('DOMContentLoaded', function() {
    loadEmployees();
    
    const form = document.getElementById('employee-form');
    const cancelBtn = document.getElementById('cancel-btn');
    
    form.addEventListener('submit', handleFormSubmit);
    cancelBtn.addEventListener('click', cancelEdit);
});

// Load employees from localStorage and display them
function loadEmployees() {
    const employees = getEmployeesFromStorage();
    displayEmployees(employees);
}

// Get employees from localStorage
function getEmployeesFromStorage() {
    const employees = localStorage.getItem('employees');
    return employees ? JSON.parse(employees) : [];
}

// Save employees to localStorage
function saveEmployeesToStorage(employees) {
    localStorage.setItem('employees', JSON.stringify(employees));
}

// Display employees in the table
function displayEmployees(employees) {
    const tbody = document.getElementById('employee-tbody');
    tbody.innerHTML = '';
    
    employees.forEach(employee => {
        const row = createEmployeeRow(employee);
        tbody.appendChild(row);
    });
}

// Create a table row for an employee
function createEmployeeRow(employee) {
    const row = document.createElement('tr');
    
    row.innerHTML = `
        <td>${employee.id}</td>
        <td>${employee.name}</td>
        <td>${employee.email}</td>
        <td>${employee.department}</td>
        <td>${employee.position}</td>
        <td>$${employee.salary}</td>
        <td>
            <button class="action-btn edit-btn" onclick="editEmployee(${employee.id})">Edit</button>
            <button class="action-btn delete-btn" onclick="deleteEmployee(${employee.id})">Delete</button>
        </td>
    `;
    
    return row;
}

// Handle form submission (add or update)
function handleFormSubmit(event) {
    event.preventDefault();
    
    const id = document.getElementById('employee-id').value;
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const department = document.getElementById('department').value;
    const position = document.getElementById('position').value;
    const salary = parseFloat(document.getElementById('salary').value);
    
    const employee = {
        id: id ? parseInt(id) : Date.now(),
        name,
        email,
        department,
        position,
        salary
    };
    
    const employees = getEmployeesFromStorage();
    
    if (id) {
        // Update existing employee
        const index = employees.findIndex(emp => emp.id === parseInt(id));
        if (index !== -1) {
            employees[index] = employee;
        }
    } else {
        // Add new employee
        employees.push(employee);
    }
    
    saveEmployeesToStorage(employees);
    displayEmployees(employees);
    resetForm();
}

// Edit an employee
function editEmployee(id) {
    const employees = getEmployeesFromStorage();
    const employee = employees.find(emp => emp.id === id);
    
    if (employee) {
        document.getElementById('employee-id').value = employee.id;
        document.getElementById('name').value = employee.name;
        document.getElementById('email').value = employee.email;
        document.getElementById('department').value = employee.department;
        document.getElementById('position').value = employee.position;
        document.getElementById('salary').value = employee.salary;
        
        document.getElementById('form-title').textContent = 'Edit Employee';
        document.getElementById('submit-btn').textContent = 'Update Employee';
        document.getElementById('cancel-btn').style.display = 'inline-block';
    }
}

// Delete an employee
function deleteEmployee(id) {
    if (confirm('Are you sure you want to delete this employee?')) {
        const employees = getEmployeesFromStorage();
        const filteredEmployees = employees.filter(emp => emp.id !== id);
        saveEmployeesToStorage(filteredEmployees);
        displayEmployees(filteredEmployees);
    }
}

// Cancel edit and reset form
function cancelEdit() {
    resetForm();
}

// Reset form to add mode
function resetForm() {
    document.getElementById('employee-form').reset();
    document.getElementById('employee-id').value = '';
    document.getElementById('form-title').textContent = 'Add Employee';
    document.getElementById('submit-btn').textContent = 'Add Employee';
    document.getElementById('cancel-btn').style.display = 'none';
}
