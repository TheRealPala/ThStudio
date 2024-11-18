package domainModel;

public class Train extends Activity {

    public Train() {
    }

    public Train(String title, String description, String status, String date, String start_time, String duration, String end_time, int id_trainer, String state_extra_info, int id_customer, int id) {
        super(title, description, status, date, start_time, duration, end_time, id_trainer, state_extra_info, id_customer, id);
    }
}
