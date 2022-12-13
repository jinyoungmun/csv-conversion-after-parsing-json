package service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import vo.Company;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVService {

    // Generating CSV with OpenCsv
    public static void generateCsv(List<Company> companies) {
        try {
            CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream("src/main/resources/CSV.csv"), "EUC-KR"),
                    ',', '"', '"', "\n");
            cw.writeNext(new String[]{"name", "numberOfEmployees", "services"});

            try {
                for (Company company : companies) {
                    cw.writeNext(new String[]{String.valueOf(company.getName()), String.valueOf(company.getNumberOfEmployees()), String.valueOf(company.getService()
                    )});
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cw.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parsing CSV with OpenCsv
    public static ArrayList<Map<String, Object>> parseCsv(String filePath){

        Map<String, Object> hmap = null;
        ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();

        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] nextLine;

            while((nextLine = reader.readNext()) != null){
                for(int i = 0; i < nextLine.length; i++){
                    if(i == 0){
                        hmap = new HashMap<String, Object>();
                        hmap.put("name", nextLine[i]);
                    }else if(i == 1){
                        hmap.put("number_of_employees", nextLine[i]);
                    }else{
                        hmap.put("service", nextLine[i]);
                    }
                }
                arrayList.add(hmap);
            }
            arrayList.remove(0);

        }catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
