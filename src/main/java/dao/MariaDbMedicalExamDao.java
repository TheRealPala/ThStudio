package dao;

import domainModel.MedicalExam;
import domainModel.Search.Search;
import domainModel.State.*;
import domainModel.Tags.Tag;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MariaDbMedicalExamDao implements MedicalExamDao {
    private final TagDao tagDao;

    MariaDbMedicalExamDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }
    private void setMedicalExamState(ResultSet rs, MedicalExam m) throws SQLException {
        if (Objects.equals(rs.getString("state"), "Booked")) {
            Booked booked = new Booked();
            m.setState(booked);
        } else if (Objects.equals(rs.getString("state"), "Cancelled")) {
            LocalDateTime ldt = LocalDateTime.parse(rs.getString("stateExtraInfo")); //ldt = cancelledTime
            Deleted deleted = new Deleted(ldt);
            m.setState(deleted);
        } else if (Objects.equals(rs.getString("state"), "Completed")) {
            LocalDateTime ldt = LocalDateTime.parse(rs.getString("stateExtraInfo")); //ldt = completedTime
            Completed completed = new Completed(ldt);
            m.setState(completed);
        } else {
            Available available = new Available();
            m.setState(available);
        }
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
            if (rs.next()) {
                m = new MedicalExam(
                        rs.getInt("id"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_doctor"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getString("description"),
                        rs.getString("title"),
                        rs.getDouble("price")
                );
                this.setMedicalExamState(rs, m);
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
            while (rs.next()) {
                MedicalExam tmp = new MedicalExam(
                        rs.getInt("id"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_doctor"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getString("description"),
                        rs.getString("title"),
                        rs.getDouble("price")
                );
                this.setMedicalExamState(rs, tmp);
                ArrayList<Tag> tags = this.tagDao.getTagsFromMedicalExam(tmp.getId());
                tmp.setTags(tags);
                mEList.add(tmp);
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
            ps.setString(6, m.getStateExtraInfo());
            ps.setInt(7, m.getIdCustomer());
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
                    "price = ? where id = ?");
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getStartTime().toString());
            ps.setString(4, m.getEndTime().toString());
            ps.setDouble(5, m.getPrice());
            ps.setInt(6, m.getId());
            ps.executeUpdate();
        } finally {
            assert ps != null: "preparedStatement is Null";
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
            assert ps != null: "preparedStatement is Null";
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
            while (rs.next()) {
                MedicalExam tmp = new MedicalExam(
                        rs.getInt("id"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_doctor"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getString("description"),
                        rs.getString("title"),
                        rs.getDouble("price")
                );
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
            while (rs.next()) {
                MedicalExam tmp = new MedicalExam(
                        rs.getInt("id"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_doctor"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getString("description"),
                        rs.getString("title"),
                        rs.getDouble("price")
                );
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
            while (rs.next()) {
                MedicalExam tmp = new MedicalExam(
                        rs.getInt("id"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_doctor"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getString("description"),
                        rs.getString("title"),
                        rs.getDouble("price")
                );
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
            while (rs.next()) {
                MedicalExam tmp = new MedicalExam(
                        rs.getInt("id"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_doctor"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getString("description"),
                        rs.getString("title"),
                        rs.getDouble("price")
                );
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
            assert ps != null: "preparedStatement is Null";
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
            rs = ps.executeQuery();
            while (rs.next()) {
                MedicalExam tmp = new MedicalExam(
                        rs.getInt("id"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_doctor"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getString("description"),
                        rs.getString("title"),
                        rs.getDouble("price")
                );
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


}

