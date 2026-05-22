
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UniversityService extends Remote {

    void addStudent(StudentData s) throws RemoteException;

    List<StudentData> getAllStudents() throws RemoteException;

    void addTeacher(TeacherData t) throws RemoteException;

    List<TeacherData> getAllTeachers() throws RemoteException;
}
