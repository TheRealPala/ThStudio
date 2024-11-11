package domainModel;

public class MedicalExamTags extends Tags {
    int idMedicalExam;
    public MedicalExamTags() {
    }

    public MedicalExamTags(String tags, String tags_type, int idMedicalExam) {
        super(tags, tags_type);
        this.idMedicalExam = idMedicalExam;
    }
    public int getIdMedicalExam() {
        return idMedicalExam;
    }
    public void setIdMedicalExam(int idMedicalExam) {
        this.idMedicalExam = idMedicalExam;
    }
}
