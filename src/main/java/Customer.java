
import java.util.ArrayList;
public class Customer extends Person{
    private int level;
    private ArrayList<Activity> Activity_list;
    public Customer(String Name, String Surname, String Date_of_birth, String Iban, int Id, int level){
        super(Name, Surname, Date_of_birth, Iban, Id);
        this.level = level;
    }
    public Customer(){}

    public ArrayList<Activity> getActivity_list() {
        return Activity_list;
    }

    public void setActivity_list(ArrayList<Activity> activity_list) {
        Activity_list = activity_list;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    /*    public void Book_activity( Activity p){
        Activity book=Search_Activity(p);
        Pay(book);
        Activity_list.add(book);
        // TODO implement observer to the Employee

    }
    public void Cancel_activity( Activity p){
        Activity cancel= Search_Activity(p);
        Activity_list.remove(cancel);

    }
    public void Pay(Activity p){             // to be used in book activity?
        // TODO implement here
    }
    public Activity Search_Activity (Activity p){
        // TODO implement here filter
        return p;
    }
    public ArrayList<Activity> getTrains_list() {
        ArrayList<Activity> trains_list = new ArrayList<Activity>();
        for(int i=0; i<Activity_list.size(); i++){
            if(Activity_list.get(i) instanceof Trains){
                trains_list.add(Activity_list.get(i));
            }
        }
        return trains_list;
    }
    public ArrayList<Activity> getmedical_exams(){
        ArrayList<Activity> medical_exams = new ArrayList<Activity>();
        for(int i=0; i<Activity_list.size(); i++){
            if(Activity_list.get(i) instanceof Medical_exams){
                medical_exams.add(Activity_list.get(i));
            }
        }
        return medical_exams;
    }
    public ArrayList<Activity> getActivity_list(){
        return Activity_list;
    }*/



}
