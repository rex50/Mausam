package com.rex50.mausam.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerOnScrollListener : RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 5
    // The current offset index of data you have loaded
    private var currentPage = 1
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    private var lastPreviousTotalItemCount = previousTotalItemCount
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    private var startingPageIndex = 1
    // Sets how many elements will be added each time.
    private var loadItemFreq = 20

    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private constructor()

    constructor(layoutManager: LinearLayoutManager?) {
        mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    //Call this method if last list request failed
    fun lastRequestFailed() {
        currentPage--
        previousTotalItemCount = lastPreviousTotalItemCount
    }

    //Call this method to set visible threshold
    fun setVisibleThreshold(threshold: Int) {
        visibleThreshold = threshold
    }

    //call this method to set numbers of items will loaded at once
    open fun setLoadItemsFrequency(loadItemFreq: Int) {
        this.loadItemFreq = loadItemFreq
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager!!.itemCount
        if (mLayoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (mLayoutManager is GridLayoutManager) {
            lastVisibleItemPosition = (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
        } else if (mLayoutManager is LinearLayoutManager) {
            lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            lastPreviousTotalItemCount = previousTotalItemCount
            previousTotalItemCount = totalItemCount
        }

        if(totalItemCount - lastPreviousTotalItemCount == loadItemFreq) {
            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) >= totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount, view);
                loading = true;
            }
        }
    }

    // Call this method whenever performing new searches
    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    fun setInitialPage(initialPage: Int){
        startingPageIndex = initialPage
        currentPage = initialPage
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}