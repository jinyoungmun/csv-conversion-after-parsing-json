package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.MariaDBService;
import vo.Company;
import service.CSVService;
import service.JSONService;
import service.SFTPService;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // Parsing JSON with Jackson
        ObjectMapper objectMapper = new ObjectMapper();

        JSONService jsonService = new JSONService();

        try {
            // JSON to jsonString
            String jsonString = jsonService.getJsonFile("JSON.json");
            System.out.println(jsonString);
            System.out.println();

            // jsonString Parsing
            List<Company> companies = objectMapper.readValue(jsonString, new TypeReference<List<Company>>(){});

            for(Company company : companies){
                System.out.println("name : " + company.getName());
                System.out.println("numberOfEmployees : " + company.getNumberOfEmployees());
                System.out.println("services : " + company.getService());
                System.out.println();
            }

            // Generate CSV
            CSVService.generateCsv(companies);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // SFTP
        final SFTPService sftpService = new SFTPService();

        sftpService.init("192.168.0.71", "inspien", "inspien00", 22);

        sftpService.mkdir("/home/inspien", "jym");

        sftpService.upload("/home/inspien/jym", "src/main/resources/CSV.csv");

        if(sftpService.exists("/home/inspien/jym/CSV.csv") == true){

            sftpService.download("/home/inspien/jym", "CSV.csv", "src/main/resources/CSV_1.csv");
        }

        sftpService.disconnection();


        // Parsing CSV with OpenCsv
        ArrayList<Map<String, Object>> arrayList = CSVService.parseCsv("src/main/resources/CSV_1.csv");


        // Connecting MariaDB
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;

        String driver = "org.mariadb.jdbc.Driver";
        String ip = "localhost";
        String port = "3307";
        String dbName = "mariadb";
        String url = "jdbc:mariadb://" + ip + ":" + port + "/" + dbName;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "user", "user");
            // autoCommitFalse
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("DB Connection fail");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
            e.printStackTrace();
        }

        final MariaDBService mariaDBService = new MariaDBService();

        mariaDBService.insert(arrayList, conn, stmt);

        mariaDBService.update(arrayList, conn, pstmt);

        mariaDBService.disconnection(conn, stmt, pstmt);

    }
}
