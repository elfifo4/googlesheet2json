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
    
    implementation 'com.github.elfifo4:googlesheet2json:1.3.1'
    annotationProcessor 'com.github.elfifo4:googlesheet2json:1.3.1'
    
/*
    Last line is required because of this:
    Annotation processors must be explicitly declared now. The following dependencies on the compile classpath are found to contain annotation processor.  Please add them to the annotationProcessor configuration.
    - googlesheet2json-1.3.1.jar (com.github.elfifo4:googlesheet2json:1.3.1)
    Alternatively, set android.defaultConfig.javaCompileOptions.annotationProcessorOptions.includeCompileClasspath = true to continue with previous behavior.  Note that this option is deprecated and will be removed in the future.
    See https://developer.android.com/r/tools/annotation-processor-error-message.html for more details.
*/

}
```

## Usage

```xml

<!-- don't forget to add this permission in AndroidManifest.xml -->
    <uses-permission android:name="android.permission.INTERNET" />
```

The use of the library is very simple. 
```java

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
                SpreadsheetService.getId(spreadsheetUrl), 1, "json");


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

        //String url = call.request().url().toString();
        //Log.d(MY_TAG, "url of json format: " + url);

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