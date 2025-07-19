import office.Department;
import office.Employee;
import office.Service;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.*;


public class ServiceTests {

    @BeforeAll
    static void setup() {
        Service.setDbUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Service.createDB();
    }

    @Test
    void testAddAndRemoveDepartment() throws SQLException {
        Department d = new Department(10, "TestDept");
        Service.addDepartment(d);

        Employee emp = new Employee(100, "TestUser", 10);
        Service.addEmployee(emp);

        Service.removeDepartment(d);

        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "")) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Employee WHERE ID = 100");
            assertFalse(rs.next(), "Сотрудник должен быть удалён вместе с отделом");
        }
    }

    @Test
    void testFixLowercaseNames() throws SQLException {
        Employee emp = new Employee(101, "john", 1);
        Service.addEmployee(emp);

        Service.fixLowercaseNames();

        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "")) {
            ResultSet rs = con.createStatement().executeQuery("SELECT Name FROM Employee WHERE ID = 101");
            assertTrue(rs.next());
            assertEquals("John", rs.getString("Name"));
        }
    }

    @Test
    void testUpdateAnnToHR() throws SQLException {
        Service.updateAnnDepartmentToHR();

        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "")) {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT DepartmentID FROM Employee WHERE Name = 'Ann'"
            );
            assertTrue(rs.next());

            int departmentId = rs.getInt("DepartmentID");
            ResultSet hr = con.createStatement().executeQuery(
                    "SELECT ID FROM Department WHERE Name = 'HR'"
            );
            assertTrue(hr.next());
            assertEquals(hr.getInt("ID"), departmentId);
        }
    }

    @Test
    void testCountEmployeesInIT() throws SQLException {
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "")) {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM Employee e JOIN Department d ON e.DepartmentID = d.ID WHERE d.Name = 'IT'"
            );
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
        }
    }

}
