package domainModel.State;

public class Booked extends State {

    public Booked(){
        this.state = "Booked";
    }

    @Override
    public String getExtraInfo(){
        return null;
    }



}
