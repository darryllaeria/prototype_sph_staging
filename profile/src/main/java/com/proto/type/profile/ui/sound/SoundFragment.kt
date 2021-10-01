package com.proto.type.profile.ui.sound

import androidx.lifecycle.Observer
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import com.proto.type.profile.adapter.SoundAdapter
import com.proto.type.profile.adapter.SoundMenuItem
import kotlinx.android.synthetic.main.fragment_language.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SoundFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_sound
    private val viewModel: SoundViewModel by viewModel()
    private var adapter = SoundAdapter()

    override fun initView() {
        super.initView()

        rvMenus.adapter = adapter
        adapter.display(mutableListOf(
            SoundMenuItem("None"),
            SoundMenuItem("Note (Default)"),
            SoundMenuItem("Bell"),
            SoundMenuItem("Whistle"),
            SoundMenuItem("Melody")
        ))
    }

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        btnSave.setOnClickListener {
            showLoading()
            viewModel.saveSelectedSound(adapter.getSelectedSound())
            hideLoading()
            navigateBack()
        }
    }

    override fun initLogic() {
        super.initLogic()

        viewModel.soundData.observe(this, Observer {
            adapter.setSelectedSound(SoundMenuItem(it))
        })
        viewModel.loadSoundConfig()
    }
}