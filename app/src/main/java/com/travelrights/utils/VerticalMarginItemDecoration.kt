package com.travelrights.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalMarginItemDecoration(context: Context, horizontalMarginInDp: Int) :
    RecyclerView.ItemDecoration() {

    private val horizontalMarginInPx: Int = horizontalMarginInDp

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {

        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = 0
        }else{
            outRect.top = horizontalMarginInPx
        }

       // outRect.left = horizontalMarginInPx
    }

}