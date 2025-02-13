package domainModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Person {
    protected String name;
    protected String surname;
    protected LocalDate dateOfBirth;
    protected int id;
    protected double balance;

    public Person(int id, String name, String surname, String dateOfBirth, double balance){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = parseDate(dateOfBirth);
        this.balance = balance;

    }

    public Person(String name, String surname, String dateOfBirth, double balance){
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = parseDate(dateOfBirth);
        this.balance = balance;
    }

    private LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateOfBirth() {
        return dateOfBirth.toString();
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = parseDate(dateOfBirth);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance(){
        return balance;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }
}
