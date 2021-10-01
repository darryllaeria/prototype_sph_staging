package com.proto.type.profile.ui.profileTab

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.ui.dialog.FullScreenPhotoDialog
import com.proto.type.base.ui.dialog.ImagePickerDialog
import com.proto.type.base.utils.AppLog
import com.proto.type.base.utils.EmailUtils
import com.proto.type.base.utils.RuntimePermissionCallback
import com.proto.type.base.utils.RuntimePermissionUtils
import com.proto.type.profile.R
import com.proto.type.profile.dialog.LogoutDialog
import com.proto.type.profile.ui.email.request.EmailVerifyFragment
import com.proto.type.profile.ui.status.StatusFragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.fragment_profile_tab.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ProfileTabFragment: BaseFragment() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ProfileTabFragment::class.java.simpleName
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_profile_tab

    // MARK: - Private Variables
    private val viewModel: ProfileTabViewModel by viewModel()
    private var avatarUri: Uri? = null

    // MARK: - Override Functions
    override fun initEvent() {
        super.initEvent()

        civAvatar.setOnClickListener {
            FullScreenPhotoDialog(viewModel.getLocalUser(), false).show(childFragmentManager, "fullscreen_photo")
        }

        toolbar.apply {
            addOnBackProfileClickListener {
                pnvUser.toggleDone()
            }

            addOnSaveClickListener {
                showLoading()
                viewModel.updateDisplayName(pnvUser.getValue())
            }
        }

        ivEditImage.setOnClickListener {
            ImagePickerDialog { action ->
                when (action) {
                    ImagePickerDialog.ACTION_TAKE_PICTURE -> {
                        RuntimePermissionUtils.requestPermission(requireActivity(),
                            listOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            object : RuntimePermissionCallback {
                                override fun onGranted() {
                                    runCamera()
                                }

                                override fun onFailed() { }
                            })
                    }
                    ImagePickerDialog.ACTION_PICK_PHOTO -> {
                        RuntimePermissionUtils.requestPermission(requireActivity(), listOf(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE),
                            object: RuntimePermissionCallback {
                                override fun onGranted() {
                                    Matisse.from(this@ProfileTabFragment)
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
                                override fun onFailed() { }
                            })
                    }
                    ImagePickerDialog.ACTION_REMOVE_AVATAR -> {
                        civAvatar.setBackgroundResource(R.drawable.ic_group_placeholder)
                    }
                }
            }.show(childFragmentManager, "picker")
        }

        pnvUser.subscribeModeChange { isEditMode ->
            toolbar.enterEditMode(isEditMode)
        }

        ipvStatus.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_status, bundleOf(StatusFragment.KEY_STATUS to ipvStatus.getValue()))
        }

        ipvLanguage.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_language)
        }

        ipvPrivacy.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_privacy)
        }

        ipvId.addOnTouchListener {
            if (ipvId.getValue().isNotEmpty()) {
                navigateTo(
                    R.id.action_profileTabFragment_to_username,
                    bundleOf(Constants.KEY_USER_ID to ipvId.getValue().substring(1))
                )
            } else {
                navigateTo(R.id.action_profileTabFragment_to_username)
            }
        }

        ipvPhone.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_request_change_phone)
        }

        ipvNotification.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_notification)
        }

        ipvAppearance.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_appearance)
        }

        ipvDelete.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_delete)
        }

        ipvAbout.addOnTouchListener {
            navigateTo(R.id.action_profileTabFragment_to_about)
        }

        ipvReport.addOnTouchListener {
            EmailUtils.sendEmail(requireActivity(), getString(R.string.report_app), getString(R.string.mail_title))
        }

        ipvImprove.addOnTouchListener {
            EmailUtils.sendEmail(requireActivity(), getString(R.string.improve_app), getString(R.string.mail_title))
        }

        tvLogout.setOnClickListener {
            LogoutDialog {
                showLoading()
                viewModel.logout().observe(this, Observer {
                    hideLoading()
                    if (it) {
                        viewModel.clearFirebaseToken()
                        viewModel.cleanupMQTTSubscriptions()
                        clearCached()
                        navigateTo(Uri.parse(Constants.Uri.URI_ONBOARD))
                    } else {
                        AppLog.d(TAG, "Failed to logout due to Firebase logout unsuccessfully")
                    }
                })
            }.show(childFragmentManager, "logout")
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.isVerifiedEmail.observe(this,  Observer {
            when (it) {
                is UIState.FINISHED<*> -> {
                    if (it.data as? Boolean != true) {
                        ipvEmail.showNotification()
                        ipvEmail.setOnClickListener {
                            navigateTo(R.id.action_profileTabFragment_to_verify_email, bundleOf(
                                EmailVerifyFragment.KEY_EMAIL to ipvEmail.getValue()))
                        }
                    } else {
                        ipvEmail.hideNotification()
                        ipvEmail.setOnClickListener {
                            // TODO("Navigate to change email")
                        }
                    }
                }
                else -> {}
            }
        })
        viewModel.localUser.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    showLoading()
                }
                is UIState.FINISHED<*> -> {
                    hideLoading()
                    (it.data as? UserModel)?.let { user ->
                        civAvatar.loadAvatar(user)
                        pnvUser.toggleDone()
                        pnvUser.setValue(user.display_name)
                        ipvStatus.setValue(user.bio)
                        ipvEmail.setValue(user.email)
                        ipvPhone.setValue(user.phone_number)
                        if (user.username?.isNotEmpty() == true) ipvId.setValue("@${user.username}")
                    }
                }
                is UIState.FAILED -> {
                    showInfo(getString(it.messageId))
                }
            }
        })
        viewModel.loadProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.Request.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && data != null) {
            avatarUri = Matisse.obtainResult(data)[0]
        }
        if (avatarUri != null) {
            Glide.with(requireContext())
                .load(avatarUri)
                .into(civAvatar)
            viewModel.updateAvatar(avatarUri!!)
        } else {
            showInfo(getString(R.string.txt_err_update_avatar))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.localUser.removeObservers(this)
        viewModel.isVerifiedEmail.removeObservers(this)
    }

    // MARK: - Private Functions
    private fun clearCached() {
        CoroutineScope(Dispatchers.IO).launch {
            Glide.get(requireContext()).clearDiskCache()
        }
    }

    private fun runCamera() {
        val path = File(requireActivity().filesDir, "images")
        val image = File(File(requireActivity().filesDir, "images"), "${System.currentTimeMillis()}.jpg")
        path.mkdir()
        avatarUri = FileProvider.getUriForFile(requireContext(), "com.proto.type", image)
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