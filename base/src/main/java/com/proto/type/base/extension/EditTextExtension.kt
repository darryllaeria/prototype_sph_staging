package com.proto.type.base.extension

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.core.text.toSpannable
import com.proto.type.base.HighlightParameters
import com.proto.type.base.HighlightObject
import com.proto.type.base.TextSuggestion
import com.proto.type.base.data.database.entity.UserEntity
import com.proto.type.base.data.database.entity.market_data.AssetExchangeEntity
import com.proto.type.base.data.model.UserModel
import kotlinx.coroutines.*

// MARK: - EditText
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun EditText.hideKeyboard(time: Long = 0L) {
    CoroutineScope(Dispatchers.Main).launch {
        delay(time)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

fun EditText.getHighlightObjects(
    highlightParametersList: List<HighlightParameters>,
    normalForegroundColor: Int,
    currentHighlightObjects: List<HighlightObject>
): Pair<Spannable, List<HighlightObject>> {
    if (highlightParametersList.isEmpty()) // Don't do anything if there is no parameter to highlight
        return Pair(this.text.toSpannable(), listOf())

    // Setup initial values
    val highlightedObjects = mutableListOf<HighlightObject>()
    val currentText = this.text.toString()
    var spannableText = this.text.toSpannable()

    // Reset all existing spans in the current EditText
    spannableText.getSpans<ForegroundColorSpan>().forEach { spannableText.removeSpan(it) }
    spannableText.getSpans<UnderlineSpan>().forEach { spannableText.removeSpan(it) }
    spannableText.setSpan(
        ForegroundColorSpan(normalForegroundColor),
        0,
        currentText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    // Apply new spans per new highlighting parameters for TextSuggestion and UserModel
    for (highlightParameters in highlightParametersList) {
        val sortedDataSource = highlightParameters.dataSource.sortedWith(Comparator { lhs, rhs ->
            if (lhs is TextSuggestion && rhs is TextSuggestion)
                if (lhs.name.length > rhs.name.length) -1 else 1
            else
                0
        })
        for (dataItem in sortedDataSource) {
            when (dataItem) {
                is TextSuggestion -> {
                     val checkingString = highlightParameters.prefix + dataItem.name
                    rangeChecker@ for (range in currentText.ranges(checkingString)) {
                        val highlightedObject = highlightedObjects.firstOrNull { range.isOverlap(it.range) }
                        if (highlightedObject != null) {
                            if (highlightedObject.range.count() >= range.count()) continue@rangeChecker
                            val index = highlightedObjects.indexOfFirst { it.id == highlightedObject.id }
                            if (index != -1) { // Found this object, -1 means not found
                                spannableText.setSpan(
                                    ForegroundColorSpan(normalForegroundColor),
                                    highlightedObject.range.first,
                                    highlightedObject.range.last + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                highlightedObjects.removeAt(index)
                            }
                        }
                        spannableText.setSpan(
                            ForegroundColorSpan(highlightParameters.highlightedForegroundColor),
                            range.first,
                            range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        if (highlightParameters.shouldUnderLine) {
                            spannableText.setSpan(
                                UnderlineSpan(),
                                range.first,
                                range.last + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        highlightedObjects.add(HighlightObject(dataItem.id, range, AssetExchangeEntity.TAG, dataItem.name))
                    }
                }
                is UserModel -> {
                    val checkingString = highlightParameters.prefix + dataItem.display_name
                    rangeChecker@ for (range in currentText.ranges(checkingString)) {
                        val highlightedObject = highlightedObjects.firstOrNull { range.isOverlap(it.range) }
                        if (highlightedObject != null) {
                            if (highlightedObject.range.count() >= range.count()) continue@rangeChecker
                            val index = highlightedObjects.indexOfFirst { it.id == highlightedObject.id }
                            if (index != -1) { // Found this object, -1 means not found
                                spannableText.setSpan(
                                    ForegroundColorSpan(normalForegroundColor),
                                    highlightedObject.range.first,
                                    highlightedObject.range.last + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                highlightedObjects.removeAt(index)
                            }
                        }
                        spannableText.setSpan(
                            ForegroundColorSpan(highlightParameters.highlightedForegroundColor),
                            range.first,
                            range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        if (highlightParameters.shouldUnderLine) {
                            spannableText.setSpan(
                                UnderlineSpan(),
                                range.first,
                                range.last + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        highlightedObjects.add(HighlightObject(dataItem.id, range, UserEntity.TAG, dataItem.display_name))
                    }
                }
            }
        }
    }

    // Check if the new text has one highlightedObject removed, if yes then clear that object from the spannable string
    if (highlightedObjects.size == currentHighlightObjects.size - 1) {
        var removedHighlightedObject = HighlightObject()
        for (highlightedObject in currentHighlightObjects) {
            if (highlightedObjects.firstOrNull { it.id == highlightedObject.id && it.range == highlightedObject.range } == null) {
                removedHighlightedObject = highlightedObject
                break
            }
        }
        if (removedHighlightedObject.id.isNotEmpty()) {
            val removedLastCharString = removedHighlightedObject.value.substring(0, removedHighlightedObject.value.length - 1)
            currentText.lastRange(if (removedHighlightedObject.type == UserEntity.TAG) "@${removedLastCharString}" else removedLastCharString)?.let {
                spannableText = spannableText.removeRange(it).toSpannable()
            }
        }
    }
    return Pair(spannableText, highlightedObjects)
}

fun EditText.replaceLast(toReplaceText: String, replaceText: String) {
    val currentText = this.text.toString()
    currentText.lastRange(toReplaceText)?.let {
        this.setText(this.text.replaceRange(it, replaceText))
        this.setSelection(this.length())
    }
}

fun EditText.showKeyboard(time: Long = 0L) {
    val view = this
    CoroutineScope(Dispatchers.Main).launch {
        delay(time)
        if (requestFocus()) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}

fun EditText.setupCursorEvents() {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            isCursorVisible = false
            true
        }
        false
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) { hideKeyboard() }
        isCursorVisible = hasFocus
    }
}