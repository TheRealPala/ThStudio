package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.MedicalExamDao;
import com.thstudio.project.dao.TagDao;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Tags.Tag;
import com.thstudio.project.domainModel.Tags.TagIsOnline;
import com.thstudio.project.domainModel.Tags.TagType;
import com.thstudio.project.domainModel.Tags.TagZone;
import com.thstudio.project.security.Authz;
import com.thstudio.project.security.JwtService;

public class TagsController {
    private final TagDao tagDao;
    private final MedicalExamDao medicalExamDao;
    private final Authz authz;

    public TagsController(TagDao tagDao, MedicalExamDao medicalExamDao) throws Exception {
        this.tagDao = tagDao;
        this.medicalExamDao = medicalExamDao;
        this.authz = new Authz(new JwtService());
    }

    /**
     * Creates a tag of the specified type
     *
     * @param tag     the tag to be created
     * @param tagType the type of the tag
     * @return the created tag
     * @throws Exception if the tag type is invalid
     */
    public Tag createTag(String tag, String tagType, String token) throws Exception {
        this.authz.requireAnyRole(token, "admin", "doctor");
        Tag tagToReturn = null;
        switch (tagType) {
            case "Zone":
                tagToReturn = new TagZone(tag);
                this.tagDao.insert(tagToReturn);
                break;
            case "Type":
                tagToReturn = new TagType(tag);
                this.tagDao.insert(tagToReturn);
                break;
            case "Online":
                tagToReturn = new TagIsOnline(tag);
                this.tagDao.insert(tagToReturn);
                break;
            default:
                throw new IllegalArgumentException("Invalid tag type");
        }
        return tagToReturn;
    }

    /**
     * Deletes a tag
     *
     * @param tag     the tag to be deleted
     * @param tagType the type of the tag
     * @return true if the tag was deleted, false otherwise
     * @throws Exception if the tag type is invalid
     */
    public boolean deleteTag(String tag, String tagType, String token) throws Exception {
        this.authz.requireAnyRole(token, "admin", "doctor");
        String[] tagToRemove = new String[]{tag, tagType};
        return this.tagDao.delete(tagToRemove);
    }

    public boolean attachTagToMedicalExam(String tag, String tagType, int medicalExamId, String token) throws Exception {
        this.authz.requireAnyRole(token, "admin", "doctor");
        MedicalExam medicalExam = this.medicalExamDao.get(medicalExamId);
        String[] tagCompositeKey = {tag, tagType};
        Tag tagToAttach = this.tagDao.get(tagCompositeKey);
        boolean isTagAlreadyPresent = false;
        for (Tag iter : medicalExam.getTags()) {
            if (iter.equals(tagToAttach)) {
                isTagAlreadyPresent = true;
                break;
            }
        }
        if (isTagAlreadyPresent) {
            throw new RuntimeException("The tag is already present");
        }
        this.tagDao.attachTagToMedicalExam(medicalExam, tagToAttach);
        return true;
    }

    public boolean detachTagToMedicalExam(String tag, String tagType, int medicalExamId, String token) throws Exception {
        this.authz.requireAnyRole(token, "admin", "doctor");
        MedicalExam medicalExam = this.medicalExamDao.get(medicalExamId);
        String[] tagCompositeKey = {tag, tagType};
        Tag tagToDetach = this.tagDao.get(tagCompositeKey);
        boolean isTagAbsent = true;
        for (Tag iter : medicalExam.getTags()) {
            if (iter.equals(tagToDetach)) {
                isTagAbsent = false;
                break;
            }
        }
        if (isTagAbsent) {
            throw new RuntimeException("The tag has been already detached");
        }
        this.tagDao.detachTagFromMedicalExam(medicalExam, tagToDetach);
        return true;
    }

}
