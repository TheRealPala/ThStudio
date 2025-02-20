package com.thstudio.project.dao;

import com.thstudio.project.domainModel.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MariaDbPersonDao implements PersonDao{
    @Override
    public Person get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Person p = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from people where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(!rs.next()) {
                throw new RuntimeException("The person looked for in not present in the database");
            }
            p = new Person(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("date_of_birth"),
                    rs.getDouble("balance")
            );

        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return p;
    }

    @Override
    public List<Person> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<Person> pList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from people");
            while (rs.next()) {
                pList.add(
                        new Person(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getString("date_of_birth"),
                                rs.getDouble("balance")
                        )
                );
            }
            if (pList.isEmpty()) {
                throw new RuntimeException("There is no people in the database");
            }

        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return pList;
    }

    @Override
    public void insert(Person person) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into people (name, surname, date_of_birth, balance) " +
                    "values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, person.getName());
            ps.setString(2, person.getSurname());
            ps.setString(3, person.getDateOfBirth());
            ps.setDouble(4, person.getBalance());
            ps.executeUpdate();
            //get generated id from dbms
            rs = ps.getGeneratedKeys();
            if (rs.next()){
                person.setId(rs.getInt(1));
            }
        } finally {
            assert rs != null: "ResultSet is null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public void update(Person person) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update people set name = ?, surname = ?, date_of_birth = ?, balance = ? where id = ?");
            ps.setString(1, person.getName());
            ps.setString(2, person.getSurname());
            ps.setString(3, person.getDateOfBirth());
            ps.setDouble(4, person.getBalance());
            ps.setInt(5, person.getId());
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
            ps = con.prepareStatement("delete from people where id = ?");
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
