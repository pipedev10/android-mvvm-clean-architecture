package com.pipe.cleanapp.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pipe.cleanapp.data.common.utils.WrappedResponse
import com.pipe.cleanapp.data.login.remote.dto.LoginRequest
import com.pipe.cleanapp.data.login.remote.dto.LoginResponse
import com.pipe.cleanapp.domain.common.base.BaseResult
import com.pipe.cleanapp.domain.login.entity.LoginEntity
import com.pipe.cleanapp.domain.login.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase): ViewModel() {
    private val state = MutableStateFlow<LoginActivityState>(LoginActivityState.Init)
    val mState : StateFlow<LoginActivityState> get() = state

    private fun setLoading(){
        state.value = LoginActivityState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = LoginActivityState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = LoginActivityState.ShowToast(message)
    }

    private fun successLogin(loginEntity: LoginEntity){
        state.value = LoginActivityState.SuccessLogin(loginEntity)
    }

    private fun errorLogin(rawResponse: WrappedResponse<LoginResponse>){
        state.value = LoginActivityState.ErrorLogin(rawResponse)
    }

    fun login(loginRequest: LoginRequest){
        viewModelScope.launch {
            loginUseCase.execute(loginRequest)
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showToast(exception.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when(result){
                        is BaseResult.Success -> successLogin(result.data)
                        is BaseResult.Error -> errorLogin(result.rawResponse)
                    }
                }
        }
    }
}

sealed class LoginActivityState {
    object Init : LoginActivityState()
    data class IsLoading(val isLoading: Boolean) : LoginActivityState()
    data class ShowToast(val message: String) : LoginActivityState()
    data class SuccessLogin(val loginEntity: LoginEntity) : LoginActivityState()
    data class ErrorLogin(val rawResponse: WrappedResponse<LoginResponse>) : LoginActivityState()
}