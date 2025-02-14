package dao;

import domainModel.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MariaDbNotificationDao implements NotificationDao {

    private Notification parseNotification(ResultSet rs) throws Exception {
        Notification n = null;
        n = new Notification(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("id_receiver")
        );
        return n;
    }

    @Override
    public Notification get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Notification n = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from notifications where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("The Notification looked for in not present in the database");
            }
            n = parseNotification(rs);
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return n;
    }

    @Override
    public List<Notification> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<Notification> nList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from notifications");
            while (rs.next()) {
                nList.add(this.parseNotification(rs));
            }
            if (nList.isEmpty()) {
                throw new RuntimeException("There is no Notifications in the database");
            }
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            stm.close();
            Database.closeConnection(con);
        }
        return nList;
    }

    @Override
    public void insert(Notification notification) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into notifications(title, id_receiver) " +
                    "values (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, notification.getTitle());
            ps.setInt(2, notification.getReceiverId());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                notification.setId(rs.getInt(1));
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
    public void update(Notification document) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update notifications set title = ?, id_receiver = ? where id = ?");
            ps.setString(1, document.getTitle());
            ps.setInt(2, document.getReceiverId());
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
            ps = con.prepareStatement("delete from notifications where id = ?");
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
    public List<Notification> getNotificationsByReceiverId(int id) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Notification> nList = new ArrayList<>();
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from notifications where id_receiver = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("The Notification looked for in not present in the database");
            }
            nList.add(parseNotification(rs));
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            assert rs != null : "ResultSet is Null";
            rs.close();
            ps.close();
            Database.closeConnection(con);
        }
        return nList;
    }
}
