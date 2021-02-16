package stream;

import lombok.Data;


@Data
//@AllArgsConstructor
public class Employee {
    private String firstName;
    private String lastName;
    private int id;
    private int age;
    private Position position;




    public Employee(String firstName, String lastName, int id, int age, Position position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.age = age;
        this.position = position;
    }



    public String getFirstName() { return firstName; }
    public Position getPosition() { return position; }
    public String getLastName() { return lastName; }
    public void setAge(int age) { this.age = age; }
    public int getAge() { return age; }
    public int getId() { return id; }
}