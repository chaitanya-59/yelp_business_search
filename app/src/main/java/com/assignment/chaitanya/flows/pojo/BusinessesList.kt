package com.assignment.chaitanya.flows.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusinessesList(@SerializedName("businesses") val businessesList: List<BusinessDetails>): Parcelable