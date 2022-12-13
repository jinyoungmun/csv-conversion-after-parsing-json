package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class JSONService {

    public String getJsonFile(String fileName) throws IOException {
        return readLines(fileName);
    }

    public String readLines(String fileName){
        return new BufferedReader(
                new InputStreamReader(
                        this.getClass().getClassLoader().getResourceAsStream(fileName)
                )
        ).lines().collect(Collectors.joining());
    }
}
