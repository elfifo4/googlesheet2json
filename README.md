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

Add the following to gradle dependencies:

```gradle
dependencies {
    
    //... other dependencies here
    
    implementation 'com.github.elfifo4:googlesheet2json:1.2.1'
    
    //we need below libraries too:
    implementation 'com.squareup.retrofit2:retrofit:2.6.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
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