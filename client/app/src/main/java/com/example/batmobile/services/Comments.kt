package com.example.batmobile.services

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.batmobile.DTOFromServer.ProductComment
import com.example.batmobile.R

class Comments {


    companion object{

        var marginInDp = 4
        var itemLayoutParamsForHorizontal = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1f
        )

        var itemLayoutParamsForVertical = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        fun renderComments(product_comments: List<ProductComment>, row:LinearLayout,type: String ,activity: Activity, context: Context){
            val marginInPx = (marginInDp * activity.resources.displayMetrics.density).toInt()
            val itemLayoutParams : LinearLayout.LayoutParams;
            if(type.equals("HORIZONTAL")){
                itemLayoutParams = itemLayoutParamsForHorizontal
                itemLayoutParams.setMargins(marginInPx, (marginInDp+14), marginInPx, 0)
            }
            else if(type.equals("VERTICAL")){
                itemLayoutParams = itemLayoutParamsForVertical
                itemLayoutParams.setMargins(0, (marginInDp+14), 0, 0)
            }
            else{
                //default da je vertical
                itemLayoutParams = itemLayoutParamsForVertical
                itemLayoutParams.setMargins(0, (marginInDp+14), 0, 0)
            }

            val widthInDp = 15
            val heightInDp = 15

            val density = activity.resources.displayMetrics.density

            val widthInPx = (widthInDp * density).toInt()
            val heightInPx = (heightInDp * density).toInt()

            var commentParams = LinearLayout.LayoutParams(widthInPx, heightInPx)
            commentParams.rightMargin = marginInPx

            for((index, comment) in product_comments.withIndex()){
                val itemView = activity.layoutInflater.inflate(R.layout.component_comment, null)
                val comment_image           = itemView.findViewById<ImageView>(R.id.comment_image)
                val comment_username        = itemView.findViewById<TextView>(R.id.comment_username)
                val comment_comment         = itemView.findViewById<TextView>(R.id.comment_comment)
                val comment_horizontal_star = itemView.findViewById<LinearLayout>(R.id.horizontal_layout_star)

                if(comment.picture != null)
                    Image.setImageResource(comment_image, comment.picture, -1)
                else
                    Image.setImageResource(comment_image, null, -1)
                comment_username.text = comment.username
                comment_comment.text = comment.text
                for(i in 0 until comment.grade){
                    val star = ImageView(context)
                    star.layoutParams = commentParams
                    star.setImageResource(R.drawable.star_filled)
                    comment_horizontal_star.addView(star)
                }
                for(i in 0 until (5-comment.grade)){
                    val star = ImageView(context)
                    star.layoutParams = commentParams
                    star.setImageResource(R.drawable.star)
                    comment_horizontal_star.addView(star)
                }
                itemView.layoutParams = itemLayoutParams
                if(type.equals("VERTICAL")){
                    val comment_text        = itemView.findViewById<TextView>(R.id.comment_comment)
                    comment_text.maxLines   = Int.MAX_VALUE
                    comment_text.ellipsize  = null
                }
                row.addView(itemView);
            }
        }

    }

}