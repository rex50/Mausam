package com.rex50.mausam.views.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.StringDef;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rex50.mausam.R;
import com.rex50.mausam.base_classes.BaseActivity;
import com.rex50.mausam.base_classes.BaseFragment;
import com.rex50.mausam.enums.MainTabs;
import com.rex50.mausam.model_classes.utils.MoreListData;
import com.rex50.mausam.storage.MausamSharedPrefs;
import com.rex50.mausam.utils.Constants;
import com.rex50.mausam.utils.CustomViewPager;
import com.rex50.mausam.utils.FlashyTabBar;
import com.rex50.mausam.utils.MaterialSnackBar;
import com.rex50.mausam.views.activities.collections.ActCollectionsList;
import com.rex50.mausam.views.activities.photos.ActPhotosList;
import com.rex50.mausam.views.activities.settings.ActSettings;
import com.rex50.mausam.views.fragments.FragSearchResult;
import com.rex50.mausam.views.fragments.discover.FragDiscover;
import com.rex50.mausam.views.fragments.favourites.FragGallery;
import com.rex50.mausam.views.fragments.home.FragHome;
import com.suddenh4x.ratingdialog.AppRating;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class ActMain extends BaseActivity implements
        FragDiscover.OnFragmentInteractionListener,
        FragGallery.OnFragmentInteractionListener,
        FragHome.OnFragmentInteractionListener,
        FragSearchResult.OnFragmentInteractionListener {

    private String TAG = "ActMain";
    private FragHome fragHome;
    private FragDiscover fragDiscover;
    private FragGallery fragFav;
    private FlashyTabBar tabFlashyAnimator;
    private CustomViewPager viewPager;
    private final List<BaseFragment> mFragmentList = new ArrayList<>();

    private int currentTabIndex = 0;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
            Constants.ListModes.LIST_MODE_POPULAR_PHOTOS,
            Constants.ListModes.LIST_MODE_USER_PHOTOS,
            Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS,
            Constants.ListModes.LIST_MODE_COLLECTIONS,
            Constants.ListModes.LIST_MODE_USER_COLLECTIONS})
    public @interface photosListMode {
    }

    @Override
    protected void loadAct(@Nullable Bundle savedInstanceState) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        analytics.logEvent("on_main_activity", new Bundle());

        new AppRating.Builder(this)
                .setMinimumLaunchTimes(5)
                .setMinimumDays(7)
                .setMinimumLaunchTimesToShowAgain(50)
                .setMinimumDaysToShowAgain(20)
                .useGoogleInAppReview()
                .showIfMeetsConditions();

        init();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.act_main;
    }

    private void init() {
        prepareFragments();
    }

    private void prepareFragments() {
        viewPager = findViewById(R.id.homeViewPager);
        viewPager.setPagingEnabled(false);
        fragHome = new FragHome();
        fragDiscover = new FragDiscover();
        fragFav = new FragGallery();

        mFragmentList.add(fragHome);
        mFragmentList.add(fragDiscover);
        mFragmentList.add(fragFav);

        FragmentStatePagerAdapter viewPagerAdapter = getFragmentAdapter(mFragmentList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(mFragmentList.size());

        setupTabLayout(viewPager);
    }

    private FragmentStatePagerAdapter getFragmentAdapter(List<BaseFragment> mFragmentList) {
         return new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NotNull
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
    }

    private void setupTabLayout(CustomViewPager viewPager) {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabFlashyAnimator = new FlashyTabBar(tabLayout);
        for (MainTabs value : MainTabs.values()) {
            tabFlashyAnimator.addTabItem(value.getTitle(), value.getIcon());
        }
        tabFlashyAnimator.highLightTab(0);
        viewPager.addOnPageChangeListener(tabFlashyAnimator);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabIndex = tab.getPosition();
                if(currentTabIndex == 1 && fragDiscover != null){
                    fragDiscover.setFocusToSearchBox(false);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(fragDiscover != null)
                    fragDiscover.setFocusToSearchBox(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab != null && currentTabIndex < mFragmentList.size()) {
                    BaseFragment frag = mFragmentList.get(tab.getPosition());
                    if(frag != null) {
                        frag.onScrollToTop();
                    }
                }
            }
        });
    }

    @Override
    public void navigateToDiscover() {
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(tabFlashyAnimator != null)
            tabFlashyAnimator.onStart(findViewById(R.id.tabLayout));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(tabFlashyAnimator != null)
            tabFlashyAnimator.onStop();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() != 0)
            viewPager.setCurrentItem(0, true);
        else
            super.onBackPressed();
    }

    @Override
    public void nextBtnClicked() {

    }

    @Override
    public MausamSharedPrefs getSharedPrefs() {
        return mausamSharedPrefs;
    }

    @Override
    public void startMorePhotosActivity(@NotNull MoreListData data) {
        startActivity(new Intent(this, ActPhotosList.class)
                .putExtra(Constants.IntentConstants.LIST_DATA, data)
        );
    }

    @Override
    public void startMoreFeaturedCollections(@NotNull MoreListData data) {
        startActivity(new Intent(this, ActCollectionsList.class)
                .putExtra(Constants.IntentConstants.SEARCH_FEAT_COLLECTION, true)
                .putExtra(Constants.IntentConstants.LIST_DATA, data)
        );
    }

    @Nullable
    @Override
    public MaterialSnackBar snackBar() {
        return materialSnackBar;
    }

    @Override
    public void startSettings() {
        startActivity(new Intent(this, ActSettings.class));
    }

    //TODO: show in-app-review dialog after few days of usage
    /*val manager = ReviewManagerFactory.create(this)

    //Request a ReviewInfo object
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // We got the ReviewInfo object
            val reviewInfo = task.result

            //Launch the in-app review flow
            val flow = manager.launchReviewFlow(this, reviewInfo)
            flow.addOnCompleteListener { _ ->
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
            }

        } else {
            // There was some problem, log or handle the error code.
            openUrl(getString(R.string.link_play_store))
        }
    }*/

}
