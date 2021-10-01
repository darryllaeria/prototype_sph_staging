package com.proto.type.profile.ui.appearance.wallpaper

import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import com.proto.type.profile.adapter.Wallpaper
import com.proto.type.profile.adapter.WallpaperAdapter
import kotlinx.android.synthetic.main.fragment_wallpaper.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WallpaperFragment: BaseFragment() {
    override var layoutId = R.layout.fragment_wallpaper
    private val viewModel: WallpaperViewModel by viewModel()

    private var adapter = WallpaperAdapter {
        viewModel.setWallpaper(it)
    }

    override fun initView() {
        super.initView()

        rvBackground.adapter = adapter
        adapter.display(listOf(Wallpaper(), Wallpaper(), Wallpaper()))
    }

    override fun initEvent() {
        super.initEvent()
        ivBack.setOnClickListener {
            navigateBack()
        }
    }

    override fun initLogic() {
        super.initLogic()

        viewModel.loadSelectedBackground()
    }

}