# googlesheet2json

Download data from Google spreadsheet and parse it as json (using Retrofit2)

## Background

## Installation
[![](https://jitpack.io/v/aitorvs/auto-parcel.svg)](https://jitpack.io/#aitorvs/auto-parcel)

### Repository

Add the following repo to your `app/build.gradle`

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

### Dependencies

Add the following to build.gradle:

```gradle

android {
...
// if you encounter this Error:
// Invoke-customs are only supported starting with Android O (--min-api 26),
// add these lines:
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    
    //... other dependencies here
    
    implementation 'com.github.elfifo4:googlesheet2json:1.3.0'
    
    //we need below libraries too:
    //implementation 'com.squareup.retrofit2:retrofit:2.6.0'
    //implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
    //implementation 'org.jetbrains:annotations:16.0.1'
}
```

## Usage

The use of the library is very simple. 
```java

public class MainActivity extends AppCompatActivity {

    String spreadsheetUrl = "https://docs.google.com/spreadsheets/d/1l2ZqkbAT-y8lbkHtlnINBz_kl2wYnqQ-arxhlI1JnuI/edit#gid=157166260";

    @SheetDetails(entryName = "european_capitals",
            labels = {"Question", "Answer"},
            fields = {"countryName", "cityName"})
    European_Capitals_SheetEntry europeanCapitalsSheetEntry;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpreadsheetService service =
                RetrofitClientInstance.getRetrofitInstance().create(SpreadsheetService.class);

        Call<Sheet> call = service.getDataFromSpreadsheet(
                SpreadsheetService.getId(spreadsheetUrl), 1, "json");


        String url = call.request().url().toString();
        Timber.d("url of json format %s", url);

        call.enqueue(new Callback<Sheet>() {
            @Override
            public void onResponse(@NonNull Call<Sheet> call, @NonNull Response<Sheet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rows = response.body().getFeed().getRows().toString();
                    ArrayList<BaseSheetEntry> items;
                    try {
                        items = new Gson().fromJson(rows,
                                new TypeToken<ArrayList<European_Capitals_SheetEntry>>() {
                                }.getType());
                    } catch (Exception e) { //JsonSyntaxException
                        Timber.e(e);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String rows = response.body().getFeed().getRows().toString();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(rows);
                    String prettyJsonString = gson.toJson(jsonElement);
                    ArrayList<Sheet.Author> authors = response.body().getFeed().getAuthor();
                    String title = response.body().getFeed().getTitle().getText();
                    
                    System.out.println(prettyJsonString);
                    System.out.println("rows: " + rows);
                    System.out.println("authors: " + authors);
                    System.out.println("title: " + title);
                    System.out.println("items: " + items);
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.eladfinish.googlesheet2json.model.Sheet> call, @NonNull Throwable t) {
                Timber.e(t, "Throwable");
            }
        });

    }
}


//new in version 1.3.0

public class MainActivity extends AppCompatActivity {
    private static final String MY_TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @SheetDetails(entryName = "flag",
            fields = {"code", "country", "svg", "png"})
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
                SpreadsheetService.getId(spreadsheetUrl),
                1, "json");


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
                Log.d(MY_TAG, "Version: " + sheetData.getVersion());
                Log.d(MY_TAG, "Authors: " + sheetData.getAuthors().toString());

                ArrayList<BaseSheetEntry> rows = sheetData.getRows();

                ArrayList<String> myData = new ArrayList<>();
                ArrayList<String> imageUrl = new ArrayList<>();
                for (BaseSheetEntry row : rows) {
                    Flag_SheetEntry flagItem = (Flag_SheetEntry) row;
                    String country = flagItem.getCountry();
                    if (!country.isEmpty()) {
                        myData.add(country);
                        imageUrl.add(flagItem.getPng());
                    }
                }

                mAdapter = new MyAdapter(MainActivity.this, myData, imageUrl);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFail(String message) {
                Log.e(MY_TAG, "onFail: " + message);
            }
        });

        String url = call.request().url().toString();
        Log.d(MY_TAG, "url of json format: " + url);

    }
}


```


## License

```
Copyright 2019 Elad Finish

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```