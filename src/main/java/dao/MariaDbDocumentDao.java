package dao;

import domainModel.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MariaDbDocumentDao implements DocumentDao {

    private Document parseDocument(ResultSet rs) throws Exception {
        Document d = null;
        d = new Document(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("path"),
                rs.getInt("id_owner")
        );

        int idReceiver = rs.getInt("id_receiver");
        if (idReceiver != 0) {
            d.setReceiverId(idReceiver);
        }

        int idMedicalExam = rs.getInt("id_medical_exam");
        if (idMedicalExam != 0) {
            d.setMedicalExamId(idMedicalExam);
        }
        return d;
    }

    @Override
    public Document get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Document d = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from documents where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("The Customer looked for in not present in the database");
            }
            d = parseDocument(rs);
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return d;
    }

    @Override
    public List<Document> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<Document> dList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from documents");
            while (rs.next()) {
                dList.add(this.parseDocument(rs));
            }
            if (dList.isEmpty()) {
                throw new RuntimeException("There is no Customers in the database");
            }
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return dList;
    }

    @Override
    public void insert(Document document) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into documents(title, path, id_owner, id_medical_exam, id_receiver) " +
                    "values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, document.getTitle());
            ps.setString(2, document.getPath());
            ps.setInt(3, document.getOwnerId());
            ps.setObject(4, document.getMedicalExamId());
            ps.setObject(5, document.getReceiverId());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                document.setId(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert ps != null : "Prepared statement is null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public void update(Document document) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update documents set title = ?, path = ?, id_owner = ?, id_receiver = ?, id_medical_exam = ? where id = ?");
            ps.setString(1, document.getTitle());
            ps.setString(2, document.getPath());
            ps.setInt(3, document.getOwnerId());
            ps.setObject(4, document.getMedicalExamId());
            ps.setObject(5, document.getReceiverId());
            ps.setInt(6, document.getId());
            ps.executeUpdate();
        } finally {
            assert ps != null : "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        int rows = 0;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("delete from documents where id = ?");
            ps.setInt(1, id);
            rows = ps.executeUpdate();
        } finally {
            assert ps != null: "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
        return rows > 0;
    }
}
