package com.pratthamarora.proman.model

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var title: String = "",
    val createdBy: String = "",
    var cards: ArrayList<Card> = ArrayList()
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.createTypedArrayList(Card.CREATOR)!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(createdBy)
        writeTypedList(cards)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Task> = object : Parcelable.Creator<Task> {
            override fun createFromParcel(source: Parcel): Task = Task(source)
            override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)
        }
    }
}