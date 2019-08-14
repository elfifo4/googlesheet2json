package com.eladfinish.googlesheet2json.retrofit;

import com.eladfinish.googlesheet2json.model.SheetEntryInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://spreadsheets.google.com";

    public static Retrofit getRetrofitInstance(/*@NotNull*/ SheetEntryInterface sheetEntry) {
        //make sure that sheetEntry is not null
        if (sheetEntry == null) {
            // TODO: 09/08/2019 create custom Exception: MissingSheetEntryException
            throw new NullPointerException("You must pass a SheetEntry object.");
        }

        System.out.println("SheetEntryDetails");
        System.out.println(sheetEntry.getClass().getSimpleName());
        System.out.println(sheetEntry.getSheetEntryDetails());

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}