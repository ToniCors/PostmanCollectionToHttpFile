package org.example.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public final class Auth {

    public static String getAuth(JsonNode node, String parentAuth) {
        if (node==null) return null;
        String type = node.get("type").asText();
        switch (type) {
            case "noauth" -> {
                return parentAuth;
            }
            case "basic" -> {
                return getBasicAuth((ArrayNode) node.get("basic"));
            }
            case "bearer" -> {
                return getBearerAuth((ArrayNode) node.get("bearer"));
            }
            default -> {
                return "#Auth type Not implemented yet:  " + type;
            }
        }
    }

    private static String getBasicAuth(ArrayNode node) {
        if (node.size() == 2) {
            return "Authorization: Basic " + node.get(1).get("value").asText() + " " + node.get(0).get("value").asText();
        } else {
            return "#Authorization: Basic too many params: " + node.size();
        }
    }

    private static String getBearerAuth(ArrayNode node) {
        if (node.size() == 1) {
            return "Authorization: Bearer " + node.get(0).get("value").asText();
        } else {
            return "#Authorization: Bearer too many params: " + node.size();
        }
    }
}
