package dao;

import domainModel.Customer;

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
            if(!rs.next()) {
                throw new RuntimeException("The Customer looked for in not present in the database");
            }
            c = new Customer(
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("date_of_birth"),
                rs.getInt("id"),
                rs.getInt("level"),
                rs.getDouble("balance")
            );
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
                        rs.getInt("id"),
                        rs.getInt("level"),
                        rs.getDouble("balance")
                    )
                );
            }
            if(cList.isEmpty()){
                throw new RuntimeException("There is no Customers in the database");
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
            ps = con.prepareStatement("insert into customers (name, surname, date_of_birth, level, balance) " +
                    "values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getSurname());
            ps.setString(3, customer.getDateOfBirth());
            ps.setInt(4, customer.getLevel() );
            ps.setDouble(5, customer.getBalance());
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
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update customers set name = ?, surname = ?, date_of_birth = ?, level = ?, balance = ? where id = ?");
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getSurname());
            ps.setString(3, customer.getDateOfBirth());
            ps.setInt(4, customer.getLevel());
            ps.setInt(5, customer.getId());
            ps.setDouble(6, customer.getBalance());
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
