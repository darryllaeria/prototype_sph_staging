package com.proto.type.chat.sharedmedia

import LinksListingAdapter
import MediaListingAdapter
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.Constants.DEFAULT_MESSAGE_ID
import com.proto.type.base.Constants.INITIAL_MORE_DATA
import com.proto.type.base.Constants.INITIAL_MORE_DATA_AND_MERGE
import com.proto.type.base.Constants.INITIAL_NO_MORE_DATA
import com.proto.type.base.Constants.LOADMORE_MORE_DATA
import com.proto.type.base.Constants.LOADMORE_NO_MORE_DATA
import com.proto.type.base.Constants.UPDATE_DATA_ITEM
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.MessageLinkItemModel
import com.proto.type.base.data.model.MessageMediaItemModel
import com.proto.type.base.ui.dialog.FullScreenPhotoDialog
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.fragment_shared_media.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SharedMediaFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_shared_media

    private val roomId: String by lazy { arguments?.getString(Constants.KEY_ROOM_ID) ?: "" }
    private val viewModel: SharedMediaViewModel by viewModel()

    private lateinit var linksListingAdapter: LinksListingAdapter
    private lateinit var mediaListingAdapter: MediaListingAdapter
    private lateinit var pagerAdapter: SharedMediaPagerAdapter

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initEvent() {
        super.initEvent()

        toolbar.apply {
            addOnBackClickListener {
                navigateBack()
            }
        }
    }

    override fun initLogic() {
        super.initLogic()

        // Setup links
        viewModel.linksList.observe(this, linksListObserver)
        viewModel.getAllMessageLinkItemsFromRealm(roomId)
        linksListingAdapter = LinksListingAdapter(context!!, mutableListOf()) { action, linkItem, position ->
            when (action) {
                LinksListingAdapter.Actions.ACTION_SELECT -> {
                    navigateTo(
                        R.id.action_shareMediaFragment_to_chatQWebViewFragment,
                        bundleOf(Constants.KEY_LINK_URL to linkItem.link)
                    )
                }

                LinksListingAdapter.Actions.ACTION_UPDATE -> {
                    viewModel.loadMetadata(linkItem, position)
                }
            }
        }

        // Setup medias
        viewModel.mediaList.observe(this, mediaListObserver)
        viewModel.getAllMessageMediaItemsFromRealm(roomId)
        mediaListingAdapter = MediaListingAdapter(
            context!!,
            mutableListOf()
        ) { action, mediaItem, _ ->
            when (action) {
                MediaListingAdapter.Actions.ACTION_SELECT -> {
                    FullScreenPhotoDialog(mediaItem.data.url, false).show(childFragmentManager, "fullscreen_photo")
                }
            }
        }

        // Setup tab pagers
        pagerAdapter = SharedMediaPagerAdapter(
            linksListingAdapter, mediaListingAdapter,
            arrayOf(getString(R.string.photos), getString(R.string.links)),
            onLoadMoreMediaItems = {
                if (mediaListingAdapter.isLastPage || mediaListingAdapter.isLoading) return@SharedMediaPagerAdapter
                mediaListingAdapter.isLoading = true
                viewModel.loadMoreMessageMediaItems(roomId)
            },
            onLoadMoreLinkItems = {
                if (linksListingAdapter.isLastPage || linksListingAdapter.isLoading) return@SharedMediaPagerAdapter
                linksListingAdapter.isLoading = true
                viewModel.loadMoreMessageLinkItems(roomId)
            }
        )
        viewPager.adapter = pagerAdapter
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.linksList.removeObservers(this)
        viewModel.mediaList.removeObservers(this)
    }

    // MARK: - Private Observers
    private var linksListObserver = Observer<UIState> { state ->
        when (state) {
            is UIState.FINISHED<*> -> {
                linksListingAdapter.isLastPage = false
                linksListingAdapter.isLoading = false
                val linkItems = (state.data as? MutableList<*>)?.filterIsInstance<MessageLinkItemModel>()?.toMutableList() ?: mutableListOf()
                when (state.messageId) {
                    INITIAL_MORE_DATA -> {
                        linksListingAdapter.setData(linkItems)
                    }
                    INITIAL_MORE_DATA_AND_MERGE -> {
                        linksListingAdapter.addData(linkItems)
                    }
                    INITIAL_NO_MORE_DATA -> {
                        linksListingAdapter.isLastPage = true
                        linksListingAdapter.setData(linkItems)
                    }
                    LOADMORE_MORE_DATA -> {
                        linksListingAdapter.addData(linkItems)
                    }
                    LOADMORE_NO_MORE_DATA -> {
                        linksListingAdapter.isLastPage = true
                        linksListingAdapter.addData(linkItems)
                    }
                    UPDATE_DATA_ITEM -> {
                        linksListingAdapter.setData(linkItems)
                    }
                    DEFAULT_MESSAGE_ID -> {
                        linksListingAdapter.isLoading = true
                        linksListingAdapter.setData(linkItems)
                        viewModel.getInitialMessageLinkItems(roomId)
                    }
                }
            }
            is UIState.FAILED -> {
                linksListingAdapter.isLastPage = false
                linksListingAdapter.isLoading = false
                when (state.messageId) {
                    DEFAULT_MESSAGE_ID -> {
                        linksListingAdapter.isLoading = true
                        viewModel.getInitialMessageLinkItems(roomId)
                    }
                }
            }
            else -> {}
        }
    }

    private var mediaListObserver = Observer<UIState> { state ->
        when (state) {
            is UIState.FINISHED<*> -> {
                mediaListingAdapter.isLastPage = false
                mediaListingAdapter.isLoading = false
                val mediaItems = (state.data as? MutableList<*>)?.filterIsInstance<MessageMediaItemModel>()?.toMutableList() ?: mutableListOf()
                when (state.messageId) {
                    INITIAL_MORE_DATA -> {
                        mediaListingAdapter.setData(mediaItems)
                    }
                    INITIAL_MORE_DATA_AND_MERGE -> {
                        mediaListingAdapter.addData(mediaItems)
                    }
                    INITIAL_NO_MORE_DATA -> {
                        mediaListingAdapter.isLastPage = true
                        mediaListingAdapter.setData(mediaItems)
                    }
                    LOADMORE_MORE_DATA -> {
                        mediaListingAdapter.addData(mediaItems)
                    }
                    LOADMORE_NO_MORE_DATA -> {
                        mediaListingAdapter.isLastPage = true
                        mediaListingAdapter.addData(mediaItems)
                    }
                    DEFAULT_MESSAGE_ID -> {
                        mediaListingAdapter.isLoading = true
                        mediaListingAdapter.setData(mediaItems)
                        viewModel.getInitialMessageMediaItems(roomId)
                    }
                }
            }
            is UIState.FAILED -> {
                mediaListingAdapter.isLastPage = false
                mediaListingAdapter.isLoading = false
                when (state.messageId) {
                    DEFAULT_MESSAGE_ID -> {
                        mediaListingAdapter.isLoading = true
                        viewModel.getInitialMessageMediaItems(roomId)
                    }
                }
            }
            else -> {}
        }
    }
}