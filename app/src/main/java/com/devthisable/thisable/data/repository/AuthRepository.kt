package com.devthisable.thisable.data.repository

import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.auth.login.LoginBody
import com.devthisable.thisable.data.remote.auth.login.LoginResponse
import com.devthisable.thisable.data.remote.auth.register.RegisterBody
import com.devthisable.thisable.data.remote.auth.register.RegisterResponse
import com.devthisable.thisable.data.source.AuthDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val dataSource: AuthDataSource) {

    suspend fun registerUser(registerBody: RegisterBody): Flow<ApiResponse<RegisterResponse>> {
        return dataSource.registerUser(registerBody).flowOn(Dispatchers.IO)
    }

    suspend fun loginUser(loginBody: LoginBody): Flow<ApiResponse<LoginResponse>> {
        return dataSource.loginUser(loginBody).flowOn(Dispatchers.IO)
    }

}