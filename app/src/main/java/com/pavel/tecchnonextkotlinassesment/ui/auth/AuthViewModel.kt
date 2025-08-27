package com.pavel.tecchnonextkotlinassesment.ui.auth

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavel.tecchnonextkotlinassesment.util.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val error: String? = null,
    val loading: Boolean = false,
    val success: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val prefsManager: PrefsManager) : ViewModel() {

    private val _registerState = MutableStateFlow(AuthState())
    val registerState: StateFlow<AuthState> = _registerState

    private val _loginState = MutableStateFlow(AuthState())
    val loginState: StateFlow<AuthState> = _loginState


    fun register(email: String, password: String, confirm: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _registerState.value = AuthState(loading = true)
            when{
                (!Patterns.EMAIL_ADDRESS.matcher(email).matches())->{
                    _registerState.value = AuthState(error = "Invalid email format")
                }

                (password.length < 6)-> {
                    _registerState.value = AuthState(error = "Password must be at least 6 chars")
                }

                (password != confirm)-> {
                    _registerState.value = AuthState(error = "Passwords do not match")
                }

                else->{
                    onResult(true)
                    prefsManager.saveUser(email,password)
                    _registerState.value = AuthState(success = true)
                }
            }



        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loginState.value = AuthState(loading = true)
            val savedEmail = prefsManager.userEmail
            val savedPass = prefsManager.userPassword

            savedEmail.collect { e ->
                savedPass.collect { p ->
                    val ok = e == email && p == password
                    _loginState.value = AuthState(success = ok, error = if (!ok) "Invalid credentials" else null)
                    onResult(ok)
                    return@collect
                }
                return@collect
            }


        }
    }
}