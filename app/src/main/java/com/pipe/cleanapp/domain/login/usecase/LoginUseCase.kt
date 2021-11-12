package com.pipe.cleanapp.domain.login.usecase

import com.pipe.cleanapp.data.common.utils.WrappedResponse
import com.pipe.cleanapp.data.login.remote.dto.LoginRequest
import com.pipe.cleanapp.data.login.remote.dto.LoginResponse
import com.pipe.cleanapp.domain.common.base.BaseResult
import com.pipe.cleanapp.domain.login.LoginRepository
import com.pipe.cleanapp.domain.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val loginRepository: LoginRepository) {
    suspend fun execute(loginRequest: LoginRequest) : Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>> {
        return loginRepository.login(loginRequest)
    }
}