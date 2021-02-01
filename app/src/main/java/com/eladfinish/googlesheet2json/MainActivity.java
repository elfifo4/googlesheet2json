package com.eladfinish.googlesheet2json;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eladfinish.googlesheet2json.annotations.SheetDetails;
import com.eladfinish.googlesheet2json.model.BaseSheetEntry;
import com.eladfinish.googlesheet2json.model.Sheet;
import com.eladfinish.googlesheet2json.model.SheetData;
import com.eladfinish.googlesheet2json.retrofit.RetrofitClientInstance;
import com.eladfinish.googlesheet2json.retrofit.RetrofitHelper;
import com.eladfinish.googlesheet2json.retrofit.SpreadsheetService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;


public class MainActivity extends AppCompatActivity {
    private static final String MY_TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<?> mAdapter;

    @SheetDetails(
            //entryName will be prefixed to the name of the generated class (<entryName>_SheetEntry)
            entryName = "flag",

            //fields are the names of member variables in Flag_SheetEntry
            fields = {"code", "country", "svg", "png"},

            //labels are the names of columns in Google Sheet
            //(optional, necessary in case the column names are different from the field names)
            labels = {"code", "country", "svg", "png"}
    )
    Flag_SheetEntry flagSheetEntry;
    String spreadsheetUrl = "https://docs.google.com/spreadsheets/d/1RaXAwjx4Q8OzXVt3nXfuf1ZppBV_lFWXfKGG2TTzglU";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        flagSheetEntry = new Flag_SheetEntry();

        SpreadsheetService service =
                RetrofitClientInstance.getRetrofitInstance(flagSheetEntry)
                        .create(SpreadsheetService.class);

        Call<Sheet> call = service.getDataFromSpreadsheet(
                SpreadsheetService.getId(spreadsheetUrl), 1, "json");

        String url = call.request().url().toString();
        Log.d(MY_TAG, "url of json format: " + url);

        RetrofitHelper<Flag_SheetEntry> helper = new RetrofitHelper<>(
                new TypeToken<ArrayList<Flag_SheetEntry>>() {
                },
                flagSheetEntry
        );

        helper.download(call, new RetrofitHelper.DoAfterDownloadListener() {
            @Override
            public void onSuccess(SheetData sheetData) {
                Log.d(MY_TAG, "onSuccess sheetData: ");

                Log.d(MY_TAG, "Title: " + sheetData.getTitle());
                Log.d(MY_TAG, "Updated: " + sheetData.getUpdated());
                Log.d(MY_TAG, "Updated (Formatted): " + sheetData.getUpdatedFormatted());
                Log.d(MY_TAG, "Version: " + sheetData.getVersion());
                Log.d(MY_TAG, "Authors: " + sheetData.getAuthors().toString());

                ArrayList<BaseSheetEntry> rows = sheetData.getRows();

                ArrayList<String> myData = new ArrayList<>();
                ArrayList<String> imageUrl = new ArrayList<>();
                for (BaseSheetEntry row : rows) {
                    Flag_SheetEntry item = (Flag_SheetEntry) row;
                    String country = item.getCountry();
                    if (!country.isEmpty()) {
                        myData.add(country);
                        imageUrl.add(item.getPng());
                    }
                }

                mAdapter = new MyAdapter(MainActivity.this, myData, imageUrl);
                recyclerView.setAdapter(mAdapter);

                System.out.println("sheetData.getRowsAsPrettyJson()");
                System.out.println(sheetData.getRowsAsPrettyJson());
                System.out.println("sheetData.getRowsAsRawJson()");
                System.out.println(sheetData.getRowsAsRawJson());
                System.out.println("sheetData.getSheetDataAsRawJson()");
                System.out.println(sheetData.getSheetDataAsRawJson());

            }

            @Override
            public void onFail(String message) {
                Log.e(MY_TAG, "onFail: " + message);
            }
        });

    }
}
