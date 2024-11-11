package domainModel;

public class Activity {
    protected String title;
    protected String description;
    protected String status;
    protected String date;
    protected String state;
    protected String startTime;
    protected String duration;
    protected String endTime;
    protected int idEmployee;
    protected String stateExtraInfo;
    protected int idCustomer;
    protected int id;

    public Activity(){}
    public Activity(String title, String description, String status, String date, String state, String start_time, String duration, String end_time, int id_trainer, String state_extra_info, int id_customer, int id){
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.state = state;
        this.startTime = start_time;
        this.duration = duration;
        this.endTime = end_time;
        this.idEmployee = id_trainer;
        this.stateExtraInfo = state_extra_info;
        this.idCustomer = id_customer;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getStateExtraInfo() {
        return stateExtraInfo;
    }

    public void setStateExtraInfo(String stateExtraInfo) {
        this.stateExtraInfo = stateExtraInfo;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
