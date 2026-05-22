
import java.sql.*;
import java.util.Scanner;

public class Teacher {

    int id;
    String name;
    String dep;

    public void addTeacher() {
        try {
            Scanner sc = new Scanner(System.in);
            Connection con = Server.getConnection();

            System.out.print("Enter ID: ");
            id = sc.nextInt();

            System.out.print("Enter Name: ");
            name = sc.next();

            System.out.print("Enter Department: ");
            dep = sc.next();

            String query = "INSERT INTO teachers VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, dep);

            pst.executeUpdate();

            System.out.println("Teacher added!");

        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    public void showTeacher() {
        try {
            Connection con = Server.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM teachers");

            System.out.println("Teachers List:");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " "
                        + rs.getString("name") + " "
                        + rs.getString("department")
                );
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }
}
