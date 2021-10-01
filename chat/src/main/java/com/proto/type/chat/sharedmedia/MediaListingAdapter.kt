import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.proto.type.base.data.model.MessageMediaItemModel
import com.proto.type.base.extension.inflate
import com.proto.type.base.utils.DateTimeUtils
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.grid_header_item_media.view.*
import kotlinx.android.synthetic.main.grid_item_media.view.*

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class MediaListingAdapter(val context: Context,
                          private val dataItemList: MutableList<MediaDataItem> = mutableListOf(),
                          val onItemSelected: (action: Int, messageMedia: MessageMediaItemModel, position: Int) -> Unit): RecyclerView.Adapter<MediaListingAdapter.ViewHolder>() {

    // MARK: - Public Variables
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    // MARK: - Classes
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    // MARK: - Objects
    object Actions {
        const val ACTION_SELECT = 0
    }

    // MARK: - Override Functions
    override fun getItemCount() = dataItemList.size

    override fun getItemViewType(position: Int): Int {
        return when (dataItemList[position]) {
            is MediaDataItem.MessageMediaHeader -> ITEM_VIEW_TYPE_HEADER
            is MediaDataItem.MessageMediaDataItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataItemList[position]
        when (data) {
            is MediaDataItem.MessageMediaDataItem -> {
                val itemView = holder.itemView
                val media = data.messageMedia
                Glide.with(context)
                    .load(media.data.url)
                    .fitCenter()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.ivMediaItem)
                itemView.clMediaItem.setOnClickListener {
                    onItemSelected(Actions.ACTION_SELECT, media, position)
                }
            }
            is MediaDataItem.MessageMediaHeader -> {
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
                val inflatedView = parent.inflate(R.layout.grid_item_media, false)
                ViewHolder(inflatedView)
            }
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    // MARK: - Public Functions
    fun addData(newList: MutableList<MessageMediaItemModel>) {
        var oldSize = dataItemList.size
        replaceDataSource(generateMediaDataItems(newList))
        var newSize = dataItemList.size
        notifyItemRangeChanged(oldSize, newSize)
    }

    fun isHeader(position: Int): Boolean {
        return when (dataItemList[position]) {
            is MediaDataItem.MessageMediaHeader -> true
            else -> false
        }
    }

    fun setData(list: MutableList<MessageMediaItemModel>) {
        replaceDataSource(generateMediaDataItems(list))
        notifyDataSetChanged()
    }

    // MARK: Private Functions
    private fun replaceDataSource(withList: MutableList<MediaDataItem>) {
        dataItemList.clear()
        dataItemList.addAll(withList)
    }

    private fun generateMediaDataItems(fromList: MutableList<MessageMediaItemModel>): MutableList<MediaDataItem> {
        val result = mutableListOf<MediaDataItem>()
        var currentItem: MessageMediaItemModel? = null
        for (item in fromList) {
            currentItem?.let {
                if (!DateTimeUtils.isInTheSameMonth(it.sent_ts, item.sent_ts)) { result.add(MediaDataItem.MessageMediaHeader(item.sent_ts)) }
            } ?: run {
                result.add(MediaDataItem.MessageMediaHeader(item.sent_ts))
            }
            result.add(MediaDataItem.MessageMediaDataItem(item))
            currentItem = item
        }
        return result
    }

    private fun generateMessageMediaItems(fromList: MutableList<MediaDataItem>): MutableList<MessageMediaItemModel> {
        return fromList.mapNotNull { (it as? MediaDataItem.MessageMediaDataItem)?.messageMedia }.toMutableList()
    }
}

sealed class MediaDataItem {
    abstract val id: String

    data class MessageMediaDataItem(val messageMedia: MessageMediaItemModel): MediaDataItem() {
        override val id = messageMedia.room_id + messageMedia.message_id
    }

    data class MessageMediaHeader(val timestamp: Double): MediaDataItem() {
        override val id = "Header_${timestamp}"
    }
}

