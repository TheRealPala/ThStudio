package BusinessLogic;

import dao.TagDao;
import domainModel.Tags.*;

public class TagsController {
    private final TagDao tagDao;

    public TagsController(TagDao tagDao){
        this.tagDao = tagDao;
    }

    /**
     * Creates a tag of the specified type
     * @param tag           the tag to be created
     * @param tagType       the type of the tag
     * @return              the created tag
     * @throws Exception    if the tag type is invalid
     */
    public Tag createTag(String tag, String tagType) throws Exception {
        String[] stringTag = new String[]{tag, tagType};
        Tag t = this.tagDao.get(stringTag);
        if (t == null) {
            switch (tagType) {
                case "Zone":
                    TagZone tagZone = new TagZone(tag);
                    this.tagDao.insert(tagZone);
                    return this.tagDao.get(stringTag);
                case "Subject":
                    TagSubject tagSubject = new TagSubject(tag);
                    this.tagDao.insert(tagSubject);
                    return this.tagDao.get(stringTag);
                case "Online":
                    TagIsOnline tagIsOnline = new TagIsOnline(tag);
                    this.tagDao.insert(tagIsOnline);
                    return this.tagDao.get(stringTag);
                default:
                    throw new IllegalArgumentException("Invalid tag type");
            }
        }
        return t;
    }


}
