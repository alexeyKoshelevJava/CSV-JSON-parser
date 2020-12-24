package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class App {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        createCsvWithEmployee("1,John,Smith,USA,25");
        addToCsv("2,Ivan,Petrov,RU,23");

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        System.out.println("список сотрудников из CSV");
        list.forEach(System.out::println);

        String json = listToJson(list);
        System.out.println("список сотрудников Json"+json);
        writeString(json, "employees.json");

        createXmlWithEmployee("new_staff.xml", "1", "John", "Smith", "USA", "25");
        addEmployeeToXml("2", "Ivan", "Petrov", "RU", "23");
        List<Employee> listEmployee = parseXML("new_staff.xml");
        System.out.println("список сотрудников из xml"+listEmployee);
        String new_json = listToJson(listEmployee);
        writeString(json, "new_json.json");







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

    public static void createXmlWithEmployee(String pathname, String id, String name, String lastName, String country, String age) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element staff = document.createElement("staff");
        document.appendChild(staff);

        Element employee = document.createElement("employee");
        staff.appendChild(employee);
        employee.setAttribute("id", id);
        employee.setAttribute("firstname", name);
        employee.setAttribute("lastname", lastName);
        employee.setAttribute("country", country);
        employee.setAttribute("age", age);
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(pathname));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }

    public static void addEmployeeToXml(String id, String name, String lastName, String country, String age) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("new_staff.xml"));
        Node root = document.getDocumentElement();

        Element employee = document.createElement("employee");
        root.appendChild(employee);

        employee.setAttribute("id", id);
        employee.setAttribute("firstname", name);
        employee.setAttribute("lastname", lastName);
        employee.setAttribute("country", country);
        employee.setAttribute("age", age);


        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("new_staff.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }
    public static List<Employee> parseXML(String pathname) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(pathname));
        NodeList employeeElements = document.getDocumentElement().getElementsByTagName("employee");
        for (int i = 0; i < employeeElements.getLength(); i++) {
            Node employee = employeeElements.item(i);
            NamedNodeMap attributes = employee.getAttributes();

            employees.add(new Employee(Integer.parseInt(attributes.getNamedItem("id").getNodeValue()),
                    attributes.getNamedItem("firstname").getNodeValue(),
                    attributes.getNamedItem("lastname").getNodeValue(),
                    attributes.getNamedItem("country").getNodeValue(),
                    Integer.parseInt(attributes.getNamedItem("age").getNodeValue())));
        }
        return employees;
}


    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String path) {


        try (FileWriter file = new FileWriter(path)) {
            file.write(json);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


