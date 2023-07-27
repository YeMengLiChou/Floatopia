//package cn.csd.lib_framework.toast
//
//import android.animation.Animator
//import android.animation.AnimatorSet
//import android.app.Activity
//import android.content.Context
//import android.graphics.drawable.GradientDrawable
//import android.os.Handler
//import android.os.Looper
//import android.os.Message
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
//import android.widget.FrameLayout
//import android.widget.LinearLayout
//import android.widget.TextView
//import cn.csd.lib_framework.databinding.FrameworkLayoutToastBinding
//import cn.csd.lib_framework.toast.LToast.LIToastHandler.Companion
//import java.util.*
//
//
///**
// *
// *
// *
// * @author Gleamrise
// * <br/>Created: 2023/07/18
// */
//class LToast {
//    companion object {
//
//        private val TAG = LToast::class.simpleName
//
//        const val DURATION_LONG = 3500
//
//        const val DURATION_SHORT = 2000
//
//        fun build(context: Context): LToast {
//            return LToast(context)
//        }
//
//        fun build(context: Context, message: String?): LToast {
//            return LToast(context, message)
//        }
//    }
//
//    private var mContext: Context
//
//    private var mView: View? = null
//
//    private var mViewGroup: ViewGroup? = null
//
//    private var mViewRoot: ViewGroup? = null
//
//    private var mBinding: FrameworkLayoutToastBinding? = null
//
//    private var mToastBackgound: GradientDrawable? = null
//
//    private var mInflater: LayoutInflater
//
//    private var mTextView: TextView? = null
//
//    private var message: String? = null
//
//    private var mShowAnimatorSet: AnimatorSet? = null
//
//    private var mHideAnimatorSet: AnimatorSet? = null
//
//    private val mShowAnimationType = 0
//
//    private val mHideAnimationType = 0
//
//    private var mDuration = 0
//
//    private val mBackgroundColor = 0
//
//    private val mOnDisappearListener: LToast.OnDisappearListener? = null
//
//    interface OnDisappearListener {
//        fun onDisappear(xToast: LToast?)
//    }
//
//    constructor(context: Context) {
//        mContext = context
//        mInflater = LayoutInflater.from(context)
//    }
//
//    constructor(context: Context, message: String?) {
//        mContext = context
//        this.message = message
//        mInflater = LayoutInflater.from(context)
//    }
//
//    val isShowing: Boolean
//        get() = mView != null && mView!!.isShown
//
//    private fun initViews() {
//        mViewRoot = (mContext as Activity).findViewById<View>(R.id.content) as ViewGroup
//        mViewGroup = LinearLayout(mContext)
//        (mViewGroup as LinearLayout).layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
//            gravity = Gravity.BOTTOM or Gravity.CENTER
//            bottomMargin = 200
//        }
//        mViewRoot!!.addView(mViewGroup)
//
//        // 如果用户没有使用自己的View，那么使用默认的mView
//        if (mView == null) {
//            mBinding = FrameworkLayoutToastBinding.inflate(mInflater, mViewGroup, false)
//            mView = mBinding!!.root
//            mToastBackgound = mView!!.background as GradientDrawable
//            if (mBackgroundColor != 0) {
//                mToastBackgound!!.setColor(mBackgroundColor)
//            }
//            mTextView = mBinding!!.frameworkTvToastContent
//            mTextView!!.text = message
//
//        }
//        //对mView的大小进行测量
//        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
//        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
//        mView!!.measure(widthMeasureSpec, heightMeasureSpec)
//    }
//
//    fun show() {
//
//        //准备工作
//        initViews()
////        if (mShowAnimationType == 0) mShowAnimatorSet = AnimationUtils.getShowAnimation(this, AnimationUtils.ANIMATION_DEFAULT) else mShowAnimatorSet =
////            AnimationUtils.getShowAnimation(this, mShowAnimationType)
////        if (mHideAnimationType == 0) mHideAnimatorSet = AnimationUtils.getHideAnimation(this, AnimationUtils.ANIMATION_DEFAULT) else mHideAnimatorSet =
////            AnimationUtils.getHideAnimation(this, mHideAnimationType)
//        if (mDuration == 0) mDuration = XTOAST_SHORT
//        val LIToastHandler: LIToastHandler = LIToastHandler.getInstance()
//        LIToastHandler.add(this)
//    }
//
//    object LIToastHandler : Handler(Looper.myLooper()!!) {
//        private val mQueue: Queue<LToast> = LinkedList()
//
//
//        /**
//         * 该方法把 LIToast添加到消息队列中
//         * @param xToast
//         */
//        fun add(toast: LToast) {
//            mQueue.offer(toast)
//            showNextToast()
//        }
//
//        fun showNextToast() {
//            if (mQueue.isEmpty()) return
//            //获取队列头部的XToast
//            val toast: LToast = mQueue.peek() as LToast
//            if (!toast.isShowing) {
//                val message: Message = Message.obtain()
//                message.what = SHOW_TOAST
//                message.obj = toast
//                sendMessage(message)
//            }
//        }
//
//        override fun handleMessage(msg: Message) {
//            val xToast: LToast = msg.obj as LToast
//            when (msg.what) {
//                SHOW_TOAST -> showToast(xToast)
//                HIDE_TOAST -> hideToast(xToast)
//                SHOWNEXT_TOAST -> showNextToast()
//            }
//        }
//
//        private fun hideToast(toast: LToast) {
//            val set: AnimatorSet? = toast.mHideAnimatorSet
//            set?.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(animation: Animator) {}
//                override fun onAnimationEnd(animation: Animator) {
//                    //如果动画结束了，移除队列头部元素以及从界面中移除mView
//                    toast.mViewGroup?.removeView(toast.mView)
//                    toast.mOnDisappearListener?.onDisappear(toast)
//                    mQueue.poll()
//                    sendEmptyMessage(SHOWNEXT_TOAST)
//                }
//
//                override fun onAnimationCancel(animation: Animator) {}
//                override fun onAnimationRepeat(animation: Animator) {}
//            })
//            set?.start()
//        }
//
//        private fun showToast(toast: LToast) {
//
//            //如果当前有XToast正在展示，直接返回
//            if (toast.isShowing) return
//            //把mView添加到界面中，实现Toast效果
//            toast.mViewGroup?.addView(toast.mView)
//            //获取动画效果
//            val set: AnimatorSet = toast.getShowAnimatorSet()
//            set.start()
//            val message: Message = Message.obtain()
//            message.what = HIDE_TOAST
//            message.obj = toast
//            sendMessageDelayed(message, toast.getDuration())
//        }
//
//        companion object {
//            private var mToastHandler: XToastHandler? = null
//            private const val SHOW_TOAST = 0x123
//            private const val HIDE_TOAST = 0x456
//            private const val SHOWNEXT_TOAST = 0X789
//
//            @get:Synchronized
//            val instance: XToastHandler?
//                get() = if (mToastHandler != null) mToastHandler else {
//                    mToastHandler = XToastHandler()
//                    mToastHandler
//                }
//        }
//    }
//
//}
//
