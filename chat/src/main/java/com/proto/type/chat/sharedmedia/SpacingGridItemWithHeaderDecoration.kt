package com.proto.type.chat.sharedmedia

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager

import androidx.recyclerview.widget.RecyclerView

class SpacingGridWithHeaderItemDecoration(
    private val spacingPx: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    private val headerPositions = mutableListOf<Int>()

    // MARK: - Override Functions
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // current item position
        val gridLayoutManager = parent.layoutManager as GridLayoutManager
        val spanCount = gridLayoutManager.spanCount // number of items per row
        val params = view.layoutParams as GridLayoutManager.LayoutParams
        if (params.spanSize == spanCount) {
            if (!headerPositions.contains(position)) {
                headerPositions.add(position)
                headerPositions.sort()
            }
        } else {
            if (headerPositions.contains(position)) {
                headerPositions.remove(position)
                headerPositions.sort()
            }
            val newPosition = position - (headerPositions.lastOrNull { it < position } ?: -1) - 1
            val column = newPosition % spanCount // item column
            if (includeEdge) {
                outRect.left = spacingPx - column * spacingPx / spanCount
                outRect.right = (column + 1) * spacingPx / spanCount

                if (newPosition < spanCount) { // top edge
                    outRect.top = spacingPx
                }
                outRect.bottom = spacingPx // item bottom
            } else {
                outRect.left = column * spacingPx / spanCount
                outRect.right = spacingPx - (column + 1) * spacingPx / spanCount
                if (newPosition >= spanCount) {
                    outRect.top = spacingPx // item top
                }
            }
        }
    }
}