package dao;

import domainModel.Customer;
import domainModel.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MariaDbDoctorDao implements DoctorDao{
    @Override
    public Doctor get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Doctor d = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from doctors where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(!rs.next()) {
                throw new RuntimeException("The Doctor looked for in not present in the database");
            }
            d = new Doctor(
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("date_of_birth"),
                rs.getString("iban"),
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
            rs = stm.executeQuery("select * from doctors");
            while (rs.next()) {
                dList.add(
                        new Doctor(
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getString("date_of_birth"),
                                rs.getString("iban"),
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
            ps = con.prepareStatement("insert into doctors (name, surname, date_of_birth, iban, medical_license_number, balance) " +
                    "values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSurname());
            ps.setString(3, doctor.getDateOfBirth());
            ps.setString(4, doctor.getIban());
            ps.setString(5, doctor.getMedicalLicenseNumber());
            ps.setDouble(6, doctor.getBalance());
            ps.executeUpdate();
            //get generated id from dbms
            rs = ps.getGeneratedKeys();
            if (rs.next()){
                doctor.setId(rs.getInt(1));
            }
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
            ps = con.prepareStatement("update doctors set name = ?, surname = ?, date_of_birth = ?, iban = ?, medical_license_number = ?, balance = ? where id = ?");
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSurname());
            ps.setString(3, doctor.getDateOfBirth());
            ps.setString(4, doctor.getIban());
            ps.setString(5, doctor.getMedicalLicenseNumber());
            ps.setInt(6, doctor.getId());
            ps.setDouble(7, doctor.getBalance());
            ps.executeUpdate();
        } finally {
            assert ps != null: "preparedStatement is Null";
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
            ps = con.prepareStatement("delete from doctors where id = ?");
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
