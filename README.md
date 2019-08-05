# sheet2json

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
    
    implementation 'com.github.elfifo4:sheet2json:1.6.0'
}
```

## Usage

The use of the library is very simple. 
```java
@SheetColumnHeaders(entryName = "European Capitals", fields = {"Question", "Answer"})

public class MainActivity extends AppCompatActivity {

    String spreadsheetUrl = "https://docs.google.com/spreadsheets/d/1l2ZqkbAT-y8lbkHtlnINBz_kl2wYnqQ-arxhlI1JnuI/edit#gid=157166260";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        com.eladfinish.sheet2json.retrofit.SpreadsheetService service =
                com.eladfinish.sheet2json.retrofit.RetrofitClientInstance.getRetrofitInstance().create(com.eladfinish.sheet2json.retrofit.SpreadsheetService.class);

        Call<com.eladfinish.sheet2json.model.Sheet> call = service.getDataFromSpreadsheet(
                com.eladfinish.sheet2json.retrofit.SpreadsheetService.getId(spreadsheetUrl), 1, "json");


        String url = call.request().url().toString();
        Timber.d("url of json format %s", url);

        call.enqueue(new Callback<com.eladfinish.sheet2json.model.Sheet>() {
            @Override
            public void onResponse(@NonNull Call<com.eladfinish.sheet2json.model.Sheet> call, @NonNull Response<com.eladfinish.sheet2json.model.Sheet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rows = response.body().getFeed().getRows().toString();
                    ArrayList<SheetEntryEuropeanCapitals> items = new Gson().fromJson(rows,
                            new TypeToken<ArrayList<SheetEntryEuropeanCapitals>>() {
                            }.getType());
                    ArrayList<com.eladfinish.sheet2json.model.Sheet.Author> authors = response.body().getFeed().getAuthor();
                    String title = response.body().getFeed().getTitle().getText();

                    Timber.d("rows %s", rows);
                    Timber.d("authors %s", authors);
                    Timber.d("title %s", title);
                    Timber.d("items %s", items);

                }
            }

            @Override
            public void onFailure(@NonNull Call<com.eladfinish.sheet2json.model.Sheet> call, @NonNull Throwable t) {
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