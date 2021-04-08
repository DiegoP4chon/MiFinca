package com.ganawin.mifinca.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.auth.LoginDataSource
import com.ganawin.mifinca.databinding.FragmentLoginBinding
import com.ganawin.mifinca.domain.auth.LoginRepoImpl
import com.ganawin.mifinca.presentation.auth.LoginScreenViewModel
import com.ganawin.mifinca.presentation.auth.LoginScreenViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<LoginScreenViewModel> { LoginScreenViewModelFactory(LoginRepoImpl(
        LoginDataSource()
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        isUserLoggedIn()
        doLogin()

        binding.tvRegister.setOnClickListener { callFragmentRegister() }
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