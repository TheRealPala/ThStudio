package domainModel;

public class MedicalExamTag extends Tag {
    int idMedicalExam;
    public MedicalExamTag() {
    }

    public MedicalExamTag(String tags, String tags_type, int idMedicalExam) {
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
