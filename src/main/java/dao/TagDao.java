package dao;
import domainModel.MedicalExam;
import domainModel.Tags.Tag;
import java.util.ArrayList;

public interface TagDao extends DAO<Tag, String[]> {

    public void attachTagToMedicalExam(MedicalExam medicalExam, Tag tagToAttach) throws Exception;
    public boolean detachTagFromMedicalExam(MedicalExam medicalExam, Tag tagToDetach) throws Exception;
    public ArrayList<Tag> getTagsFromMedicalExam(Integer idExam) throws Exception;
}
