package com.proto.type.profile.ui.appearance

import android.util.TypedValue
import androidx.lifecycle.Observer
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_appearance.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppearanceFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_appearance
    private val viewModel: AppearanceViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        textSizeSeekbar.onSeekChangeListener = object: OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                updateTextPreview(seekParams!!.progressFloat)
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                updateTextPreview(seekBar!!.progressFloat)
                viewModel.setTextSize(seekBar.progressFloat)
            }
        }

        itvDarkMode.subscribeOnCheckChanged {
            viewModel.setDarkMode(it)
        }

        itvSuggestion.subscribeOnCheckChanged {
            viewModel.setSuggestionBar(it)
        }

        ipvWallpaper.setOnClickListener {
            navigateTo(R.id.action_appearance_to_wallpaper)
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.loadConfig()
        viewModel.appearanceConfigs.observe(this, Observer { config ->
            config?.let {
                itvDarkMode.setChecked(it.darkMode)
                itvSuggestion.setChecked(it.suggestionBar)
                textSizeSeekbar.setProgress(it.textSize)
            }
        })
    }

    private fun updateTextPreview(value: Float) {
        tvChatLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        tvChatRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
    }

}