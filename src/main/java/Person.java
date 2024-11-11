public class Person {
    public Person(){}
    public Person(String Name, String Surname, String Date_of_birth, String Iban, int Id){
        this.Name = Name;
        this.Surname = Surname;
        this.Date_of_birth = Date_of_birth;
        this.Iban = Iban;
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getDate_of_birth() {
        return Date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        Date_of_birth = date_of_birth;
    }

    public String getIban() {
        return Iban;
    }

    public void setIban(String iban) {
        Iban = iban;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    protected String Name;
    protected String Surname;
    protected String Date_of_birth;
    protected String Iban;
    protected int Id;




}
