package com.example.testapp1.search

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
    val id: Int,
    val name: String,
    val cashback: String,
    val actions: List<Action>,
    val imageUrl: String,
    val paymentTime: String
)

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
)

data class Action(
    val value: String,
    val text: String
)
