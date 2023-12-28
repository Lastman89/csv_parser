package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int counter = 1;
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, Paths.get("files/data.csv").toString());
        String json = listToJson(list);
        writeString(json, counter);
        counter++;

        list = parseXML(Paths.get("files/data.xml").toString());
        String toJsonson = listToJson(list);
        writeString(toJsonson, counter);

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .setPrettyPrinting()
                .create();
        return gson.toJson(list);
    }

    public static void writeString(String file, int counter) {
        String Name = null;
        Path path = Paths.get("files");
        if (counter != 1) {
            Name = path.toString() + "/data" + counter + ".json";
        } else {
            Name = path.toString() + "/data.json";
        }
        try (FileWriter writer = new FileWriter(Name, false)) {
            // запись всей строки
            writer.write(file.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static List<Employee> parseXML(String file) {
        List<Employee> list = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        try {
            doc = builder.parse(new File(file));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Node root = doc.getDocumentElement();

        NodeList nodeList = root.getChildNodes();
        Employee values = new Employee();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.TEXT_NODE != node_.getNodeType()) {
                Element element = (Element) node_;
                values.setId(Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()));
                values.setFirstName(element.getElementsByTagName("firstName").item(0).getTextContent());
                values.setLastName(element.getElementsByTagName("lastName").item(0).getTextContent());
                values.setCountry(element.getElementsByTagName("country").item(0).getTextContent());
                values.setAge(Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent()));
                list.add(values);
            }
        }
        return list;
    }
}