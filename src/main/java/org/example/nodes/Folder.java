package org.example.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.interfaces.Visitable;
import org.example.interfaces.Visitor;

/**One of the primary goals of Postman is to organize the development of APIs.
 * To this end, it is necessary to be able to group requests together.
 * This can be achived using 'Folders'.
 * A folder just is an ordered set of requests.*/
public class Folder implements Visitable {

    private ArrayNode items;
    private String name;
    private String auth;
    private String parentDir;
    private static final String ERROR_LABEL = "INFO NOT PRESENT";

    public Folder(JsonNode node, String parentAuth, String parentDir){

        this.name= node.get("name").asText(ERROR_LABEL);
        this.items= (ArrayNode)node.get("item");
        this.auth = Auth.getAuth(node.get("auth"), parentAuth);
        this.parentDir=parentDir;
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public ArrayNode getItems() {
        return items;
    }

    public void setItems(ArrayNode items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }
}