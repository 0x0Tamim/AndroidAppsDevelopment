package ewubd.edu.tamim2023360298;

public class Birthday {
    public String name;
    public String phone;
    public long dob; // date of birth stored as milliseconds (long)

    public Birthday(String name, String phone, long dob) {
        this.name = name;
        this.phone = phone;
        this.dob = dob;
    }
}