package com.eladfinish.googlesheet2json

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Developed by
 * @author Elad Finish
 */

fun testKotlinVersion(context: @NotNull Context, progressBar: @NotNull ProgressBar, recyclerView: RecyclerView): Disposable {
    val spreadsheetUrl = "https://docs.google.com/spreadsheets/d/1RaXAwjx4Q8OzXVt3nXfuf1ZppBV_lFWXfKGG2TTzglU"

    val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

    val service = Retrofit.Builder()
            .baseUrl("https://spreadsheets.google.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(SpreadsheetService::class.java)

    service.getDataFromSpreadsheet(spreadsheetUrl.getId())
            .subscribeOn(Schedulers.io())
            .map { (_, feed) -> feed.getItems(Flag::class.java) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBar.visibility = View.VISIBLE }
            .doFinally { progressBar.visibility = View.GONE }
            .subscribe({ items ->
                items.filter { it.country.isNotEmpty() }.let { list ->
                    recyclerView.adapter = MyAdapter(context, list.map { it.country }, list.map { it.png })
                }
            }) {
                Log.e("Spreadsheet", "failure", it)
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }.also {
                return it
            }
}