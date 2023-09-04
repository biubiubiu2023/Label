package com.example.label

import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ImageUtils
import com.example.label.databinding.ActivityMainBinding
import com.example.label.keyboard.KeyboardHeightProvider
import com.example.label.utils.ViewUtils
import com.example.label.view.PizhuContainView

class MainActivity : AppCompatActivity() {
    lateinit var binding :ActivityMainBinding

    var keyboardHeightProvider: KeyboardHeightProvider? = null
    override fun onDestroy() {
        keyboardHeightProvider?.close()
        super.onDestroy()
    }

    val mColorAdapter:ColorSelectAdapter by lazy {
        ColorSelectAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvConfirm.setOnClickListener{
            binding.pizhucontainer.onConfirmed()
            binding.pizhucontainer.post{
                val view2Bitmap = ImageUtils.view2Bitmap(binding.flImageContainer)
                val imageRealPosition = ViewUtils.getImageRealPosition(binding.image)
                val clip = ImageUtils.clip(
                    view2Bitmap,
                    imageRealPosition.left.toInt(),
                    imageRealPosition.top.toInt(),
                    imageRealPosition.width().toInt(),
                    imageRealPosition.height().toInt(),
                    true
                )
                ImageUtils.save(clip, ViewUtils.getClipCachePath()+"out.jpg", Bitmap.CompressFormat.JPEG)
                startActivity(Intent(this,ResultActivity::class.java))
            }
        }
        keyboardHeightProvider = KeyboardHeightProvider(this)
        keyboardHeightProvider?.setKeyboardHeightObserver { height, orientation ->
            if(height > 10){
                binding.llColor.animate().translationY((-height).toFloat()).setDuration(200).setListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                        binding.flBottom.visibility = View.GONE
                        binding.llColor.visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(p0: Animator) {}
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                }).start()

                val coveredHeightByKeyboard = binding.pizhucontainer.getCoveredHeightByKeyboard(height+ViewUtils.dp2px(43f))
                if(coveredHeightByKeyboard > 0){
                    val tY = ViewUtils.dp2px(20f)+coveredHeightByKeyboard
                    binding.flImageContainer.animate().translationY( -tY.toFloat()).setDuration(150).start()
                }
            }else{
                binding.llColor.animate().translationY(0f).setDuration(200).setListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationEnd(p0: Animator) {
                        binding.flBottom.visibility = View.VISIBLE
                        binding.llColor.visibility = View.GONE
                    }
                    override fun onAnimationCancel(p0: Animator) {
                        binding.flBottom.visibility = View.VISIBLE
                        binding.llColor.visibility = View.GONE
                    }
                    override fun onAnimationRepeat(p0: Animator) {}
                }).start()
                binding.flImageContainer.animate().translationY(0f).setDuration(150).start()
            }
        }
        binding.image.post {
            keyboardHeightProvider?.start()
        }
        binding.pizhucontainer.addPizhu(
            (ViewUtils.getScreenWidth()/2- PizhuContainView.minPizhuWidth/2),
            ViewUtils.dp2px(200f).toFloat()
        )
        binding.rvColor.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL,false)
            adapter = mColorAdapter
        }
        mColorAdapter.setOnItemClickListener { adapter, view, position ->
            val i = ColorSelectAdapter.colors[position]
            binding.pizhucontainer.setCurrentTextColor(i)
        }
        mColorAdapter.setNewInstance(ColorSelectAdapter.colors)
    }

}