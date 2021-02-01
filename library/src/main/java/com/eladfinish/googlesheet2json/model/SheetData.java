package com.eladfinish.googlesheet2json.model;

import com.eladfinish.googlesheet2json.utils.JsonUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SheetData {

    private final String version;
    private final String title;
    private final String updated;
    private final ArrayList<Sheet.Author> authors;
    private final ArrayList<BaseSheetEntry> rows;

    private SheetData(Builder builder) {
        this.version = builder.version;
        this.title = builder.title;
        this.updated = builder.updated;
        this.authors = builder.authors;
        this.rows = builder.rows;
    }

    public static class Builder {
        private String version;
        private String title;
        private String updated;
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

        public Builder setUpdated(String updated) {
            this.updated = updated;
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

    public String getUpdated() {
        return updated;
    }

    public String getUpdatedFormatted() {
        // convert datetime format. e.g. 2017-11-26 to 26.11.2017
        return updated.replaceAll("(\\d+)-(\\d+)-(\\d+)T(.*)\\..*", "$3.$2.$1 $4");
    }

    public ArrayList<Sheet.Author> getAuthors() {
        return authors;
    }

    public ArrayList<BaseSheetEntry> getRows() {
        return rows;
    }


    public String getRowsAsOriginalFormPrettyJson() {
        return JsonUtils.getPrettyJsonString(rows);
    }

    public String getRowsAsOriginalFormRawJson() {
        return new Gson().toJson(rows);
    }

    public String getSheetDataAsOriginalFormRawJson() {
        return new Gson().toJson(SheetData.this);
    }

    public String getRowsAsPrettyJson() {
        return JsonUtils.toPrettyFormat(getRowsAsRawJson());
    }

    public String getRowsAsRawJson() {
        //regex to match string of the form: "gsx$firstname":{"$t":"Elad"}
        //and transform it to: "firstname": "Elad"
        //noinspection RegExpRedundantEscape
        String regex = "\"gsx\\$(.*?)\":\\{\"\\$t\":\"(.*?)\"\\}";

        return new Gson().toJson(rows).replaceAll(regex, "\"$1\":\"$2\"");
    }

    public String getSheetDataAsRawJson() {
        //regex to match string of the form: "gsx$firstname":{"$t":"Elad"}
        //and transform it to: "firstname": "Elad"
        //noinspection RegExpRedundantEscape
        String regex = "\"gsx\\$(.*?)\":\\{\"\\$t\":\"(.*?)\"\\}";

        String json = new Gson().toJson(SheetData.this);
        json = json.replaceAll(regex, "\"$1\":\"$2\"");

        //{"$t":"elfifo4"} -> "elfifo4"
        //noinspection RegExpRedundantEscape
        json = json.replaceAll("\\{\"\\$t\":\"(.*?)\"\\}", "\"$1\"");

        return json;
    }


    @Override
    public @NotNull String toString() {
        return "SheetData{" +
                "version='" + version + '\'' +
                ", title='" + title + '\'' +
                ", updated='" + updated + '\'' +
                ", authors=" + authors +
                ", rows=" + rows +
                '}';
    }
}
