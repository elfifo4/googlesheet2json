package com.eladfinish.googlesheet2json

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.math.abs


/**
 * Developed by
 * @author Elad Finish
 */

const val FIELD_PREFIX = "gsx$"
const val TEXT_KEY = "\$t"

interface SpreadsheetService {

    @GET("https://spreadsheets.google.com/feeds/list/{id}/{num}/public/full")
    fun getDataFromSpreadsheet(
            @Path("id") sheetId: String,
            @Path("num") sheetNumber: Int = 1,
            @Query("alt") format: String = "json",
    ): Single<Sheet>

    //for calling from Java
    @GET("https://spreadsheets.google.com/feeds/list/{id}/1/public/full?alt=json")
    fun getDataFromSpreadsheet(
            @Path("id") sheetId: String,
    ): Single<Sheet>

    //for calling from Java
    @GET("https://spreadsheets.google.com/feeds/list/{id}/{num}/public/full?alt=json")
    fun getDataFromSpreadsheet(
            @Path("id") sheetId: String,
            @Path("num") sheetNumber: Int = 1,
    ): Single<Sheet>

}

fun String.getId(): String {
    if (this.matches("[a-zA-Z0-9_-]+".toRegex())) {
        return this
    }
    val str = "/d/"
    val beginIndex = this.indexOf(str) + str.length
    var endIndex = this.lastIndexOf("/")
    if (abs(endIndex - beginIndex) <= 1) {
        endIndex = this.length
    }
    return this.substring(beginIndex, endIndex)
}

data class Sheet(
        @SerializedName("version") val version: String,
        @SerializedName("feed") val feed: Feed,
)

data class Feed(
        @SerializedName("title") val title: Text,
        @SerializedName("updated") val updated: Text,
        @SerializedName("author") val authors: List<Author>,
        @SerializedName("entry") val rows: List<JsonElement>,
) {
    fun <T> getItems(clazz: Class<T>): List<T> =
            rows.filter { it.isJsonObject }.map { it.asJsonObject }.map {
                mutableMapOf<String, JsonElement>().apply {
                    it.keySet().filter { key -> (key.startsWith(FIELD_PREFIX)) }
                            .forEach { key -> this[key.drop(FIELD_PREFIX.length)] = it.get(key).asJsonObject.get(TEXT_KEY) }
                }
            }.toString().let {
                Gson().fromJson(it, TypeToken.getParameterized(List::class.java, clazz).type)
            }
}

data class Text(
        @SerializedName("\$t") val text: String,
)

data class Author(
        @SerializedName("name") val name: Text,
        @SerializedName("email") val email: Text,
)