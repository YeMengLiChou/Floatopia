package cn.csd.lib_framework.interfaces

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * 初始化 [ViewBinding] 的接口， 需实现
 *
 * Created: 2023/07/16
 * @author Gleamrise
 */
interface IViewBinding<VB : ViewBinding> {

    /**
     * 初始化 Activity/Fragment 的 ViewBinding
     *
     * Activity:
     *
     *
     *
     * @param container Fragment 初始化所需要的
     *
     *
     * */
    fun initViewBinding(container: ViewGroup? = null): VB

}