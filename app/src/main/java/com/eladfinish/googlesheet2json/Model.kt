package com.eladfinish.googlesheet2json

import com.google.gson.annotations.SerializedName

/**
 * Developed by
 * @author Elad Finish
 */

data class Flag(
        @SerializedName("code") val code: String,
        @SerializedName("country") val country: String,
        @SerializedName("svg") val svg: String,
        @SerializedName("png") val png: String,
)