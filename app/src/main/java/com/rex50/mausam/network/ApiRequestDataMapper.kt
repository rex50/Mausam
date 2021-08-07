package com.rex50.mausam.network

import com.rex50.mausam.utils.Constants

object ApiRequestDataMapper {

    fun mapPhotosAndUsersRequest(
        orderBy: String,
        page: Int,
        perPage: Int,
        orientation: String = UnsplashHelper.ORIENTATION_UNSPECIFIED
    ): HashMap<String, String> {
        val extras = HashMap<String, String>()
        extras["order_by"] = orderBy
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        if (!orientation.equals(UnsplashHelper.ORIENTATION_UNSPECIFIED, ignoreCase = true))
            extras["orientation"] = orientation
        return extras
    }

    fun mapSearchedPhotosRequest(
        searchTerm: String,
        page: Int,
        perPage: Int,
        orientation: String = UnsplashHelper.ORIENTATION_UNSPECIFIED
    ): HashMap<String, String> {
        val extras = HashMap<String, String>()
        extras["query"] = searchTerm
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        if (!orientation.equals(UnsplashHelper.ORIENTATION_UNSPECIFIED, ignoreCase = true))
            extras["orientation"] = orientation

        return extras
    }

    fun mapRandomPhotosRequest(
        searchTerm: String = "",
        topicIdsCommaSeparated: String = "",
        count: Int = 1,
    ): HashMap<String, String> {
        val extras = hashMapOf<String, String>()
        when {
            topicIdsCommaSeparated.isNotEmpty() -> extras["topics"] = topicIdsCommaSeparated
            searchTerm.isNotEmpty() -> extras["query"] = searchTerm
        }
        extras["count"] = count.toString()
        return extras
    }

    fun mapCollectionPhotosRequest(
        collectionId: String,
        page: Int,
        perPage: Int
    ): HashMap<String, String> {
        val extras = HashMap<String, String>()
        extras[Constants.ApiConstants.COLLECTION_ID] = collectionId
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()

        return extras
    }

    fun mapCollectionsAndTagsRequest(
        page: Int,
        perPage: Int
    ): HashMap<String, String> {
        val extras = HashMap<String, String>()
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()

        return extras
    }

    fun mapUserCollectionsRequest(
        unsplashUserName: String,
        page: Int,
        perPage: Int
    ): HashMap<String, String> {
        val extras = HashMap<String, String>()
        extras[Constants.ApiConstants.UNSPLASH_USERNAME] = unsplashUserName
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()

        return extras
    }

    fun mapUserPhotosRequest(
        unsplashUserName: String,
        page: Int,
        perPage: Int
    ): HashMap<String, String> {
        val extras = HashMap<String, String>()
        extras[Constants.ApiConstants.UNSPLASH_USERNAME] = unsplashUserName
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()

        return extras
    }

}