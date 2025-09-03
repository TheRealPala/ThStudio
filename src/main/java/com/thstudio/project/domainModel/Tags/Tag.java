package com.thstudio.project.domainModel.Tags;

import java.util.Objects;

public abstract class Tag {
    protected String tag;
    protected String tagType;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag1 = (Tag) o;
        return Objects.equals(getTag(), tag1.getTag()) && Objects.equals(getTagType(), tag1.getTagType());
    }
}