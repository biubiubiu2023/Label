package com.example.label

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.label.utils.setRoundConner

/**
 * 创建时间：2023/2/12
 * 编写人： 陈陈陈
 * 功能描述：
 */
class ColorSelectAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_color) {

    companion object{
        val colors = mutableListOf<Int>(R.color.white,R.color.black,R.color.color_306, R.color.color_db4,
            R.color.color_e37,R.color.color_e4b,R.color.color_53a,R.color.color_5f3)
    }


    override fun convert(holder: BaseViewHolder, item: Int) {
        val view = holder.getView<ImageView>(R.id.iv_color)
        view.setRoundConner(15)
        view.setImageResource(item)
    }

}