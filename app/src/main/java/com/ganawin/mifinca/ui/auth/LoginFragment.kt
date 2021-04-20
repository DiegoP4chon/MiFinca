package com.ganawin.mifinca.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.LoginTelefono
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.auth.LoginDataSource
import com.ganawin.mifinca.databinding.FragmentLoginBinding
import com.ganawin.mifinca.domain.auth.LoginRepoImpl
import com.ganawin.mifinca.presentation.auth.LoginScreenViewModel
import com.ganawin.mifinca.presentation.auth.LoginScreenViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<LoginScreenViewModel> { LoginScreenViewModelFactory(LoginRepoImpl(
        LoginDataSource()
    )) }
    private lateinit var loginTelefono: LoginTelefono

    private val TAG = "verificarAuthPhone"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        loginTelefono = LoginTelefono(requireActivity())
        isUserLoggedIn()
        doLogin()

        binding.btnLoginChange.setOnClickListener { changeMethodLogin() }
        binding.tvRegister.setOnClickListener { callFragmentRegister() }
        binding.btnSendCode.setOnClickListener { authTelefono() }
        binding.btnVerificarCode.setOnClickListener { signInWithPhoneAuthCredential() }
    }

    private fun changeMethodLogin(){
        if(binding.constraintEmail.visibility == View.VISIBLE) {
            binding.constraintEmail.visibility = View.GONE
            binding.constraintPhoneNumber.visibility = View.VISIBLE
            binding.btnLoginChange.text = getString(R.string.login_email)
            binding.btnLoginChange.setIconResource(R.drawable.ic_email)
        } else {
            binding.constraintEmail.visibility = View.VISIBLE
            binding.constraintPhoneNumber.visibility = View.GONE
            binding.constraintPhoneCode.visibility = View.GONE
            binding.btnLoginChange.text = getString(R.string.login_telefono)
            binding.btnLoginChange.setIconResource(R.drawable.ic_phone)
        }
    }

    private fun authTelefono(){

        if(binding.etPhone.text.toString().isNotEmpty()) {
            loginTelefono.verificarTelefono(binding.etPhone.text.toString().trim())
            binding.constraintPhoneNumber.visibility = View.GONE
            binding.constraintPhoneCode.visibility = View.VISIBLE
        } else {
            binding.etPhone.error = getString(R.string.error_phone_require)
        }
    }

    private fun signInWithPhoneAuthCredential() {
        firebaseAuth.signInWithCredential(loginTelefono.verificarCodigo(binding.etCode.text.toString().trim()))
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        Toast.makeText(requireContext(), "Bienvenido", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(
                                R.id.action_loginFragment_to_homeFragment)
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.d(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                        // Update UI
                    }
                }
    }

    private fun callFragmentRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerUserFragment)
    }

    private fun isUserLoggedIn() {
        firebaseAuth.currentUser?.let {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    private fun doLogin(){
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPass.text.toString().trim()
            validateCredentials(email, password)
            signIn(email, password)
        }

    }

    private fun validateCredentials(email: String, password: String) {

        if(email.isEmpty()){
            binding.etEmail.error = getString(R.string.error_email_require)
            return
        }

        if(password.isEmpty()){
            binding.etPass.error = getString(R.string.error_pass_require)
            return
        }
    }

    private fun signIn(email: String, password: String) {
        viewModel.signIn(email, password).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.invalid_user), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

}