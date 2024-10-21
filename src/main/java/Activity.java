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
    private String title;
    private String description;
    private String status;
    private String date;
    private String state;
    private String start_time;
    private String duration;
    private String end_time;
    private int id_Employee;
    private String state_extra_info;
    private int id_customer;
    private int id;
}
