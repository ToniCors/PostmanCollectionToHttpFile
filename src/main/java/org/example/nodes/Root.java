package org.example.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.interfaces.Visitable;
import org.example.interfaces.Visitor;

public class Root implements Visitable{

    private String name;

    private String schema;
    private ArrayNode items;
    private String auth;

    public Root(JsonNode root){

        name = root.get("info").get("name").asText();
        schema = root.get("info").get("schema").asText();
        items = (ArrayNode) root.get("item");
        auth = Auth.getAuth(root.get("auth"), null);
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public ArrayNode getItems() {
        return items;
    }

    public void setItems(ArrayNode items) {
        this.items = items;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
