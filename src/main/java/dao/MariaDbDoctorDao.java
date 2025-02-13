package dao;

import domainModel.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MariaDbDoctorDao implements DoctorDao{
    private final PersonDao personDao;

    public MariaDbDoctorDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public Doctor get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Doctor d = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from people natural join doctors where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(!rs.next()) {
                throw new RuntimeException("The Doctor looked for in not present in the database");
            }
            d = new Doctor(
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("date_of_birth"),
                rs.getInt("id"),
                rs.getString("medical_license_number"),
                rs.getDouble("balance")
            );

        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return d;
    }

    @Override
    public List<Doctor> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<Doctor> dList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from people natural join doctors");
            while (rs.next()) {
                dList.add(
                        new Doctor(
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getString("date_of_birth"),
                                rs.getInt("id"),
                                rs.getString("medical_license_number"),
                                rs.getDouble("balance")
                        )
                );
            }
            if (dList.isEmpty()) {
                throw new RuntimeException("There is no Doctors in the database");
            }

        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return dList;
    }

    @Override
    public void insert(Doctor doctor) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Database.getConnection();
            this.personDao.insert(doctor);
            ps = con.prepareStatement("insert into doctors (id, medical_license_number) " +
                    "values (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, doctor.getId());
            ps.setString(2, doctor.getMedicalLicenseNumber());
            ps.executeUpdate();
            //get generated id from dbms
            rs = ps.getGeneratedKeys();
            if (rs.next()){
                doctor.setId(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert rs != null: "ResultSet is null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public void update(Doctor doctor) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            this.personDao.update(doctor);
            ps = con.prepareStatement("update doctors set medical_license_number = ? where id = ?");
            ps.setString(1, doctor.getMedicalLicenseNumber());
            ps.setInt(2, doctor.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert ps != null: "preparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        boolean success;
        try {
            success = this.personDao.delete(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return success;
    }
}
