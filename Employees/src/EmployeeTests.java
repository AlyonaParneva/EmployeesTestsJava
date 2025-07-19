import java.sql.*;

public class EmployeeTests {
    public static final String DB_URL = "jdbc:h2:file:./Office/Office";
    public static final String DB_USER = "sa";
    public static final String DB_PASS = "";

    public static void main(String[] args) {
        updateAnnDepartmentToHR();
        fixLowercaseNames();
        countEmployeesInIT();
    }

    public static void updateAnnDepartmentToHR() {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            PreparedStatement findAnn = con.prepareStatement(
                    "SELECT ID FROM Employee WHERE Name = ?");
            findAnn.setString(1, "Ann");
            ResultSet rs = findAnn.executeQuery();

            int annId = -1;
            int count = 0;
            while (rs.next()) {
                annId = rs.getInt("ID");
                count++;
            }

            if (count == 1) {
                PreparedStatement findHr = con.prepareStatement(
                        "SELECT ID FROM Department WHERE Name = ?");
                findHr.setString(1, "HR");
                ResultSet hrRs = findHr.executeQuery();
                if (hrRs.next()) {
                    int hrId = hrRs.getInt("ID");

                    PreparedStatement update = con.prepareStatement(
                            "UPDATE Employee SET DepartmentID = ? WHERE ID = ?");
                    update.setInt(1, hrId);
                    update.setInt(2, annId);
                    update.executeUpdate();
                    System.out.println("Ann переведена в HR.");
                }
            } else {
                System.out.println("Найдено сотрудников с именем Ann: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void fixLowercaseNames() {
        int updatedCount = 0;
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery("SELECT ID, Name FROM Employee");

            while (rs.next()) {
                String name = rs.getString("Name");
                if (!name.isEmpty() && Character.isLowerCase(name.charAt(0))) {
                    String fixed = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    rs.updateString("Name", fixed);
                    rs.updateRow();
                    updatedCount++;
                }
            }

            System.out.println("Исправлено имён: " + updatedCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void countEmployeesInIT() {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM Employee " +
                            "JOIN Department ON Employee.DepartmentID = Department.ID " +
                            "WHERE Department.Name = ?");
            ps.setString(1, "IT");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Количество сотрудников в IT: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}