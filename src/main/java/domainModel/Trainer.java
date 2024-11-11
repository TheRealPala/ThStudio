package domainModel;

import java.util.ArrayList;

public class Trainer extends Employee{
    public Trainer(){}
    public Trainer(String Name, String Surname, String Date_of_birth, String Iban, int Id, String Specialization){
        super(Name, Surname, Date_of_birth, Iban, Id);
        this.specialization = Specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        specialization = specialization;
    }

    public ArrayList<Trains> getTrainslist() {
        return trainsList;
    }

    public void setTrainslist(ArrayList<Trains> trainsList) {
        this.trainsList = trainsList;
    }
    /*public void Create_activity(){
        // TODO implement here
        domainModel.Trains train = new domainModel.Trains(); // to add,  a way to add description and the trainer id
        trainslist.add(train);


    }
    public  void Delete_activity(){
        // TODO implement here
        // TODO implement observer

    }*/











    private String specialization;
    private ArrayList<Trains> trainsList;

}
