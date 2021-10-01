package com.proto.type.main.ui.signup.name

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import com.proto.type.main.R
import com.proto.type.base.base_component.BaseFragment
import kotlinx.android.synthetic.main.fragment_signup_name.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @details Signup name fragment
 */

class SignUpNameFragment : BaseFragment() {

    override var layoutId = R.layout.fragment_signup_name
    private val viewModel: SignUpNameViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        checkEnableButton()
        etFirstName.apply {
            addTextChangedListener(textWatcher)
        }
        etLastName.apply {
            addTextChangedListener(textWatcher)
            setOnEditorActionListener { _, actionId, event ->
                if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btContinue.performClick()
                }
                false
            }
        }

        ivBack.setOnClickListener {
            navigateBack()
        }
        btContinue.setOnClickListener {
            viewModel.storeUser(etFirstName.text.toString(), etLastName.text.toString())
            navigateTo(R.id.action_signup_name_to_email)
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.loadSaveForm()
//        viewModel.signUpForm.observe(this, Observer {
//            etFirstName.setText(it.firstName)
//            etLastName.setText(it.lastName)
//        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
//        viewModel.signUpForm.removeObservers(this)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            checkEnableButton()
        }
    }

    private fun checkEnableButton() {
        btContinue.isEnabled = !(etFirstName.text.toString().isEmpty() || etLastName.text.toString().isEmpty())
    }
}