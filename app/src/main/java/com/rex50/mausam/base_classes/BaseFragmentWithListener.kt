package com.rex50.mausam.base_classes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.BuildConfig
import com.rex50.mausam.R
import java.lang.Exception

abstract class BaseFragmentWithListener<Binding, Listener> : BaseFragment(){

    var binding: Binding? = null
        private set

    var listener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bindView(inflater, container)
        return (binding as ViewBinding).root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        val exception = RuntimeException(context.toString()
                + " must implement OnFragmentInteractionListener")

        listener = try {
            context as Listener?
        } catch (e: Exception) {
            when {
                BuildConfig.DEBUG -> {
                    throw exception
                }

                else -> {
                    FirebaseCrashlytics.getInstance().recordException(exception)
                    null
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        binding = null
    }

    /**
     * To bind the view
     *
     * Example: return <LayoutNameBinding>.inflate(inflater,container,false)
     *
     * @return the binding
     */
    abstract fun bindView(inflater: LayoutInflater, container: ViewGroup?): Binding

    override fun getResourceLayout(): Int {
        //This will be not used as we are using View binding
        return R.layout.empty_layout
    }
}