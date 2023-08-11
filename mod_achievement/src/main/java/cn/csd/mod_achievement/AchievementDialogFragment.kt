package cn.csd.mod_achievement

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class AchievementFragment : DialogFragment() {

    private var resId : Int = 0
    private var times : Int = 0

    companion object {
        fun newInstance(resId: Int, times: Int): AchievementFragment {
            val fragment = AchievementFragment()
            val args = Bundle()
            args.putInt("resId", resId)
            args.putInt("times", times)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resId = arguments?.getInt("resId") ?: 0
        times = arguments?.getInt("times") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(resId, container, false)

        val textView = TextView(context).apply {
            text = "你已经完成了 $times / 1000 次"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#000000"))
            textSize = 16f

        }

        val layout = view as ViewGroup // Since view is your root layout
        layout.addView(textView)

        return view
    }
}