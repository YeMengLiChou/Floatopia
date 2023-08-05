package cn.csd.mod_main

import android.os.Bundle
import android.view.ViewGroup
import cn.csd.lib_common.constant.ROUTE_MAIN_ACTIVITY_MAIN
import cn.csd.lib_framework.base.activity.BaseBindingActivity
import cn.csd.lib_framework.ext.click
import cn.csd.mod_main.databinding.MainActivityMainBinding
import com.therouter.TheRouter
import com.therouter.router.Route

@Route(path = ROUTE_MAIN_ACTIVITY_MAIN)
class MainActivity : BaseBindingActivity<MainActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
        binding.root.click {

        }
    }

    override fun initViewBinding(container: ViewGroup?): MainActivityMainBinding {
        return MainActivityMainBinding.inflate(layoutInflater)
    }
}