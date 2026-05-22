
import java.io.Serializable;

public class TeacherData implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id;
    public String name;
    public String department;

    public TeacherData() {
    }

    public TeacherData(int id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }
}
