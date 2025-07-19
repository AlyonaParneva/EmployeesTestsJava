package office;

import java.sql.*;

public class Service {

    private static String dbUrl = "jdbc:h2:file:./Office/Office";

    public static void setDbUrl(String url) {
        dbUrl = url;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, "sa", "");
    }

    public static void createDB() {
        try (Connection con = getConnection()) {
            Statement stm = con.createStatement();

            stm.executeUpdate("DROP TABLE IF EXISTS Employee");
            stm.executeUpdate("DROP TABLE IF EXISTS Department");

            stm.executeUpdate("CREATE TABLE Department(ID INT PRIMARY KEY, NAME VARCHAR(255))");
            stm.executeUpdate("CREATE TABLE Employee(" +
                    "ID INT PRIMARY KEY, " +
                    "NAME VARCHAR(255), " +
                    "DepartmentID INT, " +
                    "FOREIGN KEY (DepartmentID) REFERENCES Department(ID) ON DELETE CASCADE)");

            stm.executeUpdate("INSERT INTO Department VALUES(1,'Accounting')");
            stm.executeUpdate("INSERT INTO Department VALUES(2,'IT')");
            stm.executeUpdate("INSERT INTO Department VALUES(3,'HR')");

            stm.executeUpdate("INSERT INTO Employee VALUES(1,'Pete',1)");
            stm.executeUpdate("INSERT INTO Employee VALUES(2,'Ann',1)");
            stm.executeUpdate("INSERT INTO Employee VALUES(3,'Liz',2)");
            stm.executeUpdate("INSERT INTO Employee VALUES(4,'Tom',2)");
            stm.executeUpdate("INSERT INTO Employee VALUES(5,'Todd',3)");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void addDepartment(Department d) {
        try (Connection con = getConnection()) {
            PreparedStatement stm = con.prepareStatement("INSERT INTO Department VALUES(?,?)");
            stm.setInt(1, d.departmentID);
            stm.setString(2, d.getName());
            stm.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void removeDepartment(Department d) {
        try (Connection con = getConnection()) {
            PreparedStatement stm = con.prepareStatement("DELETE FROM Department WHERE ID=?");
            stm.setInt(1, d.departmentID);
            stm.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void addEmployee(Employee empl) {
        try (Connection con = getConnection()) {
            PreparedStatement stm = con.prepareStatement("INSERT INTO Employee VALUES(?,?,?)");
            stm.setInt(1, empl.getEmployeeId());
            stm.setString(2, empl.getName());
            stm.setInt(3, empl.getDepartmentId());
            stm.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void removeEmployee(Employee empl) {
        try (Connection con = getConnection()) {
            PreparedStatement stm = con.prepareStatement("DELETE FROM Employee WHERE ID=?");
            stm.setInt(1, empl.getEmployeeId());
            stm.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // === Методы из задания #1 ===

    public static void updateAnnDepartmentToHR() {
        try (Connection con = getConnection()) {
            PreparedStatement findAnn = con.prepareStatement("SELECT ID FROM Employee WHERE Name = ?");
            findAnn.setString(1, "Ann");
            ResultSet rs = findAnn.executeQuery();

            int annId = -1;
            int count = 0;
            while (rs.next()) {
                annId = rs.getInt("ID");
                count++;
            }

            if (count == 1) {
                PreparedStatement findHr = con.prepareStatement("SELECT ID FROM Department WHERE Name = ?");
                findHr.setString(1, "HR");
                ResultSet hrRs = findHr.executeQuery();
                if (hrRs.next()) {
                    int hrId = hrRs.getInt("ID");

                    PreparedStatement update = con.prepareStatement("UPDATE Employee SET DepartmentID = ? WHERE ID = ?");
                    update.setInt(1, hrId);
                    update.setInt(2, annId);
                    update.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void fixLowercaseNames() {
        try (Connection con = getConnection()) {
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery("SELECT ID, Name FROM Employee");

            while (rs.next()) {
                String name = rs.getString("Name");
                if (!name.isEmpty() && Character.isLowerCase(name.charAt(0))) {
                    String fixed = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    rs.updateString("Name", fixed);
                    rs.updateRow();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}