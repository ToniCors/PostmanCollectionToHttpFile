package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.nodes.Root;
import org.example.parser.PostmanParser;

import java.io.File;
import java.io.IOException;

public class MainVisitor {

    static String rootDir = "D:\\Desktop\\POSTMAN BACKUP\\collection";


    public static void main(String[] args) throws IOException {

        PostmanParser p = new PostmanParser(rootDir);

        ObjectMapper mapper = new ObjectMapper();


        JsonNode root;

        File[] files = new File(rootDir).listFiles();
        assert files != null;
        for (File f : files) {
            if (f.isFile()) {
                if (f.getName().endsWith(".json")) {
                    System.out.println(f.getAbsolutePath());
                    root = mapper.readTree(f);
                    Root rr = new Root(root);
                    rr.accept(p);

//            break;
                }
            }
        }
    }
}
