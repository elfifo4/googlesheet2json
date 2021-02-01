package com.eladfinish.googlesheet2json.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Sheet {

    @SerializedName("version")
    private final String version;
    @SerializedName("feed")
    private final Feed feed;

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
    public @NotNull String toString() {
        return "Sheet{" +
                "version='" + version + '\'' +
                ", feed=" + feed +
                '}';
    }

    public static class Feed {

        @SerializedName("title")
        private final Text title;

        @SerializedName("updated")
        private final Text updated;

        @SerializedName("author")
        private final ArrayList<Author> authors;

        @SerializedName("entry")
        private final ArrayList<JsonElement> rows;

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
        public @NotNull String toString() {
            return "Feed{" +
                    "title=" + title +
                    ", updated=" + updated +
                    ", authors=" + authors +
                    ", rows=" + rows +
                    '}';
        }
    }


    public static class Text {

        @SerializedName("$t")
        private final String text;

        public Text(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        public @NotNull String toString() {
            return text;
        }

    }


    public static class Author {

        @SerializedName("name")
        private final Text name;

        @SerializedName("email")
        private final Text email;

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
        public @NotNull String toString() {
            return "Author{" +
                    "name=" + name +
                    ", email=" + email +
                    '}';
        }
    }
}


