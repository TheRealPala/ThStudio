package dao;
import domainModel.Tags.Tag;
import java.util.ArrayList;

public interface TagDao extends DAO<Tag, String[]> {

    public void attachTagToMedicalExam(Integer idExam, Tag tagToAttach) throws Exception;
    public boolean detachTagFromMedicalExam(Integer idExam, Tag tagToDetach) throws Exception;
    public ArrayList<Tag> getTagsFromMedicalExam(Integer idExam) throws Exception;
}
