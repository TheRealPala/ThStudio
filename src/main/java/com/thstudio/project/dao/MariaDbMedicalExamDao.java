package com.thstudio.project.dao;

import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Search.Search;
import com.thstudio.project.domainModel.State.*;
import com.thstudio.project.domainModel.Tags.Tag;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MariaDbMedicalExamDao implements MedicalExamDao {
    private final TagDao tagDao;

    public MariaDbMedicalExamDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    private void setMedicalExamState(ResultSet rs, MedicalExam m) throws SQLException {
        if (Objects.equals(rs.getString("state"), "Booked")) {
            LocalDateTime ldt = rs.getTimestamp("stateExtraInfo").toLocalDateTime();
            Booked booked = new Booked(ldt);
            m.setState(booked);
        } else if (Objects.equals(rs.getString("state"), "Cancelled")) {
            LocalDateTime ldt = rs.getTimestamp("stateExtraInfo").toLocalDateTime();
            Deleted deleted = new Deleted(ldt);
            m.setState(deleted);
        } else if (Objects.equals(rs.getString("state"), "Completed")) {
            LocalDateTime ldt = rs.getTimestamp("stateExtraInfo").toLocalDateTime();
            Completed completed = new Completed(ldt);
            m.setState(completed);
        } else {
            Available available = new Available();
            m.setState(available);
        }
    }

    private MedicalExam parseMedicalExam(ResultSet rs) throws SQLException {
        MedicalExam me = new MedicalExam(
                rs.getInt("id"),
                rs.getInt("id_doctor"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime(),
                rs.getString("description"),
                rs.getString("title"),
                rs.getDouble("price")
        );
        if (rs.getInt("id_customer") != 0) {
            me.setIdCustomer(rs.getInt("id_customer"));
        }
        this.setMedicalExamState(rs, me);
        return me;
    }

    @Override
    public MedicalExam get(Integer id) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        MedicalExam m = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from medical_exams where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next())
                throw new RuntimeException("The Medical Exam looked for in not present in the database");
            else {
                m = this.parseMedicalExam(rs);
                ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(m.getId());
                m.setTags(tags);
            }
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return m;
    }

    @Override
    public List<MedicalExam> getAll() throws Exception {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<MedicalExam> mEList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from medical_exams");

            if (!rs.next()) {
                throw new RuntimeException("There are no Medical Exams in the database");
            } else {
                while (rs.next()) {
                    MedicalExam tmp = this.parseMedicalExam(rs);
                    ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(tmp.getId());
                    tmp.setTags(tags);
                    mEList.add(tmp);
                }
            }

        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return mEList;
    }

    @Override
    public void insert(MedicalExam m) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into medical_exams (title, description, start_time, end_time, price, " +
                    "state, state_extra_info, id_customer, id_doctor) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getStartTime().toString());
            ps.setString(3, m.getEndTime().toString());
            ps.setDouble(4, m.getPrice());
            ps.setString(5, (m.getState()).getState());
            ps.setString(6, m.getState().getExtraInfo());
            if (m.getIdCustomer() != 0) {
                ps.setInt(7, m.getIdCustomer());
            } else {
                ps.setNull(7, m.getIdCustomer());
            }
            ps.setInt(8, m.getIdDoctor());
            ps.executeUpdate();
            //get generated id from dbms
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                m.setId(rs.getInt(1));
            }
        } finally {
            assert rs != null : "ResultSet is null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public void update(MedicalExam m) throws Exception {
        //update base info of a medical exam
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update medical_exams set title = ?, description = ?, start_time = ?, end_time = ?, " +
                    "price = ?, state=?, state_extra_info = ?, id_customer = ?, id_doctor = ?  where id = ?");
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getStartTime().toString());
            ps.setString(4, m.getEndTime().toString());
            ps.setDouble(5, m.getPrice());
            ps.setString(5, (m.getState()).getState());
            ps.setString(6, m.getState().getExtraInfo());
            if (m.getIdCustomer() != 0) {
                ps.setInt(7, m.getIdCustomer());
            } else {
                ps.setNull(7, m.getIdCustomer());
            }
            ps.setInt(8, m.getIdDoctor());
            ps.setInt(9, m.getId());
            ps.executeUpdate();
        } finally {
            assert ps != null : "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        int rows = 0;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("delete from medical_exams where id = ?");
            ps.setInt(1, id);
            rows = ps.executeUpdate();
        } finally {
            assert ps != null : "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
        return rows > 0;
    }

    @Override
    public List<MedicalExam> getDoctorExams(int doctorId) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<MedicalExam> mEList = new ArrayList<>();
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from medical_exams where id_doctor = ?");
            ps.setInt(1, doctorId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("The Doctor has no Medical Exams in the database");
            }
            while (rs.next()) {
                MedicalExam tmp = this.parseMedicalExam(rs);
                ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(tmp.getId());
                tmp.setTags(tags);
                mEList.add(tmp);
            }

        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return mEList;
    }

    @Override
    public List<MedicalExam> getCustomerExams(int customerId) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<MedicalExam> mEList = new ArrayList<>();
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from medical_exams where id_customer = ?");
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("The Customer has no Medical Exams in the database");
            }
            while (rs.next()) {
                MedicalExam tmp = this.parseMedicalExam(rs);
                ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(tmp.getId());
                tmp.setTags(tags);
                mEList.add(tmp);
            }

        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return mEList;
    }

    @Override
    public List<MedicalExam> getExamsByState(String state) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<MedicalExam> mEList = new ArrayList<>();
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from medical_exams where state = ?");
            ps.setString(1, state);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("There are no Medical Exams in the database with the state " + state);
            }
            while (rs.next()) {
                MedicalExam tmp = this.parseMedicalExam(rs);
                ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(tmp.getId());
                tmp.setTags(tags);
                mEList.add(tmp);
            }

        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return mEList;
    }

    @Override
    public List<MedicalExam> getCustomerBookedExams(int customerId) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<MedicalExam> mEList = new ArrayList<>();
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from medical_exams where id_customer = ? and state = 'Booked'");
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("The Customer has no Booked Medical Exams in the database");
            }
            while (rs.next()) {
                MedicalExam tmp = this.parseMedicalExam(rs);
                this.setMedicalExamState(rs, tmp);
                ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(tmp.getId());
                tmp.setTags(tags);
                mEList.add(tmp);
            }
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return mEList;
    }

    @Override
    public void changeState(Integer idExam, State newState) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update medical_exams set state = ?, state_extra_info = ? where id = ?");
            ps.setString(1, newState.getState());
            ps.setString(2, LocalDateTime.now().toString());
            ps.setInt(3, idExam);
            ps.executeUpdate();
        } finally {
            assert ps != null : "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public List<MedicalExam> search(Search search) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<MedicalExam> mEList = new ArrayList<>();
        try {
            con = Database.getConnection();
            ps = con.prepareStatement(search.getSearchQuery());
            int index = 1;
            for (Object tmp : search.getArguments()) {
                ps.setObject(index++, tmp);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                MedicalExam tmp = this.parseMedicalExam(rs);
                this.setMedicalExamState(rs, tmp);
                ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(tmp.getId());
                tmp.setTags(tags);
                mEList.add(tmp);
            }
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return mEList;
    }

    public void bookMedicalExam(MedicalExam me, int idCustomer) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        me.setIdCustomer(idCustomer);
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update medical_exams set id_customer = ?, state = ?, state_extra_info = ? where id = ?");
            ps.setInt(1, me.getIdCustomer());
            ps.setString(2, me.getState().getState());
            ps.setObject(3, me.getState().getExtraInfo());
            ps.executeUpdate();
        } finally {
            assert ps != null : "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
    }
}

