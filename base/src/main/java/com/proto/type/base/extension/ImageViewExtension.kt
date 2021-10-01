package com.proto.type.base.extension

import android.content.Context
import android.widget.ImageView
import androidx.emoji.text.EmojiCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.proto.type.base.Constants
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.ui.widget.TextDrawable

// MARK: - ImageView
fun ImageView.loadAvatar(user: UserModel, shouldCircle: Boolean = true) {
    Glide.with(context!!).clear(this)
    if (user.avatar != null && user.avatar!!.url.isNotEmpty()) {
        if (shouldCircle) {
            Glide.with(context!!).load(user.avatar!!.url)
                .fitCenter()
                .circleCrop()
                .placeholder(buildLoader(context))
                .into(this)
        } else {
            Glide.with(context!!).load(user.avatar!!.url)
                .fitCenter()
                .placeholder(buildLoader(context))
                .into(this)
        }
    } else {
        val arr: List<String> = if (!user.display_name.isNullOrEmpty()) {
            user.display_name!!.split(" ")
        } else if (!user.first_name.isNullOrEmpty() && !user.last_name.isNullOrEmpty()) {
            listOf(user.first_name!!.first().toString(), user.last_name!!.first().toString())
        } else {
            listOf()
        }
        val abbr = loadEmoticonsText(arr)
        Glide.with(context)
            .load(TextDrawable(this, abbr))
            .skipMemoryCache(true)
            .fitCenter()
            .circleCrop()
            .placeholder(buildLoader(context))
            .into(this)
    }
}

fun ImageView.loadAvatar(displayName: String?) {
    val abbr = if (displayName.isNullOrEmpty()) {
        Constants.KEY_CHATQ_USER.getInitials(Constants.AVATAR_INITIALS_DEFAULT)
    } else {
        val words = displayName.split(" ".toRegex())
        loadEmoticonsText(words)
    }

    Glide.with(context)
        .load(TextDrawable(this, abbr))
        .fitCenter()
        .circleCrop()
        .placeholder(buildLoader(context))
        .into(this)
}

fun ImageView.loadAvatar(chat: ChatModel, shouldCircle: Boolean = true) {
    Glide.with(context!!).clear(this)
    if (chat.avatar != null && !chat.avatar!!.url.isNullOrEmpty()) {
        if (shouldCircle) {
            Glide.with(context!!).load(chat.avatar!!.url)
                .fitCenter()
                .circleCrop()
                .placeholder(buildLoader(context))
                .into(this)
        } else {
            Glide.with(context!!).load(chat.avatar!!.url)
                .fitCenter()
                .placeholder(buildLoader(context))
                .into(this)
        }
    } else {
        val arr: List<String> = if (!chat.name.isNullOrEmpty()) {
            chat.name!!.split(" ")
        } else {
            listOf()
        }
        val abbr = loadEmoticonsText(arr)
        Glide.with(context)
            .load(TextDrawable(this, abbr))
            .fitCenter()
            .circleCrop()
            .placeholder(buildLoader(context))
            .into(this)
    }
}

fun ImageView.setImage(resId: Int) {
    setImageResource(resId)
    tag = resId
}

// MARK: - Private Functions
private fun buildLoader(context: Context): CircularProgressDrawable {
    val circle = CircularProgressDrawable(context)
    circle.strokeWidth = 10f
    circle.setStyle(CircularProgressDrawable.LARGE)
    circle.start()
    return circle
}

private fun loadEmoticonsText(words: List<String>): String {
    val emoticon = EmojiCompat.get()
    return if (words.size > 1) {
        if (emoticon.loadState == EmojiCompat.LOAD_STATE_SUCCEEDED) {
            val first = if (emoticon.hasEmojiGlyph(words[0])) {
                emoticon.process(words[0])
            } else {
                words[0][0]
            }
            val second = if (emoticon.hasEmojiGlyph(words[1])) {
                emoticon.process(words[1])
            } else {
                words[1][0]
            }
            "$first$second"
        } else {
            "${words[0].substring(0, 1)}${words[1].substring(0, 1)}"
        }
    } else if (words.size == 1){
        if (emoticon.loadState == EmojiCompat.LOAD_STATE_SUCCEEDED && emoticon.hasEmojiGlyph(words[0])) {
            "${emoticon.process(words[0])}"
        } else {
            words[0].substring(0, 1)
        }
    } else {
        Constants.KEY_CHATQ_USER.getInitials(Constants.AVATAR_INITIALS_DEFAULT)
    }
}