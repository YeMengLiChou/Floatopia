package cn.csd.lib_framework.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import cn.csd.lib_framework.interfaces.IViewBinding

/**
 *
 *
 * Created: 2023/07/15
 * @author Gleamrise
 */
abstract class BaseBindingFragment<VB : ViewBinding>: BaseFragment(), IViewBinding<VB> {
    protected lateinit var mContainer: ViewGroup

    /** binding实例 */
    protected val binding by lazy {
        initViewBinding()
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mContainer = container!!
        onDefCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    /**
     * 重写该方法，将会在 [onCreateView] 中调用
     * */
    protected open fun onDefCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {

    }



}