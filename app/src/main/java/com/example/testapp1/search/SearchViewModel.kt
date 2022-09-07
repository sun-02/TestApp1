package com.example.testapp1.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp1.BuildConfig
import com.example.testapp1.ServerResponse
import com.example.testapp1.SingleLiveEvent
import com.example.testapp1.login.step.getResponse
import com.example.testapp1.login.step.toJSONObjectList
import com.example.testapp1.login.step.toStringList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

private const val TAG = "SearchViewModel"

class SearchViewModel : ViewModel() {

    private var _searchResponseData: MutableLiveData<SearchResponse> = MutableLiveData()
    val searchResponseData: LiveData<SearchResponse> get() = _searchResponseData

    private var _errorMsg: SingleLiveEvent<ServerResponse?> = SingleLiveEvent()
    val errorMsg: LiveData<ServerResponse?> get() = _errorMsg

    fun queryForActions(query: String) {
        val url = URL("https://utcoin.one/loyality/search?search_string=$query")
        viewModelScope.launch(Dispatchers.IO) {
            val response = getResponse(url)
            if (BuildConfig.DEBUG) Log.d(TAG, "Got actions response $response")
            if (response.code in 200..299) {
                _searchResponseData.postValue(parseSearchResponse(response.body))
            } else {
                _errorMsg.postValue(response)
            }
        }
    }

    private fun parseSearchResponse(json: String): SearchResponse {
        val root = JSONObject(json)
        val campaigns = root.getJSONArray("campaigns")
            .toJSONObjectList()
            .map { campaign ->
                Campaign(
                    id = campaign.getInt("id"),
                    name = campaign.getString("name"),
                    cashback = campaign.getString("cashback"),
                    actions = campaign.getJSONArray("actions")
                        .toJSONObjectList().map { action ->
                            Action(
                                value = action.getString("value"),
                                text = action.getString("text")
                            )
                        },
                    imageUrl = campaign.getString("imageUrl"),
                    paymentTime = campaign.getString("paymentTime")
                )
            }
        val products = root.getJSONArray("products")
            .toJSONObjectList()
            .map { product ->
                Product(
                    id = product.getInt("id"),
                    name = product.getString("name"),
                    cashback = product.getString("cashback"),
                    actions = product.getJSONArray("actions")
                        .toJSONObjectList()
                        .map { action ->
                             Action(
                                 value = action.getString("value"),
                                 text = action.getString("text")
                             )
                        },
                    imageUrls = product.getJSONArray("imageUrls")
                        .toStringList(),
                    price = product.getString("price"),
                    campaignName = product.getString("campaignName"),
                    campaignImageUrl = product.getString("campaignImageUrl"),
                    paymentTime = product.getString("paymentTime"),
                )
            }

        return SearchResponse(
            successful = root.getBoolean("successful"),
            errorMessage = root.getString("errorMessage"),
            errorMessageCode = root.getString("errorMessageCode"),
            settings = null,
            campaigns = campaigns,
            products = products,
            more = root.getBoolean("more"),
            moreCampaigns = root.getBoolean("moreCampaigns"),
        )
    }

}
