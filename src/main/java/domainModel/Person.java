package domainModel;

public class Person {
    protected String name;
    protected String surname;
    protected String dateOfBirth;
    protected String iban;
    protected int id;
    protected double balance;

    public Person(int id, String name, String surname, String dateOfBirth, String iban, double balance){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.iban = iban;
        this.balance = balance;

    }

    public Person(String name, String surname, String dateOfBirth, String iban){
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.iban = iban;
        this.balance = 0.0;
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
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
