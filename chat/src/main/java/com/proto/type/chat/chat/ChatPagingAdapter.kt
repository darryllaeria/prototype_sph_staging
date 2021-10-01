package com.proto.type.chat.chat

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.bold
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.proto.type.base.Constants
import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.*
import com.proto.type.base.ui.widget.CircularImageView
import com.proto.type.base.ui.widget.CustomRoundedImageView
import com.proto.type.base.utils.AppLog
import com.proto.type.base.utils.DateTimeUtils
import com.proto.type.chat.R
import com.google.android.gms.common.internal.Objects
import kotlinx.android.synthetic.main.item_chat_default.view.*

class
ChatPagingAdapter(
    private var currentUser: UserModel,
    private var participants: List<UserModel>,
    private var room: ChatModel,
    val onItemSelected: (action: Int, user: UserModel?, position: Int, message: MessageModel?) -> Unit
) : PagedListAdapter<MessageModel, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ChatPagingAdapter::class.java.simpleName
        const val ACTION_USER_CHART = 0
        const val ACTION_USER_IMAGE = 1
        const val ACTION_RESPONDER_CHART = 2
        const val ACTION_RESPONDER_IMAGE = 3
        const val ACTION_RESPONDER_PROFILE = 4
        private const val TYPE_DEFAULT = 0
        private const val TYPE_HEADER = 1 // CHAT_SYSTEM_ROOM_CREATE
        private const val TYPE_USER_CHART = 2
        private const val TYPE_USER_IMAGE = 3
        private const val TYPE_USER_TEXT = 4
        private const val TYPE_RESPONDER_CHART = 5
        private const val TYPE_RESPONDER_IMAGE = 6
        private const val TYPE_RESPONDER_TEXT = 7
    }

    var messageOnLongPressed = MutableLiveData<MessageModel>()

    // MARK: - Override Functions
    init {
        setHasStableIds(true)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        val dataType = message?.data?.type
        val senderId = message?.sender?.id

        return if (dataType == MessageDataType.SystemRoomCreate) {
            TYPE_HEADER
        } else if (dataType == MessageDataType.Text) {
            if (senderId == currentUser.id) TYPE_USER_TEXT else TYPE_RESPONDER_TEXT
        } else if (dataType == MessageDataType.Image) {
            if (senderId == currentUser.id) TYPE_USER_IMAGE else TYPE_RESPONDER_IMAGE
        } else if (dataType == MessageDataType.Chart) {
            if (senderId == currentUser.id) TYPE_USER_CHART else TYPE_RESPONDER_CHART
        } else {
            TYPE_DEFAULT
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_info, parent, false))
        TYPE_USER_TEXT -> UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right_text, parent, false))
        TYPE_USER_IMAGE -> UserImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right_image, parent, false))
        TYPE_USER_CHART -> UserChartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right_image, parent, false))
        TYPE_RESPONDER_TEXT -> ResponderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left_text, parent, false))
        TYPE_RESPONDER_IMAGE -> ResponderImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left_image, parent, false))
        TYPE_RESPONDER_CHART -> ResponderChartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left_image, parent, false))
        else ->  DefaultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_default, parent, false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val prevMsg = if (position - 1 >= 0) getItem(position - 1) else null
        val message = getItem(position)!!
        val nextMsg = if (position + 1 < itemCount) getItem(position + 1) else null

        holder.itemView.setOnLongClickListener {
            messageOnLongPressed.postValue(message)
            return@setOnLongClickListener true
        }

        when (holder.itemViewType) {
            TYPE_HEADER -> (holder as HeaderViewHolder).bindView(message)
            TYPE_USER_TEXT -> (holder as UserViewHolder).bindView(message)
            TYPE_USER_IMAGE -> (holder as UserImageViewHolder).bindView(message, position)
            TYPE_USER_CHART -> (holder as UserChartViewHolder).bindView(message, position)
            TYPE_RESPONDER_TEXT -> (holder as ResponderViewHolder).bindView(message, position, prevMsg, nextMsg)
            TYPE_RESPONDER_IMAGE -> (holder as ResponderImageViewHolder).bindView(message, position, prevMsg, nextMsg)
            TYPE_RESPONDER_CHART -> (holder as ResponderChartViewHolder).bindView(message, position, prevMsg, nextMsg)
            else -> (holder as DefaultViewHolder).bindView(message, position)
        }
    }

    // MARK: - Public Functions
    fun setChat(chat: ChatModel) {
        room = chat
    }
    fun setCurrentUser(user: UserModel) {
        currentUser = user
    }
    fun setParticipants(participants: MutableList<UserModel>) {
        this.participants = participants
    }

    // MARK: - Inner Classes
    inner class DefaultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindView(message: MessageModel, position: Int) {
            // If the data type is unknown, this is an empty img_placeholder instead of crashing
            itemView.tvContentNotSupported.text = "Chat type not yet supported - " + message.data.type
        }
    }
    inner class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val ivAvatar1: CircularImageView = itemView.findViewById(R.id.ivAvatar1)
        private val ivAvatar2: CircularImageView = itemView.findViewById(R.id.ivAvatar2)
        private val ivAvatar3: CircularImageView = itemView.findViewById(R.id.ivAvatar3)
        private val tvDateHeader: TextView = itemView.findViewById(R.id.tvDateHeader)
        private val tvExtraMembers: TextView = itemView.findViewById(R.id.tvExtraMembers)
        private val tvGroupName: TextView = itemView.findViewById(R.id.tvGroupName)
        private val tvHeaderInfo: TextView = itemView.findViewById(R.id.tvHeaderInfo)
        private val tvMemberCount: TextView = itemView.findViewById(R.id.tvMemberCount)

        fun bindView(message: MessageModel) {
            tvDateHeader.text = if (DateTimeUtils.isToday(message.sent_ts)) {
                itemView.context.getString(R.string.today)
            } else {
                DateTimeUtils.timeDateDayMonthYear(message.sent_ts)
            }
            when (room.category) {
                Constants.CHAT_CATEGORY_PRIVATE -> {
                    val privateUser = participants.firstOrNull { it.id != currentUser.id }
                    tvGroupName.text = privateUser?.displayingName() ?: Constants.KEY_CHATQ_USER
                    hideViews(ivAvatar1, ivAvatar3)
                    ivAvatar2.showView()
                    tvExtraMembers.hideView()
                    if (privateUser?.avatar?.url.isNullOrEmpty()) {
                        ivAvatar2.loadAvatar(privateUser?.displayingName())
                    } else {
                        Glide.with(itemView.context)
                            .load(privateUser!!.avatar!!.url)
                            .placeholder(R.drawable.img_placeholder)
                            .into(ivAvatar2)
                    }
                }
                Constants.CHAT_CATEGORY_GROUP -> {
                    if (participants.size == 1 && participants[0].id == currentUser.id) { // Self chat
                        tvGroupName.text = currentUser.displayingName()
                        ivAvatar2.showView()
                        hideViews(ivAvatar1, ivAvatar3)
                        ivAvatar2.loadAvatar(currentUser)
                        tvHeaderInfo.text = SpannableStringBuilder().bold { append(currentUser.displayingName()) }.append(itemView.context.getString(R.string.created_this_group))
                        tvMemberCount.text = itemView.context.getString(R.string.self_chat)
                    } else {
                        tvGroupName.text = room.name
                        try {
                            val roomCreator = participants.firstOrNull { message.room_id.split(".").contains(it.id) }
                            tvHeaderInfo.text = SpannableStringBuilder().bold {append(roomCreator?.displayingName()) }.append(itemView.context.getString(R.string.created_this_group))
                        } catch (e: Exception) {
                            tvHeaderInfo.hideView()
                        }
                        tvMemberCount.text = String.format(itemView.context.getString(R.string.chat_members), participants.size)
                        ivAvatar1.showView()
                        ivAvatar2.showView()
                        ivAvatar3.showView()

                        when {
                            participants.size == 2 -> {
                                val set = ConstraintSet()
                                val layout: ConstraintLayout = itemView as ConstraintLayout
                                set.clone(layout)
                                set.setHorizontalBias(R.id.ivAvatar2,0.6F)
                                set.applyTo(layout)
                                ivAvatar3.hideView()
                                ivAvatar1.loadAvatar(participants[0])
                                ivAvatar2.loadAvatar(participants[1])
                            }
                            participants.size == 3 -> {
                                ivAvatar1.loadAvatar(participants[0])
                                ivAvatar2.loadAvatar(participants[1])
                                ivAvatar3.loadAvatar(participants[2])
                            }
                            participants.size > 3 -> {
                                ivAvatar1.loadAvatar(participants[0])
                                ivAvatar2.loadAvatar(participants[1])
                                tvExtraMembers.showView()
                                tvExtraMembers.text = "+${participants.size - 2}"
                            }
                            else -> {
                                tvExtraMembers.hideView()
                            }
                        }
                    }
                }
            }
        }
    }
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMessageReply: TextView = itemView.findViewById(R.id.tvMessageReply)
        private val tvQuotedMessage: TextView = itemView.findViewById(R.id.tvQuotedMessage)
        private val tvQuotedSenderName: TextView = itemView.findViewById(R.id.tvQuotedSenderName)
        private val tvForwarded: TextView = itemView.findViewById(R.id.tvForwarded)
        private val llRepliedMessage: LinearLayoutCompat = itemView.findViewById(R.id.llRepliedMessage)

        fun bindView(message: MessageModel) {
            AppLog.d(TAG, "Bind view with message: ${message.sender.id}")
            val messageValue = message.data.value.trim()
            val quotedMessage = message.quoted_message
            val quotedMessageValue = quotedMessage?.data?.value?.trim()
            if (messageValue != "") {
                tvMessageReply.text = messageValue
                tvMessageReply.showView()
                llRepliedMessage.hideView()
            } else if (quotedMessageValue != "") {
                if (quotedMessage?.room_id == room.id) { // Quote message
                    llRepliedMessage.showView()
                    tvQuotedSenderName.text = participants.firstOrNull {
                        it.id == quotedMessage.sender.id
                    }?.displayingName() ?: quotedMessage.sender.id
                    tvQuotedMessage.text = quotedMessageValue
                    tvMessageReply.hideView()
                } else if (quotedMessage?.room_id != room.id) {
                    tvMessageReply.text = quotedMessageValue
                    tvForwarded.showView()
                }
            }
            tvDate.text = DateTimeUtils.timeHourMinutes(message.sent_ts)
        }
    }
    inner class UserImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val ivImage: CustomRoundedImageView = itemView.findViewById(R.id.ivImage)
        private val pbImageLoad: ProgressBar = itemView.findViewById(R.id.pbImageLoad)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bindView(message: MessageModel, position: Int) {
            Glide.with(itemView.context)
                .load(message.data.value_url)
                .override(600, 800)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.img_placeholder)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivImage.setImage(R.drawable.img_placeholder)
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivImage.setImageDrawable(resource)
                        return false
                    }
                }).into(ivImage)
            tvDate.text = DateTimeUtils.timeHourMinutes(message.sent_ts)
            itemView.setOnClickListener {
                onItemSelected(ACTION_USER_IMAGE, null, position, message)
            }
        }
    }
    inner class UserChartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val ivImage: CustomRoundedImageView = itemView.findViewById(R.id.ivImage)
        private val pbImageLoad: ProgressBar = itemView.findViewById(R.id.pbImageLoad)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bindView(message: MessageModel, position: Int) {
            Glide.with(itemView.context)
                .load(message.data.value_url)
                .override(800, 800)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.img_placeholder)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivImage.setImage(R.drawable.img_placeholder)
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivImage.setImageDrawable(resource)
                        return false
                    }
                }).into(ivImage)
            tvDate.text = DateTimeUtils.timeHourMinutes(message.sent_ts)
            itemView.setOnClickListener {
                onItemSelected(ACTION_USER_CHART, null, position, message)
            }
        }
    }
    inner class ResponderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val civAvatar: CircularImageView = itemView.findViewById(R.id.civAvatar)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvForwarded: TextView = itemView.findViewById(R.id.tvForwarded)
        private val tvMessageReply: TextView = itemView.findViewById(R.id.tvMessageReply)
        private val tvQuotedMessage: TextView = itemView.findViewById(R.id.tvQuotedMessage)
        private val tvQuotedSenderName: TextView = itemView.findViewById(R.id.tvQuotedSenderName)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val llQuotedMessage: LinearLayoutCompat = itemView.findViewById(R.id.llQuotedMessage)

        fun bindView(message: MessageModel, position: Int, prevMsg: MessageModel?, nextMsg: MessageModel?) {
            AppLog.d(TAG, "Bind view with sender id: ${message.sender.id}")
            llQuotedMessage.visibility = if (message.quoted_message == null) View.GONE else View.VISIBLE
            message.quoted_message?.let { quotedMessage ->
                participants.firstOrNull {
                    it.id == quotedMessage.sender.id
                }?.let {
                    tvQuotedSenderName.text = it.displayingName()
                    civAvatar.loadAvatar(it.displayingName())
                }
                tvQuotedMessage.text = quotedMessage.data.value
            }
            tvDate.text = DateTimeUtils.timeHourMinutes(message.sent_ts)
            participants.find { it.id == message.sender.id }.apply {
                this?.let {
                    civAvatar.loadAvatar(it)
                    tvUserName.text = it.displayingName()
                }
            }
            civAvatar.setOnClickListener {
                onItemSelected(ACTION_RESPONDER_PROFILE, null, position, message)
            }

            if (prevMsg != null && prevMsg.sender == message.sender && message.sender.type != Constants.Message.CHAT_SENDER_BOT) {
                invisibleViews(civAvatar)
                hideViews(tvUserName)
            } else {
                showViews(civAvatar, tvUserName)
            }
            hideViews(tvForwarded, tvQuotedMessage)

            // Bot messages
            if (message.sender.type == Constants.Message.CHAT_SENDER_BOT) {
                tvUserName.text = itemView.context.getString(R.string.chatq_bot)
                civAvatar.setImage(R.drawable.bot_default)
                try {
                    val botMessageHeader = message.data.value.split(":").firstOrNull()
                    tvMessageReply.text = SpannableStringBuilder().bold { append("${botMessageHeader}: ") }.append(message.data.value.replace("${botMessageHeader}:", ""))
                } catch (e: Exception) {
                    AppLog.d(TAG, "Failed to set bot message header due to $e")
                }
            } else {
                tvMessageReply.text = message.data.value
            }

            // Quote, forward message
            message.quoted_message?.let { quotedMessage ->
                val quotedMessageValue = quotedMessage.data.value?.trim()
                if (quotedMessage.room_id == room.id) { // Quote message
                    llQuotedMessage.visibility = View.VISIBLE
                    participants.firstOrNull {
                        it.id == quotedMessage.sender.id
                    }?.let {
                        tvQuotedSenderName.text = it.displayingName()
                        civAvatar.loadAvatar(it)
                    }
                    tvQuotedMessage.text = quotedMessageValue
                    if (message.data.value?.trim() == "") {
                        tvMessageReply.visibility = View.GONE
                    }
                } else { // Forward message
                    tvForwarded.visibility = View.VISIBLE
                    tvMessageReply.text = quotedMessageValue
                }
            }
        }
    }
    inner class ResponderImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val civAvatar: CircularImageView = itemView.findViewById(R.id.civAvatar)
        private val ivChatImageLeft: CustomRoundedImageView = itemView.findViewById(R.id.ivChatImageLeft)
        private val pbImageLoad: ProgressBar = itemView.findViewById(R.id.pbImageLoad)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)

        fun bindView(message: MessageModel, position: Int, prevMsg: MessageModel?, nextMsg: MessageModel?) {
            if (room.category == Constants.CHAT_CATEGORY_PRIVATE) {
                Glide.with(itemView.context)
                    .load(room.avatar?.url)
                    .placeholder(buildLoader(itemView.context))
                    .error(R.drawable.ic_avatar_unknown)
                    .into(civAvatar)
                tvUserName.text = room.name
            } else {
                participants.find { it.id == message.sender.id }.apply {
                    this?.let {
                        civAvatar.loadAvatar(it)
                        tvUserName.text = it.displayingName()
                    }
                }
                civAvatar.setOnClickListener {
                    onItemSelected(ACTION_RESPONDER_PROFILE, null, position, message)
                }
            }
            if (prevMsg != null && prevMsg.sender == message.sender) {
                invisibleViews(civAvatar)
                hideViews(tvUserName)
            }
            Glide.with(itemView.context)
                .load(message.data.value_url)
                .override(600, 800)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.img_placeholder)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivChatImageLeft.setImage(R.drawable.img_placeholder)
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivChatImageLeft.setImageDrawable(resource)
                        return false
                    }
                }).into(ivChatImageLeft)
            tvDate.text = DateTimeUtils.timeHourMinutes(message.sent_ts)
            itemView.setOnClickListener {
                onItemSelected(ACTION_RESPONDER_IMAGE, null, position, message)
            }
        }
    }
    inner class ResponderChartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val civAvatar: CircularImageView = itemView.findViewById(R.id.civAvatar)
        private val ivChatImageLeft: CustomRoundedImageView = itemView.findViewById(R.id.ivChatImageLeft)
        private val pbImageLoad: ProgressBar = itemView.findViewById(R.id.pbImageLoad)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)

        fun bindView(message: MessageModel, position: Int, prevMsg: MessageModel?, nextMsg: MessageModel?) {
            Glide.with(itemView.context)
                .load(message.data.value_url)
                .override(800, 800)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.img_placeholder)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivChatImageLeft.setImage(R.drawable.img_placeholder)
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        pbImageLoad.hideView()
                        ivChatImageLeft.setImageDrawable(resource)
                        return false
                    }
                }).into(ivChatImageLeft)
            if (room.category == Constants.CHAT_CATEGORY_PRIVATE) {
                Glide.with(itemView.context)
                    .load(room.avatar?.url)
                    .placeholder(buildLoader(itemView.context))
                    .error(R.drawable.ic_avatar_unknown)
                    .into(civAvatar)
                tvUserName.text = room.name
            } else {
                participants.find { it.id == message.sender.id }.apply {
                    this?.let {
                        civAvatar.loadAvatar(it)
                        tvUserName.text = it.displayingName()
                    }
                }
                civAvatar.setOnClickListener {
                    onItemSelected(ACTION_RESPONDER_PROFILE, null, position, message)
                }
            }
            if (prevMsg != null && prevMsg.sender == message.sender) {
                invisibleViews(civAvatar)
                hideViews(tvUserName)
            }
            tvDate.text = DateTimeUtils.timeHourMinutes(message.sent_ts)
            itemView.setOnClickListener {
                onItemSelected(ACTION_RESPONDER_CHART, null, position, message)
            }
        }
    }

    // MARK: - Private Functions
    private fun buildLoader(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).also {
            it.setColorSchemeColors(Color.RED)
            it.strokeWidth = 5f
            it.centerRadius = 60f
            it.start()
        }
    }
}

// MARK: - MessageDiffCallback Class
class MessageDiffCallback: DiffUtil.ItemCallback<MessageModel>() {

    // MARK: - Override Functions
    override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return Objects.equal(oldItem, newItem)
    }
    override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return oldItem.id == newItem.id && oldItem.room_id == newItem.room_id
    }
}