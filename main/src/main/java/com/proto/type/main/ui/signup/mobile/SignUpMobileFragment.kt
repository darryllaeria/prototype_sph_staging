package com.proto.type.main.ui.signup.mobile

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.proto.type.main.R
import com.proto.type.base.base_component.BaseFragment
import kotlinx.android.synthetic.main.fragment_signup_mobile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpMobileFragment : BaseFragment() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = SignUpMobileFragment::class.java.simpleName
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_signup_mobile

    // MARK: - Private Constants
    private val viewModel: SignUpMobileViewModel by viewModel()

    // MARK: - Override Functions
    override fun initEvent() {
        super.initEvent()

        etMobileNumber.addTextChangedListener(textWatcher)

        btContinue.apply {
            setOnClickListener {
//                viewModel.storePhoneNumber(countryCodePicker.selectedCountryCodeWithPlus + etMobileNumber.text.toString())
                navigateTo(R.id.action_signup_mobile_to_chatq_id)
            }
            isEnabled = etMobileNumber.text.toString().isNotEmpty()
        }
        ivBack.setOnClickListener {
            navigateBack()
        }

        tvChangeCountryCode.setOnClickListener {
//            countryCodePicker.performClick()
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.getCountryCode()
        viewModel.countryCode.observe(this, Observer {
//            countryCodePicker.setCountryForNameCode(it)
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.countryCode.removeObservers(this)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            btContinue.isEnabled = etMobileNumber.text.toString().isNotEmpty()
        }
    }
}

