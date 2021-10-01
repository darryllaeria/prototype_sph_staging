package com.proto.type.main

import com.proto.type.main.ui.login.email.EmailViewModel
import com.proto.type.main.ui.login.forgot_password.ForgotPasswordViewModel
import com.proto.type.main.ui.on_boarding.OnBoardViewModel
import com.proto.type.main.ui.signup.email.SignUpEmailViewModel
import com.proto.type.main.ui.signup.mobile.SignUpMobileViewModel
import com.proto.type.main.ui.signup.name.SignUpNameViewModel
import com.proto.type.main.ui.signup.password.CreatePasswordViewModel
import com.proto.type.main.ui.signup.term.TermOfServiceViewModel
import com.proto.type.main.ui.signup.username.UsernameViewModel
import com.proto.type.main.ui.signup.verify.EmailVerifyViewModel
import com.proto.type.main.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { CreatePasswordViewModel(get(), get()) }
    viewModel { EmailVerifyViewModel(get()) }
    viewModel { EmailViewModel(get(), get(), get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { OnBoardViewModel(get()) }
    viewModel { SignUpEmailViewModel(get(), get()) }
    viewModel { SignUpMobileViewModel(get(), get()) }
    viewModel { SignUpNameViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { TermOfServiceViewModel(get(), get(), get()) }
    viewModel { UsernameViewModel(get(), get(), get()) }
}