public class Activity {
    public Activity(){}
    public Activity(String title, String description, String status, String date, String state, String start_time, String duration, String end_time, int id_trainer, String state_extra_info, int id_customer, int id){
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.state = state;
        this.start_time = start_time;
        this.duration = duration;
        this.end_time = end_time;
        this.id_Employee = id_trainer;
        this.state_extra_info = state_extra_info;
        this.id_customer = id_customer;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getId_Employee() {
        return id_Employee;
    }

    public void setId_Employee(int id_Employee) {
        this.id_Employee = id_Employee;
    }

    public String getState_extra_info() {
        return state_extra_info;
    }

    public void setState_extra_info(String state_extra_info) {
        this.state_extra_info = state_extra_info;
    }

    public int getId_customer() {
        return id_customer;
    }

    public void setId_customer(int id_customer) {
        this.id_customer = id_customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected String title;
    protected String description;
    protected String status;
    protected String date;
    protected String state;
    protected String start_time;
    protected String duration;
    protected String end_time;
    protected int id_Employee;
    protected String state_extra_info;
    protected int id_customer;
    protected int id;
}
