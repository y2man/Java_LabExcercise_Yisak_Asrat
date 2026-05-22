import java.io.Serializable;

public class StudentData implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id;
    public String name;
    public String section;
    public int year;
    public String department;

    public StudentData() {
    }

    public StudentData(int id, String name, String section, int year, String department) {
        this.id = id;
        this.name = name;
        this.section = section;
        this.year = year;
        this.department = department;
    }
}