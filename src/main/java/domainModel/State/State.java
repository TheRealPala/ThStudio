package domainModel.State;

public abstract class State {
    protected String state;

    public String getState(){
        return this.state;
    }

    public abstract String getExtraInfo();
}
