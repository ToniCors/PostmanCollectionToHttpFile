package org.example.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.interfaces.Visitable;
import org.example.interfaces.Visitor;
import org.example.nodes.Folder;
import org.example.nodes.Item;
import org.example.nodes.Root;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class PostmanParser implements Visitor {

    private String rootDir;

    private boolean oneFileForRequest=false;
    public PostmanParser(String rootDir) {
        this.rootDir=rootDir+"\\out";
        new File(this.rootDir).mkdirs();
    }

    public static boolean isPostmanFolder(JsonNode node) {
        return node.has("item");
    }

    public static boolean isPostmanRequest(JsonNode node) {
        return node.has("request");
    }

    @Override
    public void visit(Visitable v) {
        FileWriter fw;
        String dir;

        if (v instanceof Root) {
            System.out.print("Root: "+((Root) v).getName());
            dir = rootDir +"\\"+((Root) v).getName();
            System.out.println(new File(dir).mkdirs());
            iterateItem(((Root) v).getItems(),((Root) v).getAuth(),dir);

        } else if (v instanceof Folder) {
            System.out.print("Folder: "+((Folder) v).getName());
            dir = ((Folder) v).getParentDir() +"\\"+((Folder) v).getName();
            System.out.println(new File(dir).mkdirs());
            iterateItem(((Folder) v).getItems(),((Folder) v).getAuth(),dir);

        } else if (v instanceof Item) {
            try {
                fw = new FileWriter(getFilePath((Item) v,((Item) v).getParentDir()),true);
                fw.append(((Item) v).toHttpString());
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IMPOSSIBLE TO CREATE ITEM: "+ ((Item) v).getName());
            }
        } else System.out.println("visit: ELEMENT NOT RECOGNIZED");
    }

    private void iterateItem(ArrayNode items, String parentAuth,String parentDir) {

        for (JsonNode i : items) {
            if (isPostmanFolder(i)) {
                new Folder(i,parentAuth,parentDir).accept(this);
            } else if (isPostmanRequest(i)) {
                new Item(i,parentAuth, parentDir).accept(this);
            } else System.out.println("iterateItem: ELEMENT NOT RECOGNIZED");
        }

    }

    private String getFilePath(Item v, String currentDir){
        String filePath;
        if (oneFileForRequest){
            filePath = currentDir+"\n"+v.getName()+".http";
        }else {
            String[] split = currentDir.split("\\\\");
            filePath = currentDir+"\\"+split[split.length-1]+".http";
        }
        return filePath;
    }

    public String getRootDir() {
        return rootDir;
    }

    public boolean isOneFileForRequest() {
        return oneFileForRequest;
    }

    public void setOneFileForRequest(boolean oneFileForRequest) {
        this.oneFileForRequest = oneFileForRequest;
    }
}
