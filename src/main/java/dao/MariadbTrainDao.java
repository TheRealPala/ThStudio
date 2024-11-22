package dao;
import domainModel.Train;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class MariadbTrainDao implements TrainDAO{
    @Override
    public Train get(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Train t = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("select * from trains where id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                t = new Train(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("date"),
                        rs.getString("start_time"),
                        rs.getString("duration"),
                        rs.getString("end_time"),
                        rs.getInt("id_trainer"),
                        rs.getString("state_extra_info"),
                        rs.getInt("id_customer"),
                        rs.getInt("id")

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
    public List<Train> getAll() throws SQLException {
        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;
        List<Train> tList = new ArrayList<>();
        try {
            con = Database.getConnection();
            stm = con.createStatement();
            rs = stm.executeQuery("select * from trains");
            while (rs.next()) {
                tList.add(
                        new Train(
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getString("status"),
                                rs.getString("date"),
                                rs.getString("start_time"),
                                rs.getString("duration"),
                                rs.getString("end_time"),
                                rs.getInt("id_trainer"),
                                rs.getString("state_extra_info"),
                                rs.getInt("id_customer"),
                                rs.getInt("id")
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
    public void insert(Train train) throws SQLException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("insert into trains (title, description, status, date, start_time, duration, end_time, id_trainer, state_extra_info, id_customer)" +
                    " values(?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, train.getTitle());
            ps.setString(2, train.getDescription());
            ps.setString(3, train.getStatus());
            ps.setString(4, train.getDate());
            ps.setString(5, train.getStartTime());
            ps.setString(6, train.getDuration());
            ps.setString(7, train.getEndTime());
            ps.setInt(8, train.getIdEmployee());
            ps.setString(9, train.getStateExtraInfo());
            ps.setInt(10, train.getIdCustomer());
            ps.executeUpdate();
        }
        finally {
            assert rs != null: "ResultSet is Null";
            ps.close();
            Database.closeConnection(con);

        }


    }

    @Override
    public void update(Train train) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("update trains set title = ?, description = ?, status = ?, date = ?, start_time = ?, duration = ?, end_time = ?," +
                    " id_trainer = ?, state_extra_info = ?, id_customer = ? where id = ?");
            ps.setString(1, train.getTitle());
            ps.setString(2, train.getDescription());
            ps.setString(3, train.getStatus());
            ps.setString(4, train.getDate());
            ps.setString(5, train.getStartTime());
            ps.setString(6, train.getDuration());
            ps.setString(7, train.getEndTime());
            ps.setInt(8, train.getIdEmployee());
            ps.setString(9, train.getStateExtraInfo());
            ps.setInt(10, train.getIdCustomer());
            ps.setInt(11, train.getId());
            ps.executeUpdate();
        } finally {
            assert ps != null: "PreparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
    }
    @Override
    public boolean delete(Integer id) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        int rows=0;
        try {
            con = Database.getConnection();
            ps = con.prepareStatement("delete from trains where id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            assert ps != null: "PreparedStatement is Null";
            ps.close();
            Database.closeConnection(con);
        }
        return rows > 0;
    }
}
