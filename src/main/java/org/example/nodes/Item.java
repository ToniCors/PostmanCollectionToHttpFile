package org.example.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.interfaces.Visitable;
import org.example.interfaces.Visitor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Items are entities which contain an actual HTTP request, and sample responses attached to it.
 */
public class Item implements Visitable {

    private static String httpVersion = " HTTP/1.1";

    private static final String FORMDATA_TEMPLATE = """
            Content-Disposition: form-data; name=%s; filename=%s
                        
            < %s
            --WebAppBoundary
            """;
    private static final String REQUEST_TEMPLATE = """
            # @name %s
            %s %s %s
            %s
            %s
            ###""";
    private String name;
    private String method;
    private String url;
    private ArrayList<String> headers;
    private String body;

    private String parentDir;


    public Item(JsonNode node, String parentAuth, String parentDir) {
        name = node.get("name").asText("NAME IS ABSENT");
        method = node.get("request").get("method").asText();
        url = node.get("request").get("url").get("raw").asText();
        headers = getHeader((ArrayNode) node.get("request").get("header"));
        body = getBody((ObjectNode) node.get("request").get("body"));
        this.parentDir=parentDir;

        String auth = Auth.getAuth(node.get("auth"), parentAuth);
        if (auth!=null)headers.add(auth);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public ArrayList<String> getHeader(ArrayNode headersNode) {
        ArrayList<String> headers = new ArrayList<>();
        if (headersNode == null) return headers;

        String s;
        for (JsonNode h : headersNode) {
            s = h.get("key").asText() + ": " + h.get("value").asText();
            if (h.has("disabled"))
                if (h.get("disabled").asBoolean()) s = "# " + s;
            if (h.has("description")) s = s + "\t" + h.get("description").asText("");
            headers.add(s);
        }
        return headers;
    }

    public String getBody(ObjectNode node) {
        if (node == null) return "";

        String mode = node.get("mode").asText();
        return switch (mode) {
            case "raw" -> node.get("raw").asText();
            case "urlencoded" -> getUrlencoded((ArrayNode) node.get("urlencoded"));
            case "file", "graphql" -> mode + " Not implemented yet";
            case "formdata" -> getFormdata((ArrayNode) node.get("formdata"));
            default -> "Body mode unaspected:  " + mode;
        };
    }

    private String getFormdata(ArrayNode headersNode) {
        StringBuilder s = new StringBuilder("--WebAppBoundary\n");
        for (JsonNode n : headersNode) {
            s.append(String.format(FORMDATA_TEMPLATE, n.get("key").asText(), n.get("src").asText(), n.get("src").asText()));
        }
        return s.toString();
    }

    private String getUrlencoded(ArrayNode headersNode) {
        StringBuilder s = new StringBuilder();
        for (JsonNode n : headersNode) {
            s.append(n.get("key").asText()).append(" ");
            s.append(n.get("value").asText()).append("\n");
        }
        return s.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public ArrayList<String> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    public String toHttpString() {
        return String.format(REQUEST_TEMPLATE, this.name, this.method, this.url, httpVersion,this.headerToString(), this.body);
    }

    public void appendtoHttpFile(String fileName) throws IOException {

        FileWriter fw = new FileWriter(fileName);
        fw.append(this.toHttpString());
        fw.flush();
        fw.close();
    }

    private String headerToString(){
        StringBuilder sb = new StringBuilder();
        for (String s : headers){
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}