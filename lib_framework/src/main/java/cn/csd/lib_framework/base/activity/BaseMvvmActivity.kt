package cn.csd.lib_framework.base.activity

import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.viewbinding.ViewBinding
import cn.csd.lib_framework.interfaces.IViewModel

/**
 *
 *
 * Created: 2023/07/15
 * @author Gleamrise
 */
abstract class BaseMvvmActivity<VM : ViewModel, VB : ViewBinding> : BaseBindingActivity<VB>(), IViewModel<VM> {

    // 实例化对应的 ViewModel
    protected val viewModel: VM by lazy {
        ViewModelProvider(this, getViewModelFactory())[getViewModelClass().java]
    }

    /** [viewModels] 的初始化方式  */
//    protected val viewModel: VM by ViewModelLazy(this.getViewModelClass(), { viewModelStore }, { getViewModelFactory() } ,{ getCreationExtras() })

    /**
     * 默认使用 [androidx.lifecycle.SavedStateViewModelFactory] 作为默认 Factory
     */
    override fun getViewModelFactory(): ViewModelProvider.Factory = defaultViewModelProviderFactory

    /**
     * 传入 ViewModel 的一些额外信息，例如 Intent
     * */
    override fun getCreationExtras(): CreationExtras = defaultViewModelCreationExtras

}

