package com.eladfinish.googlesheet2json.retrofit;

import com.eladfinish.googlesheet2json.model.BaseSheetEntry;
import com.eladfinish.googlesheet2json.model.Sheet;
import com.eladfinish.googlesheet2json.model.SheetData;
import com.eladfinish.googlesheet2json.model.SheetEntryInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitHelper<T extends BaseSheetEntry> {

    private TypeToken<ArrayList<T>> typeToken;

    public interface DoAfterDownloadListener {
        void onSuccess(SheetData sheetData);

        void onFail(String message);
    }

    public RetrofitHelper(TypeToken<ArrayList<T>> typeToken, SheetEntryInterface sheetEntry) {
        this.typeToken = typeToken;
        if (sheetEntry == null) {
            // TODO: 09/08/2019 create custom Exception: MissingSheetEntryException
            throw new NullPointerException("You must pass a SheetEntry object!");
        }
    }

    public void download(Call<Sheet> call, DoAfterDownloadListener listener) {
        String url = call.request().url().toString();
        System.out.println("url of json format: " + url);

        call.enqueue(new Callback<Sheet>() {
            @Override
            public void onResponse(@NotNull Call<Sheet> call, @NotNull Response<Sheet> response) {
                Sheet body = response.body();
                if (response.isSuccessful() && body != null) {
                    Sheet.Feed feed = body.getFeed();
                    String rows = feed.getRows().toString();
                    ArrayList<BaseSheetEntry> items;

                    try {

//                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                        JsonParser jsonParser = new JsonParser();
//                        JsonElement jsonElement = jsonParser.parse(rows);
//                        String prettyJsonString = gson.toJson(jsonElement);
//                        System.out.println("prettyJsonString:\n" + prettyJsonString);

//                        TypeToken<ArrayList<T>> typeToken = new TypeToken<ArrayList<T>>() {};
                        items = new Gson().fromJson(rows, typeToken.getType());

                        SheetData sheetData = new SheetData.Builder(items)
                                .setTitle(feed.getTitle().getText())
                                .setUpdated(feed.getUpdated().getText())
                                .setAuthors(feed.getAuthors())
                                .setVersion(body.getVersion())
                                .build();

                        listener.onSuccess(sheetData);

                    } catch (Exception e) { //JsonSyntaxException
                        e.printStackTrace();
                        listener.onFail(e.getMessage());
                    }
                } else {
                    listener.onFail(response.toString());
                }
            }

            @Override
            public void onFailure(@NotNull Call<Sheet> call, @NotNull Throwable t) {
                listener.onFail(t.getMessage());
            }
        });

    }
}