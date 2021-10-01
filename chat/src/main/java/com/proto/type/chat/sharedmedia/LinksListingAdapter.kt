import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.proto.type.base.data.model.MessageLinkItemModel
import com.proto.type.base.extension.inflate
import com.proto.type.base.extension.setImage
import com.proto.type.base.utils.DateTimeUtils
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.grid_header_item_media.view.*
import kotlinx.android.synthetic.main.list_item_link.view.*
import java.net.URL

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class LinksListingAdapter(val context: Context,
                          private val dataItemList: MutableList<LinkDataItem> = mutableListOf(),
                          val onItemActed: (action: Int, messageMedia: MessageLinkItemModel, position: Int) -> Unit): RecyclerView.Adapter<LinksListingAdapter.ViewHolder>() {

    // MARK: - Public Variables
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    // MARK: - Classes
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    // MARK: - Objects
    object Actions {
        const val ACTION_SELECT = 0
        const val ACTION_UPDATE = 1
    }

    // MARK: - Override Functions
    override fun getItemCount() = dataItemList.size

    override fun getItemViewType(position: Int): Int {
        return when (dataItemList[position]) {
            is LinkDataItem.MessageLinkHeader -> ITEM_VIEW_TYPE_HEADER
            is LinkDataItem.MessageLinkDataItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        when (val data = dataItemList[position]) {
            is LinkDataItem.MessageLinkDataItem -> {
                val itemView = holder.itemView
                val linkItem = data.messageLink
                val linkItemTitle = linkItem.title ?: ""
                itemView.tvLinkDomain.text = if (URLUtil.isValidUrl(linkItem.link)) URL(linkItem.link).host else linkItem.link
                itemView.tvTitle.text = if (linkItemTitle.isEmpty()) context.getString(R.string.loading) else linkItemTitle
                if (linkItemTitle.isEmpty() && linkItem.link.isNotEmpty()) onItemActed(Actions.ACTION_UPDATE, linkItem, position)
                if (linkItem.main_image_url?.isNotEmpty() == true) {
                    Glide.with(context)
                        .load(linkItem.main_image_url)
                        .fitCenter()
                        .centerCrop()
                        .placeholder(R.drawable.ic_default_url_preview)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.ivUrlPreview)
                } else {
                    itemView.ivUrlPreview.setImage(R.drawable.ic_default_url_preview)
                }
                itemView.clContainer.setOnClickListener {
                    onItemActed(Actions.ACTION_SELECT, linkItem, position)
                }
            }
            is LinkDataItem.MessageLinkHeader -> {
                val itemView = holder.itemView
                itemView.tvHeaderTitle.text = DateTimeUtils.getDateString(data.timestamp, DateTimeUtils.MMMM_yyyy)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                val inflatedView = parent.inflate(R.layout.grid_header_item_media, false)
                ViewHolder(inflatedView)
            }
            ITEM_VIEW_TYPE_ITEM -> {
                val inflatedView = parent.inflate(R.layout.list_item_link, false)
                ViewHolder(inflatedView)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    // MARK: - Public Functions
    fun addData(newList: MutableList<MessageLinkItemModel>) {
        var oldSize = dataItemList.size
        replaceDataSource(generateLinkDataItems(newList))
        var newSize = dataItemList.size
        notifyItemRangeChanged(oldSize, newSize)
    }

    fun setData(list: MutableList<MessageLinkItemModel>) {
        replaceDataSource(generateLinkDataItems(list))
        notifyDataSetChanged()
    }

    // MARK: Private Functions
    private fun generateLinkDataItems(fromList: MutableList<MessageLinkItemModel>): MutableList<LinkDataItem> {
        val result = mutableListOf<LinkDataItem>()
        var currentItem: MessageLinkItemModel? = null
        for (item in fromList.toMutableList()) {
            currentItem?.let {
                if (!DateTimeUtils.isInTheSameMonth(it.sent_ts, item.sent_ts)) { result.add(LinkDataItem.MessageLinkHeader(item.sent_ts)) }
            } ?: run {
                result.add(LinkDataItem.MessageLinkHeader(item.sent_ts))
            }
            result.add(LinkDataItem.MessageLinkDataItem(item))
            currentItem = item
        }
        return result
    }

    private fun generateMessageLinkItems(fromList: MutableList<LinkDataItem>): MutableList<MessageLinkItemModel> {
        return fromList.mapNotNull { (it as? LinkDataItem.MessageLinkDataItem)?.messageLink }.toMutableList()
    }

    private fun replaceDataSource(withList: MutableList<LinkDataItem>) {
        dataItemList.clear()
        dataItemList.addAll(withList)
    }

    private fun updateLinkDataItem(newLinkItem: MessageLinkItemModel) {
        dataItemList.forEachIndexed { index, linkDataItem ->
            (linkDataItem as? LinkDataItem.MessageLinkDataItem)?.messageLink?.let {
                if (it.room_id == newLinkItem.room_id && it.message_id == newLinkItem.message_id) {
                    dataItemList[index] = LinkDataItem.MessageLinkDataItem(newLinkItem)
                    return@forEachIndexed
                }
            }
        }
    }
}

sealed class LinkDataItem {
    abstract val id: String

    data class MessageLinkDataItem(val messageLink: MessageLinkItemModel): LinkDataItem() {
        override val id = messageLink.room_id + messageLink.message_id
    }

    data class MessageLinkHeader(val timestamp: Double): LinkDataItem() {
        override val id = "Header_${timestamp}"
    }
}