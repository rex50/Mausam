package com.rex50.mausam.base_classes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.MaterialSnackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseActivityWithBinding<Binding: Any> : AppCompatActivity() {
    @JvmField
    protected var materialSnackBar: MaterialSnackBar? = null
    @JvmField
    protected var mausamSharedPrefs: MausamSharedPrefs? = null

    protected var binding: Binding? = null

    protected val actScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindView()
        val root = (binding as ViewBinding).root
        setContentView(root)

        mausamSharedPrefs = MausamSharedPrefs(this)
        materialSnackBar = MaterialSnackBar(this, root)
        loadAct(savedInstanceState)
    }

    protected abstract fun loadAct(savedInstanceState: Bundle?)

    /**
     * To bind the layout to activity.
     *
     * Example: LayoutNameBinding.inflate(getLayoutInflater());
     */
    protected abstract fun bindView(): Binding

    companion object {
        private const val TAG = "BaseActivity"
    }
}