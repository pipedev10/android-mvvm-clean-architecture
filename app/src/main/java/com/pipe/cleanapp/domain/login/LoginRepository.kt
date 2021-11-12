package com.pipe.cleanapp.domain.login

import com.pipe.cleanapp.data.common.utils.WrappedResponse
import com.pipe.cleanapp.data.login.remote.dto.LoginRequest
import com.pipe.cleanapp.data.login.remote.dto.LoginResponse
import com.pipe.cleanapp.domain.common.base.BaseResult
import com.pipe.cleanapp.domain.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(loginRequest: LoginRequest) : Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>>
}