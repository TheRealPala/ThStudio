package domainModel;

import java.util.ArrayList;
public class Customer extends Person{
    private int level;
    private ArrayList<Activity> activityList;

    public Customer(String name, String surname, String dateOfBirth, String iban, int id, int level){
        super(name, surname, dateOfBirth , iban, id);
        this.level = level;
    }
    public Customer(){}

    public ArrayList<Activity> getActivity_list() {
        return activityList;
    }

    public void setActivity_list(ArrayList<Activity> activity_list) {
        this.activityList = activity_list;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }




    /*    public void Book_activity( domainModel.Activity p){
        domainModel.Activity book=Search_Activity(p);
        Pay(book);
        Activity_list.add(book);
        // TODO implement observer to the domainModel.Employee

    }
    public void Cancel_activity( domainModel.Activity p){
        domainModel.Activity cancel= Search_Activity(p);
        Activity_list.remove(cancel);

    }
    public void Pay(domainModel.Activity p){             // to be used in book activity?
        // TODO implement here
    }
    public domainModel.Activity Search_Activity (domainModel.Activity p){
        // TODO implement here filter
        return p;
    }
    public ArrayList<domainModel.Activity> getTrains_list() {
        ArrayList<domainModel.Activity> trains_list = new ArrayList<domainModel.Activity>();
        for(int i=0; i<Activity_list.size(); i++){
            if(Activity_list.get(i) instanceof domainModel.Trains){
                trains_list.add(Activity_list.get(i));
            }
        }
        return trains_list;
    }
    public ArrayList<domainModel.Activity> getmedical_exams(){
        ArrayList<domainModel.Activity> medical_exams = new ArrayList<domainModel.Activity>();
        for(int i=0; i<Activity_list.size(); i++){
            if(Activity_list.get(i) instanceof domainModel.Medical_exams){
                medical_exams.add(Activity_list.get(i));
            }
        }
        return medical_exams;
    }
    public ArrayList<domainModel.Activity> getActivity_list(){
        return Activity_list;
    }*/



}
