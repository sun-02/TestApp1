package com.example.testapp1.search

import android.os.Parcel
import android.os.Parcelable

data class SearchResponse(
    val successful: Boolean,
    val errorMessage: String,
    val errorMessageCode: String,
    val settings: Any?,
    val campaigns: List<Campaign>,
    val products: List<Product>,
    val more: Boolean,
    val moreCampaigns: Boolean
)

data class Campaign(
    var id: Int,
    val name: String,
    val cashback: String,
    val actions: List<Action>,
    val imageUrl: String,
    val paymentTime: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Action.CREATOR)!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(cashback)
        parcel.writeTypedList(actions)
        parcel.writeString(imageUrl)
        parcel.writeString(paymentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Campaign> {
        override fun createFromParcel(parcel: Parcel): Campaign {
            return Campaign(parcel)
        }

        override fun newArray(size: Int): Array<Campaign?> {
            return arrayOfNulls(size)
        }
    }
}

data class Product(
    val id: Int,
    val name: String,
    val cashback: String,
    val actions: List<Action>,
    val imageUrls: List<String>,
    val price: String,
    val campaignName: String,
    val campaignImageUrl: String,
    val paymentTime: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Action.CREATOR)!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(cashback)
        parcel.writeTypedList(actions)
        parcel.writeStringList(imageUrls)
        parcel.writeString(price)
        parcel.writeString(campaignName)
        parcel.writeString(campaignImageUrl)
        parcel.writeString(paymentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}

data class Action(
    val value: String,
    val text: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Action> {
        override fun createFromParcel(parcel: Parcel): Action {
            return Action(parcel)
        }

        override fun newArray(size: Int): Array<Action?> {
            return arrayOfNulls(size)
        }
    }
}

