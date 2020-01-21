# googlesheet2json

## Background

Download data from Google spreadsheet and parse it as json (using Retrofit2)

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
    
    implementation 'com.github.elfifo4:googlesheet2json:1.3.5'
    annotationProcessor 'com.github.elfifo4:googlesheet2json:1.3.5'
    
/*
    The last line is required because of this:
    Annotation processors must be explicitly declared now. The following dependencies on the compile classpath are found to contain annotation processor.  Please add them to the annotationProcessor configuration.
    - googlesheet2json-1.3.5.jar (com.github.elfifo4:googlesheet2json:1.3.5)
    Alternatively, set android.defaultConfig.javaCompileOptions.annotationProcessorOptions.includeCompileClasspath = true to continue with previous behavior.  Note that this option is deprecated and will be removed in the future.
    See https://developer.android.com/r/tools/annotation-processor-error-message.html for more details.
*/


/*
    If you encounter the following error after build:
    Error:
      java.lang.RuntimeException: Duplicate class org.jetbrains.annotations.NotNull found in 
      modules annotations-16.0.1.jar (org.jetbrains:annotations:16.0.1) 
      and kotlin-runtime-0.11.91.1.jar (org.jetbrains.kotlin:kotlin-runtime:0.11.91.1)

    You should append this exclude statement after the dependency:
    
    implementation ("com.github.elfifo4:googlesheet2json:1.3.5") {
        exclude group: 'org.jetbrains'
    }
*/

}
```

---

## Usage
**important line (when app is on release buildType)**

proguard-rules.pro:
```pro
-keep class com.eladfinish.googlesheet2json.model.*$* { *; }
```

AndroidManifest.xml:
```xml
<!-- don't forget to add this permission in AndroidManifest.xml -->
    <uses-permission android:name="android.permission.INTERNET" />
```

The usage of the library is very simple. 
```java

// IMPORTANT: Enclosing class of @SheetDetails annotation must reside in some package.
// Otherwise compilation process will end up with a "unnamed package" which causes error in class generating.

package com.eladfinish.googlesheet2json;

//import ...

public class MainActivity extends AppCompatActivity {
    private static final String MY_TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    // this annotation makes the magic:
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

        //String url = call.request().url().toString();
        //Log.d(MY_TAG, "url of json format: " + url);
        
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
                    Flag_SheetEntry item = (Flag_SheetEntry) row;
                    String country = item.getCountry();
                    if (!country.isEmpty()) {
                        myData.add(country);
                        imageUrl.add(item.getPng());
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
    }
}
```

### Example

* public HTML format:  
https://docs.google.com/spreadsheets/d/e/2PACX-1vSRgrclz00_Pbc7MTJ3n6vcHr6c9uJVpfWueWJccV7gzR-H3MiYcUc5xs-RdVI6paDo1YCs289NQQio/pubhtml

* original link (available to sheet authors):  
https://docs.google.com/spreadsheets/d/1RaXAwjx4Q8OzXVt3nXfuf1ZppBV_lFWXfKGG2TTzglU

* public JSON format:  
https://spreadsheets.google.com/feeds/list/1RaXAwjx4Q8OzXVt3nXfuf1ZppBV_lFWXfKGG2TTzglU/1/public/full?alt=json


* export to JSON by this url:  
https://spreadsheets.google.com/feeds/list/{spreadsheetID}/{worksheetNumber}/public/full?alt=json  
(replace {spreadsheetID} and {worksheetNumber} with your own)

* for more information look at:  
https://medium.com/@scottcents/how-to-convert-google-sheets-to-json-in-just-3-steps-228fe2c24e6


Original Table:
![google sheet table](https://raw.githubusercontent.com/elfifo4/googlesheet2json/master/screenshots/google_sheet_table.png)

Library **googlesheet2json** turns this table to pretty json format:
```json
[
  {
    "code": "",
    "country": "",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/"
  },
  {
    "code": "AD",
    "country": "Andorra",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ad.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ad.svg"
  },
  {
    "code": "AE",
    "country": "United Arab Emirates",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ae.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ae.svg"
  },
  {
    "code": "AF",
    "country": "Afghanistan",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/af.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/af.svg"
  },
  {
    "code": "AG",
    "country": "Antigua and Barbuda",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ag.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ag.svg"
  },
  {
    "code": "AI",
    "country": "Anguilla",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ai.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ai.svg"
  },
  {
    "code": "AL",
    "country": "Albania",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/al.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/al.svg"
  },
  {
    "code": "AM",
    "country": "Armenia",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/am.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/am.svg"
  },
  {
    "code": "AN",
    "country": "Netherlands Antilles",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/an.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/an.svg"
  },
  {
    "code": "AO",
    "country": "Angola",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ao.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ao.svg"
  },
  {
    "code": "AQ",
    "country": "Antarctica",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/aq.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/aq.svg"
  },
  {
    "code": "AR",
    "country": "Argentina",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ar.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ar.svg"
  },
  {
    "code": "AS",
    "country": "American Samoa",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/as.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/as.svg"
  },
  {
    "code": "AT",
    "country": "Austria",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/at.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/at.svg"
  },
  {
    "code": "AU",
    "country": "Australia",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/au.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/au.svg"
  },
  {
    "code": "AW",
    "country": "Aruba",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/aw.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/aw.svg"
  },
  {
    "code": "AX",
    "country": "Åland Islands",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ax.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ax.svg"
  },
  {
    "code": "AZ",
    "country": "Azerbaijan",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/az.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/az.svg"
  },
  {
    "code": "BA",
    "country": "Bosnia and Herzegovina",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/ba.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/ba.svg"
  },
  {
    "code": "BB",
    "country": "Barbados",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/bb.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/bb.svg"
  },
  {
    "code": "BD",
    "country": "Bangladesh",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/bd.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/bd.svg"
  },
  {
    "code": "BE",
    "country": "Belgium",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/be.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/be.svg"
  },
  {
    "code": "BF",
    "country": "Burkina Faso",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/bf.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/bf.svg"
  },
  {
    "code": "BG",
    "country": "Bulgaria",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/bg.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/bg.svg"
  },
  {
    "code": "BH",
    "country": "Bahrain",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/bh.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/bh.svg"
  },
  {
    "code": "BI",
    "country": "Burundi",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/bi.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/bi.svg"
  },
  
  
  {
    "...": "..."
  },
  
  
  {
    "code": "ZW",
    "country": "Zimbabwe",
    "png": "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png1000px/zw.png",
    "svg": "https://github.com/hjnilsson/country-flags/blob/master/svg/zw.svg"
  }
]

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
