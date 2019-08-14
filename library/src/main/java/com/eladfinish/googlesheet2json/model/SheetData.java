package com.eladfinish.googlesheet2json.model;

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

    @Override
    public String toString() {
        return "SheetData{" +
                "version='" + version + '\'' +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", rows=" + rows +
                '}';
    }
}
