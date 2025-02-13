package dao;

import domainModel.Customer;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class MariaDbCustomerDao implements CustomerDao {
    private final PersonDao personDao;

    public MariaDbCustomerDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public Customer get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer c = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from people natural join customers where id = ?");
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
            rs = stm.executeQuery("select * from people natural join customers");
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
        try {
            con = Database.getConnection();
            this.personDao.insert(customer);
            System.out.println("id of customer after person insert: " + customer.getId());
            ps = con.prepareStatement("insert into customers (id, level) " +
                    "values (?, ?)");
            ps.setInt(1, customer.getId());
            ps.setInt(2, customer.getLevel());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert ps != null : "Prepared statement is null";
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
            this.personDao.update(customer);
            ps = con.prepareStatement("update customers set level=? where id = ?");
            ps.setInt(1, customer.getLevel());
            ps.setInt(2, customer.getId());
            ps.executeUpdate();
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
