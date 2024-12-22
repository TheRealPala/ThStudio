package dao;

import domainModel.Tags.Tag;
import domainModel.Tags.TagIsOnline;
import domainModel.Tags.TagZone;

import java.sql.*;
import java.util.ArrayList;

public class MariaDbTagDao implements TagDao{
    @Override
    public void attachTagToMedicalExam(Integer idExam, Tag tagToAttach) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into medical_exams_tags (id_medical_exam, tag, tag_type) " +
                    "values (?, ?, ?)");
            ps.setInt(1, idExam);
            ps.setString(2, tagToAttach.getTag());
            ps.setString(3, tagToAttach.getTagType());
            ps.executeUpdate();
        } finally {
            assert ps != null: "PreparedStatement is null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public boolean detachTagFromMedicalExam(Integer idExam, Tag tagToDetach) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        int rows = 0;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("delete from medical_exams_tags where tag = ? and tag_type = ? and id_medical_exam = ?");
            ps.setString(1, tagToDetach.getTag());
            ps.setString(2, tagToDetach.getTagType());
            ps.setInt(3, idExam);
            rows = ps.executeUpdate();
        } finally {
            assert ps != null: "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
        return rows > 0;
    }


    @Override
    public ArrayList<Tag> getTagsFromMedicalExam(Integer idExam) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Tag> tList = new ArrayList<>();
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from medical_exams_tags where id_medical_exam = ?");
            ps.setInt(1, idExam);
            rs = ps.executeQuery();
            parseTagsFromDb(rs, tList);

        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return tList;
    }

    private void parseTagsFromDb(ResultSet rs, ArrayList<Tag> tList) throws SQLException {
        while (rs.next()) {
            String tag = rs.getString("tag");
            String tagType = rs.getString("tag_type");
            Tag tmp = switch (tagType) {
                case "Online" -> new TagIsOnline(tag);
                case "Zone" -> new TagZone(tag);
                default -> null;
            };
            tList.add(tmp);
        }
        //TODO:check if this should have an exception if the tagType is not recognized
    }

    @Override
    public Tag get(String[] id) throws SQLException {
        String tag = id[0];
        String tagType = id[1];
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Tag t;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from tags where tag = ? and tag_type = ?");
            ps.setString(1, tag);
            ps.setString(2, tagType);
            rs = ps.executeQuery();
            if(!rs.next()) {
                throw new RuntimeException("The Tag looked for in not present in the database");
            }
            tag = rs.getString("tag");
            tagType = rs.getString("tag_type");

            t = switch (tagType) {
                case "Online" -> new TagIsOnline(tag);
                case "Zone" -> new TagZone(tag);
                default -> null;
            };
        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return t;
    }

    @Override
    public ArrayList<Tag> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        ArrayList<Tag> tList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from tags");
            parseTagsFromDb(rs, tList);
        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return tList;
    }

    @Override
    public void insert(Tag tag) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into tags (tag, tag_type) " +
                    "values (?, ?)");
            ps.setString(1, tag.getTag());
            ps.setString(2, tag.getTagType());
            ps.executeUpdate();
        } finally {
            assert ps != null: "PreparedStatement is null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public void update(Tag tag) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update tags set tag = ?, tag_type = ? where tag = ? and tag_type = ?");
            ps.setString(1, tag.getTag());
            ps.setString(2, tag.getTagType());
            ps.executeUpdate();
        } finally {
            assert ps != null: "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public boolean delete(String[] id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String tag = id[0];
        String tagType = id[1];
        int rows = 0;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("delete from tags where tag = ? and tag_type = ?");
            ps.setString(1, tag);
            ps.setString(2, tagType);
            rows = ps.executeUpdate();
        } finally {
            assert ps != null: "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
        return rows > 0;
    }
}
