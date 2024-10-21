
import java.util.ArrayList;
public class Customer extends Person{
    public Customer(){}
    public Customer(String Name, String Surname, String Date_of_birth, String Iban, int Id, int level){
        super(Name, Surname, Date_of_birth, Iban, Id);
        this.level = level;
    }
    public void Book_activity(){
        // TODO implement here
    }
    public void Cancel_activity(){
        // TODO implement here
    }
    public void Pay(){             // to be used in book activity?
        // TODO implement here
    }
    public void Search_Activity (){
        // TODO implement here
    }
    public ArrayList<Activity> getTrainslist() {
        return Activity_list;
    }
    private int level;
    private ArrayList<Activity> Activity_list;




}
