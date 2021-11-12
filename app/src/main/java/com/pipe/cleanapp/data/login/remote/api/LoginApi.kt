package com.pipe.cleanapp.data.login.remote.api

import com.pipe.cleanapp.data.common.utils.WrappedResponse
import com.pipe.cleanapp.data.login.remote.dto.LoginRequest
import com.pipe.cleanapp.data.login.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest) : Response<WrappedResponse<LoginResponse>>
}