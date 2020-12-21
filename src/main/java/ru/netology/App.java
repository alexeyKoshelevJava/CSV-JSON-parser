package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class App {
    public static void main(String[] args) {
        createCsvWithEmployee("1,John,Smith,USA,25");
        addToCsv("2,Ivan,Petrov,RU,23");

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        list.forEach(System.out::println);
        String json = listToJson(list);
        System.out.println(json);
        writeString(json);


    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createCsvWithEmployee(String lineWithEmployee) {
        String[] employee = lineWithEmployee.split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            writer.writeNext(employee);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void addToCsv(String lineWithEmployee) {
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv", true))) {
            String[] employee = lineWithEmployee.split(",");
            writer.writeNext(employee);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json) {


        try (FileWriter file = new FileWriter("employees.json")) {
            file.write(json);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


