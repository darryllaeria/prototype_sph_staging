package com.proto.type.chat.creategroup

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.ui.dialog.ImagePickerDialog
import com.proto.type.base.utils.RuntimePermissionCallback
import com.proto.type.base.utils.RuntimePermissionUtils
import com.proto.type.base.utils.Utils
import com.proto.type.chat.R
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.fragment_create_group.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class CreateGroupFragment : BaseFragment() {

    //MARK: - Override Variable
    override var layoutId = R.layout.fragment_create_group


    // MARK: - Private Constants
    private val isAddFriend: Boolean by lazy { arguments?.getBoolean(Constants.KEY_IS_ADD_FRIEND) ?: false }
    private val isSelfChat: Boolean by lazy { arguments?.getBoolean(Constants.KEY_IS_SELF_CHAT) ?: false }
    private val participantIds: ArrayList<String> by lazy { arguments?.getStringArrayList(Constants.KEY_CHAT_PARTICIPANTS_IDS) ?: arrayListOf() }
    private val viewModel: CreateGroupViewModel by viewModel()

    // MARK: - Private Variables
    private var avatarUri: Uri? = null
    private lateinit var createGroupListingAdapter: CreateGroupListingAdapter


    override fun initView() {
        super.initView()
        tvNameCounter.text = String.format(getString(R.string.group_name_max_length), 0, Constants.CHAT_GROUP_NAME_MAX_LENGTH)
        tvParticipantCount.text = String.format(getString(R.string.participants_title), 0)
        Utils.setEditTextInputFilter(etGroupName, Constants.CHAT_GROUP_NAME_MAX_LENGTH)
    }

    override fun initEvent() {
        super.initEvent()

        ivChangeAvatar.setOnClickListener {
            showImagePickerMenu()
        }

        ivCross.setOnClickListener {
            etGroupName.setText("")
        }

        toolbar.apply {
            addOnBackClickListener {
                navigateBack()
            }

            addOnRightTextClickListener {
                if (etGroupName.text.isNotEmpty()) {
                    viewModel.createChatRoom(avatarUri, etGroupName.text.toString(), participantIds)
                }
            }
        }

        etGroupName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tvNameCounter.text = String.format(getString(R.string.group_name_max_length), s.length, Constants.CHAT_GROUP_NAME_MAX_LENGTH)
            }
        })
    }

    override fun initLogic() {
        super.initLogic()
        setUserList()
        viewModel.getUserList(isSelfChat, participantIds)
        viewModel.users.observe(this, Observer {
            when (it) {
                is UIState.FAILED -> {
                    showInfo(getString(R.string.txt_unexpected_error))
                }
                is UIState.FINISHED<*> -> {
                    (it.data as? MutableList<UserModel>)?.let { users ->
                        tvParticipantCount.text = String.format(getString(R.string.participants_title), users.size)
                        createGroupListingAdapter.setData(users)
                    }
                }
            }
        })
        viewModel.newChat.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    showLoading(true)
                }
                is UIState.FINISHED<*> -> {
                    hideLoading()
                    val chat = it.data as? ChatModel
                    if (isAddFriend) {
                        navigateTo(
                            R.id.action_createGroupFragment_to_inboxFragment,
                            bundleOf(Constants.KEY_OPEN_ROOM_ID to chat?.id)
                        )
                    } else {
                        navigateTo(Uri.parse("${Constants.Uri.URI_NEW_CHAT}/${(it.data as? ChatModel)?.id}/${Constants.Uri.URI_INBOX}"))
                    }
                }
                is UIState.FAILED -> {
                    hideLoading()
                    showInfo(getString(R.string.txt_unexpected_error))
                }
                else -> {
                    hideLoading()
                }
            }
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.newChat.removeObservers(this)
        viewModel.users.removeObservers(this)
    }

    private fun setUserList() {
        createGroupListingAdapter = CreateGroupListingAdapter(
            context!!,
            mutableListOf()
        ) { action, _, position ->
            when (action) {
                CreateGroupListingAdapter.Actions.ACTION_REMOVE -> {
                    createGroupListingAdapter.removeItem(position)
                    viewModel.setRemoveUser(position)
                }
            }
        }
        rvParticipants.adapter = createGroupListingAdapter
    }

    private fun showImagePickerMenu() {
        ImagePickerDialog(shouldShowDeleteOption = false) {action ->
            when (action) {
                ImagePickerDialog.ACTION_TAKE_PICTURE -> {
                    RuntimePermissionUtils.requestPermission(requireActivity(),
                        listOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        object : RuntimePermissionCallback {
                            override fun onGranted() { runCamera() }
                            override fun onFailed() {}
                        })
                }
                ImagePickerDialog.ACTION_PICK_PHOTO -> {
                    RuntimePermissionUtils.requestPermission(requireActivity(), listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE),
                        object: RuntimePermissionCallback {
                            override fun onGranted() {
                                Matisse.from(this@CreateGroupFragment)
                                    .choose(MimeType.ofImage())
                                    .countable(true)
                                    .maxSelectable(1)
                                    .theme(R.style.Matisse_Zhihu)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(GlideEngine())
                                    .showPreview(true)
                                    .autoHideToolbarOnSingleTap(true)
                                    .forResult(Constants.Request.REQUEST_CODE_CHOOSE)
                            }

                            override fun onFailed() {
                            }
                        })
                }
            }
        }.show(childFragmentManager, "picker")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.Request.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && data != null) {
            avatarUri = Matisse.obtainResult(data)[0]
        }

        if (avatarUri != null) {
            Glide.with(this)
                .load(avatarUri)
                .into(ivGroupIcon)
        } else {
            showInfo(getString(R.string.txt_unexpected_error))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // MARK: - Private Function
    private fun runCamera() {
        val path = File(requireActivity().filesDir, "images")
        val image = File(File(requireActivity().filesDir, "images"), "${System.currentTimeMillis()}.jpg")
        path.mkdir()
        avatarUri = FileProvider.getUriForFile(context!!, "com.proto.type", image)
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri)
            activity?.packageManager?.let {
                takePictureIntent.resolveActivity(it)?.also {
                    startActivityForResult(takePictureIntent, Constants.Request.REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}