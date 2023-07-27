package cn.csd.lib_framework.base.viewholder

import androidx.viewbinding.ViewBinding

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
open class BaseBindViewHolder<VB : ViewBinding>(
    val binding: VB
) : BaseViewHolder(binding.root)