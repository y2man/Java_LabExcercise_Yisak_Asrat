import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UniversityServiceImpl extends UnicastRemoteObject implements UniversityService {

    protected UniversityServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void addStudent(StudentData s) throws RemoteException {
        try {
            Connection con = Server.getConnection();
            String query = "INSERT INTO students VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, s.id);
            pst.setString(2, s.name);
            pst.setString(3, s.section);
            pst.setInt(4, s.year);
            pst.setString(5, s.department);
            pst.executeUpdate();
        } catch (Exception ex) {
            throw new RemoteException("addStudent failed", ex);
        }
    }

    @Override
    public List<StudentData> getAllStudents() throws RemoteException {
        try {
            Connection con = Server.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM students");
            List<StudentData> list = new ArrayList<>();
            while (rs.next()) {
                StudentData s = new StudentData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("section"),
                        rs.getInt("year"),
                        rs.getString("department")
                );
                list.add(s);
            }
            return list;
        } catch (Exception ex) {
            throw new RemoteException("getAllStudents failed", ex);
        }
    }

    @Override
    public void addTeacher(TeacherData t) throws RemoteException {
        try {
            Connection con = Server.getConnection();
            String query = "INSERT INTO teachers VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, t.id);
            pst.setString(2, t.name);
            pst.setString(3, t.department);
            pst.executeUpdate();
        } catch (Exception ex) {
            throw new RemoteException("addTeacher failed", ex);
        }
    }

    @Override
    public List<TeacherData> getAllTeachers() throws RemoteException {
        try {
            Connection con = Server.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM teachers");
            List<TeacherData> list = new ArrayList<>();
            while (rs.next()) {
                TeacherData t = new TeacherData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department")
                );
                list.add(t);
            }
            return list;
        } catch (Exception ex) {
            throw new RemoteException("getAllTeachers failed", ex);
        }
    }
}