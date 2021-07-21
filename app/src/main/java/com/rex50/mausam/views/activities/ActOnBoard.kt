package com.rex50.mausam.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.MaterialSnackBar
import com.rex50.mausam.views.fragments.FragStoragePerm
import kotlinx.android.synthetic.main.act_onboard.*
import kotlinx.android.synthetic.main.header_custom_general.*
import java.util.*

class ActOnBoard : BaseActivity(), FragStoragePerm.OnFragmentInteractionListener {

    override val layoutResource: Int
        get() = R.layout.act_onboard

    private var fragStoragePerm: FragStoragePerm? = null

    override fun loadAct(savedInstanceState: Bundle?) {
        tvPageTitle?.text = getString(R.string.permission_title)
        tvPageDesc?.text = getString(R.string.permission_desc)
        prepareFragments()
    }

    private fun prepareFragments(){
        vpOnBoard?.setPagingEnabled(false)
        val mFragmentList: MutableList<Fragment?> = ArrayList()
        fragStoragePerm = FragStoragePerm.newInstance()

        mFragmentList.add(fragStoragePerm)

        vpOnBoard?.adapter = getFragmentAdapter(mFragmentList)
        vpOnBoard?.offscreenPageLimit = mFragmentList.size
    }

    private fun getFragmentAdapter(mFragmentList: List<Fragment?>): FragmentStatePagerAdapter? {
        return object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return mFragmentList[position] ?: Fragment()
            }

            override fun getCount(): Int {
                return mFragmentList.size
            }
        }
    }

    override fun internetStatus(internetType: Int) {
        //TODO("Not yet implemented")
    }

    override fun materialSnackBar(): MaterialSnackBar? = materialSnackBar

    override fun storagePermGranted() {
        materialSnackBar?.show(R.string.storage_perm_granted_msg, MaterialSnackBar.LENGTH_SHORT)
        mausamSharedPrefs?.isFirstTime = false
        Handler().postDelayed({
            startActivity(Intent(this, ActMain::class.java))
            finish()
        }, 300)
    }

    override fun permStoragePermanentlyDenied() {
        mausamSharedPrefs?.isStoragePermanentlyDenied = true
        materialSnackBar?.showActionSnackBar(
                getString(R.string.storage_perm_denied_msg),
                getString(R.string.ok_caps),
                MaterialSnackBar.LENGTH_INDEFINITE
                , object : MaterialSnackBar.SnackBarListener{
            override fun onActionPressed() {
                materialSnackBar?.dismiss()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //for getting callback in fragments
        fragStoragePerm?.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

}