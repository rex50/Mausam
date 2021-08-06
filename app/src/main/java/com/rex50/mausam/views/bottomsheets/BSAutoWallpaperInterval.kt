package com.rex50.mausam.views.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.enums.AutoWallpaperInterval
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.getString
import com.rex50.mausam.utils.showToast
import com.rex50.mausam.utils.toColorStateList
import com.rex50.mausam.utils.toDp
import kotlinx.android.synthetic.main.bs_auto_wallpaper_interval.*
import java.lang.RuntimeException
import java.util.*

class BSAutoWallpaperInterval : MaterialBottomSheet(){

    private val sharedPrefs: MausamSharedPrefs? by lazy {
        MausamApplication.getInstance()?.getSharedPrefs()
    }

    /**
     * This is invoked when done button is clicked or when dismissed
     *
     * @return TRUE: if value is changed, FALSE: When no change has been made or selected same interval
     */
    var onSetSuccess: ((Boolean) -> Unit)? = null

    private var lastInterval: AutoWallpaperInterval? = null

    var selected: AutoWallpaperInterval? = null
        private set

    override fun layoutId(): Int = R.layout.bs_auto_wallpaper_interval

    fun show(fragmentManager: FragmentManager): BSAutoWallpaperInterval {
        showNow(fragmentManager, TAG)
        return this
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {

        getLastSelectedValues()

        inflateRadioButtons()

        setRadioGroupClickListener()

        initDoneClick()

    }

    private fun getLastSelectedValues() {
        //Last selected interval
        lastInterval = sharedPrefs?.autoWallpaperInterval

        //Store it in selected to show the interval as selected
        selected = lastInterval
    }

    fun inflateRadioButtons() {

        var selectedId: Int = -1

        //Create layout param for radio buttons
        val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT).also {
            it.topMargin = 48.toDp
            it.bottomMargin = 48.toDp
        }

        AutoWallpaperInterval.values().forEach { value ->
            rgInterval?.addView(
                //Create a new radio button
                RadioButton(requireContext()).apply {
                    //Set text
                    "${value.interval} ${value.unit.getString(value.interval).toLowerCase(Locale.ENGLISH)}".let { text = it }

                    //Set id
                    id = value.ordinal

                    //Store id for checking it after
                    // all the radio buttons are inflated
                    if(selected == value)
                        selectedId = id

                    //Store value as tag to use it
                    // later (inside click listener event)
                    tag = value

                    //Theming
                    buttonTintList = ContextCompat.getColor(requireContext(), R.color.colorAccent).toColorStateList
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black_to_white))
                },
                layoutParams
            )
        }

        //Set checked state of radio button if selectedId != -1
        if(selectedId != -1)
            rgInterval?.check(selectedId)
    }

    private fun initDoneClick() {
        btnDone?.setOnClickListener { _ ->
            dismiss()
        }
    }

    private fun setRadioGroupClickListener() {

        rgInterval?.setOnCheckedChangeListener { radioGroup, checkedId ->
            //Find the radio button from radio group
            val rb = radioGroup.findViewById<RadioButton>(checkedId)

            //Get value from tag
            val interval = rb?.tag as AutoWallpaperInterval?

            //Store the value in sharedPrefs if it is not null
            interval?.let {
                selected = it
                sharedPrefs?.autoWallpaperInterval = it
            } ?: let {
                val msg = "We are facing a problem while changing interval"
                showToast(msg)
                FirebaseCrashlytics.getInstance().recordException(RuntimeException("$msg: Entry not found"))
            }
        }

    }

    companion object {
        const val TAG = "BSAutoWallpaperInterval"
    }

    override fun onDismiss(dialog: DialogInterface) {
        onSetSuccess?.invoke(lastInterval != selected)
        super.onDismiss(dialog)
    }

}