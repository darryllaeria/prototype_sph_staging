package com.proto.type.profile.ui.phone.changenumber

import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_change_phone.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPhoneNumberFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_change_phone
    private val viewModel: NewPhoneViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        etMobileNumber.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btSave.isEnabled = s.toString().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        btSave.setOnClickListener {
//            navigateTo(R.id.action_change_phone_to_otp, bundleOf(Constants.KEY_PHONE to countryCodePicker.selectedCountryCodeWithPlus + etMobileNumber.text.toString()))
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.getCountryCode().observe(this, Observer {
//            countryCodePicker.setCountryForNameCode(it)
        })
    }
}