package com.proto.type.home.ui.on_boarding

import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.base_component.BaseViewModel

/**
 * @Details view model for on-board screen
 */

class OnBoardViewModel(private val localRepository: ILocalRepository): BaseViewModel() {

    fun signUp() {
        localRepository.setSignUpAuth()
    }

    fun signIn() {
        localRepository.setSignInAuth()
    }

}