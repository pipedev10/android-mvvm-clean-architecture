package com.pipe.cleanapp.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pipe.cleanapp.R
import com.pipe.cleanapp.data.common.utils.WrappedResponse
import com.pipe.cleanapp.data.login.remote.dto.LoginRequest
import com.pipe.cleanapp.data.login.remote.dto.LoginResponse
import com.pipe.cleanapp.databinding.ActivityLoginBinding
import com.pipe.cleanapp.domain.login.entity.LoginEntity
import com.pipe.cleanapp.infra.utils.SharedPrefs
import com.pipe.cleanapp.presentation.common.extension.isEmail
import com.pipe.cleanapp.presentation.common.extension.showGenericAlertDialog
import com.pipe.cleanapp.presentation.common.extension.showToast
import com.pipe.cleanapp.presentation.main.MainActivity
import com.pipe.cleanapp.presentation.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel : LoginViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPrefs

    private val openRegisterActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            goToMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        login()
        observe()
    }

    private fun observe(){
        viewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: LoginActivityState) {
        when(state){
            is LoginActivityState.ShowToast -> showToast(state.message)
            is LoginActivityState.IsLoading -> handleLoading(state.isLoading)
            is LoginActivityState.Init -> Unit
            is LoginActivityState.ErrorLogin -> handleErrorLogin(state.rawResponse)
            is LoginActivityState.SuccessLogin -> handleSuccessLogin(state.loginEntity)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if (!isLoading){
            binding.loadingProgressBar.progress = 0
        }
    }

    private fun handleSuccessLogin(loginEntity: LoginEntity) {
        pref.saveToken(loginEntity.token)
        goToMainActivity()
    }

    private fun goToMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goToRegisterActivity(){
        binding.registerButton.setOnClickListener{
            openRegisterActivity.launch(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun handleErrorLogin(rawResponse: WrappedResponse<LoginResponse>) {
        showGenericAlertDialog(rawResponse.message)
    }

    private fun login(){
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if(validate(email, password)){
                val loginRequest = LoginRequest(email, password)
                viewModel.login(loginRequest)
            }
        }
    }

    private fun setEmailError(e: String?){
        binding.emailInput.error = e
    }

    private fun setPasswordError(e: String?){
        binding.passwordInput.error = e
    }

    private fun resetAllError(){
        setEmailError(null)
        setPasswordError(null)
    }

    private fun validate(email: String, password: String): Boolean {
        resetAllError()

        if (!email.isEmail()){
            setEmailError(getString(R.string.error_email_not_valid))
            return false
        }

        if(password.length < 8){
            setPasswordError(getString(R.string.error_password_not_valid))
            return false
        }

        return true
    }
}