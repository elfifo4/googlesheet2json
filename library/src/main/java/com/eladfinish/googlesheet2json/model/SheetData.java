package com.eladfinish.googlesheet2json.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class SheetData {

    private String version;
    private String title;
    private ArrayList<Sheet.Author> authors;
    private ArrayList<BaseSheetEntry> rows;

    private SheetData(Builder builder) {
        this.version = builder.version;
        this.title = builder.title;
        this.authors = builder.authors;
        this.rows = builder.rows;
    }

    public static class Builder {
        private String version;
        private String title;
        private ArrayList<Sheet.Author> authors;
        private final ArrayList<BaseSheetEntry> rows;

        public Builder(ArrayList<BaseSheetEntry> rows) {
            this.rows = rows;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setAuthors(ArrayList<Sheet.Author> authors) {
            this.authors = authors;
            return this;
        }

        public SheetData build() {
            // call the private constructor in the outer class
            return new SheetData(this);
        }
    }

    public String getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Sheet.Author> getAuthors() {
        return authors;
    }

    public ArrayList<BaseSheetEntry> getRows() {
        return rows;
    }


    public String getRowsAsOriginalFormPrettyJson() {
      return getPrettyJsonString(rows);
    }

    public String getRowsAsPrettyJson() {
        //regex to match string of the form: "gsx$firstname":{"$t":"Elad"}
        //noinspection RegExpRedundantEscape
        String regex = "\"gsx\\$(.*?)\":\\{\"\\$t\":\"(.*?)\"\\}";

        String simpleJson = new Gson().toJson(rows).replaceAll(regex, "\"$1\":\"$2\"");
        return toPrettyFormat(simpleJson);
    }


    @Override
    public String toString() {
        return "SheetData{" +
                "version='" + version + '\'' +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", rows=" + rows +
                '}';
    }


    /**
     * Convert an object to pretty print json version
     *
     * @param obj object to be converted to json
     * @return pretty format of the json representation of the object
     */
    private static String getPrettyJsonString(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonInString = gson.toJson(obj);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonInString);
        return gson.toJson(jsonElement);
    }


    /**
     * Convert a JSON string to pretty print version
     *
     * @param jsonString raw json as string (JsonObject or JsonArray)
     * @return pretty format of the json
     */
    private static String toPrettyFormat(String jsonString) {
        JsonParser parser = new JsonParser();

        JsonElement jsonElement;
        JsonElement parse = parser.parse(jsonString);
        try {
            jsonElement = parse.getAsJsonObject();
        } catch (Exception e) {//java.lang.IllegalStateException: Not a JSON Object
            // if jsonElement is not a JsonObject, try to parse it as a JsonArray
            jsonElement = parse.getAsJsonArray();
//            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonElement);
    }
}
