package application;

import db.DB;
import db.DbException;

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
            DB.closeConnection();
        }
    }
}