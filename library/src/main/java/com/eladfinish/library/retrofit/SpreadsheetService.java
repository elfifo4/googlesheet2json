package com.eladfinish.library.retrofit;

import com.eladfinish.library.model.BaseSheetEntry;
import com.eladfinish.library.model.Sheet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpreadsheetService {
//    String PREFIX = "gsx$";

    @GET("/feeds/list/{id}/{num}/public/full")
    Call<Sheet> getDataFromSpreadsheet(
            @Path("id") String sheetId,
            @Path("num") int sheetNumber,
            @Query("alt") String format);


    //getSpreadsheetId
    static String getId(String url) {
        if (url.matches("[a-zA-Z0-9_-]+")) {
            return url;
        }
        String str = "/d/";
        int beginIndex = url.indexOf(str) + str.length();
        int endIndex = url.lastIndexOf("/");
        if (Math.abs(endIndex - beginIndex) <= 1) {
            endIndex = url.length();
        }
        return url.substring(beginIndex, endIndex);
    }

//    static void setSheetEntry(SheetEntryInterface sheetEntryInterface) {
////        System.out.println("sheetEntryInterface: " + sheetEntryInterface);
////        System.out.println("sheetEntryInterface is null: " + sheetEntryInterface == null);
//        System.out.println("sheetEntryInterface");
//        System.out.println(sheetEntryInterface.getSheetEntryDetails());
//    }


    static void setSheetEntry(BaseSheetEntry sheetEntry) {
//        System.out.println("sheetEntryInterface: " + sheetEntryInterface);
//        System.out.println("sheetEntryInterface is null: " + sheetEntryInterface == null);
        System.out.println("sheetEntry");
        System.out.println(sheetEntry);
    }


//    @SheetDetails
//    public static void setHeaders(@Name String name,
//                                  @Labels String[] labels,
//                                  @Fields String[] fields) {
//        System.out.println("name: " + name);
//        System.out.println("labels: " + Arrays.toString(labels));
//        System.out.println("fields: " + Arrays.toString(fields));
//    }


}