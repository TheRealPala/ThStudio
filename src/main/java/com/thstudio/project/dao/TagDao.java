package com.thstudio.project.dao;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Tags.Tag;
import java.util.ArrayList;

public interface TagDao extends DAO<Tag, String[]> {

    public void attachTagToMedicalExam(MedicalExam medicalExam, Tag tagToAttach) throws Exception;
    public boolean detachTagFromMedicalExam(MedicalExam medicalExam, Tag tagToDetach) throws Exception;
    public ArrayList<Tag> getTagsFromMedicalExam(Integer idExam) throws Exception;
}
