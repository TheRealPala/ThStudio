package domainModel;

import java.util.ArrayList;

public class Trainer extends Employee{
    private String specialization;
    private ArrayList<Train> trainList;

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

    public ArrayList<Train> getTrainslist() {
        return trainList;
    }

    public void setTrainslist(ArrayList<Train> trainList) {
        this.trainList = trainList;
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
}
