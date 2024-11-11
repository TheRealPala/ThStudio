package domainModel;

public class MedicalExams extends Activity{
    public MedicalExams() {}
    public MedicalExams(String title, String description, String status, String date, String state, String startTime, String duration, String endTime,
                        int idDoctor, String stateExtraInfo, int idCustomer, int id){
        super(title, description, status, date, startTime, duration, endTime, idDoctor, stateExtraInfo, idCustomer, id);
    }
}
