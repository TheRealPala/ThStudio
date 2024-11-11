package domainModel;

public class Activity {
    protected String title;
    protected String description;
    protected String status;
    protected String date;
    protected String startTime;
    protected String duration;
    protected String endTime;
    protected int idEmployee;
    protected String stateExtraInfo;
    protected int idCustomer;
    protected int id;

    public Activity(){}
    public Activity(String title, String description, String status, String date, String startTime, String duration, String endTime, int idEmployee, String stateExtraInfo, int idCustomer, int id){
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = endTime;
        this.idEmployee = idEmployee;
        this.stateExtraInfo = stateExtraInfo;
        this.idCustomer = idCustomer;
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
