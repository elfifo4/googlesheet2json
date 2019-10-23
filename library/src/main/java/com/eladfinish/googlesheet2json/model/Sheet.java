package com.eladfinish.googlesheet2json.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Sheet {

    @SerializedName("version")
    private String version;
    @SerializedName("feed")
    private Feed feed;

    public Sheet(String version, Feed feed) {
        this.version = version;
        this.feed = feed;
    }

    public Feed getFeed() {
        return feed;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Sheet{" +
                "version='" + version + '\'' +
                ", feed=" + feed +
                '}';
    }

    public class Feed {

        @SerializedName("title")
        private Text title;

        @SerializedName("updated")
        private Text updated;

        @SerializedName("author")
        private ArrayList<Author> authors;

        @SerializedName("entry")
        private ArrayList<JsonElement> rows;

        public Feed(Text title, Text updated, ArrayList<Author> authors, ArrayList<JsonElement> rows) {
            this.title = title;
            this.updated = updated;
            this.authors = authors;
            this.rows = rows;
        }

        public ArrayList<JsonElement> getRows() {
            return rows;
        }

        public ArrayList<Author> getAuthors() {
            return authors;
        }

        public Text getTitle() {
            return title;
        }

        public Text getUpdated() {
            return updated;
        }

        @Override
        public String toString() {
            return "Feed{" +
                    "title=" + title +
                    ", updated=" + updated +
                    ", authors=" + authors +
                    ", rows=" + rows +
                    '}';
        }
    }


    public class Text {

        @SerializedName("$t")
        private String text;

        public Text(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return text;
        }

    }


    public class Author {

        @SerializedName("name")
        private Text name;

        @SerializedName("email")
        private Text email;

        public Author(Text name, Text email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name.getText();
        }

        public String getEmail() {
            return email.getText();
        }

        @Override
        public String toString() {
            return "Author{" +
                    "name=" + name +
                    ", email=" + email +
                    '}';
        }
    }
}


