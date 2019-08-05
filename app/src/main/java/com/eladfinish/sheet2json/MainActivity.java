package com.eladfinish.sheet2json;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eladfinish.sheet2json.annotations.SheetDetails;
import com.eladfinish.sheet2json.annotations.SheetName;
import com.eladfinish.sheet2json.model.BaseSheetEntry;
import com.eladfinish.sheet2json.model.Sheet;
import com.eladfinish.sheet2json.retrofit.RetrofitClientInstance;
import com.eladfinish.sheet2json.retrofit.SpreadsheetService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

//@SheetDetails(entryName = "pair",
//        labels = {"רשימה", "מפתח", "ערך"},
//        fields = {"list", "key", "value"})

//@SheetDetails(
//        entryName = "MultiChoice",
//        labels = {"Question", "Answer A", "Answer B", "Answer C", "Answer D", "Correct"},
//        fields = {"question", "answerA", "answerB", "answerC", "answerD", "correct"})


//@SheetDetails(
//        entryName = "european_capitals123",
//        labels = {"Question ASD", "Answer"},
//        fields = {"countryName", "cityName"})


public class MainActivity extends AppCompatActivity {


    @SheetDetails(entryName = "european_capitals",
            labels = {"Question", "Answer"},
            fields = {"countryName", "cityName"})
    European_Capitals_SheetEntry europeanCapitalsSheetEntry;

    @SheetDetails(
            labels = {"abcd", "1234"},
            fields = {"a1", "a2"})
    int test2;

    @SheetDetails(entryName = "pair",
            labels = {"רשימה", "מפתח", "ערך"},
            fields = {"list", "key", "value"})
    Pair_SheetEntry pairSheetEntry;

////    Pair_SheetEntry pairSheetEntry;
//
//    @SheetDetails(entryName = "european_capitals123",
//            labels = {"Question ASD", "Answer"},
//            fields = {"countryName", "cityName"})
//    int x;
//
////    European_capitals123_SheetEntry entry;
//
//    @SheetDetails(entryName = "pair",
//            labels = {"Qu124", "zxcv"},
//            fields = {"yes", "yes3"})
//    int y;
//    @SheetDetails(entryName = "    a   b   ",
//            labels = {"Qu124", "zxcv"},
//            fields = {"yes", "yes3"})
//    int aa;
//
//

    String spreadsheetUrl = "https://docs.google.com/spreadsheets/d/1l2ZqkbAT-y8lbkHtlnINBz_kl2wYnqQ-arxhlI1JnuI/edit#gid=157166260";

//    String spreadsheetUrl = "https://docs.google.com/spreadsheets/d/1aIOcjMgBVOjCD2bnePsM-HSAxjEkbj1SDbo3rU_pgNU";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpreadsheetService service =
                RetrofitClientInstance.getRetrofitInstance().create(SpreadsheetService.class);

        Call<Sheet> call = service.getDataFromSpreadsheet(
                SpreadsheetService.getId(spreadsheetUrl), 1, "json");

        europeanCapitalsSheetEntry = new European_Capitals_SheetEntry();

        SpreadsheetService.setSheetEntry(europeanCapitalsSheetEntry);

//        com.eladfinish.sheet2json.retrofit.SpreadsheetService.setHeaders("Elad");

//        com.eladfinish.sheet2json.retrofit.SpreadsheetService.setHeadersNew("Elad");

//        setHeaders("MultiChoice",
//                new String[]{"Question", "Answer A", "Answer B", "Answer C", "Answer D", "Correct"},
//                new String[]{"question", "answerA", "answerB", "answerC", "answerD", "correct"});

//        setHeadersNew("Elad");

        String url = call.request().url().toString();
        Timber.d("url of json format %s", url);

        call.enqueue(new Callback<Sheet>() {
            @Override
            public void onResponse(@NonNull Call<Sheet> call, @NonNull Response<Sheet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rows = response.body().getFeed().getRows().toString();

                    Timber.d("rows %s", rows);

                    ArrayList<BaseSheetEntry> items;
                    try {
                        items = new Gson().fromJson(rows,
                                new TypeToken<ArrayList<European_Capitals_SheetEntry>>() {
                                }.getType());
                    } catch (Exception e) { //JsonSyntaxException
                        Timber.e(e);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;

                        //ArrayList<? extends SheetEntryInterface> items;
                        //java.lang.RuntimeException: Unable to invoke no-args constructor
                        // for ? extends com.eladfinish.sheet2json.model.SheetEntryInterface.
                        // Registering an InstanceCreator with Gson for this type
                        // may fix this problem.
                    }


                    ArrayList<Sheet.Author> authors = response.body().getFeed().getAuthor();
                    String title = response.body().getFeed().getTitle().getText();

                    Timber.d("authors %s", authors);
                    Timber.d("items %s#\n%s", items.size(), items);

                    showMessageBuilder(title, authors.toString() + "\n" + items.toString(),
                            false, 0, MainActivity.this).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<Sheet> call, @NonNull Throwable t) {
                Timber.e(t, "Throwable");
            }
        });

    }


    public static AlertDialog.Builder showMessageBuilder(final String title, CharSequence message,
                                                         boolean isHtml, int icon, final Context context) {
        final TextView showText = new TextView(context);


        showText.setTextColor(Color.BLACK);
        showText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            showText.setText(isHtml ? Html.fromHtml(message.toString(), Html.FROM_HTML_MODE_LEGACY) : message);
        } else {
            showText.setText(isHtml ? Html.fromHtml(message.toString()) : message);
        }

        showText.setTextIsSelectable(true);
        showText.setPadding(25, 10, 25, 10);
        showText.setMovementMethod(LinkMovementMethod.getInstance());

        if (isHtml) showText.setGravity(Gravity.CENTER);

        final ScrollView scrollView = new ScrollView(context);


        scrollView.addView(showText);
        ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(25, 20, 25, 5);
        showText.setLayoutParams(layoutParams);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(
//                new ContextThemeWrapper(context, R.style.AlertDialogCustom));


        builder.setView(scrollView)
                .setTitle(title)
                .setCancelable(true) //!isHtml
                .setPositiveButton(android.R.string.ok, null);

        if (icon != 0) builder.setIcon(icon);

        return builder;

    }


    //    @SheetDetails
    static void setHeadersNew(@SheetName String name123) {
        System.out.println("setHeaders: " + name123);
    }

}
