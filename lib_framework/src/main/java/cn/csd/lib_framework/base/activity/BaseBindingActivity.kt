package cn.csd.lib_framework.base.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import cn.csd.lib_framework.interfaces.IViewBinding

/**
 * ViewBinding 实例的Activity
 *
 * Created: 2023/07/15
 * @author Gleamrise
 */
abstract class BaseBindingActivity<VB : ViewBinding>: BaseActivity(), IViewBinding<VB> {
    protected open val binding: VB by lazy {
        initViewBinding()
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}



