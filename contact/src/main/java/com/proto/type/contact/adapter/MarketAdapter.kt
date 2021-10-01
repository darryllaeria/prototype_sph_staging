package com.proto.type.contact.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.ui.widget.ChatQRecyclerView
import com.proto.type.contact.R
import kotlinx.android.synthetic.main.item_market.view.*

data class MarketData(val cryptoName: String,
                      val currency: String,
                      val code: String,
                      val price: Double,
                      val marginPercent: Double)

class MarketAdapter: ChatQRecyclerView.ChatQAdapter<MarketData, MarketAdapter.MarketViewHolder>() {

    override var layoutItemId = R.layout.item_market

    override fun createHolder(view: View) = MarketViewHolder(view)

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    inner class MarketViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindView(data: MarketData) {
            itemView.tvSymbolName.text = "${data.cryptoName} (${data.currency})"
            itemView.tvPrice.text = "${data.price}"
            itemView.tvSymbolCode.text = "${data.code}"
            itemView.tvMargin.text = "+${data.marginPercent}%"
        }
    }
}