package domainModel;

import java.util.ArrayList;

public class Trainer extends Employee{
    private String specialization;

    public Trainer(){}
    public Trainer(String name, String surname, String dateOfBirth, String iban, int id, String specialization){
        super(name, surname, dateOfBirth, iban, id);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        specialization = specialization;
    }

    // public void setTrainslist(ArrayList<Trains> trainsList) {
     //   this.trainsList = trainsList;
    // }
    /*public void Create_activity(){
        // TODO implement here
        domainModel.Trains train = new domainModel.Trains(); // to add,  a way to add description and the trainer id
        trainslist.add(train);


    }
    public  void Delete_activity(){
        // TODO implement here
        // TODO implement observer

    }*/
}
