import java.sql.*;
import java.util.Scanner;

public class Student {

    int id;
    String name;
    char sec;
    int year;
    String dep;

    public void addStudent() {
        try {
            Scanner sc = new Scanner(System.in);
            Connection con = Server.getConnection();

            System.out.print("Enter ID: ");
            id = sc.nextInt();

            System.out.print("Enter Name: ");
            name = sc.next();

            System.out.print("Enter Section: ");
            sec = sc.next().charAt(0);

            System.out.print("Enter Year: ");
            year = sc.nextInt();

            System.out.print("Enter Department: ");
            dep = sc.next();

            String query = "INSERT INTO students VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, String.valueOf(sec));
            pst.setInt(4, year);
            pst.setString(5, dep);

            pst.executeUpdate();

            System.out.println("Student added!");

        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    public void showStudent() {
        try {
            Connection con = Server.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM students");

            System.out.println("Students List:");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " " +
                                rs.getString("name") + " " +
                                rs.getString("section") + " " +
                                rs.getInt("year") + " " +
                                rs.getString("department")
                );
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }
}