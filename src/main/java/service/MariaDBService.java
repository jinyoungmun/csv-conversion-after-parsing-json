package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class MariaDBService {

    public void insert(ArrayList<Map<String, Object>> arrayList, Connection conn, Statement stmt){
        try{
            String insert = "insert into companies(name, number_of_employees, service) ";
            String values = "values";

            for(int i = 0; i  < arrayList.size(); i++) {
                if(arrayList.size()-1 == i){
                    values += ("("+"\'"+arrayList.get(i).get("name") +"\'"+ "," +Integer.parseInt((String)arrayList.get(i).get("number_of_employees")) + "," + "\'"+arrayList.get(i).get("service")+"\'"+");");
                    continue;
                }
                values += ("("+"\'"+arrayList.get(i).get("name") +"\'"+ "," + Integer.parseInt((String)arrayList.get(i).get("number_of_employees"))+ "," + "\'"+arrayList.get(i).get("service")+"\'"+"), ");
            }

            stmt = conn.createStatement();

            boolean result = stmt.execute(insert+values);
            conn.commit();

            if(result){
                System.out.println("insert fail");
            }else {
                System.out.println("insert success");
            }

        }catch(Exception e) {
            e.printStackTrace();
            if(conn != null){
                try {
                    conn.rollback();
                    System.out.println("rollback");
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }
            }
        }
    }

    public void update(ArrayList<Map<String, Object>> arrayList, Connection conn, PreparedStatement pstmt){
        try{
            String update = "update companies set name=? where number_of_employees>?";

            pstmt = conn.prepareStatement(update);

            pstmt.setString(1, "ÓÞÐêåö"); // Ã¹ ¹øÂ° ÆÄ¶ó¹ÌÅÍ¿¡ ´ëÀÔ
            pstmt.setInt(2, 10000);

            int result = pstmt.executeUpdate();
            conn.commit();

            if(result > 0){
                System.out.println("update success");
            }else{
                System.out.println("update fail");
            }

        }catch(Exception e) {
            e.printStackTrace();
            if(conn != null){
                try {
                    conn.rollback();
                    System.out.println("rollback");
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }
            }
        }
    }

    public void disconnection(Connection conn, Statement stmt, PreparedStatement pstmt){
        try{
            if(stmt != null){
                stmt.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null && !conn.isClosed()){
                conn.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
