package com.proto.type.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.R
import com.proto.type.base.extension.hideView
import kotlinx.android.synthetic.main.view_custom_recyclerview.view.*

class ChatQRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var viewId: Int = R.layout.view_empty
    private var emptyView: View

    init {
        if (attrs != null) {
            var arr = context.obtainStyledAttributes(attrs, R.styleable.ChatQRecyclerView)
            try {
                viewId = arr.getResourceId(R.styleable.ChatQRecyclerView_cqrv_empty, R.layout.view_empty)
            } finally {
                arr.recycle()
            }
            emptyView = LayoutInflater.from(context).inflate(viewId, null)
            val params = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            emptyView.layoutParams = params
        } else {
            throw IllegalArgumentException()
        }
    }

    fun <D, H, R: ChatQAdapter<D, H>> setAdapter(adapter: R) {
        rvContent.adapter = adapter
    }

    fun <D> setData(data: List<D>) {
        if (data.isEmpty()) {
            rvContent.hideView()
            addView(emptyView)
        } else {
            (rvContent.adapter as ChatQAdapter<D, *>).display(data)
        }
    }

    abstract class ChatQAdapter<D, VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {

        abstract var layoutItemId: Int
        var items = mutableListOf<D>()
            private set

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(layoutItemId, parent, false)
            return createHolder(view)
        }

        abstract fun createHolder(view: View): VH

        override fun getItemId(position: Int) = position.toLong()

        override fun getItemCount() = items.size

        fun display(newData: List<D>) {
            items.clear()
            items.addAll(newData)
            notifyDataSetChanged()
        }
    }
}