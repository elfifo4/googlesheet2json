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

        @SerializedName("author")
        private ArrayList<Author> author;

        @SerializedName("entry")
        private ArrayList<JsonElement> rows;

        public Feed(Text title, ArrayList<Author> author, ArrayList<JsonElement> rows) {
            this.title = title;
            this.author = author;
            this.rows = rows;
        }

        public ArrayList<JsonElement> getRows() {
            return rows;
        }

        public ArrayList<Author> getAuthor() {
            return author;
        }

        public Text getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "Feed{" +
                    "title=" + title +
                    ", author=" + author +
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

        @Override
        public String toString() {
            return "Author{" +
                    "name=" + name +
                    ", email=" + email +
                    '}';
        }
    }
}


