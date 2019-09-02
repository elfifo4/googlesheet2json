package com.eladfinish.googlesheet2json.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtils {

    /**
     * Convert a JSON string to pretty print version
     *
     * @param jsonString raw json as string (JsonObject or JsonArray)
     * @return pretty format of the json
     */
    public static String toPrettyFormat(String jsonString) {
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



    /**
     * Convert an object to pretty print json version
     *
     * @param obj object to be converted to json
     * @return pretty format of the json representation of the object
     */
    public static String getPrettyJsonString(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonInString = gson.toJson(obj);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonInString);
        return gson.toJson(jsonElement);
    }
}
