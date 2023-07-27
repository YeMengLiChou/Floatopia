package cn.csd.lib_framework.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cn.csd.lib_framework.base.viewholder.BaseBindViewHolder
import cn.csd.lib_framework.base.viewholder.BaseViewHolder

/**
 * 对 [RecyclerView] 使用的 [RecyclerView.Adapter] 进行封装
 *
 * 实现功能：
 *
 * Created: 2023/07/08
 * @author Gleamrise
 */
abstract class BaseRecyclerAdapter<VB : ViewBinding, T>(
    protected val context: Context,
    protected val data: MutableList<T>
): RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        /** header view type */
        private const val VIEW_TYPE_HEADER = 0x1111111

        /** footer view type */
        private const val VIEW_TYPE_FOOTER = 0x2222222
    }

    // =========================== member variable =============================================
    /** 头部的布局 */
    private lateinit var mHeaderLayout: LinearLayout

    /** 尾部的布局 */
    private lateinit var mFooterLayout: LinearLayout

    /** 是否有头部视图 */
    val hasHeaderView
        get() = this::mHeaderLayout.isInitialized && mHeaderLayout.childCount > 0

    /** 头部布局个数，用于求偏移量 */
    val headerLayoutCount: Int
        get() = if (hasHeaderView) 1 else 0

    /** 头部布局的子View个数 */
    val headerViewCount: Int
        get() = if (hasHeaderView) mHeaderLayout.childCount else 0

    /** header 的位置 */
    val headerViewPosition = 0

    val hasFooterView
        get() = this::mFooterLayout.isInitialized && mFooterLayout.childCount > 0

    /** 尾部布局的子 View 个数 */
    val footerViewCount: Int
        get() = if (hasFooterView) mFooterLayout.childCount else 0

    /** footer 的位置 */
    val footerViewPosition: Int
        get() = headerLayoutCount + data.size


    /** 点击监听 */
    var mOnItemClickListener: ((ViewHolder: BaseViewHolder, position: Int) -> Unit)? = null

    /** 长按监听 */
    var mOnItemLongClickListener: ((ViewHolder: BaseViewHolder, position: Int) -> Boolean) = {viewHolder, position -> false}

    /** 删除监听 */
    var mOnItemRemovedListener: ((removed: T,  position: Int) -> Unit)? = null

    /** 插入监听 */
    var mOnItemInsertedListener: ((insert: T,  position: Int) -> Unit)? = null


    // ============================== override method ==========================================

    /**
     * 子类无需重载，需要实现 [onCreateDefViewHolder] 即可
     * */
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder
            = when (viewType) {
                // header 的 ViewHolder
                VIEW_TYPE_HEADER -> {
                    val headerParent = mHeaderLayout.parent
                    if (headerParent is ViewGroup) {
                        headerParent.removeView(mHeaderLayout)
                    }
                    BaseViewHolder(mHeaderLayout)
                }
                // footer 的 ViewHolder
                VIEW_TYPE_FOOTER -> {
                    val footerParent = mFooterLayout.parent
                    if (footerParent is ViewGroup) {
                        footerParent.removeView(mFooterLayout)
                    }
                    BaseViewHolder(mFooterLayout)
                }
                else -> {
                    val layoutInflater = LayoutInflater.from(context)
                    onCreateDefViewHolder(layoutInflater, parent, viewType)
                        .apply { bindViewListener(this) }
                }
            }
        return viewHolder
    }



    /**
     * 预先重写该方法,设置 [holder] 中itemView的点击/长按事件；
     *
     * [bindData]将会在设置完后调用，用于绑定数据
     *
     * */
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_HEADER, VIEW_TYPE_FOOTER -> return
            else -> {
                // 子项实际位置
                val realPosition = getRealPosition(position)
                val item = getItem(realPosition)
                item?.let {
                    bindData(holder, it, realPosition)
                }
            }
        }
    }

    /**
     * header 和 footer 的 view type 判断
     *
     * 其他子项使用 [getDefItemViewType]
     * */
    final override fun getItemViewType(position: Int): Int {
        return when {
            hasHeaderView && position == headerViewPosition -> VIEW_TYPE_HEADER
            hasFooterView && position == footerViewPosition -> VIEW_TYPE_FOOTER
            else -> {
                val realPosition = getRealPosition(position)
                getDefItemViewType(realPosition)
            }
        }
    }

    /** 重写该方法，设置为 true， 解决加载闪烁问题
     * @see getItemId
     * */
    final override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    /** 重写该方法，解决加载闪烁问题（未核验）
     *
     *  [解决文章](https://blog.csdn.net/NakajimaFN/article/details/117816111?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-0-117816111-blog-83659576.235^v38^pc_relevant_sort_base2&spm=1001.2101.3001.4242.1&utm_relevant_index=3)
     * */
    final override fun getItemId(position: Int): Long = position.toLong()

    /**
     * 无需重写该方法，请重写 [getDefItemSize]
     * */
    final override fun getItemCount(): Int = headerViewCount + data.size + footerViewCount


    // ================================ protected method ========================================


    /**
     * 绑定数据，将会在 [onBindViewHolder] 中最后调用，如果设置监听事件，将会覆盖 [setItemClickListener] 的事件
     * */
    protected abstract fun bindData(holder: BaseViewHolder, item: T, position: Int)

    /**
     * 实现 ViewBinding
     * */
    protected abstract fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): VB


    /**
     * 自定义创建 ViewHolder， 提供了 [layoutInflater]
     *
     * */
    protected open fun onCreateDefViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseBindViewHolder(getViewBinding(layoutInflater, parent, viewType))
    }

    /**
     * 重写该方法来返回指定的 view type
     * @param realPosition 实际位置
     * */
    protected open fun getDefItemViewType(realPosition: Int): Int {
        return super.getItemViewType(realPosition)
    }

    /**
     * 返回子项数目
     * */
    protected open fun getDefItemSize(): Int = data.size


    /**
     * 绑定点击事件
     * */
    protected open fun bindViewListener(viewHolder: BaseViewHolder) {
        mOnItemClickListener?.let { listener ->
            viewHolder.itemView.setOnClickListener {
                var position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                position -= headerLayoutCount
                listener.invoke(viewHolder, position)
            }
        }

        mOnItemLongClickListener.let { listener ->
            viewHolder.itemView.setOnLongClickListener {
                var position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnLongClickListener false
                }
                position -= headerLayoutCount
                listener.invoke(viewHolder, position)
            }

        }
    }


    //====================================== public method =======================================


    /**
     * 往 Header 添加 View
     * @param view
     * @param index view 在 header 中的位置，默认为尾部插入
     * */
    fun addHeadView(view: View, index: Int = -1): Int {
        if (!this::mHeaderLayout.isInitialized) {
            initHeadLayout(view.context)
        }
        val childCount = mHeaderLayout.childCount
        var mIndex = index
        if (index < 0 || index > childCount) {
            mIndex = childCount
        }
        mHeaderLayout.addView(view, mIndex)
        // 从 0 到 1 需要有一个动画过渡
        if (mHeaderLayout.childCount == 1) {
            notifyItemInserted(headerViewPosition)
        }
        return mIndex
    }

    /**
     * 移除指定 [header]
     * */
    fun removeHeaderView(header: View) {
        if (hasHeaderView) {
            mHeaderLayout.removeView(header)
            if (headerViewCount == 0) {
                // 从 1 到 0 需要有一个动画过渡
                notifyItemRemoved(headerViewPosition)
            }
        }
    }

    /**
     * 移除所有头布局
     * */
    fun removeAllHeaderView() {
        if (hasHeaderView) {
            mHeaderLayout.removeAllViews()
            notifyItemRemoved(headerViewPosition)
        }
    }

    /**
     * 往 Footer 添加 View
     * @param view
     * @param index 添加的位置，默认为尾部添加
     * */
    fun addFooterView(view: View, index: Int = -1): Int {
        if (!this::mFooterLayout.isInitialized) {
            initFooterLayout(view.context)
        }
        val childCount = mFooterLayout.childCount
        var mIndex = index
        if (index < 0 || index > childCount) {
            mIndex = childCount
        }
        mFooterLayout.addView(view, mIndex)
        if (mFooterLayout.childCount == 1) {
            notifyItemInserted(footerViewPosition)
        }
        return mIndex
    }

    /**
     * 移除指定 [footer]
     * */
    fun removeFooterView(footer: View) {
        if (hasFooterView) {
            mFooterLayout.removeView(footer)
            if (footerViewCount == 0) {
                // 从 1 到 0 需要有一个动画过渡
                notifyItemRemoved(footerViewPosition)
            }
        }
    }

    /**
     * 移除所有尾部布局
     * */
    fun removeAllFooterView() {
        if (hasFooterView) {
            mFooterLayout.removeAllViews()
            notifyItemRemoved(footerViewPosition)
        }
    }


    /**
     * 插入单个数据，调用 [notifyItemInserted] 有插入动画
     *
     * @param position 插入位置， < 0为插入尾部(没有检查参数合法)
     * @param insert 插入数据
     * */
    fun insertItem(position: Int, insert: T) {
        val size = data.size
        if (position < 0 || (position == 0 && size == 0)) {
            this.data += insert
            mOnItemInsertedListener?.invoke(insert, size)
            notifyItemInserted(getRealPosition(size)) // notify 尾部有 item 插入
            // 尾部插入无需调用 notifyItemRangeChanged
        } else {
            this.data.add(position, insert)
            val realPosition = getRealPosition(position)
            notifyItemInserted(realPosition)
            // 刷新插入位置后面全部数据
            notifyItemRangeChanged(realPosition + 1, size - position)
            mOnItemInsertedListener?.invoke(insert, position)
        }
    }

    /**
     * 插入多项数据，调用 [notifyItemRangeInserted] 有插入动画
     *
     * @param position 插入位置， < 0为插入尾部(没有检查参数合法)
     * @param inserts 插入数据集合
     * */
    fun insertItems(position: Int, inserts: Collection<T>) {
        val size = data.size
        val insertSize = inserts.size
        if (position < 0 || (position == 0 && size == 0)) {
            this.data += inserts
            notifyItemRangeInserted(getRealPosition(size), insertSize) // notify 尾部有 item 插入
            // 尾部插入无需调用 notifyItemRangeChanged

            for (i in size until data.size) mOnItemInsertedListener?.invoke(data[i], i)
        } else {
            this.data.addAll(position, inserts)
            val realPosition = getRealPosition(position)
            notifyItemRangeInserted(realPosition, insertSize)
            // 刷新插入位置后面全部数据
            notifyItemRangeChanged(realPosition + insertSize, size - position)
            for (i in position until position + insertSize) mOnItemInsertedListener?.invoke(data[i], i)
        }
    }

    /**
     * 删除数据，调用 [notifyItemRemoved] 和 [notifyItemRangeChanged] 有删除动画
     *
     * @param position 删除位置， < 0为删除尾部(没有检查参数合法)
     * */
    fun removeItem(position: Int) {
        val size = this.data.size
        if (position < 0) {
            this.data.removeAt(size - 1)
            notifyItemRemoved(getRealPosition(size) - 1)
        } else {
            require(position >= data.size) {
                "remove $position out of the length $size"
            }
            this.data.removeAt(position)
            val realPosition = getRealPosition(position)
            notifyItemRemoved(realPosition)
            notifyItemRangeChanged(realPosition, size - position)
        }
    }

    /**
     * 删除数据，调用 [notifyItemRemoved] 和 [notifyItemRangeChanged] 有删除动画
     *
     * @param startPosition 删除位置， < 0为删除尾部(没有检查参数合法)
     * @param itemCount 删除个数
     * */
    fun removeItems(startPosition: Int, itemCount: Int) {
        val size = data.size
        require(itemCount <= data.size) {
            "remove $itemCount is greater than $itemCount"
        }
        if (startPosition < 0) {
            repeat(itemCount) { count ->
                val removed =  data.removeLastOrNull()
                removed?.let {
                    mOnItemRemovedListener?.invoke(it, size - count)
                }
            }
            notifyItemRangeRemoved(getRealPosition(size) - itemCount, itemCount)
        } else {
            repeat(itemCount) { count ->
                val removed = data.removeAt(startPosition)
                removed?.let {
                    mOnItemRemovedListener?.invoke(it, startPosition + count)
                }
            }
            val realPosition = getRealPosition(startPosition)
            notifyItemRangeRemoved(realPosition, itemCount)
            notifyItemRangeChanged(realPosition, size - startPosition)
        }
    }

    /**
     * 更新指定 [position] 的数据为 [update]
     *
     * @return true更新成功，false下标越界
     * */
    fun updateItem(position: Int, update: T): Boolean {
        if (position in data.indices) {
            data[position] = update
            notifyItemChanged(getRealPosition(position))
            return true
        }
        return false
    }

    /**
     * TODO: 测试
     * 更新指定范围 [[startPosition], startPosition + [updates].size) 内的数据 [updates]
     * - 更新内容过多，则添加到末尾
     * @return 更新成功的数据条数
     * */
    fun updateItems(startPosition: Int, updates: Collection<T>): Int {
        val size = data.size
        var changedCount = 0
        val inserts = mutableListOf<T>()
        updates.forEachIndexed { index, t ->
            val targetIndex = startPosition + index
            if (targetIndex < size) {
                data[targetIndex] = t
                changedCount ++
            } else {
                inserts += t
            }
        }
        notifyItemRangeChanged(startPosition, changedCount)
        insertItems(-1, inserts)
        return changedCount
    }


    /**
     * 替换数据：先清空后增加
     *
     * @param swap 替换的数据
     * */
    fun swapData(swap: Collection<T>) {
        clear()
        insertItems(-1, swap)
    }


    /**
     * 直接清空数据
     * */
    fun clear() {
        val size = this.data.size
        this.data.clear()
        notifyItemRangeRemoved(headerLayoutCount, getRealPosition(size))
        notifyItemRangeChanged(headerLayoutCount, getRealPosition(size))
    }

    /**
     * 获取指定 [position] 的子项数据, 注意需要 [getRealPosition]
     * @return position合法返回对应数据，不合法返回 null
     * */
    fun getItem(position: Int): T? {
        return if (position in this.data.indices) {
            this.data[position]
        } else null
    }


    // ========================== private method ========================================

    /**
     * 返回实际位置
     * */
    private fun getRealPosition(position: Int): Int = position - headerLayoutCount

    /**
     * 初始化 [mHeaderLayout]
     * */
    private fun initHeadLayout(context: Context) {
        mHeaderLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }

    /**
     * 初始化 [mFooterLayout]
     * @param context view 的 context
     * */
    private fun initFooterLayout(context: Context) {
        mFooterLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }


}