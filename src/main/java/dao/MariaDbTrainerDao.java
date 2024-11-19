package dao;
import domainModel.Trainer;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class MariaDbTrainerDao implements TrainerDAO{

    @Override
    public Trainer get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Trainer t = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from trainers where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                t = new Trainer(
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("date_of_birth"),
                        rs.getString("iban"),
                        rs.getInt("id"),
                        rs.getString("specialization")
                );
            }
        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return t;
    }

    @Override
    public List<Trainer> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<Trainer> tList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from trainers");
            while (rs.next()) {
                tList.add(
                        new Trainer(
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getString("date_of_birth"),
                                rs.getString("iban"),
                                rs.getInt("id"),
                                rs.getString("specialization")
                        )
                );
            }
        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return tList;
    }
    @Override
    public void insert(Trainer t) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into trainers (name, surname, date_of_birth, iban, specialization) " +
                    "values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, t.getName());
            ps.setString(2, t.getSurname());
            ps.setString(3, t.getDateOfBirth());
            ps.setString(4, t.getIban());
            ps.setString(5, t.getSpecialization());
            ps.executeUpdate();
        } finally {
            ps.close();
            Database.closeConnection(con);
        }
    }
    @Override
    public void update(Trainer t) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update trainers set name = ?, surname = ?, date_of_birth = ?, iban = ?, specialization = ? where id = ?");
            ps.setString(1, t.getName());
            ps.setString(2, t.getSurname());
            ps.setString(3, t.getDateOfBirth());
            ps.setString(4, t.getIban());
            ps.setString(5, t.getSpecialization());
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } finally {
            ps.close();
            Database.closeConnection(con);
        }
    }
    @Override
    public boolean delete(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("delete from trainers where id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } finally {
            ps.close();
            Database.closeConnection(con);
        }

    }
}

