package cn.csd.mod_achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView

import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AchievementMain.newInstance] factory method to
 * create an instance of this fragment.
 */
class AchievementMain : Fragment() {

    private var firstRow: LinearLayoutCompat? = null
    private var secondRow: LinearLayoutCompat? = null
    private var thirdRow: LinearLayoutCompat? = null

//    companion object {
//        fun newInstance(resId: Int): AchievementMain {
//            val fragment = AchievementMain()
//            val args = Bundle()
//            args.putInt("resId", resId)
//            fragment.arguments = args
//            return fragment
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.achievement_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstRow = addLinearLayout()
        secondRow = addLinearLayout()
        thirdRow = addLinearLayout()

        val topBar = view.findViewById<Toolbar>(R.id.topToolbar1)
        val fragmentContainer = view.findViewById<FrameLayout>(R.id.myFragment)
        // 找到按钮
        val myButton = topBar.findViewById<ImageButton>(R.id.back)

        // 设置按钮的点击监听器
        myButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }


        val gameKing = ImageView(context)
        gameKing.setOnClickListener { gameKingFragment() }
        addAchievementToRow(firstRow!!, gameKing, R.drawable.achievement_game_king, fragmentContainer)

        //截屏达人勋章
        val screenShot = ImageView(context)
        screenShot.setOnClickListener { screenShotFragment() }
        addAchievementToRow(firstRow!!,screenShot, R.drawable.achievement_screen_shot, fragmentContainer)

        //社交达人勋章
        val socializingKing = ImageView(context)
        socializingKing.setOnClickListener { socialFragment() }
        addAchievementToRow(firstRow!!,socializingKing, R.drawable.achievement_socializing_king, fragmentContainer)
    }

    private fun addAchievementToRow(row: LinearLayoutCompat, imageView: ImageView, resId: Int, fragmentContainer: FrameLayout) {
        imageView.setImageResource(resId)

        fragmentContainer.post {
            val containerWidth = fragmentContainer.width
            val containerHeight = fragmentContainer.height

            // Set the size of the view
            val viewWidth = containerWidth / 3 // One third of container width
            val viewHeight = containerHeight / 4 // One fourth of container height

            val params = LinearLayoutCompat.LayoutParams(viewWidth, viewHeight)
            params.topMargin = dpToPx(10)
            imageView.layoutParams = params
        }




        row.addView(imageView)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).roundToInt()
    }

    private fun addLinearLayout(): LinearLayoutCompat {
        val myLayout: LinearLayoutCompat = requireView().findViewById(R.id.my_linear)
        val context = requireContext()

        val newLinearLayout = LinearLayoutCompat(context)
        newLinearLayout.orientation = LinearLayoutCompat.HORIZONTAL

        val params = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
        newLinearLayout.layoutParams = params

        myLayout.addView(newLinearLayout)

        return newLinearLayout
    }

    private fun gameKingFragment() {
        val gameKingFragment = AchievementFragment.newInstance(R.layout.achievement_game_king_fragment, 20)
        gameKingFragment.show(childFragmentManager, "gameKingFragment")
    }

    private fun screenShotFragment() {
        val screenShotFragment = AchievementFragment.newInstance(R.layout.achievement_screen_shot_fragment, 10)
        screenShotFragment.show(childFragmentManager, "screenShotFragment")
    }

    private fun socialFragment() {
        val socialFragment = AchievementFragment.newInstance(R.layout.achivement_social_fragment, 10)
        socialFragment.show(childFragmentManager, "socialFragment")
    }
}