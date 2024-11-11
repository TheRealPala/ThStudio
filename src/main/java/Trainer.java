import java.util.ArrayList;

public class Trainer extends Employee{
    public Trainer(){}
    public Trainer(String Name, String Surname, String Date_of_birth, String Iban, int Id, String Specialization){
        super(Name, Surname, Date_of_birth, Iban, Id);
        this.Specialization = Specialization;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        Specialization = specialization;
    }

    public ArrayList<Trains> getTrainslist() {
        return trainslist;
    }

    public void setTrainslist(ArrayList<Trains> trainslist) {
        this.trainslist = trainslist;
    }
    /*public void Create_activity(){
        // TODO implement here
        Trains train = new Trains(); // to add,  a way to add description and the trainer id
        trainslist.add(train);


    }
    public  void Delete_activity(){
        // TODO implement here
        // TODO implement observer

    }*/











    private String Specialization;
    private ArrayList<Trains> trainslist;

}
