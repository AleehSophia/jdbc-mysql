package application;

import db.DB;
import db.DbException;
import db.DbIntegrityException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Program {
    public static void main(String[] args) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        PreparedStatement pst = null;
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        //        recuperação de dados
        try {
            conn = DB.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select * from department");

            while (rs.next()) {
                System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
//            DB.closeConnection();
        }

//        inserção de dados
        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(
                    "INSERT INTO seller "
                    + "(Name, Email, BirthDate, BaseSalary, DepartmentId)"
                    + "VALUES "
                    + "(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, "Carl Purple");
            pst.setString(2, "carl@gmail.com");
            pst.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
            pst.setDouble(4, 3000.0);
            pst.setInt(5, 4);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                rs = pst.getGeneratedKeys();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Done! Id = " + id);
                }
            }
            else {
                System.out.println("No rows affected!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            DB.closeStatement(pst);
//            DB.closeConnection();
        }

        //atuaizar salario do vendedor
        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(
                    "UPDATE seller "
                    + "SET BaseSalary = BaseSalary + ? "
                    + "WHERE "
                    + "(DepartmentId = ?)");
            pst.setDouble(1, 200.0);
            pst.setInt(2, 2);

            int rowsAffected = pst.executeUpdate();
            System.out.println("Done! Rows affected: " + rowsAffected);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DB.closeStatement(pst);
//            DB.closeConnection();
        }

//        deletar dados
//        try {
//            conn = DB.getConnection();
//            pst = conn.prepareStatement(
//                    "DELETE FROM department "
//                    + "WHERE "
//                    + "Id = ?");
//            pst.setInt(1, 1);
//
//            int rowsAffected = pst.executeUpdate();
//            System.out.println("Done! Rows affected: " + rowsAffected);
//        }
//        catch (SQLException e) {
//            throw new DbIntegrityException(e.getMessage());
//        }
//        finally {
//            DB.closeStatement(pst);
////            DB.closeConnection();
//        }

//        transações
        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false);

            st = conn.createStatement();

            int rows1 = st.executeUpdate("UPDATE seller SET baseSalary = 2090 WHERE DepartmentId = 1");
            int x = 1;
//            if (x < 2) {
//                throw new SQLException("Fake error");
//            }
            int rows2 = st.executeUpdate("UPDATE seller SET baseSalary = 3090 WHERE DepartmentId = 2");
            conn.commit();

            System.out.println("rows1 " + rows1);
            System.out.println("rows2 " + rows2);
        }
        catch (SQLException e) {
            try {
                conn.rollback();
                throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
            }
            catch (SQLException e1) {
                throw new DbException("Error trying to rollback! Caused by: " + e.getMessage());
            }
        }
        finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }
}