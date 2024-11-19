package dao;

import domainModel.Customer;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class MariaDbCustomerDao implements CustomerDao {
    @Override
    public Customer get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer c = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from customers where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                c = new Customer(
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("date_of_birth"),
                        rs.getString("iban"),
                        rs.getInt("id"),
                        rs.getInt("level")
                );
            }
        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return c;
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<Customer> cList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from customers");
            while (rs.next()) {
                cList.add(
                    new Customer(
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("date_of_birth"),
                        rs.getString("iban"),
                        rs.getInt("id"),
                        rs.getInt("level")
                    )
                );
            }
        } finally {
            assert rs != null: "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return cList;
    }

    @Override
    public void insert(Customer customer) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into customers (name, surname, date_of_birth, iban, level) " +
                    "values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getSurname());
            ps.setString(3, customer.getDateOfBirth());
            ps.setString(4, customer.getIban());
            ps.setInt(5, customer.getLevel());
            ps.executeUpdate();
            //get generated id from dbms
            rs = ps.getGeneratedKeys();
            if (rs.next()){
                customer.setId(rs.getInt(1));
            }
        } finally {
            assert rs != null: "ResultSet is null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
    }

    @Override
    public void update(Customer customer) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        Customer c = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update customers set name = ?, surname = ?, date_of_birth = ?, iban = ?, level = ? where id = ?");
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getSurname());
            ps.setString(3, customer.getDateOfBirth());
            ps.setString(4, customer.getIban());
            ps.setInt(5, customer.getLevel());
            ps.setInt(6, customer.getId());
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
        Customer c = null;
        int rows = 0;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("delete from customers where id = ?");
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
